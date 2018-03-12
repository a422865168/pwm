package com.hisun.lemon.pwm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hisun.lemon.cmm.client.CmmServerClient;
import com.hisun.lemon.cmm.dto.MessageSendReqDTO;
import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.common.utils.StringUtils;
import com.hisun.lemon.constants.AccConstants;
import com.hisun.lemon.constants.RiskConstants;
import com.hisun.lemon.cpi.client.RemittanceClient;
import com.hisun.lemon.cpi.client.RouteClient;
import com.hisun.lemon.csh.client.CshOrderClient;
import com.hisun.lemon.csh.client.CshRefundClient;
import com.hisun.lemon.csh.dto.cashier.CashierViewDTO;
import com.hisun.lemon.csh.dto.cashier.InitCashierDTO;
import com.hisun.lemon.csh.dto.order.OrderDTO;
import com.hisun.lemon.csh.dto.refund.RefundOrderDTO;
import com.hisun.lemon.csh.dto.refund.RefundOrderRspDTO;
import com.hisun.lemon.dto.AccDataListDTO;
import com.hisun.lemon.dto.RiskDataDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.framework.i18n.LocaleMessageSource;
import com.hisun.lemon.framework.lock.DistributedLocker;
import com.hisun.lemon.framework.service.BaseService;
import com.hisun.lemon.framework.utils.IdGenUtils;
import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeRevokeDTO;
import com.hisun.lemon.pwm.dto.UserInfoRspDTO;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
import com.hisun.lemon.pwm.mq.BillSyncHandler;
import com.hisun.lemon.pwm.mq.PaymentHandler;
import com.hisun.lemon.pwm.service.IRechargeOrderService;
import com.hisun.lemon.pwm.utils.AcsUtils;
import com.hisun.lemon.rsk.client.XXRiskHandleClient;
import com.hisun.lemon.tfm.client.TfmServerClient;
import com.hisun.lemon.tfm.dto.MerchantRefundFeeReversalReqDTO;
import com.hisun.lemon.urm.client.UserBasicInfClient;
import com.hisun.lemon.urm.dto.UserBasicInfDTO;
import com.hisun.lemon.xxka.client.XXAccHandleClient;


@Service
public class RechargeOrderServiceImpl extends BaseService implements IRechargeOrderService {
	// 短信推送
	public static final int RECHARGE_SUCCESS = 1;
	public static final int RECHARGE_OFFLINE_BACK = 2;
	// 账单同步
	public static final int CREATE_BIL = 1;
	public static final int UPD_BIL = 2;

	private static final Logger logger = LoggerFactory.getLogger(RechargeOrderServiceImpl.class);
	@Resource
	RechargeOrderTransactionalService service;

	@Resource
	CshOrderClient cshOrderClient;
	@Resource
    private XXRiskHandleClient riskCheckClient;

	@Resource
	UserBasicInfClient userBasicInfClient;

	@Resource
	TfmServerClient fmServerClient;


	@Resource
	private RouteClient routeClient;


	@Resource
	protected DistributedLocker locker;

	@Resource
	private CmmServerClient cmmServerClient;

	@Resource
	private CshRefundClient cshRefundClient;


	@Resource
	protected PaymentHandler paymentHandler;

	@Resource
	private TfmServerClient tmfServerClient;

	@Resource
	BillSyncHandler billSyncHandler;

	@Resource
	LocaleMessageSource localeMessageSource;

	@Resource
	RemittanceClient remittanceClient;

	@Resource
	private XXAccHandleClient acsTreatmentClient;

