package com.hisun.lemon.pwm.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.lemon.acm.client.AccountManagementClient;
import com.hisun.lemon.acm.client.AccountingTreatmentClient;
import com.hisun.lemon.acm.constants.ACMConstants;
import com.hisun.lemon.acm.constants.CapTypEnum;
import com.hisun.lemon.acm.dto.AccountingReqDTO;
import com.hisun.lemon.bil.dto.CreateUserBillDTO;
import com.hisun.lemon.bil.dto.UpdateUserBillDTO;
import com.hisun.lemon.cmm.client.CmmServerClient;
import com.hisun.lemon.cmm.dto.MessageSendReqDTO;
import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.BeanUtils;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.common.utils.StringUtils;
import com.hisun.lemon.cpi.client.RouteClient;
import com.hisun.lemon.cpi.dto.RouteRspDTO;
import com.hisun.lemon.cpi.enums.CorpBusSubTyp;
import com.hisun.lemon.cpi.enums.CorpBusTyp;
import com.hisun.lemon.csh.client.CshOrderClient;
import com.hisun.lemon.csh.client.CshRefundClient;
import com.hisun.lemon.csh.constants.CshConstants;
import com.hisun.lemon.csh.dto.cashier.CashierViewDTO;
import com.hisun.lemon.csh.dto.cashier.DirectPaymentDTO;
import com.hisun.lemon.csh.dto.cashier.InitCashierDTO;
import com.hisun.lemon.csh.dto.order.OrderDTO;
import com.hisun.lemon.csh.dto.payment.OfflinePaymentDTO;
import com.hisun.lemon.csh.dto.payment.OfflinePaymentResultDTO;
import com.hisun.lemon.csh.dto.payment.PaymentResultDTO;
import com.hisun.lemon.csh.dto.refund.RefundOrderDTO;
import com.hisun.lemon.csh.dto.refund.RefundOrderRspDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.framework.i18n.LocaleMessageSource;
import com.hisun.lemon.framework.lock.DistributedLocker;
import com.hisun.lemon.framework.utils.IdGenUtils;
import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.jcommon.file.FileSftpUtils;
import com.hisun.lemon.jcommon.phonenumber.PhoneNumberUtils;
import com.hisun.lemon.mkm.client.MarketActivityClient;
import com.hisun.lemon.mkm.req.dto.RechargeMkmToolReqDTO;
import com.hisun.lemon.mkm.res.dto.RechargeMkmToolResDTO;
import com.hisun.lemon.pwm.component.AcmComponent;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dto.HallOrderQueryResultDTO;
import com.hisun.lemon.pwm.dto.HallQueryResultDTO;
import com.hisun.lemon.pwm.dto.HallRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.HallRechargeErrorFundDTO;
import com.hisun.lemon.pwm.dto.HallRechargeMatchDTO;
import com.hisun.lemon.pwm.dto.HallRechargeResultDTO;
import com.hisun.lemon.pwm.dto.OfflineRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.OfflineRechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponResultDTO;
import com.hisun.lemon.pwm.dto.RechargeReqHCouponDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeRevokeDTO;
import com.hisun.lemon.pwm.dto.RechargeRspHCouponDTO;
import com.hisun.lemon.pwm.dto.RemittanceUploadDTO;
import com.hisun.lemon.pwm.dto.UserInfoRspDTO;
import com.hisun.lemon.pwm.entity.RechargeHCouponDO;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
import com.hisun.lemon.pwm.mq.BillSyncHandler;
import com.hisun.lemon.pwm.mq.PaymentHandler;
import com.hisun.lemon.pwm.service.IRechargeOrderService;
import com.hisun.lemon.rsm.Constants;
import com.hisun.lemon.rsm.client.RiskCheckClient;
import com.hisun.lemon.rsm.dto.req.checkstatus.RiskCheckUserStatusReqDTO;
import com.hisun.lemon.tfm.client.TfmServerClient;
import com.hisun.lemon.tfm.dto.TradeRateReqDTO;
import com.hisun.lemon.tfm.dto.TradeRateRspDTO;
import com.hisun.lemon.urm.client.UserBasicInfClient;
import com.hisun.lemon.urm.dto.UserBasicInfDTO;
@Service
public class RechargeOrderServiceImpl implements IRechargeOrderService {
    //短信推送
    public static final int RECHARGE_SUCCESS=1;
    public static final int RECHARGE_OFFLINE_BACK=2;
    //账单同步
    public static final int CREATE_BIL=1;
    public static final int UPD_BIL=2;

	private static final Logger logger = LoggerFactory.getLogger(RechargeOrderServiceImpl.class);
	@Resource
	RechargeOrderTransactionalService service;

	@Resource
	CshOrderClient cshOrderClient;

	@Resource
	private AcmComponent acmComponent;

	@Resource
	UserBasicInfClient userBasicInfClient;

	@Resource
	TfmServerClient fmServerClient;

	@Resource
	MarketActivityClient mkmClient;

	@Resource
	private AccountManagementClient accountManagementClient;

	@Resource
	private RouteClient routeClient;

	@Resource
	protected AccountingTreatmentClient accountingTreatmentClient;

	@Resource
	protected DistributedLocker locker;

    @Resource
    private CmmServerClient cmmServerClient;

	@Resource
	private CshRefundClient cshRefundClient;

    @Resource
    private RiskCheckClient riskCheckClient;

	@Resource
	protected PaymentHandler paymentHandler;

	@Resource
	BillSyncHandler billSyncHandler;
	@Resource
	LocaleMessageSource localeMessageSource;
	
	
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
	 * 海币充值(对外接口)
	 */
	@Override
	public GenericRspDTO<RechargeRspHCouponDTO> createOutHCouponOrder(
			GenericDTO<RechargeReqHCouponDTO> rechargeHCouponDTO) {
		RechargeHCouponDO rechargeDO = new RechargeHCouponDO();
		RechargeReqHCouponDTO rechargeDTO = rechargeHCouponDTO.getBody();
		String mblNo = rechargeDTO.getMblNo();
		if (!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_HCOUPON)) {
			throw new LemonException("PWM20001");
		}
		if (!JudgeUtils.isNull(rechargeDTO.getOrderCcy())) {
			rechargeDO.setOrderCcy(rechargeDTO.getOrderCcy());
		}
		rechargeDO.setOrderCcy(PwmConstants.HALL_PAY_CCY);
		// 会计日期
		rechargeDO.setAcTm(rechargeHCouponDTO.getAccDate());
		rechargeDO.setOrderStatus(PwmConstants.RECHARGE_ORD_W);
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = PwmConstants.BUS_TYPE_HCOUPON + ymd
				+ IdGenUtils.generateId(PwmConstants.BUS_TYPE_HCOUPON + ymd, 12);
		// 1:100的充值比例
		BigDecimal hCouponAmt = rechargeDTO.gethCouponAmt();
		BigDecimal amount = hCouponAmt.multiply(BigDecimal.valueOf(PwmConstants.H_USD_RATE)).setScale(2,
				BigDecimal.ROUND_DOWN);
		rechargeDO.sethCouponAmt(hCouponAmt);
		rechargeDO.setOrderNo(orderNo);
		rechargeDO.setOrderAmt(amount);
		// 交易时间
		rechargeDO.setTxTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeDO.setTxType(rechargeDTO.getTxType());
		rechargeDO.setBusType(rechargeDTO.getBusType());
		UserInfoRspDTO userInfo = userInfo(mblNo);
		String userNo = userInfo.getUserId();
		rechargeDO.setUserId(userNo);
		// 生成海币充值订单
		this.service.initSeaOrder(rechargeDO);

		// 调用收银台 直付接口 进行充值海币
		DirectPaymentDTO directPaymentDTO = new DirectPaymentDTO();
		directPaymentDTO.setExtOrderNo(rechargeDO.getOrderNo());
		directPaymentDTO.setOrderCcy(rechargeDO.getOrderCcy());
		directPaymentDTO.setOrderAmt(rechargeDO.getOrderAmt());
		directPaymentDTO.setTxType(rechargeDO.getTxType());
		directPaymentDTO.setBusType(rechargeDO.getBusType());
		directPaymentDTO.setSysChannel("APP");
		directPaymentDTO.setPayerId(rechargeDO.getUserId());
		directPaymentDTO.setPayPassword(rechargeDTO.getPayPassword());
		directPaymentDTO.setPayeeId(rechargeDO.getUserId());
		directPaymentDTO.setAppCnl(LemonUtils.getApplicationName());
		directPaymentDTO.setBusPaytype(PwmConstants.BUS_PAY_TYPE);
		Object[] args=new Object[]{hCouponAmt};
		String descStr=getViewOrderInfo(PwmConstants.BUS_TYPE_HCOUPON,args);
		directPaymentDTO.setGoodsDesc(descStr);
		
		directPaymentDTO.sethCouponAmt(0);
		directPaymentDTO.setCashAmt(rechargeDO.getOrderAmt());
		logger.info("订单：" + rechargeDO.getOrderNo() + " 请求收银台");
		GenericDTO<DirectPaymentDTO> DirectPayment = new GenericDTO<>();
		DirectPayment.setBody(directPaymentDTO);
		GenericRspDTO<PaymentResultDTO> rspDTO = cshOrderClient.payByDirectBal(DirectPayment);
		if (!JudgeUtils.isSuccess(rspDTO.getMsgCd())) {
			logger.error("调用收银台后台直付接口失败");
			throw new LemonException(rspDTO.getMsgCd());
		}
		PaymentResultDTO paymentResultDTO = rspDTO.getBody();

		// 账务处理
		AccountingReqDTO userAccountReqDTO = null; // xx用户海币账户
		AccountingReqDTO cshItemReqDTO = null; // 暂收收银台账务对象

		BigDecimal orderAmount = rechargeDO.getOrderAmt();
		String acmJrnNo = IdGenUtils.generateIdWithDate(PwmConstants.R_SEA_GEN_PRE, 14);
		acmJrnNo = rechargeDO.getBusType() + acmJrnNo;
		// 查询用户帐号
		String balCapType = CapTypEnum.CAP_TYP_CASH.getCapTyp();
		// 查询用户账号
		String userId = rechargeDO.getUserId();
		String balAcNo = acmComponent.getAcmAcNo(userId, balCapType);
		// 借：其他应付款-暂收-收银台 100
		cshItemReqDTO = acmComponent.createAccountingReqDTO(rechargeDO.getOrderNo(), acmJrnNo, rechargeDO.getTxType(),
				ACMConstants.ACCOUNTING_NOMARL, orderAmount, balAcNo, ACMConstants.ITM_AC_TYP, balCapType,
				ACMConstants.AC_D_FLG, PwmConstants.AC_ITEM_CSH_PAY, null, null, null, null, "PWM海币充值");