	/**
	 * 查询账户信息
	 */
	public UserInfoRspDTO userInfo(String mblNo) {
		GenericRspDTO<UserBasicInfDTO> infoDTO = userBasicInfClient.queryUserByLoginId(mblNo);
		if (!JudgeUtils.isSuccess(infoDTO.getMsgCd())) {
			logger.error("查询账户信息失败:" + mblNo);
			throw new LemonException(infoDTO.getMsgCd());
		}
		UserInfoRspDTO userInfo = new UserInfoRspDTO();
		userInfo.setUserId(infoDTO.getBody().getUserId());
		userInfo.setMblNo(infoDTO.getBody().getMblNo());
		userInfo.setDisplayNm(infoDTO.getBody().getDisplayNm());
		userInfo.setAvatarPath(infoDTO.getBody().getAvatarPath());
		userInfo.setUsrSts(infoDTO.getBody().getUsrSts());
		userInfo.setUsrLvl(infoDTO.getBody().getUsrLvl());
		userInfo.setIdChkFlg(infoDTO.getBody().getIdChkFlg());
		userInfo.setUsrNmHid(infoDTO.getBody().getUsrNmHid());
		userInfo.setIdType(infoDTO.getBody().getIdType());
		userInfo.setIdNo(infoDTO.getBody().getIdNo());
		userInfo.setUsrNm(infoDTO.getBody().getUsrNm());
		userInfo.setUsrGender(infoDTO.getBody().getUsrGender());
		userInfo.setUsrNation(infoDTO.getBody().getUsrNation());
		userInfo.setUsrBirthDt(infoDTO.getBody().getUsrBirthDt());
		return userInfo;
	}

	
	/**
	 * 充值下单
	 */
	@Override
	public GenericRspDTO<CashierViewDTO> createOrder(GenericDTO<RechargeDTO> genRechargeDTO) {
		RechargeDTO rechargeDTO = genRechargeDTO.getBody();
		if (!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_RECHANGE)) {
			throw new LemonException("PWM20001");
		}
		String payer = LemonUtils.getUserId();
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 11);
		orderNo = rechargeDTO.getBusType() + ymd + orderNo;

		// Step3:风控，黑名单检查
		GenericDTO<RiskDataDTO> genericReqDTO = new GenericDTO<>();
		RiskDataDTO riskDataDTO = new RiskDataDTO();
		Map<String, Object> riskDataMap = new HashMap();
		riskDataDTO.setRiskType(RiskConstants.RISK_TYPE_BLACK);
		// 对象规则
		//对公对私标志(0:个人|1:商户)
		if(JudgeUtils.equals(rechargeDTO.getPsnFlag(), "0")){
			riskDataMap.put("RULE_ROLE", RiskConstants.RULE_ROLE_REAL_USER);
			// 用户内部用户号
			riskDataMap.put("USER_USR_NO", LemonUtils.getUserId());
		}else if (JudgeUtils.equals(rechargeDTO.getPsnFlag(), "1")){
			riskDataMap.put("RULE_ROLE", RiskConstants.RULE_ROLE_MERC);
			// 用户内部用户号
			riskDataMap.put("MERC_USR_NO", LemonUtils.getUserId());
		}
		
		riskDataDTO.setRiskDataMap(riskDataMap);
		genericReqDTO.setBody(riskDataDTO);
		GenericRspDTO<RiskDataDTO> riskDataDTOGenericRspDTO = riskCheckClient.xxRiskHandle(genericReqDTO);
		 if(JudgeUtils.isNotSuccess(riskDataDTOGenericRspDTO.getMsgCd())){
	        	logger.info("黑白名单风控检查失败");
	        	LemonException.throwBusinessException("PWM30001");
	        }
		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(rechargeDTO.getBusType());
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setOrderAmt(rechargeDTO.getAmount());
		rechargeOrderDO.setOrderCcy("CNY");
		rechargeOrderDO.setOrderExpTm(DateTimeUtils.getCurrentLocalDateTime().plusDays(2));
		rechargeOrderDO.setOrderNo(orderNo);
		rechargeOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_W);
		rechargeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setPsnFlag(rechargeDTO.getPsnFlag());
		rechargeOrderDO.setRemark("");
		rechargeOrderDO.setPayerId(LemonUtils.getUserId());
		rechargeOrderDO.setSysChannel(rechargeDTO.getSysChannel());
		rechargeOrderDO.setTxType("01");
		service.initOrder(rechargeOrderDO);

		logger.info("登记充值订单成功，订单号：" + rechargeOrderDO.getOrderNo());
		// 调用收银
		InitCashierDTO initCashierDTO = new InitCashierDTO();
		initCashierDTO.setBusPaytype(null);// 业务指定支付方式 PwmConstants.BUS_PAY_TYPE
		// initCashierDTO.setBusPaytype(PwmConstants.BUS_PAY_TYPE);
		initCashierDTO.setBusType(rechargeOrderDO.getBusType());
		initCashierDTO.setExtOrderNo(rechargeOrderDO.getOrderNo());
		initCashierDTO.setSysChannel(rechargeDTO.getSysChannel());
		initCashierDTO.setPayerId(LemonUtils.getUserId());
		initCashierDTO.setPayeeId(LemonUtils.getUserId());
		initCashierDTO.setAppCnl(LemonUtils.getApplicationName());
		initCashierDTO.setTxType(rechargeOrderDO.getTxType());
		initCashierDTO.setOrderAmt(rechargeDTO.getAmount());
		initCashierDTO.setEffTm(rechargeOrderDO.getOrderExpTm());
		// 快捷充值订单信息国际化
		Object[] args = new Object[] { rechargeOrderDO.getOrderAmt() };
		String descStr = getViewOrderInfo(rechargeOrderDO.getBusType(), args);

		initCashierDTO.setGoodsDesc(descStr);
		GenericDTO<InitCashierDTO> genericDTO = new GenericDTO<>();
		genericDTO.setBody(initCashierDTO);
		logger.info("订单：" + rechargeOrderDO.getOrderNo() + " 请求收银台");
		// 调用收银服务接口 初始化收银订单
		GenericRspDTO<CashierViewDTO> genericCashierViewRspDTO = cshOrderClient.initCashier(genericDTO);
		if (JudgeUtils.isNotSuccess(genericCashierViewRspDTO.getMsgCd())) {
			LemonException.throwBusinessException(genericCashierViewRspDTO.getMsgCd());
		}
		CashierViewDTO cashierViewDTO = genericCashierViewRspDTO.getBody();
		// 更新充值订单信息
		RechargeOrderDO updateDO = new RechargeOrderDO();
		updateDO.setOrderNo(rechargeOrderDO.getOrderNo());
		updateDO.setFee(cashierViewDTO.getFeeAmt());// 充值手续费
		updateDO.setFeeFlag(cashierViewDTO.getFeeFlag());// 手续费类型 IN 内扣 EX 外扣
		updateDO.setExtOrderNo(cashierViewDTO.getOrderNo());
		service.updateOrder(updateDO);
		return genericCashierViewRspDTO;
	}

	/**
	 */
	@Override
	public void handleResult(GenericDTO resultDto) {
		RechargeResultDTO rechargeResultDTO = (RechargeResultDTO) resultDto.getBody();
		String orderNo = rechargeResultDTO.getOrderNo();
		//主交易类型
		String mainTxTyp = "02";
		handleSuccess(rechargeResultDTO.getStatus(), rechargeResultDTO.getOrderCcy(), rechargeResultDTO.getAmount(),
				orderNo, rechargeResultDTO.getExtOrderNo(), rechargeResultDTO.getRemark(),
				rechargeResultDTO.getBusType(), rechargeResultDTO.getPayerId(), rechargeResultDTO.getFee(),
				resultDto.getAccDate(), mainTxTyp);
	}

	/**
	 * 充值长款补单
	 */
	@Override
	public void repeatResultHandle(String orderNo) {
		System.out.println("充值长款补单   订单号:"+orderNo);
		GenericRspDTO<OrderDTO> genDto = cshOrderClient.query(orderNo);
		OrderDTO orderDTO = genDto.getBody();
		String mainTxTyp = "04";
		if (JudgeUtils.isSuccess(genDto.getMsgCd())) {
			
				handleSuccess(PwmConstants.RECHARGE_ORD_S, null, orderDTO.getOrderAmt(), orderNo,
						orderDTO.getBusOrderNo(), null, orderDTO.getBusType(), orderDTO.getPayerId(), orderDTO.getFee(),
						LemonUtils.getAccDate(), mainTxTyp);
		} else {
			LemonException.throwBusinessException(genDto.getMsgCd());
		}
	}

	private void handleSuccess(String status, String ccy, BigDecimal amount, String orderNo, String extOrderNo,
			String remark, String busType, String payerId, BigDecimal fee, LocalDate acDt, String mainTxTyp) {
		try {
			RechargeOrderDO rechargeOrderDO = service.getRechangeOrderDao().get(orderNo);
			locker.lock(//
					"PWM.RESULT_RG." + orderNo, 19, 17, () -> {
						// 未找到订单
						if (rechargeOrderDO == null) {
							throw new LemonException("PWM20002");
						}
						// 判断返回状态
						if (!StringUtils.equals(status, PwmConstants.RECHARGE_ORD_S)) {
							RechargeOrderDO updOrderDO = new RechargeOrderDO();
							updOrderDO.setExtOrderNo(extOrderNo);
							updOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_F);
							if (StringUtils.isNoneBlank(ccy)) {
								updOrderDO.setOrderCcy(ccy);
							}
							updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
							updOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
							updOrderDO.setOrderNo(orderNo);
							updOrderDO.setFee(fee);
							updOrderDO.setPayerId(rechargeOrderDO.getPayerId());
							// 若是汇款充值，此处为审核失败原因
							updOrderDO.setRemark(remark);
							if (JudgeUtils.equals(rechargeOrderDO.getBusType(), PwmConstants.BUS_TYPE_RECHARGE_OFL)) {
								logger.info("汇款拒绝理由: " + updOrderDO.getRemark());
								sendMsgCenterInfo(updOrderDO, RECHARGE_OFFLINE_BACK);
							}
							service.updateOrder(updOrderDO);
							return null;
						}

						// 比较金额
						if (rechargeOrderDO.getOrderAmt().compareTo(amount) != 0) {

							throw new LemonException("PWM20003");
						}

						// 总金额
						BigDecimal rechargeTotalAmt = rechargeOrderDO.getOrderAmt();
						// 用户账户金额
						BigDecimal userAmt = rechargeOrderDO.getOrderAmt();
						GenericRspDTO<UserBasicInfDTO> userBasicInfDTO = userBasicInfClient
								.queryUser(rechargeOrderDO.getPayerId());
						if (JudgeUtils.isNotSuccess(userBasicInfDTO.getMsgCd())) {
							LemonException.throwBusinessException(userBasicInfDTO.getMsgCd());
						}
						UserBasicInfDTO user = userBasicInfDTO.getBody();
						String acNo = user.getAcNo();
						if (JudgeUtils.isEmpty(acNo)) {
							LemonException.throwBusinessException("PWM40006");
						}
						String miniTxTyp = "";
						if (rechargeOrderDO.getPsnFlag().equals("0")) {
							miniTxTyp = mainTxTyp + "01";
							try {
								//个人账务处理
								innerAccAcsDealPerson(acNo, acNo.substring(0, 3), AccConstants.CAP_TYP_CASH, userAmt.floatValue(),
										acNo, rechargeTotalAmt.floatValue(), orderNo, mainTxTyp, miniTxTyp);
							} catch (Exception e) {
								LemonException.throwBusinessException(e.getMessage());
							}
						} else {
							miniTxTyp = mainTxTyp + "02";
							try {
								//商户账务处理
								innerAccAcsDealMer(acNo, acNo.substring(0, 3), AccConstants.CAP_TYP_CASH, userAmt.floatValue(), acNo,
										rechargeTotalAmt.floatValue(), orderNo, mainTxTyp, miniTxTyp);
							} catch (Exception e) {
								LemonException.throwBusinessException(e.getMessage());
							}
						}
						
						//累计风控
						logger.info("累计风控");
						GenericDTO<RiskDataDTO> genericTimeDTO = new GenericDTO<>();
						RiskDataDTO riskDataTimeDTO = new RiskDataDTO();
						Map<String, Object> riskDataTimeMap = new HashMap();
						riskDataTimeDTO.setRiskType(RiskConstants.RISK_TYPE_REALTIME);
						riskDataTimeMap.put("RULE_ROLE", RiskConstants.RULE_ROLE_REAL_USER);
						riskDataTimeMap.put("USER_USR_NO", rechargeOrderDO.getPayerId());
						riskDataTimeMap.put("TX_ORD_AMT", rechargeTotalAmt);
						riskDataTimeMap.put("TX_TYP", RiskConstants.TX_TYP_CZ);
						riskDataTimeMap.put("TX_ORD_NO", orderNo);
						DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
						String actDate = df.format(rechargeOrderDO.getAcTm());
						riskDataTimeMap.put("TX_ORD_DT", actDate);
						riskDataTimeMap.put("CARD_TYP", RiskConstants.CARD_TYP_DEBIT);
						riskDataTimeMap.put("PAY_TYP", RiskConstants.PAY_TYP_QUICK_PAY);
						riskDataTimeDTO.setRiskDataMap(riskDataTimeMap);
						genericTimeDTO.setBody(riskDataTimeDTO);
						GenericRspDTO<RiskDataDTO> riskTimeDTO = riskCheckClient.xxRiskHandle(genericTimeDTO);
						if (JudgeUtils.isNotSuccess(riskTimeDTO.getMsgCd())) {
							logger.info("累计风控检查失败");
							LemonException.throwBusinessException("PWM30001");
						}
						// 更新订单
						RechargeOrderDO updOrderDO = new RechargeOrderDO();
						updOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
						updOrderDO.setExtOrderNo(extOrderNo);
						updOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
						updOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
						updOrderDO.setFee(fee);
						updOrderDO.setOrderAmt(rechargeOrderDO.getOrderAmt());
						updOrderDO.setPayerId(rechargeOrderDO.getPayerId());
						if (StringUtils.isNoneBlank(ccy)) {
							updOrderDO.setOrderCcy(ccy);
						}

						updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
						updOrderDO.setOrderNo(orderNo);
						service.updateOrder(updOrderDO);
						// 推送充值信息到消息中心
						sendMsgCenterInfo(updOrderDO, RECHARGE_SUCCESS);
						return null;
					});
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			LemonException.create(e);
		}
	}
	
	/**
	 * 充值撤单处理
	 * 
	 * @param genericDTO
	 */
	@Override
	public void rechargeRevoke(GenericDTO<RechargeRevokeDTO> genericDTO) {

		RechargeRevokeDTO rechargeRevokeDTO = genericDTO.getBody();
		if (JudgeUtils.isNull(rechargeRevokeDTO)) {
			throw new LemonException("PWM20035");
		}
		String chkSubType = rechargeRevokeDTO.getChkSubType();// 对账子类型
		if (!(JudgeUtils.equals(chkSubType, "0404") || JudgeUtils.equals(chkSubType, "0405"))) {
			throw new LemonException("PWM20036");
		}
		String rechargeOrderNo = rechargeRevokeDTO.getOrderNo();
		logger.info("充值订单号：" + rechargeOrderNo + "撤单处理开始....");
		try {
			locker.lock("PWM_LOCK.RECHARGE.REVOKE." + rechargeOrderNo, 18, 22, () -> {
				RechargeOrderDO rechargeOrderDO = this.service.getRechangeOrderDao().get(rechargeOrderNo);
				RefundOrderDTO refundOrderDTO = new RefundOrderDTO();
				refundOrderDTO.setBusRdfOrdNo(rechargeOrderDO.getOrderNo());
				refundOrderDTO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_SHORTAMT_REFUND);
				// 查询收银订单信息
				GenericRspDTO<OrderDTO> genericRspDTO = cshOrderClient.query(rechargeOrderNo);
				if (JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())) {
					LemonException.throwBusinessException(genericRspDTO.getMsgCd());
				}
				OrderDTO orderDTO = genericRspDTO.getBody();
				refundOrderDTO.setGoodInfo(orderDTO.getGoodsInfo());
				refundOrderDTO.setOrderCcy(rechargeOrderDO.getOrderCcy());
				refundOrderDTO.setOrginOrderNo(rechargeOrderDO.getExtOrderNo());
				refundOrderDTO.setRefundUserId(rechargeOrderDO.getPayerId());
				refundOrderDTO.setTxType(PwmConstants.TX_TYPE_RECHARGE_REFUND);
				// 退款金额为订单金额
				refundOrderDTO.setRfdAmt(rechargeOrderDO.getOrderAmt());
				GenericDTO genericRefundOrder = new GenericDTO();
				genericRefundOrder.setBody(refundOrderDTO);
				// 调用收银,退款结果处理
				GenericRspDTO<RefundOrderRspDTO> genericRefundOrderRsp = cshRefundClient.createBill(genericRefundOrder);
				if (JudgeUtils.isNotSuccess(genericRefundOrderRsp.getMsgCd())) {
					LemonException.throwBusinessException(genericRefundOrderRsp.getMsgCd());
				}
				// 更新充值订单为已退款
				rechargeOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_R);
				rechargeOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
				rechargeOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
				this.service.updateOrder(rechargeOrderDO);
				logger.info("充值订单号:" + rechargeOrderNo + "撤单成功!");
				return null;
			});
		} catch (Exception e) {
			LemonException.throwBusinessException("PWM20028");
		}

	}




	public static final String bytesToHexString(byte[] bArray) {
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}

	


	/**
	 * 充值信息中心推送
	 * 
	 * @param rechargeOrderDO
	 * @param messageFlag
	 * @throws LemonException
	 */
	public void sendMsgCenterInfo(RechargeOrderDO rechargeOrderDO, int messageFlag) {
		try {
			String userId = rechargeOrderDO.getPayerId();
			logger.info("recharge message send to userId : " + userId);
			String language = LemonUtils.getLocale().getLanguage();
			if (JudgeUtils.isBlank(language)) {
				language = "en";
				logger.error("setting default language : " + language);
			}
			Map<String, String> map = new HashMap();
			GenericDTO<MessageSendReqDTO> reqDTO = new GenericDTO();
			MessageSendReqDTO messageSendReqDTO = new MessageSendReqDTO();
			messageSendReqDTO.setUserId(userId);
			messageSendReqDTO.setMessageLanguage(language);
			switch (messageFlag) {
			case RECHARGE_SUCCESS:
				messageSendReqDTO.setMessageTemplateId(PwmConstants.RECHARGE_SUCC_TEMPL);
				BigDecimal orderAmt = rechargeOrderDO.getOrderAmt();
				if (JudgeUtils.isNotNull(orderAmt)) {
					map.put("orderAmt", String.valueOf(rechargeOrderDO.getOrderAmt()));
				} else {
					map.put("orderAmt", "");
				}
				break;
			case RECHARGE_OFFLINE_BACK:
				messageSendReqDTO.setMessageTemplateId(PwmConstants.RECHARGE_OFFLINE_BACK_TEMPL);
				map.put("reason", rechargeOrderDO.getRemark());
				break;
			default:
				break;
			}
			map.put("date", DateTimeUtils.formatLocalDate(rechargeOrderDO.getAcTm(), "yyyy-MM-dd"));
			messageSendReqDTO.setReplaceFieldMap(map);
			reqDTO.setBody(messageSendReqDTO);
			GenericRspDTO<NoBody> genericRspDTO = cmmServerClient.messageSend(reqDTO);
			if (JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())) {
				logger.error("充值通知类型:" + messageFlag + ", 推送订单号：" + rechargeOrderDO.getOrderNo() + "信息失败。");
			}
		} catch (Exception e) {
			logger.error("推送失败:" + e.getMessage());
		}

		logger.info("消息推送成功");
	}




	/**
	 * 同步原订单和更新订单数据
	 * 
	 * @param oriOrder
	 *            原订单
	 * @param updateOrder
	 *            更新后订单
	 * @return
	 */
	private RechargeOrderDO syncOrderData(RechargeOrderDO oriOrder, RechargeOrderDO updateOrder) {
		if (JudgeUtils.isNull(updateOrder.getOrderAmt())) {
			updateOrder.setOrderAmt(oriOrder.getOrderAmt());
		}
		if (JudgeUtils.isNull(updateOrder.getAcTm())) {
			updateOrder.setAcTm(updateOrder.getAcTm());
		}
		if (JudgeUtils.isNull(updateOrder.getPayerId())) {
			updateOrder.setPayerId(oriOrder.getPayerId());
		}
		if (JudgeUtils.isNull(updateOrder.getFee())) {
			updateOrder.setFee(oriOrder.getFee());
		}
		if (JudgeUtils.isNull(updateOrder.getBusType())) {
			updateOrder.setBusType(oriOrder.getBusType());
		}
		if (JudgeUtils.isNull(updateOrder.getTxType())) {
			updateOrder.setTxType(oriOrder.getTxType());
		}
		if (JudgeUtils.isNull(updateOrder.getOrderTm())) {
			updateOrder.setOrderTm(oriOrder.getOrderTm());
		}

		return updateOrder;
	}

	
	/**
	 * 国际化商品描述信息
	 * 
	 * @param busType
	 * @param args
	 * @return
	 */
	public String getViewOrderInfo(String busType, Object[] args) {
		// 国际化配置文件中key值
		String key = "";
		try {
			if (JudgeUtils.isNull(busType)) {
				return null;
			}
			if (busType.startsWith(PwmConstants.TX_TYPE_HCOUPON)) {
				key = "view.orderinfo." + busType;
			} else if (busType.startsWith(PwmConstants.TX_TYPE_RECHANGE)) {
				key = "view.rechargeinfo." + busType;
				if (args.length > 1 && JudgeUtils.equals(busType, PwmConstants.BUS_TYPE_RECHARGE_OFL)) {
					final String seperator = ".";
					key = key + seperator + args[0] + seperator + args[1];
					return localeMessageSource.getMessage(key);
				}
			} else {

			}
			return localeMessageSource.getMessage(key, args);
		} catch (Exception e) {
			logger.error("获取国际化配置文件key=" + key + "失败!");
		}
		return null;
	}

	/**
	 * 商户手续费退款处理撤销
	 *
	 * @param merchantRfdNo
	 * @return
	 */
	public GenericRspDTO<NoBody> merchantRefundFeeReversal(String merchantRfdNo) {

		GenericDTO<MerchantRefundFeeReversalReqDTO> reqDTO = new GenericDTO<>();
		MerchantRefundFeeReversalReqDTO reversalReqDTO = new MerchantRefundFeeReversalReqDTO();
		reversalReqDTO.setOrderNo(merchantRfdNo);
		reqDTO.setBody(reversalReqDTO);
		return tmfServerClient.merchantRefundFeeReversal(reqDTO);

	}

	/**
	 * InnerAccDebit 内部账户处理(个人充值)
	 */
	private void innerAccAcsDealPerson(String acNo, String acTyp, String capTyp, Float accAmt, String oppAcNo,
			Float txAmt, String ordNo, String mainTxTyp, String miniTxTyp) throws Exception {
		GenericDTO<AccDataListDTO> genericReqDTO = new GenericDTO<>();
		AccDataListDTO accDataListDTO = new AccDataListDTO();
		List<Map<String, Object>> accDataMapList = new ArrayList();

		// 内部账户借记处理: DR-借:应收账款-渠道充值款-xx银行/中国银联/网联
		Map<String, Object> accDataMap1 = AcsUtils.getAccDataListDTO(AccConstants.INNER_ACC_DR, "3010001",
				"应收账款-渠道充值款-xx银行/中国银联/网联", "301", capTyp, accAmt, AccConstants.DC_FLG_D, "0201",
				AccConstants.BUS_TYP_WEB, oppAcNo, "个人账户", txAmt, "01", ordNo, "1", AccConstants.NOT_TX_FLG_NOT_TX,
				"個人充值");

		// 内部账户贷记处理 : CR-贷:个人账户
		Map<String, Object> accDataMap2 = AcsUtils.getAccDataListDTO(AccConstants.USR_ACC_CR, acNo, "个人账户", acTyp,
				capTyp, accAmt, AccConstants.DC_FLG_C, "0201", AccConstants.BUS_TYP_WEB, "3010001",
				"应收账款-渠道充值款-xx银行/中国银联/网联", txAmt, "01", ordNo, "2", AccConstants.NOT_TX_FLG_NOT_TX, "個人充值");

		accDataMapList.add(accDataMap1);
		accDataMapList.add(accDataMap2);

		accDataListDTO.setAccDataMapList(accDataMapList);
		accDataListDTO.setMainTxTyp(mainTxTyp);
		accDataListDTO.setMiniTxTyp(miniTxTyp);
		accDataListDTO.setRvsTxFlg(AccConstants.RVS_TX_FLG_N);// 正常交易

		genericReqDTO.setBody(accDataListDTO);
		GenericRspDTO genericRspDTO = acsTreatmentClient.xxAccHandle(genericReqDTO);
		if (JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())) {
			LemonException.throwBusinessException(genericRspDTO.getMsgCd());
		}
	}

	/**
	 * InnerAccDebit 内部账户处理(商户充值)
	 */
	private void innerAccAcsDealMer(String acNo, String acTyp, String capTyp, Float accAmt, String oppAcNo, Float txAmt,
			String ordNo, String mainTxTyp, String miniTxTyp) throws Exception {
		GenericDTO<AccDataListDTO> genericReqDTO = new GenericDTO<>();
		AccDataListDTO accDataListDTO = new AccDataListDTO();
		List<Map<String, Object>> accDataMapList = new ArrayList();

		// 内部账户借记处理: DR-借:应收账款-渠道充值款-xx银行/中国银联/网联
		Map<String, Object> accDataMap1 = AcsUtils.getAccDataListDTO(AccConstants.INNER_ACC_DR, "3010001",
				"应收账款-渠道充值款-xx银行/中国银联/网联", "301", capTyp, accAmt, AccConstants.DC_FLG_D, "0202",
				AccConstants.BUS_TYP_WEB, oppAcNo, "商户账户", txAmt, "01", ordNo, "1", AccConstants.NOT_TX_FLG_NOT_TX,
				"商戶充值");

		// 内部账户贷记处理 : CR-贷:商户账户
		Map<String, Object> accDataMap2 = AcsUtils.getAccDataListDTO(AccConstants.MERC_ACC_CR, acNo, "商户账户", acTyp,
				capTyp, accAmt, AccConstants.DC_FLG_C, "0202", AccConstants.BUS_TYP_WEB, "3010001",
				"应收账款-渠道充值款-xx银行/中国银联/网联", txAmt, "01", ordNo, "2", AccConstants.NOT_TX_FLG_NOT_TX, "商戶充值");
		accDataMapList.add(accDataMap1);
		accDataMapList.add(accDataMap2);

		accDataListDTO.setAccDataMapList(accDataMapList);
		accDataListDTO.setMainTxTyp(mainTxTyp);
		accDataListDTO.setMiniTxTyp(miniTxTyp);
		accDataListDTO.setRvsTxFlg(AccConstants.RVS_TX_FLG_N);// 正常交易

		genericReqDTO.setBody(accDataListDTO);
		GenericRspDTO genericRspDTO = acsTreatmentClient.xxAccHandle(genericReqDTO);
		if (JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())) {
			LemonException.throwBusinessException(genericRspDTO.getMsgCd());
		}
	}
}