		userAccountReqDTO = acmComponent.createAccountingReqDTO(rechargeDO.getOrderNo(), acmJrnNo,
				rechargeDO.getTxType(), ACMConstants.ACCOUNTING_NOMARL, orderAmount, null, ACMConstants.ITM_AC_TYP,
				balCapType, ACMConstants.AC_C_FLG, PwmConstants.AC_ITEM_HCOUPONE, PwmConstants.AC_ITEM_HCOUPONE, null,
				null, null, "PWM海币充值");
		acmComponent.requestAc(cshItemReqDTO, userAccountReqDTO);
		// 账务更新成功 调用海币充值接口
		logger.info("调用营销======" + rechargeDO.getOrderNo());
		RechargeMkmToolReqDTO mkmReqDTO = new RechargeMkmToolReqDTO();
		mkmReqDTO.setSeq(rechargeDO.getOrderNo());
		mkmReqDTO.setType("00");
		mkmReqDTO.setMkTool("02");
		mkmReqDTO.setUserId(userId);
		mkmReqDTO.setMobile(mblNo);
		BigDecimal CouponAmt = rechargeDO.gethCouponAmt();
		Integer count = hCouponAmt.intValue();
		mkmReqDTO.setRechargeTm(rechargeDO.getTxTm());
		mkmReqDTO.setCount(count);
		GenericDTO<RechargeMkmToolReqDTO> rechangeDTO = new GenericDTO<RechargeMkmToolReqDTO>();
		rechangeDTO.setBody(mkmReqDTO);
		GenericRspDTO<RechargeMkmToolResDTO> mkmRsp = mkmClient.getSeaCyy(rechangeDTO);
		if (JudgeUtils.isSuccess(mkmRsp.getMsgCd())) {
			RechargeMkmToolResDTO rechargeMkmToolResDTO = mkmRsp.getBody();
			if (JudgeUtils.isNotNull(rechargeMkmToolResDTO)
					&& StringUtils.equals(rechargeMkmToolResDTO.getResult(), "1")) {
				RechargeHCouponDO update = new RechargeHCouponDO();
				update.setAcTm(rechargeHCouponDTO.getAccDate());
				update.sethCouponAmt(CouponAmt);
				update.setOrderAmt(rechargeDO.getOrderAmt());
				update.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
				update.setOrderCcy(rechargeDO.getOrderCcy());
				update.setOrderNo(rechargeDO.getOrderNo());
				service.updateSeaOrder(update);
			} else {
				logger.error("营销返回失败");
				throw new LemonException("PWM40001");
			}
		} else {
			throw new LemonException(mkmRsp.getMsgCd());
		}

		// 账务成功更新订单状态
		RechargeHCouponDO updateRecharge = new RechargeHCouponDO();
		updateRecharge.setOrderNo(paymentResultDTO.getBusOrderNo());
		updateRecharge.setOrderStatus(PwmConstants.ORD_STS_S);
		updateRecharge.setOrderAmt(paymentResultDTO.getOrderAmt());
		// 更新订单信息
		this.service.updateSeaOrder(updateRecharge);

		// 组装对外传输数据
		GenericRspDTO<RechargeRspHCouponDTO> rechargeRspDTO = new GenericRspDTO<RechargeRspHCouponDTO>();
		RechargeRspHCouponDTO userOut = new RechargeRspHCouponDTO();
		userOut.setOrderAmount(paymentResultDTO.getOrderAmt());
		userOut.sethCouponAmt(CouponAmt);
		userOut.setOrderSts(PwmConstants.ORD_STS_S);
		userOut.setOrderNo(paymentResultDTO.getBusOrderNo());
		return rechargeRspDTO.newSuccessInstance(userOut);
	}

	/**
	 * 海币充值下单
	 */
	@Override
	public GenericRspDTO<CashierViewDTO> createHCouponOrder(GenericDTO<RechargeHCouponDTO> rechargeHCouponDTO) {
		RechargeHCouponDTO rechargeDTO = rechargeHCouponDTO.getBody();
		if (!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_HCOUPON)) {
			throw new LemonException("PWM20001");
		}

		RechargeHCouponDO rechargeDO = new RechargeHCouponDO();

		if (!JudgeUtils.isNull(rechargeDTO.getOrderCcy())) {

			rechargeDO.setOrderCcy(rechargeDTO.getOrderCcy());
		}
		rechargeDO.setOrderCcy(PwmConstants.HALL_PAY_CCY);
		// 会计日期
		rechargeDO.setAcTm(rechargeHCouponDTO.getAccDate());
		rechargeDO.setOrderStatus(PwmConstants.RECHARGE_ORD_W);
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = PwmConstants.BUS_TYPE_HCOUPON + ymd + IdGenUtils.generateId(PwmConstants.BUS_TYPE_HCOUPON + ymd, 12);
		// 1:100的充值比例
		BigDecimal hCouponAmt=rechargeDTO.gethCouponAmt();
		BigDecimal amount = hCouponAmt.multiply(BigDecimal.valueOf(PwmConstants.H_USD_RATE)).setScale(2, BigDecimal.ROUND_DOWN);
		rechargeDO.sethCouponAmt(hCouponAmt);
		rechargeDO.setOrderNo(orderNo);
		rechargeDO.setOrderAmt(amount);
		Object[] args=new Object[]{hCouponAmt};
		String descStr=getViewOrderInfo(PwmConstants.BUS_TYPE_HCOUPON,args);
		// 交易时间
		rechargeDO.setTxTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeDO.setTxType(rechargeDTO.getTxType());
		rechargeDO.setBusType(rechargeDTO.getBusType());
		rechargeDO.setUserId(LemonUtils.getUserId());
		// 生成海币充值订单
		this.service.initSeaOrder(rechargeDO);
		// 调用收银
		logger.info("订单：" + rechargeDO.getOrderNo() + " 请求收银台");
		InitCashierDTO initCashierDTO = new InitCashierDTO();
		initCashierDTO.setBusPaytype(null);
		initCashierDTO.setBusType(PwmConstants.BUS_TYPE_HCOUPON);
		initCashierDTO.setExtOrderNo(rechargeDO.getOrderNo());
		initCashierDTO.setSysChannel("APP");
		initCashierDTO.setPayerId(LemonUtils.getUserId());
		initCashierDTO.setPayeeId(LemonUtils.getUserId());
		initCashierDTO.setAppCnl(LemonUtils.getApplicationName());
		initCashierDTO.setTxType(rechargeDO.getTxType());
		initCashierDTO.setOrderAmt(rechargeDO.getOrderAmt());
		Object[] arg=new Object[]{hCouponAmt};
		String descStrs=getViewOrderInfo(PwmConstants.BUS_TYPE_HCOUPON,arg);
		initCashierDTO.setGoodsDesc(descStrs);
		GenericDTO<InitCashierDTO> genericDTO = new GenericDTO<>();
		genericDTO.setBody(initCashierDTO);
		GenericRspDTO<CashierViewDTO> rspDTO = new GenericRspDTO<CashierViewDTO>();
		rspDTO = cshOrderClient.initCashier(genericDTO);
		if (!JudgeUtils.isSuccess(rspDTO.getMsgCd())) {
			throw new LemonException(rspDTO.getMsgCd());
		}
		return rspDTO;
	}

	/**
	 * 海币充值下单结果通知
	 */
	@Override
	public void handleHCouponResult(GenericDTO<RechargeHCouponResultDTO> rechargeHCouponDTO) {
		RechargeHCouponResultDTO rechargSeaDTO = rechargeHCouponDTO.getBody();
		RechargeHCouponDO rechargeSeaDO = this.service.getHCoupon(rechargSeaDTO.getOrderNo());

		handleHCouponSuccess(rechargSeaDTO.getOrderNo(),rechargSeaDTO.getOrderCcy(),rechargeHCouponDTO.getAccDate(),
				rechargSeaDTO.getOrderAmt(),rechargSeaDTO.getOrderStatus(),rechargeSeaDO );
	}

	@Override
	public void repeatHCouponHandle(String orderNo){
		RechargeHCouponDO rechargeSeaDO = this.service.getHCoupon(orderNo);
		handleHCouponSuccess(orderNo,null,LemonUtils.getAccDate(),
				rechargeSeaDO.getOrderAmt(),PwmConstants.RECHARGE_ORD_S,rechargeSeaDO);
	}

	private void handleHCouponSuccess(String orderNo,String ccy,LocalDate acDt, BigDecimal amount,String status,RechargeHCouponDO rechargeSeaDO){
		try{
			locker.lock(
					"PWM.RESULT_H."+orderNo,
					18,
					16,
					()->{

						// 原订单不存在
						if (JudgeUtils.isNull(rechargeSeaDO)) {
							throw new LemonException("PWM20008");
						}
						// 订单已经成功
						if (StringUtils.equals(rechargeSeaDO.getOrderStatus(), PwmConstants.RECHARGE_ORD_S)) {
							return null;
						}
						// 订单失败
						if (!StringUtils.equals(status, PwmConstants.RECHARGE_ORD_S)) {
							RechargeHCouponDO updateSeaDTO = new RechargeHCouponDO();
							if(StringUtils.isNoneBlank(ccy)){
								updateSeaDTO.setOrderCcy(ccy);
							}

							updateSeaDTO.setOrderNo(orderNo);
							updateSeaDTO.setAcTm(acDt);
							updateSeaDTO.setOrderStatus(PwmConstants.RECHARGE_ORD_F);
							this.service.updateSeaOrder(updateSeaDTO);
							return null;
						}

						// 比较金额
						if (rechargeSeaDO.getOrderAmt().compareTo(amount) != 0) {
							throw new LemonException("PWM20009");
						}

						// 账务处理
						AccountingReqDTO userAccountReqDTO = null; // xx用户海币账户
						AccountingReqDTO cshItemReqDTO = null; // 暂收收银台账务对象
						BigDecimal orderAmt=rechargeSeaDO.getOrderAmt();
						//流水号
						//String payJrnNo=LemonUtils.getRequestId();
						String acmJrnNo = IdGenUtils.generateIdWithDate(PwmConstants.R_SEA_GEN_PRE, 14);
						acmJrnNo=rechargeSeaDO.getBusType()+acmJrnNo;
						// 查询用户帐号
						String balCapType = CapTypEnum.CAP_TYP_CASH.getCapTyp();
						//查询用户账号
						String userId=rechargeSeaDO.getUserId();
						String balAcNo = acmComponent.getAcmAcNo(userId, balCapType);
						//借：其他应付款-暂收-收银台         100
						cshItemReqDTO = acmComponent.createAccountingReqDTO(rechargeSeaDO.getOrderNo(), acmJrnNo, rechargeSeaDO.getTxType(),
								ACMConstants.ACCOUNTING_NOMARL, orderAmt, balAcNo, ACMConstants.ITM_AC_TYP, balCapType,
								ACMConstants.AC_D_FLG, PwmConstants.AC_ITEM_CSH_PAY, null, null, null,
								null, "PWM海币充值");

						userAccountReqDTO = acmComponent.createAccountingReqDTO(rechargeSeaDO.getOrderNo(),acmJrnNo, rechargeSeaDO.getTxType(),
								ACMConstants.ACCOUNTING_NOMARL,orderAmt, null, ACMConstants.ITM_AC_TYP, balCapType,
								ACMConstants.AC_C_FLG, PwmConstants.AC_ITEM_HCOUPONE, PwmConstants.AC_ITEM_HCOUPONE, null, null,
								null, "PWM海币充值");
						acmComponent.requestAc(cshItemReqDTO,userAccountReqDTO);
						// 账务更新成功  调用海币充值接口
						logger.info("调用营销======"+rechargeSeaDO.getOrderNo());
						RechargeMkmToolReqDTO mkmReqDTO=new RechargeMkmToolReqDTO();
						mkmReqDTO.setSeq(rechargeSeaDO.getOrderNo());
						mkmReqDTO.setType("00");
						mkmReqDTO.setMkTool("02");
						mkmReqDTO.setUserId(userId);
						String mblNo=accountManagementClient.queryAcNo(userId).getBody();
						String mobile=mblNo;
						mkmReqDTO.setMobile(mobile);
						BigDecimal hCouponAmt=rechargeSeaDO.gethCouponAmt();
						Integer count=hCouponAmt.intValue();
						mkmReqDTO.setRechargeTm(rechargeSeaDO.getTxTm());
						mkmReqDTO.setCount(count);
						GenericDTO<RechargeMkmToolReqDTO> rechangeDTO=new GenericDTO<RechargeMkmToolReqDTO>();
						rechangeDTO.setBody(mkmReqDTO);
						GenericRspDTO<RechargeMkmToolResDTO> mkmRsp=mkmClient.getSeaCyy(rechangeDTO);
						if (JudgeUtils.isSuccess(mkmRsp.getMsgCd())) {
							RechargeMkmToolResDTO rechargeMkmToolResDTO=mkmRsp.getBody();
							if(JudgeUtils.isNotNull(rechargeMkmToolResDTO) && StringUtils.equals(rechargeMkmToolResDTO.getResult(), "1")){
								RechargeHCouponDO update=new RechargeHCouponDO();
								update.setAcTm(acDt);
								update.sethCouponAmt(hCouponAmt);
								update.setOrderAmt(amount);
								update.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
								if(StringUtils.isNoneBlank(ccy)){
									update.setOrderCcy(ccy);
								}
								update.setOrderNo(orderNo);
								service.updateSeaOrder(update);
							}else{
								logger.error("营销返回失败 {}",orderNo);
								throw new LemonException("PWM40001");
							}
						}else{
							throw new LemonException(mkmRsp.getMsgCd());
						}
						return null;
					}
			);
		}catch (Exception e){
			LemonException.create(e);
		}
	}


	@Override
	public GenericRspDTO createOrder(RechargeDTO rechargeDTO) {
		if (!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_RECHANGE)) {
			throw new LemonException("PWM20001");
		}

		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 15);
		orderNo=rechargeDTO.getBusType()+ymd + orderNo;

		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(rechargeDTO.getBusType());
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setOrderAmt(rechargeDTO.getAmount());
		rechargeOrderDO.setOrderCcy("USD");
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
		//调用收银
		InitCashierDTO initCashierDTO=new InitCashierDTO();
	  	initCashierDTO.setBusPaytype(null);
	  	initCashierDTO.setBusType(rechargeOrderDO.getBusType());
	  	initCashierDTO.setExtOrderNo(rechargeOrderDO.getOrderNo());
	  	initCashierDTO.setSysChannel(rechargeDTO.getSysChannel());
	  	initCashierDTO.setPayerId(LemonUtils.getUserId());
        initCashierDTO.setPayeeId(LemonUtils.getUserId());
		initCashierDTO.setAppCnl(LemonUtils.getApplicationName());
	  	initCashierDTO.setTxType(rechargeOrderDO.getTxType());
		initCashierDTO.setOrderAmt(rechargeDTO.getAmount());
		initCashierDTO.setEffTm(rechargeOrderDO.getOrderExpTm());
		//快捷充值订单信息国际化
		Object[] args=new Object[]{rechargeOrderDO.getOrderAmt()};
		String descStr=getViewOrderInfo(rechargeOrderDO.getBusType(),args);

		initCashierDTO.setGoodsDesc(descStr);
		GenericDTO<InitCashierDTO> genericDTO = new GenericDTO<>();
		genericDTO.setBody(initCashierDTO);
		logger.info("订单：" + rechargeOrderDO.getOrderNo()+" 请求收银台");
		return cshOrderClient.initCashier(genericDTO);
	}

	@Override
	public void handleResult(GenericDTO resultDto) {
		RechargeResultDTO rechargeResultDTO = (RechargeResultDTO) resultDto.getBody();
		String orderNo = rechargeResultDTO.getOrderNo();
		handleSuccess(rechargeResultDTO.getStatus(),rechargeResultDTO.getOrderCcy(),rechargeResultDTO.getAmount(),
				orderNo,rechargeResultDTO.getExtOrderNo(),rechargeResultDTO.getRemark(), rechargeResultDTO.getBusType(),
				rechargeResultDTO.getPayerId(),rechargeResultDTO.getFee(),resultDto.getAccDate());
	}
	@Override
	public void repeatResultHandle(String orderNo){
		GenericRspDTO<OrderDTO> genDto=cshOrderClient.query(orderNo);
		OrderDTO orderDTO=genDto.getBody();
		if(JudgeUtils.isSuccess(genDto.getMsgCd())){
			handleSuccess(PwmConstants.RECHARGE_ORD_S,null,orderDTO.getOrderAmt(),
					orderNo,orderDTO.getBusOrderNo(),null, orderDTO.getBusType(),
					orderDTO.getPayerId(),orderDTO.getFee(),LemonUtils.getAccDate());
		}else{
			LemonException.throwBusinessException(genDto.getMsgCd());
		}

	}

	private  void handleSuccess(String status,String ccy,BigDecimal amount,
								String orderNo,String extOrderNo,String remark,String busType,String payerId,BigDecimal fee,
								LocalDate acDt){
		try{
			RechargeOrderDO rechargeOrderDO = service.getRechangeOrderDao().get(orderNo);
			locker.lock(
					"PWM.RESULT_RG."+orderNo,
					19,17,
					()->{
						// 未找到订单
						if (rechargeOrderDO == null) {
							throw new LemonException("PWM20002");
						}
						// 判断返回状态
						if (!StringUtils.equals(status, PwmConstants.RECHARGE_ORD_S)) {
							RechargeOrderDO updOrderDO = new RechargeOrderDO();
							updOrderDO.setExtOrderNo(extOrderNo);
							updOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_F);
							if(StringUtils.isNoneBlank(ccy)){
								updOrderDO.setOrderCcy(ccy);
							}
							updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
							updOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
							updOrderDO.setOrderNo(orderNo);
                            updOrderDO.setFee(fee);
							//若是汇款充值，此处为审核失败原因
							updOrderDO.setRemark(remark);
							if(JudgeUtils.equals(rechargeOrderDO.getBusType(),PwmConstants.BUS_TYPE_RECHARGE_OFL)){
								try {
									//充值退回通知发送
									logger.info("汇款拒绝理由: " + updOrderDO.getRemark());
									sendMsgCenterInfo(rechargeOrderDO,RECHARGE_OFFLINE_BACK);
								}catch (Exception e){
									logger.error("汇款充值审核失败原因通知失败:" + e.getMessage());
								}
							}
							service.updateOrder(updOrderDO);
							return null;
						}

						// 比较金额
						if (rechargeOrderDO.getOrderAmt().compareTo(amount) != 0) {
							throw new LemonException("PWM20003");
						}

						// 账务处理,根据不同业务进行不同处理
						AccountingReqDTO userAccountReqDTO=null;     //用户现金账户账务对象
						AccountingReqDTO cshItemReqDTO=null;         //暂收收银台账务对象

						//账号资金属性：1 现金 8 待结算
						String balCapType= CapTypEnum.CAP_TYP_CASH.getCapTyp();
						//现金账户
						String balAcNo=acmComponent.getAcmAcNo(payerId, balCapType);

						if(JudgeUtils.isBlank(balAcNo)){
							throw new LemonException("PWM20022");
						}
						switch (busType) {
							//个人快捷支付账户充值
							case PwmConstants.BUS_TYPE_RECHARGE_QP:
								String tmpJrnNo = LemonUtils.getApplicationName() + PwmConstants.BUS_TYPE_RECHARGE_QP + IdGenUtils.generateIdWithDate(PwmConstants.R_ORD_GEN_PRE,10);
								// 借：其他应付款-暂收-收银台
								cshItemReqDTO=acmComponent.createAccountingReqDTO(rechargeOrderDO.getOrderNo(), tmpJrnNo, rechargeOrderDO.getTxType(),
										ACMConstants.ACCOUNTING_NOMARL, rechargeOrderDO.getOrderAmt(), balAcNo, ACMConstants.ITM_AC_TYP, balCapType, ACMConstants.AC_D_FLG,
										CshConstants.AC_ITEM_CSH_PAY, null, null, null, null, null);

								// 贷：其他应付款-支付账户-xx用户现金账户
								userAccountReqDTO=acmComponent.createAccountingReqDTO(rechargeOrderDO.getOrderNo(), tmpJrnNo, rechargeOrderDO.getTxType(),
										ACMConstants.ACCOUNTING_NOMARL, rechargeOrderDO.getOrderAmt(), balAcNo, ACMConstants.USER_AC_TYP, balCapType, ACMConstants.AC_C_FLG,
										CshConstants.AC_ITEM_CSH_BAL, null, null, null, null, "快捷充值$"+rechargeOrderDO.getOrderAmt());
								acmComponent.requestAc(cshItemReqDTO,userAccountReqDTO);
								break;
							case PwmConstants.BUS_TYPE_RECHARGE_OFL:
								String acmJrnNo = LemonUtils.getApplicationName() + PwmConstants.BUS_TYPE_RECHARGE_OFL + IdGenUtils.generateIdWithDate(PwmConstants.R_ORD_GEN_PRE,10);
								// 借：其他应付款-暂收-收银台
								cshItemReqDTO=acmComponent.createAccountingReqDTO(rechargeOrderDO.getOrderNo(), acmJrnNo, rechargeOrderDO.getTxType(),
										ACMConstants.ACCOUNTING_NOMARL, rechargeOrderDO.getOrderAmt(), balAcNo, ACMConstants.ITM_AC_TYP, balCapType, ACMConstants.AC_D_FLG,
										CshConstants.AC_ITEM_CSH_PAY, null, null, null, null, null);

								// 贷：其他应付款-支付账户-xx用户现金账户
								userAccountReqDTO=acmComponent.createAccountingReqDTO(rechargeOrderDO.getOrderNo(), acmJrnNo, rechargeOrderDO.getTxType(),
										ACMConstants.ACCOUNTING_NOMARL, rechargeOrderDO.getOrderAmt(), balAcNo, ACMConstants.USER_AC_TYP, balCapType, ACMConstants.AC_C_FLG,
										CshConstants.AC_ITEM_CSH_BAL, null, null, null, null, "汇款充值$"+rechargeOrderDO.getOrderAmt());
								acmComponent.requestAc(userAccountReqDTO,cshItemReqDTO);
								break;
							default:
								break;
						}

						// 更新订单
						RechargeOrderDO updOrderDO = new RechargeOrderDO();
						updOrderDO.setAcTm(acDt);
						updOrderDO.setExtOrderNo(extOrderNo);
						updOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
						updOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
                        updOrderDO.setFee(fee);
						if(StringUtils.isNoneBlank(ccy)){
							updOrderDO.setOrderCcy(ccy);
						}

						updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
						updOrderDO.setOrderNo(orderNo);
						service.updateOrder(updOrderDO);
						return null;
					}
			);
		}catch (Exception e){
			LemonException.create(e);
		}
	}

	@Override
	public HallQueryResultDTO queryUserInfo(String key, BigDecimal amount, String type) {
		GenericRspDTO<UserBasicInfDTO> genericUserBasicInfDTO = null;
		HallQueryResultDTO hallQueryResultDTO = new HallQueryResultDTO();

		if(JudgeUtils.isNotBlank(key)){
			//判断是否是通过手机号查询,手机号格式 国家编码+手机号
			boolean isPhone = PhoneNumberUtils.isValidNumber(key);
			if(isPhone){
				genericUserBasicInfDTO = userBasicInfClient.queryUserByLoginId(key);
			}else{
				//根据id查询
				genericUserBasicInfDTO = userBasicInfClient.queryUser(key);
			}

			if(JudgeUtils.isNotSuccess(genericUserBasicInfDTO.getMsgCd())) {
				LemonException.throwBusinessException(genericUserBasicInfDTO.getMsgCd());
			}
			UserBasicInfDTO userBasicInfDTO = genericUserBasicInfDTO.getBody();
			//用户账户可用余额
			BigDecimal curAcBal = acmComponent.getAccountBal(userBasicInfDTO.getUserId(),CapTypEnum.CAP_TYP_CASH.getCapTyp());
			//计算营业厅充值手续费
			if(JudgeUtils.isNotNull(amount)){
				BigDecimal fee = caculateHallRechargeFee(amount);
				//设置充值手续费
				hallQueryResultDTO.setFee(fee);
			}
			hallQueryResultDTO.setUmId(userBasicInfDTO.getUserId());
			hallQueryResultDTO.setKey(key);
			hallQueryResultDTO.setAcBalAmt(curAcBal);
			if(JudgeUtils.equals(PwmConstants.HALL_QUERY_TYPE_U, type)) {
				hallQueryResultDTO.setUmName(userBasicInfDTO.getUsrNm());
			} else if (JudgeUtils.equals(PwmConstants.HALL_QUERY_TYPE_M,type)) {
				hallQueryResultDTO.setUmName(userBasicInfDTO.getMercName());
			}
		}
		return hallQueryResultDTO;
	}

	@Override
	public HallRechargeResultDTO hallRechargePay(HallRechargeApplyDTO dto) {
		HallRechargeApplyDTO.BussinessBody bussinessBody = dto.getBody();
		BigDecimal orderAmt = bussinessBody.getAmount();
		//充值请求校验
		checkHallRequestBeforeHandler(dto);

		//手续费校验
		BigDecimal applyFee = bussinessBody.getFee();
		BigDecimal fee = caculateHallRechargeFee(orderAmt);
		if(!JudgeUtils.equals(applyFee,fee)){
			throw new LemonException("PWM30006");
		}

		// 生成充值订单
		RechargeOrderDO rechargeOrderDO = createHallRechargeOrder(bussinessBody);

		//个人账户查询
		String balCapType= CapTypEnum.CAP_TYP_CASH.getCapTyp();
		String balAcNo=acmComponent.getAcmAcNo(bussinessBody.getPayerId(), balCapType);
		if(JudgeUtils.isBlank(balAcNo)){
			throw new LemonException("PWM20022");
		}
		String tmpJrnNo = LemonUtils.getApplicationName() + PwmConstants.BUS_TYPE_RECHARGE_HALL + IdGenUtils.generateIdWithDate(PwmConstants.R_ORD_GEN_PRE,10);

		//借：应收账款-渠道充值-营业厅
		AccountingReqDTO cnlRechargeHallReqDTO=acmComponent.createAccountingReqDTO(
				rechargeOrderDO.getOrderNo(),
				tmpJrnNo,
				PwmConstants.TX_TYPE_RECHANGE,
				ACMConstants.ACCOUNTING_NOMARL,
				orderAmt,
				balAcNo,
				ACMConstants.ITM_AC_TYP,
				balCapType,
				ACMConstants.AC_D_FLG,
				CshConstants.AC_ITEM_CNL_RECHARGE_HALL,
				null,
				null,
				null,
				null,
				null);
		//贷：其他应付款-支付账户-现金账户
		AccountingReqDTO userAccountReqDTO=acmComponent.createAccountingReqDTO(
				rechargeOrderDO.getOrderNo(),
				tmpJrnNo,
				PwmConstants.TX_TYPE_RECHANGE,
				ACMConstants.ACCOUNTING_NOMARL,
				orderAmt,
				balAcNo,
				ACMConstants.USER_AC_TYP,
				balCapType,
				ACMConstants.AC_C_FLG,
				CshConstants.AC_ITEM_CSH_BAL,
				null,
				null,
				null,
				null,
				"营业厅充值$"+rechargeOrderDO.getOrderAmt());

		try{
			acmComponent.requestAc(userAccountReqDTO,cnlRechargeHallReqDTO);
		}catch (LemonException e){
			//更新失败订单
			RechargeOrderDO updateOrderDO = new RechargeOrderDO();
            updateOrderDO.setOrderNo(rechargeOrderDO.getOrderNo());
            updateOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
            updateOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_F);
            updateOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
			this.service.updateOrder(updateOrderDO);

			updateOrderDO = syncOrderData(rechargeOrderDO,updateOrderDO);
			//同步账单
            synchronizeRechargeBil(updateOrderDO,CREATE_BIL);
			LemonException.throwBusinessException(e.getMsgCd());
		}

		//登记用户手续费
		if(rechargeOrderDO.getFee().compareTo(BigDecimal.ZERO)>0){
			paymentHandler.registUserFee(rechargeOrderDO);
		}

		//更新充值订单
		RechargeOrderDO updateOrderDO = new RechargeOrderDO();
        updateOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
        updateOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
        updateOrderDO.setOrderCcy(rechargeOrderDO.getOrderCcy());
        updateOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
        updateOrderDO.setOrderNo(rechargeOrderDO.getOrderNo());
        updateOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		service.updateOrder(updateOrderDO);
		try{
            //调用平台短信能力通知充值到账
            sendMsgCenterInfo(rechargeOrderDO,RECHARGE_SUCCESS);
        }catch (Exception e){
		    logger.error("充值成功，推送充值消息失败:" + e.getMessage());
        }

		//同步更新订单数据
        updateOrderDO = syncOrderData(rechargeOrderDO,updateOrderDO);

        //同步账单
        synchronizeRechargeBil(updateOrderDO,CREATE_BIL);
		//返回
		HallRechargeResultDTO hallRechargeResultDTO = new HallRechargeResultDTO();
		hallRechargeResultDTO.setAmount(orderAmt);
		hallRechargeResultDTO.setStatus(PwmConstants.RECHARGE_ORD_S);
		hallRechargeResultDTO.setFee(rechargeOrderDO.getFee());
		hallRechargeResultDTO.setHallOrderNo(rechargeOrderDO.getHallOrderNo());
		hallRechargeResultDTO.setOrderNo(rechargeOrderDO.getOrderNo());
		return hallRechargeResultDTO;
	}


	@Override
	public HallRechargeResultDTO hallRechargeRevocation(HallRechargeApplyDTO dto) {
		HallRechargeApplyDTO.BussinessBody busBody= dto.getBody();
		//充值冲正请求校验
		checkHallRequestBeforeHandler(dto);
		RechargeOrderDO rechargeOrderDO = this.service.getRechargeOrderByHallOrderNo(busBody.getHallOrderNo());
		//查询充值订单和收银订单状态
		if(JudgeUtils.equals(rechargeOrderDO.getOrderStatus(),PwmConstants.RECHARGE_ORD_S)){
			//个人账户信息
			String balCapType= CapTypEnum.CAP_TYP_CASH.getCapTyp();
			String balAcNo=acmComponent.getAcmAcNo(busBody.getPayerId(), balCapType);
			String tmpJrnNo =  LemonUtils.getApplicationName() + PwmConstants.BUS_TYPE_RECHARGE_HALL + IdGenUtils.generateIdWithDate(PwmConstants.R_ORD_GEN_PRE,10);
			//营业厅撤销账务处理
			//借：其他应付款-支付账户-现金账户
			AccountingReqDTO cshItemReqDTO=acmComponent.createAccountingReqDTO(
					rechargeOrderDO.getOrderNo(),
					tmpJrnNo,
					rechargeOrderDO.getTxType(),
					ACMConstants.ACCOUNTING_NOMARL,
					rechargeOrderDO.getOrderAmt(),
					balAcNo,
					ACMConstants.USER_AC_TYP,
					balCapType,
					ACMConstants.AC_D_FLG,
					CshConstants.AC_ITEM_CSH_BAL,
					null,
					null,
					null,
					null,
					"营业厅撤销$"+rechargeOrderDO.getOrderAmt());

			//贷：应收账款-渠道充值-营业厅
			AccountingReqDTO cnlRechargeHallReqDTO=acmComponent.createAccountingReqDTO(
					rechargeOrderDO.getOrderNo(),
					tmpJrnNo,
					rechargeOrderDO.getTxType(),
					ACMConstants.ACCOUNTING_NOMARL,
					rechargeOrderDO.getOrderAmt(),
					null,
					ACMConstants.ITM_AC_TYP,
					balCapType,
					ACMConstants.AC_C_FLG,
					CshConstants.AC_ITEM_CNL_RECHARGE_HALL,
					null,
					null,
					null,
					null,
					null);

			acmComponent.requestAc(cshItemReqDTO,cnlRechargeHallReqDTO);
		}

		//更新订单状态
		RechargeOrderDO updateOrderDO = new RechargeOrderDO();
        updateOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
        updateOrderDO.setExtOrderNo(rechargeOrderDO.getExtOrderNo());
        updateOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_C);
        updateOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
        updateOrderDO.setOrderCcy(rechargeOrderDO.getOrderCcy());
        updateOrderDO.setOrderAmt(rechargeOrderDO.getOrderAmt());
        updateOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
        updateOrderDO.setOrderNo(rechargeOrderDO.getOrderNo());
        updateOrderDO.setHallOrderNo(rechargeOrderDO.getHallOrderNo());
        updateOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		service.updateOrder(updateOrderDO);

		//同步账单数据
        synchronizeRechargeBil(updateOrderDO,UPD_BIL);
		//返回处理结果
		HallRechargeResultDTO hallRechargeResultDTO = new HallRechargeResultDTO();
		hallRechargeResultDTO.setAmount(updateOrderDO.getOrderAmt());
		hallRechargeResultDTO.setStatus(updateOrderDO.getOrderStatus());
		hallRechargeResultDTO.setFee(rechargeOrderDO.getFee());
		hallRechargeResultDTO.setHallOrderNo(updateOrderDO.getHallOrderNo());
		hallRechargeResultDTO.setOrderNo(updateOrderDO.getOrderNo());
		return hallRechargeResultDTO;

	}

	@Override
	public OfflineRechargeResultDTO offlineRechargeApplication(GenericDTO<OfflineRechargeApplyDTO> genericDTO) {
		OfflineRechargeApplyDTO offlineRechargeApplyDTO = genericDTO.getBody();
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = PwmConstants.BUS_TYPE_RECHARGE_OFL + ymd + IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 15);
		// 生成充值订单
		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_OFL);
		rechargeOrderDO.setExtOrderNo("");
		rechargeOrderDO.setOrderAmt(offlineRechargeApplyDTO.getAmount());
		rechargeOrderDO.setOrderCcy(offlineRechargeApplyDTO.getCcy());
		rechargeOrderDO.setOrderExpTm(DateTimeUtils.getCurrentLocalDateTime().plusDays(2));
		rechargeOrderDO.setOrderNo(orderNo);
		rechargeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setIpAddress("");
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setOrderSuccTm(null);
		rechargeOrderDO.setCrdCorpOrg(offlineRechargeApplyDTO.getCrdCorpOrg());
		//设置汇款订单状态
		rechargeOrderDO.setOrderStatus(PwmConstants.OFFLINE_RECHARGE_ORD_W);
		rechargeOrderDO.setPsnFlag(offlineRechargeApplyDTO.getPsnFlag());
		rechargeOrderDO.setSysChannel(PwmConstants.ORD_SYSCHANNEL_APP);
		rechargeOrderDO.setTxType(PwmConstants.TX_TYPE_RECHANGE);
		rechargeOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setRemark("");
		rechargeOrderDO.setPayerId(offlineRechargeApplyDTO.getPayerId());
		this.service.initOrder(rechargeOrderDO);

		// 调用收银,生成收银订单
		InitCashierDTO initCashierDTO = new InitCashierDTO();
//		initCashierDTO.setBusPaytype("");
		initCashierDTO.setBusType(rechargeOrderDO.getBusType());
		initCashierDTO.setExtOrderNo(rechargeOrderDO.getOrderNo());
		initCashierDTO.setPayeeId(offlineRechargeApplyDTO.getPayerId());
		initCashierDTO.setSysChannel(PwmConstants.ORD_SYSCHANNEL_APP);
		initCashierDTO.setPayerId(offlineRechargeApplyDTO.getPayerId());
		initCashierDTO.setTxType(rechargeOrderDO.getTxType());
		initCashierDTO.setOrderAmt(offlineRechargeApplyDTO.getAmount());
		initCashierDTO.setAppCnl("PWM");
		initCashierDTO.setFee(BigDecimal.valueOf(0));
		initCashierDTO.setTxType(PwmConstants.TX_TYPE_RECHANGE);
		initCashierDTO.setOrderCcy(PwmConstants.HALL_PAY_CCY);
		initCashierDTO.setPayerName("");
		initCashierDTO.setCrdCorpOrg(rechargeOrderDO.getCrdCorpOrg());
		//线下汇款订单信息国际化
		Object[] args=new Object[]{rechargeOrderDO.getOrderAmt()};
		String descStr=getViewOrderInfo(rechargeOrderDO.getBusType(),args);
		initCashierDTO.setGoodsDesc(descStr);

		GenericDTO<InitCashierDTO> genericCashierDTO = new GenericDTO<>();
		genericCashierDTO.setBody(initCashierDTO);
		GenericRspDTO<CashierViewDTO> genericCashierViewDTO = cshOrderClient.initCashier(genericCashierDTO);
		CashierViewDTO cashierViewDTO = genericCashierViewDTO.getBody();
		if(JudgeUtils.isNotSuccess(genericCashierViewDTO.getMsgCd())) {
			//更新充值订单状态
			RechargeOrderDO updatOrderDO = new RechargeOrderDO();
			updatOrderDO.setOrderNo(orderNo);
			updatOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
			updatOrderDO.setOrderStatus(PwmConstants.OFFLINE_RECHARGE_ORD_F);
			this.service.updateOrder(updatOrderDO);
			LemonException.throwBusinessException(genericCashierViewDTO.getMsgCd());
		}
		//更新充值订单的收银业务订单号
		rechargeOrderDO.setExtOrderNo(cashierViewDTO.getOrderNo());
		rechargeOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		this.service.updateOrder(rechargeOrderDO);

		GenericRspDTO<RouteRspDTO> genericRounteListRsp = routeClient.queryEffOrgInfo(CorpBusTyp.REMITTANCE, CorpBusSubTyp.REMITTANCE);
		RouteRspDTO routeRspDTO = genericRounteListRsp.getBody();
		List<RouteRspDTO.RouteDTO> rounteList = routeRspDTO.getList();
		if(JudgeUtils.isNotSuccess(genericRounteListRsp.getMsgCd())) {
			LemonException.throwBusinessException(genericRounteListRsp.getMsgCd());
		}
		GenericRspDTO<UserBasicInfDTO> genericUserBasicInfDTO = userBasicInfClient.queryUser(offlineRechargeApplyDTO.getPayerId());
		UserBasicInfDTO userBasicInfDTO = genericUserBasicInfDTO.getBody();
		OfflineRechargeResultDTO offlineRechargeResultDTO = new OfflineRechargeResultDTO();
		offlineRechargeResultDTO.setAmount(rechargeOrderDO.getOrderAmt());
		offlineRechargeResultDTO.setCcy(rechargeOrderDO.getOrderCcy());
		offlineRechargeResultDTO.setPayerId(offlineRechargeApplyDTO.getPayerId());
		offlineRechargeResultDTO.setStatus(PwmConstants.OFFLINE_RECHARGE_ORD_W);
		offlineRechargeResultDTO.setOrderNo(rechargeOrderDO.getOrderNo());
		offlineRechargeResultDTO.setOrderTm(DateTimeUtils.getCurrentLocalTime());
		offlineRechargeResultDTO.setCashierOrderNo(cashierViewDTO.getOrderNo());
		if(JudgeUtils.isNotNull(userBasicInfDTO)){
			offlineRechargeResultDTO.setMblNo(userBasicInfDTO.getMblNo());
		}
		String crdCorpOrg = offlineRechargeApplyDTO.getCrdCorpOrg();
		for(RouteRspDTO.RouteDTO rd : rounteList) {
			if(JudgeUtils.equals(crdCorpOrg,rd.getCrdCorpOrg())) {
				//汇款银行账号
				offlineRechargeResultDTO.setCrdNo(rd.getCorpAccNo());
				//汇款银行账户
				offlineRechargeResultDTO.setCrdUsrNm("");
				break;
			}
		}
		return offlineRechargeResultDTO;
	}

	@Override
	public OfflineRechargeResultDTO offlineRemittanceUpload(GenericDTO<RemittanceUploadDTO> genericDTO) {
		RemittanceUploadDTO remittanceUploadDTO = genericDTO.getBody();
		//原订单校验
		String cashOrderNo = remittanceUploadDTO.getOrderNo();
		RechargeOrderDO rechargeOrderDO = this.service.getRechangeOrderDao().getRechargeOrderByExtOrderNo(cashOrderNo);

		if(JudgeUtils.isNull(rechargeOrderDO)) {
			throw new LemonException("PWM20015");
		}
		//判断充值订单是否已提交审核
		if(JudgeUtils.equals(PwmConstants.OFFLINE_RECHARGE_ORD_W1,rechargeOrderDO.getOrderStatus()) || JudgeUtils.equals(PwmConstants.OFFLINE_RECHARGE_ORD_S,rechargeOrderDO.getOrderStatus())) {
			throw new LemonException("PWM20017");
		}

		//查询汇款充值个人信息
		GenericRspDTO<UserBasicInfDTO> genericUserBasicInfDTO = userBasicInfClient.queryUser(remittanceUploadDTO.getPayerId());
		UserBasicInfDTO userBasicInfDTO = genericUserBasicInfDTO.getBody();

		//根据资金机构查询汇款账户
		GenericRspDTO<RouteRspDTO> genericRounteListRsp = routeClient.queryEffOrgInfo(CorpBusTyp.REMITTANCE, CorpBusSubTyp.REMITTANCE);
		RouteRspDTO routeRspDTO = genericRounteListRsp.getBody();
		List<RouteRspDTO.RouteDTO> rounteList = routeRspDTO.getList();
		if(JudgeUtils.isNotSuccess(genericRounteListRsp.getMsgCd())) {
			LemonException.throwBusinessException(genericRounteListRsp.getMsgCd());
		}
		RouteRspDTO.RouteDTO routeDTO = rounteList.get(0);
		if(JudgeUtils.isNull(routeDTO)){
			throw new LemonException("PWM30015");
		}
		GenericDTO<OfflinePaymentDTO> genericOfflinePaymentDTO = new GenericDTO<>();
		OfflinePaymentDTO offlinePaymentDTO = new OfflinePaymentDTO();
		offlinePaymentDTO.setCashRemittUrl(remittanceUploadDTO.getRemittUrl());
		offlinePaymentDTO.setPayerId(remittanceUploadDTO.getPayerId());
		offlinePaymentDTO.setRemark(remittanceUploadDTO.getRemark());
		offlinePaymentDTO.setCrdCorpOrg(routeDTO.getCrdCorpOrg());
		offlinePaymentDTO.setCrdAcTyp(routeDTO.getCrdAcTyp());
		offlinePaymentDTO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_OFL);
		offlinePaymentDTO.setOrderNo(rechargeOrderDO.getOrderNo());
		genericOfflinePaymentDTO.setBody(offlinePaymentDTO);

		//收银台线下汇款处理
		GenericRspDTO<OfflinePaymentResultDTO> genericOfflinePaymentResultDTO = cshOrderClient.offlinePayment(genericOfflinePaymentDTO);
		OfflinePaymentResultDTO offlinePaymentResultDTO = genericOfflinePaymentResultDTO.getBody();
		if (JudgeUtils.isNotSuccess(genericOfflinePaymentResultDTO.getMsgCd())) {
			LemonException.throwBusinessException(genericOfflinePaymentResultDTO.getMsgCd());
		}
		//更新充值订单状态为已提交审核
		RechargeOrderDO updateRechargeOrderDo = new RechargeOrderDO();
		BeanUtils.copyProperties(updateRechargeOrderDo,rechargeOrderDO);
		updateRechargeOrderDo.setOrderStatus(PwmConstants.OFFLINE_RECHARGE_ORD_W1);
		updateRechargeOrderDo.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		this.service.getRechangeOrderDao().update(updateRechargeOrderDo);

		OfflineRechargeResultDTO offlineRechargeResultDTO = new OfflineRechargeResultDTO();
		offlineRechargeResultDTO.setCashierOrderNo(offlinePaymentResultDTO.getCashierOrderNo());
		offlineRechargeResultDTO.setOrderTm(DateTimeUtils.getCurrentLocalTime());
		offlineRechargeResultDTO.setPayerId(offlinePaymentResultDTO.getPayerId());
		offlineRechargeResultDTO.setAmount(offlinePaymentResultDTO.getOrderAmt());
		offlineRechargeResultDTO.setCcy(offlinePaymentResultDTO.getOrderCcy());
		offlineRechargeResultDTO.setOrderNo(offlinePaymentResultDTO.getRemittOrderNo());
		offlineRechargeResultDTO.setStatus(offlinePaymentResultDTO.getOrderStatus());
		offlineRechargeResultDTO.setMblNo(userBasicInfDTO.getMblNo());
		offlineRechargeResultDTO.setRemark(offlinePaymentResultDTO.getRemark());
		return offlineRechargeResultDTO;
	}

	@Override
	public HallOrderQueryResultDTO queryOrderInfo(String hallOrderNo) {
		if (JudgeUtils.isBlank(hallOrderNo)) {
			return null;
		}
		//订单信息查询
		RechargeOrderDO rechargeOrderDO = this.service.getRechargeOrderByHallOrderNo(hallOrderNo);
		HallOrderQueryResultDTO hallOrderQueryResultDTO = new HallOrderQueryResultDTO();
		if (JudgeUtils.isNotNull(rechargeOrderDO)) {
			hallOrderQueryResultDTO.setHallOrderNo(rechargeOrderDO.getExtOrderNo());
			hallOrderQueryResultDTO.setOrderStatus(rechargeOrderDO.getOrderStatus());
			hallOrderQueryResultDTO.setOrderNo(rechargeOrderDO.getOrderNo());
			hallOrderQueryResultDTO.setOrderAmt(rechargeOrderDO.getOrderAmt());
			hallOrderQueryResultDTO.setOrderTm(rechargeOrderDO.getOrderTm());
			hallOrderQueryResultDTO.setFee(rechargeOrderDO.getFee());
			if(JudgeUtils.isNull(rechargeOrderDO.getFee())){
				hallOrderQueryResultDTO.setTotalAmt(rechargeOrderDO.getOrderAmt());
			}else{
				hallOrderQueryResultDTO.setTotalAmt(rechargeOrderDO.getOrderAmt().add(rechargeOrderDO.getFee()).setScale(2));
			}

		}
		return hallOrderQueryResultDTO;

	}

	@Override
	public void longAmtHandle(HallRechargeErrorFundDTO hallRechargeErrorFundDTO) {
		String rechargeOrderNo = hallRechargeErrorFundDTO.getOrderNo();
		logger.error("营业厅充值订单" + rechargeOrderNo + "长款补单处理....");
		try {
			locker.lock("PWM_LOCK.HALL.LONGAMT." + rechargeOrderNo, 18, 22, () -> {
				//差错处理前校验
				RechargeOrderDO rechargeOrderDO = checkBeforeErrHandle(hallRechargeErrorFundDTO, PwmConstants.HALL_CHK_LONG_AMT);

				//营业厅长款补单处理
				List<AccountingReqDTO> accList = createLongAmtErrAccList(rechargeOrderDO);

				BigDecimal dAmt = BigDecimal.ZERO;
				BigDecimal cAmt = BigDecimal.ZERO;
				for (AccountingReqDTO dto : accList) {
					if (JudgeUtils.isNotNull(dto)) {
						if (JudgeUtils.equals(dto.getDcFlg(), ACMConstants.AC_D_FLG)) {
							dAmt = dAmt.add(dto.getTxAmt());
						} else {
							cAmt = cAmt.add(dto.getTxAmt());
						}
					}
				}

				// 借贷平衡校验
				if (cAmt.compareTo(dAmt) != 0) {
					LemonException.throwBusinessException("PWM20026");
				}

				GenericDTO<List<AccountingReqDTO>> userAccDto = new GenericDTO<>();
				userAccDto.setBody(accList);
				GenericRspDTO<NoBody> accountingTreatment = accountingTreatmentClient.accountingTreatment(userAccDto);
				if (!JudgeUtils.isSuccess(accountingTreatment.getMsgCd())) {
					logger.error("充值订单" + rechargeOrderDO.getOrderNo() + "长款补单账务处理失败：" + accountingTreatment.getMsgCd());
					LemonException.throwBusinessException(accountingTreatment.getMsgCd());
				}

				//更新充值订单状态
				rechargeOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
				rechargeOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
				rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
				rechargeOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
				this.service.updateOrder(rechargeOrderDO);
                logger.info("营业厅充值订单" + rechargeOrderNo + "补单成功");

				return null;

			});
		} catch (Exception e) {
			LemonException.throwBusinessException("PWM20027");
		}
	}

	@Override
	public void shortAmtHandle(HallRechargeErrorFundDTO hallRechargeErrorFundDTO){
		String rechargeOrderNo = hallRechargeErrorFundDTO.getOrderNo();
		logger.info("营业厅充值订单" + rechargeOrderNo + "短款撤单处理.....");
		try {
			locker.lock("PWM_LOCK.HALL.SHORTAMT." + rechargeOrderNo, 18, 22, () -> {
				//差错处理前校验
				RechargeOrderDO rechargeOrderDO = checkBeforeErrHandle(hallRechargeErrorFundDTO, PwmConstants.HALL_CHK_SHORT_AMT);

				//营业厅短款撤单处理
				List<AccountingReqDTO> accList = createShortAmtErrAccList(rechargeOrderDO);

				BigDecimal dAmt = BigDecimal.ZERO;
				BigDecimal cAmt = BigDecimal.ZERO;
				for (AccountingReqDTO dto : accList) {
					if (JudgeUtils.isNotNull(dto)) {
						if (JudgeUtils.equals(dto.getDcFlg(), ACMConstants.AC_D_FLG)) {
							dAmt = dAmt.add(dto.getTxAmt());
						} else {
							cAmt = cAmt.add(dto.getTxAmt());
						}
					}
				}

				// 借贷平衡校验
				if (cAmt.compareTo(dAmt) != 0) {
					LemonException.throwBusinessException("PWM20026");
				}

				GenericDTO<List<AccountingReqDTO>> userAccDto = new GenericDTO<>();
				userAccDto.setBody(accList);
				GenericRspDTO<NoBody> accountingTreatment = accountingTreatmentClient.accountingTreatment(userAccDto);
				if (!JudgeUtils.isSuccess(accountingTreatment.getMsgCd())) {
					logger.error("充值订单" + rechargeOrderDO.getOrderNo() + "短款撤单账务处理失败：" + accountingTreatment.getMsgCd());
					LemonException.throwBusinessException(accountingTreatment.getMsgCd());
				}
				//更新充值订单状态
				rechargeOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_C);
				rechargeOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
				rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
				this.service.updateOrder(rechargeOrderDO);
                logger.info("营业厅充值订单" + rechargeOrderNo + "撤单成功");

				return null;
			});
		} catch (Exception e) {
			LemonException.throwBusinessException("PWM20028");
		}
	}

	@Override
	public void uploadHallRechargeChkFile(String type,String date,String fileName){
		//营业厅文件服务器配置
		final String hall_server = LemonUtils.getProperty("pwm.recharge.hall-sftp.ip");
		final int hall_port = Integer.valueOf(LemonUtils.getProperty("pwm.recharge.hall-sftp.port"));
		final String hall_userName = LemonUtils.getProperty("pwm.recharge.hall-sftp.name");
		final String hall_passd = LemonUtils.getProperty("pwm.recharge.hall-sftp.password");
		final String connectTimeout = LemonUtils.getProperty("pwm.recharge.hall-sftp.connectTimeout");
		//营业厅对账文件存放地址
		final String hallRemotePath = LemonUtils.getProperty("pwm.chk.remotePath");
		//服平台务器对账文件存放地址
		final String localPath = LemonUtils.getProperty("pwm.chk.hallRemotePath");
		logger.info("从营业厅ip>>"+ hall_server + ",端口号>> " + hall_port + ",目录>>" +
				hallRemotePath +", " + "获取对账文件名>>" + fileName);

		if(JudgeUtils.isNotBlank(fileName) && !fileName.startsWith(".ck")){
			fileName = fileName + ".ck";
		}
		//充值对账文件获取
		if(JudgeUtils.equals(type,PwmConstants.HALL_CHK_TYPE_RC)){
			try{
				//将营业厅对账文件生成到服务器指定路径
				FileSftpUtils.download(hall_server,hall_port,Integer.valueOf(connectTimeout),hallRemotePath,fileName,localPath,hall_userName,hall_passd);
			}catch (Exception e){
				logger.error("获取营业厅对账文件失败!!");
				throw new LemonException("PWM30016");
			}
		}
	}

	@Override
	public void  hallRechargeMatchHandler(GenericDTO<HallRechargeMatchDTO> genericDTO){
		HallRechargeMatchDTO hallRechargeMatchDTO = genericDTO.getBody();
		if(JudgeUtils.isNull(hallRechargeMatchDTO)){
			throw new LemonException("PWM20033");
		}
		//对账类型校验
		String chkSubType = hallRechargeMatchDTO.getChkSubType();
		if(!JudgeUtils.equals(chkSubType,"0404")){
			throw new LemonException("PWM20034");
		}
		List<AccountingReqDTO> accList = createMatchAmtAccList(hallRechargeMatchDTO);

		BigDecimal dAmt = BigDecimal.ZERO;
		BigDecimal cAmt = BigDecimal.ZERO;
		for (AccountingReqDTO dto : accList) {
			if (JudgeUtils.isNotNull(dto)) {
				if (JudgeUtils.equals(dto.getDcFlg(), ACMConstants.AC_D_FLG)) {
					dAmt = dAmt.add(dto.getTxAmt());
				} else {
					cAmt = cAmt.add(dto.getTxAmt());
				}
			}
		}

		// 借贷平衡校验
		if (cAmt.compareTo(dAmt) != 0) {
			LemonException.throwBusinessException("PWM20026");
		}

		GenericDTO<List<AccountingReqDTO>> userAccDto = new GenericDTO<>();
		userAccDto.setBody(accList);
		GenericRspDTO<NoBody> accountingTreatment = accountingTreatmentClient.accountingTreatment(userAccDto);
		if (!JudgeUtils.isSuccess(accountingTreatment.getMsgCd())) {
			logger.error("营业厅对平金额账务处理失败：" + accountingTreatment.getMsgCd());
			LemonException.throwBusinessException(accountingTreatment.getMsgCd());
		}
		logger.info("营业厅对平金额账务处理成功，总金额：" + hallRechargeMatchDTO.getTotalAmt());
	}

	@Override
	public void rechargeRevoke(GenericDTO<RechargeRevokeDTO> genericDTO){

		RechargeRevokeDTO rechargeRevokeDTO = genericDTO.getBody();
		if(JudgeUtils.isNull(rechargeRevokeDTO)){
			throw new LemonException("PWM20035");
		}
		String chkSubType = rechargeRevokeDTO.getChkSubType();
		if(!(JudgeUtils.equals(chkSubType,"0404") || JudgeUtils.equals(chkSubType,"0405"))){
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
				//查询收银订单信息
				GenericRspDTO<OrderDTO> genericRspDTO = cshOrderClient.query(rechargeOrderNo);
				if(JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())){
					LemonException.throwBusinessException(genericRspDTO.getMsgCd());
				}
				OrderDTO orderDTO = genericRspDTO.getBody();
				refundOrderDTO.setGoodInfo(orderDTO.getGoodsInfo());
				refundOrderDTO.setOrderCcy(rechargeOrderDO.getOrderCcy());
				refundOrderDTO.setOrginOrderNo(rechargeOrderDO.getExtOrderNo());
				refundOrderDTO.setRefundUserId(rechargeOrderDO.getPayerId());
				refundOrderDTO.setTxType(PwmConstants.TX_TYPE_RECHARGE_REFUND);
				//退款金额为订单金额
				refundOrderDTO.setRfdAmt(rechargeOrderDO.getOrderAmt());
				GenericDTO genericRefundOrder = new GenericDTO();
				genericRefundOrder.setBody(refundOrderDTO);
				GenericRspDTO<RefundOrderRspDTO> genericRefundOrderRsp = cshRefundClient.createBill(genericRefundOrder);
				if(JudgeUtils.isNotSuccess(genericRefundOrderRsp.getMsgCd())){
					LemonException.throwBusinessException(genericRefundOrderRsp.getMsgCd());
				}
				//更新充值订单为已退款
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

	private String md5Str(HallRechargeApplyDTO.BussinessBody busBody, String key) {
		ObjectMapper om = new ObjectMapper();
		String retStr = "";
		try {
			Map<String,Object> oriMap = new LinkedHashMap<>();
			oriMap.put("amount",busBody.getAmount().setScale(2));
			oriMap.put("ccy",busBody.getCcy());
			oriMap.put("fee",busBody.getFee().setScale(2));
			oriMap.put("hallOrderNo",busBody.getHallOrderNo());
			oriMap.put("payerId",busBody.getPayerId());
			oriMap.put("status",busBody.getStatus());
			retStr = om.writeValueAsString(oriMap);

			Md5PasswordEncoder md5 = new Md5PasswordEncoder();
			retStr = md5.encodePassword(retStr+key,null);
		} catch (JsonProcessingException e) {

		}
		return retStr;
	}

	private void signCheck(HallRechargeApplyDTO dto) {
		String applySign = dto.getSign();
		// 签名密钥
		String key = LemonUtils.getProperty("pwm.recharge.HALLKEY");
		HallRechargeApplyDTO.BussinessBody bussinessBody = dto.getBody();
		String md5 = md5Str(bussinessBody, key);
		// 签名校验
		if (JudgeUtils.isNull(md5) || !JudgeUtils.equals(applySign, md5.toUpperCase())) {
			throw new LemonException("PWM10036");
		}
	}


	private RechargeOrderDO checkBeforeErrHandle(HallRechargeErrorFundDTO hallrep, String handleType){
		String orderNo = hallrep.getOrderNo();
		//差错处理类型校验：长款/短款
		if(!JudgeUtils.equals(handleType,hallrep.getFundType())){
			throw new LemonException("PWM20025");
		}
		//原订单校验
		RechargeOrderDO rechargeOrderDO = service.getRechangeOrderDao().get(orderNo);
		if(JudgeUtils.isNull(rechargeOrderDO)){
			throw new LemonException("PWM20024");
		}
		//业务类型校验
		if(!JudgeUtils.equals(rechargeOrderDO.getBusType(),PwmConstants.BUS_TYPE_RECHARGE_HALL)){
			throw new LemonException("PWM20031");
		}

		if(JudgeUtils.equals(handleType,PwmConstants.HALL_CHK_LONG_AMT)){
			//补单订单状态检查
			if(JudgeUtils.equals(rechargeOrderDO.getOrderStatus(),PwmConstants.RECHARGE_ORD_S)){
				throw new LemonException("PWM20029");
			}
		}else if(JudgeUtils.equals(handleType,PwmConstants.HALL_CHK_SHORT_AMT)){
			//撤单订单状态检查
			if(!JudgeUtils.equals(rechargeOrderDO.getOrderStatus(),PwmConstants.RECHARGE_ORD_S)){
				throw new LemonException("PWM20030");
			}
		}else{

		}
		return rechargeOrderDO;
	}

	private List<AccountingReqDTO> createLongAmtErrAccList(RechargeOrderDO rechargeOrderDO){
		List<AccountingReqDTO> accList=new ArrayList<>();
		//充值用户
		String userId = rechargeOrderDO.getPayerId();
		//查询个人账号
		String balCapType= CapTypEnum.CAP_TYP_CASH.getCapTyp();
		String balAcNo=acmComponent.getAcmAcNo(userId, balCapType);

		if(JudgeUtils.isBlank(balAcNo)){
			throw new LemonException("PWM20022");
		}
		String tmpJrnNo =  LemonUtils.getApplicationName() + IdGenUtils.generateIdWithDate(PwmConstants.R_ORD_GEN_PRE,11);
		//订单交易总金额
		BigDecimal totalAmt = rechargeOrderDO.getOrderAmt();
		if(JudgeUtils.isNotNull(rechargeOrderDO.getFee())){
			totalAmt = rechargeOrderDO.getOrderAmt().add(rechargeOrderDO.getFee());
		}
		//借:应收账款-渠道充值-营业厅
		AccountingReqDTO cnlRechargeHallReqDTO=acmComponent.createAccountingReqDTO(
				rechargeOrderDO.getOrderNo(),
				tmpJrnNo,
				rechargeOrderDO.getTxType(),
				ACMConstants.ACCOUNTING_NOMARL,
				totalAmt,
				null,
				ACMConstants.ITM_AC_TYP,
				balCapType,
				ACMConstants.AC_D_FLG,
				CshConstants.AC_ITEM_CNL_RECHARGE_HALL,
				null,
				null,
				null,
				null,
				null);

		//贷:其他应付款-支付账户-现金账户
		AccountingReqDTO userAccountReqDTO=acmComponent.createAccountingReqDTO(
				rechargeOrderDO.getOrderNo(),
				tmpJrnNo,
				rechargeOrderDO.getTxType(),
				ACMConstants.ACCOUNTING_NOMARL,
				totalAmt,
				balAcNo,
				ACMConstants.USER_AC_TYP,
				balCapType,
				ACMConstants.AC_C_FLG,
				CshConstants.AC_ITEM_CSH_BAL,
				null,
				null,
				null,
				null,
				"营业厅长款补单$"+totalAmt);

		accList.add(cnlRechargeHallReqDTO);
		accList.add(userAccountReqDTO);

		return accList;
	}

	private List<AccountingReqDTO> createShortAmtErrAccList(RechargeOrderDO rechargeOrderDO){
		List<AccountingReqDTO> accList=new ArrayList<>();
		//充值用户
		String userId = rechargeOrderDO.getPayerId();
		//查询个人账号
		String balCapType= CapTypEnum.CAP_TYP_CASH.getCapTyp();
		String balAcNo=acmComponent.getAcmAcNo(userId, balCapType);

		if(JudgeUtils.isBlank(balAcNo)){
			throw new LemonException("PWM20022");
		}
		String tmpJrnNo =  IdGenUtils.generateIdWithDate(PwmConstants.R_ORD_GEN_PRE,14);
		//订单交易总金额
//		BigDecimal totalAmt = rechargeOrderDO.getOrderAmt().multiply(rechargeOrderDO.getFee());
		BigDecimal totalAmt = rechargeOrderDO.getOrderAmt();
		//贷:应收账款-渠道充值-营业厅
		AccountingReqDTO cnlRechargeHallReqDTO=acmComponent.createAccountingReqDTO(
				rechargeOrderDO.getOrderNo(),
				tmpJrnNo,
				rechargeOrderDO.getTxType(),
				ACMConstants.ACCOUNTING_NOMARL,
				totalAmt,
				null,
				ACMConstants.ITM_AC_TYP,
				balCapType,
				ACMConstants.AC_C_FLG,
				CshConstants.AC_ITEM_CNL_RECHARGE_HALL,
				null,
				null,
				null,
				null,
				null);

		//借:其他应付款-支付账户-现金账户
		AccountingReqDTO userAccountReqDTO=acmComponent.createAccountingReqDTO(
				rechargeOrderDO.getOrderNo(),
				tmpJrnNo,
				rechargeOrderDO.getTxType(),
				ACMConstants.ACCOUNTING_NOMARL,
				totalAmt,
				balAcNo,
				ACMConstants.USER_AC_TYP,
				balCapType,
				ACMConstants.AC_D_FLG,
				CshConstants.AC_ITEM_CSH_BAL,
				null,
				null,
				null,
				null,
				"营业厅短款撤单$"+totalAmt);

		accList.add(cnlRechargeHallReqDTO);
		accList.add(userAccountReqDTO);

		return accList;
	}

	private List<AccountingReqDTO> createMatchAmtAccList(HallRechargeMatchDTO hallRechargeMatchDTO){
		List<AccountingReqDTO> accList=new ArrayList<>();

		String tmpNo = LemonUtils.getApplicationName() + PwmConstants.BUS_TYPE_RECHARGE_HALL + IdGenUtils.generateIdWithDate(PwmConstants.R_ORD_GEN_PRE,10);

		BigDecimal totalAmt = hallRechargeMatchDTO.getTotalAmt();
		//贷:应收账款-渠道充值-营业厅
		AccountingReqDTO cnlRechargeHallReqDTO=acmComponent.createAccountingReqDTO(
				tmpNo,
				tmpNo,
				PwmConstants.TX_TYPE_RECHANGE,
				ACMConstants.ACCOUNTING_NOMARL,
				totalAmt,
				null,
				ACMConstants.ITM_AC_TYP,
				null,
				ACMConstants.AC_C_FLG,
				PwmConstants.AC_ITEM_CNL_RECHARGE_HALL,
				null,
				null,
				null,
				null,
				null);

		//借:应收账款-待结算款-营业厅
		AccountingReqDTO userAccountReqDTO=acmComponent.createAccountingReqDTO(
				tmpNo,
				tmpNo,
				PwmConstants.TX_TYPE_RECHANGE,
				ACMConstants.ACCOUNTING_NOMARL,
				totalAmt,
				null,
				ACMConstants.ITM_AC_TYP,
				null,
				ACMConstants.AC_D_FLG,
				PwmConstants.AC_ITEM_PAY_HALL,
				null,
				null,
				null,
				null,
				"营业厅对平金额$"+totalAmt);

		accList.add(cnlRechargeHallReqDTO);
		accList.add(userAccountReqDTO);

		return accList;
	}


    /**
     * 充值信息中心推送
     * @param rechargeOrderDO
     * @param messageFlag
     * @throws LemonException
     */
    public void sendMsgCenterInfo(RechargeOrderDO rechargeOrderDO,int messageFlag) throws LemonException{
	    String userId = rechargeOrderDO.getPayerId();
        logger.info("recharge message send to userId : " + userId);
        String language = LemonUtils.getLocale().getLanguage();
        if (JudgeUtils.isBlank(language)) {
            language = "en";
            logger.error("setting default language : " + language);
        }
        Map<String, String> map = new HashMap<>();
        GenericDTO<MessageSendReqDTO> reqDTO = new GenericDTO();
        MessageSendReqDTO messageSendReqDTO = new MessageSendReqDTO();
        messageSendReqDTO.setUserId(userId);
        messageSendReqDTO.setMessageLanguage(language);
        switch (messageFlag){
            case RECHARGE_SUCCESS:
                messageSendReqDTO.setMessageTemplateId(PwmConstants.RECHARGE_SUCC_TEMPL);
                BigDecimal orderAmt = rechargeOrderDO.getOrderAmt();
                if(JudgeUtils.isNotNull(orderAmt)){
                    map.put("orderAmt",String.valueOf(rechargeOrderDO.getOrderAmt()));
                }else{
                    map.put("orderAmt","");
                }
                break;
            case RECHARGE_OFFLINE_BACK:
                messageSendReqDTO.setMessageTemplateId(PwmConstants.RECHARGE_OFFLINE_BACK_TEMPL);
                map.put("reason",rechargeOrderDO.getRemark());
                break;
            default:
                break;
        }
        map.put("date",DateTimeUtils.formatLocalDate(rechargeOrderDO.getAcTm(), "yyyy-MM-dd"));
        messageSendReqDTO.setReplaceFieldMap(map);
        reqDTO.setBody(messageSendReqDTO);
        GenericRspDTO<NoBody> genericRspDTO = cmmServerClient.messageSend(reqDTO);
        if(JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())){
            logger.error("充值通知类型:" + messageFlag +", 推送订单号："+ rechargeOrderDO.getOrderNo()+"信息失败。");
            LemonException.throwLemonException(genericRspDTO.getMsgCd(), genericRspDTO.getMsgInfo());
        }
        logger.info("充值消息推动成功");
    }

    /**
     * 同步账单方法
     * @param updOrderDO
     * @param flag 1 创建账单  2 更新账单
     */
    public void synchronizeRechargeBil(RechargeOrderDO updOrderDO,int flag){
        switch (flag){
            case CREATE_BIL:
            	//获取国际化账单描述
				Object[] args=new Object[]{updOrderDO.getOrderAmt()};
				String descStr=getViewOrderInfo(updOrderDO.getBusType(),args);

                CreateUserBillDTO createUserBillDTO = new CreateUserBillDTO();
                BeanUtils.copyProperties(createUserBillDTO, updOrderDO);
				createUserBillDTO.setTxTm(updOrderDO.getOrderTm());
                createUserBillDTO.setGoodsInfo(descStr);
                createUserBillDTO.setCrdPayType("3");
                createUserBillDTO.setCrdPayAmt(updOrderDO.getOrderAmt());
                billSyncHandler.createBill(createUserBillDTO);
                break;
            case UPD_BIL:
                UpdateUserBillDTO updateUserBillDTO = new UpdateUserBillDTO();
                BeanUtils.copyProperties(updateUserBillDTO, updOrderDO);
                billSyncHandler.updateBill(updateUserBillDTO);
                break;
            default:
                break;
        }
    }

	/**
	 * 创建营业厅充值订单
	 * @param busBody
	 * @return
	 */
	public RechargeOrderDO createHallRechargeOrder(HallRechargeApplyDTO.BussinessBody busBody){
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = PwmConstants.BUS_TYPE_RECHARGE_HALL + ymd + IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 15);
		// 生成充值订单
		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_HALL);
		rechargeOrderDO.setExtOrderNo("");
		rechargeOrderDO.setOrderAmt(busBody.getAmount());
		rechargeOrderDO.setOrderCcy(busBody.getCcy());
		rechargeOrderDO.setOrderExpTm(DateTimeUtils.getCurrentLocalDateTime().plusMinutes(15));
		rechargeOrderDO.setOrderNo(orderNo);
		rechargeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setOrderStatus(busBody.getStatus());
		rechargeOrderDO.setIpAddress("");
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setOrderSuccTm(null);
		rechargeOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_W);
		rechargeOrderDO.setPsnFlag(busBody.getPsnFlag());
		rechargeOrderDO.setSysChannel(PwmConstants.ORD_SYSCHANNEL_HALL);
		rechargeOrderDO.setTxType(PwmConstants.TX_TYPE_RECHANGE);
		rechargeOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setRemark("");
		rechargeOrderDO.setPayerId(busBody.getPayerId());
		rechargeOrderDO.setHallOrderNo(busBody.getHallOrderNo());
		rechargeOrderDO.setFee(busBody.getFee());
		this.service.initOrder(rechargeOrderDO);

		return rechargeOrderDO;
	}

    /**
     * 计算营业厅充值手续费
     * @param orderAmt 充值订单金额
     * @return
     */
    private BigDecimal caculateHallRechargeFee(BigDecimal orderAmt){
    	if(JudgeUtils.isNull(orderAmt)){
    		throw new LemonException("PWM10006");
		}
		TradeRateReqDTO tradeFeeReqDTO = new TradeRateReqDTO();
		tradeFeeReqDTO.setCcy(PwmConstants.HALL_PAY_CCY);
		tradeFeeReqDTO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_HALL);
		GenericDTO<TradeRateReqDTO> genericTradeRateReqDTO = new GenericDTO<>();
		genericTradeRateReqDTO.setBody(tradeFeeReqDTO);

		GenericRspDTO<TradeRateRspDTO> genericTradeFeeRspDTO = fmServerClient.tradeRate(genericTradeRateReqDTO);
		TradeRateRspDTO tradeRateRspDTO = genericTradeFeeRspDTO.getBody();
		//费率
		BigDecimal tradeFee = tradeRateRspDTO.getRate();
		return orderAmt.multiply(tradeFee).setScale(2,BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 营业厅请求处理前校验
	 * @param dto 请求对象
	 */
	private void checkHallRequestBeforeHandler(HallRechargeApplyDTO dto){
        HallRechargeApplyDTO.BussinessBody bussinessBody = dto.getBody();
        if(JudgeUtils.isNull(bussinessBody)){
            throw new LemonException("PWM10053");
        }
        RiskCheckUserStatusReqDTO riskCheckUserStatusReqDTO = new RiskCheckUserStatusReqDTO();
        riskCheckUserStatusReqDTO.setIdTyp(Constants.ID_TYP_USER_NO);
        riskCheckUserStatusReqDTO.setId(bussinessBody.getPayerId());
        riskCheckUserStatusReqDTO.setTxTyp(PwmConstants.TX_TYPE_RECHANGE);
        //用户状态检查
        GenericRspDTO<NoBody> checkStatus = riskCheckClient.checkUserStatus(riskCheckUserStatusReqDTO);
        if(JudgeUtils.isNotSuccess(checkStatus.getMsgCd())){
            LemonException.throwBusinessException(checkStatus.getMsgCd());
        }
        //签名校验
        signCheck(dto);

        //充值操作状态校验
		RechargeOrderDO oriRechargeOrderDO = this.service.getRechargeOrderByHallOrderNo(bussinessBody.getHallOrderNo());
        String operationType = bussinessBody.getStatus();
        if(JudgeUtils.equals(operationType,PwmConstants.RECHARGE_OPR_A)){
			//重复下单校验
			if(JudgeUtils.isNotNull(oriRechargeOrderDO)){
				throw new LemonException("PWM20021");
			}
        }else if(JudgeUtils.equals(operationType,PwmConstants.RECHARGE_OPR_C)){
			if(JudgeUtils.isNull(oriRechargeOrderDO)) {
				throw new LemonException("PWM20010");
			}
        }else {
            throw new LemonException("PWM10037");
        }

    }

	/**
	 * 同步原订单和更新订单数据
	 * @param oriOrder 原订单
	 * @param updateOrder 更新后订单
	 * @return
	 */
    private RechargeOrderDO syncOrderData(RechargeOrderDO oriOrder,RechargeOrderDO updateOrder){
		if(JudgeUtils.isNull(updateOrder.getOrderAmt())){
			updateOrder.setOrderAmt(oriOrder.getOrderAmt());
		}
		if(JudgeUtils.isNull(updateOrder.getAcTm())){
			updateOrder.setAcTm(updateOrder.getAcTm());
		}
		if(JudgeUtils.isNull(updateOrder.getPayerId())){
			updateOrder.setPayerId(oriOrder.getPayerId());
		}
		if(JudgeUtils.isNull(updateOrder.getFee())){
			updateOrder.setFee(oriOrder.getFee());
		}
		if(JudgeUtils.isNull(updateOrder.getBusType())){
			updateOrder.setBusType(oriOrder.getBusType());
		}
		if(JudgeUtils.isNull(updateOrder.getTxType())){
			updateOrder.setTxType(oriOrder.getTxType());
		}
		if(JudgeUtils.isNull(updateOrder.getOrderTm())){
		    updateOrder.setOrderTm(oriOrder.getOrderTm());
        }

		return updateOrder;
	}
    
    
    /**
	 * 国际化商品描述信息
	 * @param busType
	 * @param args
	 * @return
	 */
	public String getViewOrderInfo(String busType,Object[] args){
		//国际化配置文件中key值
		String key = "";
		try{
			String txType = "";
			if(JudgeUtils.isNull(busType)){
				return null;
			}
			txType = busType.length() <= 2 ? busType : busType.substring(2);
			if(JudgeUtils.equals(txType,PwmConstants.TX_TYPE_HCOUPON)){
				key="view.orderinfo."+busType;
			}else if(JudgeUtils.equals(txType,PwmConstants.TX_TYPE_RECHANGE)) {
				key="view.rechargeinfo."+busType;
			}else{

			}
			return localeMessageSource.getMessage(key,args);
		}catch (Exception e){
			logger.error("获取国际化配置文件key="+key+"失败!");
		}
		return  null;
	}

}
