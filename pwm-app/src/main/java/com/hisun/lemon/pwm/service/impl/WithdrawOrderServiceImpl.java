package com.hisun.lemon.pwm.service.impl;


import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hisun.lemon.bil.dto.CreateUserBillDTO;
import com.hisun.lemon.bil.dto.UpdateUserBillDTO;
import com.hisun.lemon.cmm.client.CmmServerClient;
import com.hisun.lemon.cmm.dto.CommonEncryptReqDTO;
import com.hisun.lemon.cmm.dto.CommonEncryptRspDTO;
import com.hisun.lemon.cmm.dto.MessageSendReqDTO;
import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.BeanUtils;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.common.utils.StringUtils;
import com.hisun.lemon.constants.AccConstants;
import com.hisun.lemon.constants.RiskConstants;
import com.hisun.lemon.cpo.client.RouteClient;
import com.hisun.lemon.cpo.client.WithdrawClient;
import com.hisun.lemon.cpo.dto.RouteRspDTO;
import com.hisun.lemon.cpo.dto.WithdrawReqDTO;
import com.hisun.lemon.cpo.enums.CorpBusSubTyp;
import com.hisun.lemon.cpo.enums.CorpBusTyp;
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
import com.hisun.lemon.pwm.component.AcmComponent;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dao.IWithdrawCardBindDao;
import com.hisun.lemon.pwm.dao.IWithdrawCardInfoDao;
import com.hisun.lemon.pwm.dao.IWithdrawOrderDao;
import com.hisun.lemon.pwm.dto.WithdrawBankRspDTO;
import com.hisun.lemon.pwm.dto.WithdrawCardBindDTO;
import com.hisun.lemon.pwm.dto.WithdrawCardDelDTO;
import com.hisun.lemon.pwm.dto.WithdrawCardDelRspDTO;
import com.hisun.lemon.pwm.dto.WithdrawCardQueryDTO;
import com.hisun.lemon.pwm.dto.WithdrawDTO;
import com.hisun.lemon.pwm.dto.WithdrawErrorHandleDTO;
import com.hisun.lemon.pwm.dto.WithdrawRateDTO;
import com.hisun.lemon.pwm.dto.WithdrawRateResultDTO;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
import com.hisun.lemon.pwm.dto.WithdrawRspDTO;
import com.hisun.lemon.pwm.entity.WithdrawCardBindDO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import com.hisun.lemon.pwm.mq.BillSyncHandler;
import com.hisun.lemon.pwm.mq.PaymentHandler;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;
import com.hisun.lemon.rsk.client.XXRiskHandleClient;
import com.hisun.lemon.tfm.client.TfmServerClient;
import com.hisun.lemon.tfm.dto.TradeFeeCaculateReqDTO;
import com.hisun.lemon.tfm.dto.TradeFeeCaculateRspDTO;
import com.hisun.lemon.tfm.dto.TradeFeeReqDTO;
import com.hisun.lemon.tfm.dto.TradeFeeRspDTO;
import com.hisun.lemon.tfm.dto.TradeRateReqDTO;
import com.hisun.lemon.urm.client.UserAuthenticationClient;
import com.hisun.lemon.urm.client.UserBasicInfClient;
import com.hisun.lemon.urm.dto.CheckPayPwdDTO;
import com.hisun.lemon.urm.dto.UserBasicInfDTO;
import com.hisun.lemon.xxka.client.XXAccHandleClient;


@Service("withdrawOrderService")
public class WithdrawOrderServiceImpl extends BaseService implements IWithdrawOrderService {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawOrderServiceImpl.class);

	@Resource
    private WithdrawOrderTransactionalService withdrawOrderTransactionalService;

	@Resource
	private WithdrawClient withdrawClient;

    @Resource
    private RouteClient routeClient;

    @Resource
    private TfmServerClient tfmServerClient;

    @Resource
    private AcmComponent acmComponent;

    @Resource
    private XXRiskHandleClient riskCheckClient;

    @Resource
    private UserAuthenticationClient userAuthenticationClient;

    @Resource
    private IWithdrawCardInfoDao withdrawCardInfoDao;

    @Resource
    private IWithdrawCardBindDao withdrawCardBindDao;

    @Resource
    private CmmServerClient cmmServerClient;

    @Resource
    LocaleMessageSource localeMessageSource;

    @Resource
    BillSyncHandler billSyncHandler;

    @Resource
    private UserBasicInfClient userBasicInfClient;

    @Resource
    private IWithdrawOrderDao withdrawOrderDao;

    @Resource
    private TfmServerClient fmServerClient;

    @Resource
    protected PaymentHandler paymentHandler;

    @Resource
    protected WithdrawClient withdrawCpoClient;

    @Resource
    protected DistributedLocker locker;
    @Resource
	protected XXAccHandleClient xxAccService;
    
    public String card(String cardNo){
   	 GenericDTO<CommonEncryptReqDTO> reqDTO = new GenericDTO<>();
		CommonEncryptReqDTO commonEncryptReqDTO = new CommonEncryptReqDTO();
		commonEncryptReqDTO.setData(cardNo);// 加解密数据
		commonEncryptReqDTO.setType("encrypt");// 加解密类型
		reqDTO.setBody(commonEncryptReqDTO);
		logger.info("====================银行卡号加密=====" +cardNo + "=====类型" + commonEncryptReqDTO.getType());
		GenericRspDTO<CommonEncryptRspDTO> commonEncrtyRspDTO = cmmServerClient.encrypt(reqDTO);
		if (JudgeUtils.isNotSuccess(commonEncrtyRspDTO.getMsgCd())) {
			LemonException.throwBusinessException(commonEncrtyRspDTO.getMsgCd());
		}
		String crdNoEnc = commonEncrtyRspDTO.getBody().getData();
		return crdNoEnc;
    }
    private String getAccountNo(String userId) {
        GenericRspDTO<UserBasicInfDTO> rspDTO = userBasicInfClient.queryUser(userId);
        if (JudgeUtils.isNotSuccess(rspDTO.getMsgCd())) {
            if (logger.isDebugEnabled()) {
                logger.debug("user:" + userId + " get account no failure");
            }
            LemonException.throwBusinessException(rspDTO.getMsgCd());
        }
        return rspDTO.getBody().getAcNo();
    }
	/**
	 * 生成提现订单
	 * 
	 * @param genericWithdrawDTO
	 */
	@Override
	public WithdrawRspDTO createOrder(GenericDTO<WithdrawDTO> genericWithdrawDTO) {
		// 生成订单号
		WithdrawDTO withdrawDTO = genericWithdrawDTO.getBody();
		String userId = withdrawDTO.getUserId();
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = IdGenUtils.generateId(PwmConstants.W_ORD_GEN_PRE + ymd, 15);
		orderNo = PwmConstants.BUS_TYPE_WITHDRAW_P + ymd + orderNo;
		GenericRspDTO genericRspDTO = new GenericRspDTO();
		GenericDTO genericDTO = new GenericDTO();

		// 实时风控
		logger.info("实时风控校验");
		GenericDTO<RiskDataDTO> genericTimeDTO = new GenericDTO<>();
		RiskDataDTO riskDataTimeDTO = new RiskDataDTO();
		Map<String, Object> riskDataTimeMap = new HashMap<>();
		riskDataTimeDTO.setRiskType(RiskConstants.RISK_TYPE_REALTIME);
		// 对象规则
		riskDataTimeMap.put("RULE_ROLE", RiskConstants.RULE_ROLE_REAL_USER);
		riskDataTimeMap.put("USER_USR_NO", userId);
		riskDataTimeMap.put("TX_ORD_AMT", withdrawDTO.getWcApplyAmt());
		riskDataTimeMap.put("TX_TYP", RiskConstants.TX_TYP_TX);
		riskDataTimeMap.put("TX_ORD_NO", orderNo);
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
		String actDate = df.format(genericWithdrawDTO.getAccDate());
		riskDataTimeMap.put("TX_ORD_DT", actDate);
		riskDataTimeMap.put("PAY_TYP", RiskConstants.PAY_TYP_BAL_PAY);
		riskDataTimeDTO.setRiskDataMap(riskDataTimeMap);
		genericTimeDTO.setBody(riskDataTimeDTO);
		GenericRspDTO<RiskDataDTO> riskTimeDTO = riskCheckClient.xxRiskHandle(genericTimeDTO);
		if (JudgeUtils.isNotSuccess(riskTimeDTO.getMsgCd())) {
			logger.info("实时风控检查失败");
			LemonException.throwBusinessException("PWM30001");
		}
		// 填充查询手续费数据
		TradeFeeReqDTO tradeFeeReqDTO = new TradeFeeReqDTO();
		tradeFeeReqDTO.setBusOrderNo(orderNo);
		tradeFeeReqDTO.setBusOrderTime(DateTimeUtils.getCurrentLocalDateTime());
		tradeFeeReqDTO.setBusType(PwmConstants.BUS_TYPE_WITHDRAW_P);
		tradeFeeReqDTO.setCcy(withdrawDTO.getOrderCcy());
		tradeFeeReqDTO.setTradeAmt(withdrawDTO.getWcApplyAmt());
		tradeFeeReqDTO.setUserId(userId);
		genericDTO.setBody(tradeFeeReqDTO);

		// 调用tfm接口查询手续费
		GenericRspDTO<TradeFeeRspDTO> tradeGenericRspDTO = tfmServerClient.tradeFee(genericDTO);
		if (JudgeUtils.isNotSuccess(tradeGenericRspDTO.getMsgCd())) {
			logger.info("调用计费失败："+tradeGenericRspDTO.getMsgCd());
			//LemonException.throwBusinessException("PWM30008");
			LemonException.throwBusinessException(tradeGenericRspDTO.getMsgCd());
		}
		// 手续费
		BigDecimal fee = tradeGenericRspDTO.getBody().getTradeFee();
		// 总提现金额
		BigDecimal totalAmt = tradeGenericRspDTO.getBody().getTradeTotalAmt();
		if (JudgeUtils.isNull(withdrawDTO.getFeeAmt())) {
			withdrawDTO.setFeeAmt(BigDecimal.ZERO);
		}
		// 校验前端传入的手续费与计算出的手续费是否一致
		if (fee.compareTo(withdrawDTO.getFeeAmt()) != 0) {
			LemonException.throwBusinessException("PWM30006");
		}

		AccDataListDTO accDataListDTO = acmComponent.createAccountingReqDTO(AccConstants.MAIN_ACC_BAL_QRY, userId,
				AccConstants.AC_TYP_USR_CASH, AccConstants.AC_ORG_CARD, AccConstants.CAP_TYP_CASH,
				AccConstants.MAIN_TX_TYP_QRY, AccConstants.MINI_TX_TYP_MAIN_ACC_QRY, AccConstants.RVS_TX_FLG_N, null,
				null);
		// 调用账户接口，查询账户余额
		AccDataListDTO AccDataList = acmComponent.queryAcBal(accDataListDTO);
		if (JudgeUtils.isNotSuccess(AccDataList.getMsgCod())) {
			LemonException.throwBusinessException(AccDataList.getMsgCod());
		}
		List<Map<String, Object>> accDataMapList = AccDataList.getAccDataMapList();
		// 可用余额
		String avaBal = (String) accDataMapList.get(0).get("AVA_BAL");

		// 校验提现金额加手续费大于用户账户余额,则抛出异常
		if (new BigDecimal(avaBal).compareTo(totalAmt) < 0) {
			logger.info("提现超额");
			LemonException.throwBusinessException("PWM30002");
		}

		// 查询用户支付密码，校验支付密码，错误则抛异常
		CheckPayPwdDTO checkPayPwdDTO = new CheckPayPwdDTO();
		checkPayPwdDTO.setUserId(userId);
		checkPayPwdDTO.setPayPwd(withdrawDTO.getPayPassWord());
		checkPayPwdDTO.setPayPwdRandom(withdrawDTO.getPayPassWordRand());
		genericDTO.setBody(checkPayPwdDTO);
		genericRspDTO = userAuthenticationClient.checkPayPwd(genericDTO);
		if (JudgeUtils.isNull(genericRspDTO)) {
			LemonException.throwBusinessException("PWM30010");
		}
		if (JudgeUtils.equals("URM30005", genericRspDTO.getMsgCd())) {
			LemonException.throwBusinessException("PWM30004");
		}
		// 查询支付密码错误次数是否超过5次
		if (JudgeUtils.equals("URM30011", genericRspDTO.getMsgCd())) {
			LemonException.throwBusinessException("PWM30017");
		}
		if (JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())) {
			if (JudgeUtils.isNotBlank(genericRspDTO.getMsgInfo())) {
				LemonException.throwLemonException(genericRspDTO.getMsgCd(), genericRspDTO.getMsgInfo());
			} else {
				LemonException.throwBusinessException(genericRspDTO.getMsgCd());
			}
		}

		// 初始化提现订单数据
		WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
		BeanUtils.copyProperties(withdrawOrderDO, withdrawDTO);
		withdrawOrderDO.setOrderNo(orderNo);
		withdrawOrderDO.setUserId(userId);
		withdrawOrderDO.setAcTm(genericWithdrawDTO.getAccDate());
		// 交易类型 04提现
		withdrawOrderDO.setTxType(PwmConstants.TX_TYPE_WITHDRAW);
		// 提现总金额=实际提现金额+手续费
		withdrawOrderDO.setWcTotalAmt(totalAmt);
		withdrawOrderDO.setWcActAmt(totalAmt.subtract(fee));

		// 业务类型 0401个人提现
		withdrawOrderDO.setBusType(PwmConstants.BUS_TYPE_WITHDRAW_P);
		withdrawOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		withdrawOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("90001231235959"));
		// 用户姓名
		withdrawOrderDO.setUserName(withdrawDTO.getCardUserName());
		withdrawOrderDO.setCapCardName(withdrawDTO.getCardUserName());
		withdrawOrderDO.setCapCardType("");
		withdrawOrderDO.setOrderStatus(PwmConstants.WITHDRAW_ORD_W1);
		// 生成提现单据
		withdrawOrderTransactionalService.createOrder(withdrawOrderDO);

		// 查询用户账号
		GenericRspDTO<UserBasicInfDTO> userBasicInfDTO = userBasicInfClient.queryUser(userId);
		if (JudgeUtils.isNotSuccess(userBasicInfDTO.getMsgCd())) {
			LemonException.throwBusinessException(userBasicInfDTO.getMsgCd());
		}
		String acNo=getAccountNo(withdrawOrderDO.getUserId());
		// 流水号s
		String jrnNo = LemonUtils.getRequestId();
		// 账务处理
		GenericRspDTO<AccDataListDTO> generic = UserAccAcsDeal(withdrawOrderDO, acNo);
		if (JudgeUtils.isNotSuccess(generic.getMsgCd())) {
			System.out.println("账务处理失败:" + generic.getMsgCd());
		}

		// 调用资金能力的提现申请接口，等待结果通知
		logger.info("调用资金能力的提现申请接口");
		WithdrawReqDTO withdrawReqDTO = new WithdrawReqDTO();
		withdrawReqDTO.setCapTyp("1");
		withdrawReqDTO.setCapCorg(withdrawOrderDO.getCapCorgNo());
		withdrawReqDTO.setUserNo(withdrawOrderDO.getUserId());
		withdrawReqDTO.setCcy(withdrawOrderDO.getOrderCcy());
		withdrawReqDTO.setCorpBusTyp(CorpBusTyp.WITHDRAW);
		withdrawReqDTO.setCorpBusSubTyp(CorpBusSubTyp.PER_WITHDRAW);
		withdrawReqDTO.setWcAplAmt(withdrawOrderDO.getWcApplyAmt());
		withdrawReqDTO.setUserNm(withdrawOrderDO.getUserName());
		withdrawReqDTO.setCrdNoEnc(withdrawOrderDO.getCapCardNo());
		withdrawReqDTO.setCapCrdNm(withdrawOrderDO.getUserName());
		withdrawReqDTO.setCrdAcTyp("D");
		withdrawReqDTO.setReqOrdNo(withdrawOrderDO.getOrderNo());
		withdrawReqDTO.setReqOrdDt(DateTimeUtils.getCurrentLocalDate());
		withdrawReqDTO.setReqOrdTm(DateTimeUtils.getCurrentLocalTime());
		withdrawReqDTO.setMblNo(withdrawOrderDO.getNtfMbl());
		withdrawReqDTO.setPsnCrpFlg("C");
		genericDTO.setBody(withdrawReqDTO);
		genericRspDTO = withdrawClient.createOrder(genericDTO);
		System.out.println("提现申请到银行卡");
		if (JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())) {
			logger.info("申请提现到银行卡失败");
			LemonException.throwBusinessException(genericRspDTO.getMsgCd());
		
		}

		// 国际化订单信息
		WithdrawCardBindDO withdrawCardBindDO = withdrawCardBindDao.query(withdrawOrderDO.getCapCardNo(), userId);
		Object[] args = new Object[] { withdrawCardBindDO.getCardNoLast(), withdrawOrderDO.getWcApplyAmt() };
		String desc = getViewOrderInfo(withdrawOrderDO.getCapCorgNo(), args);

		// 同步账单数据
		CreateUserBillDTO createUserBillDTO = new CreateUserBillDTO();
		BeanUtils.copyProperties(createUserBillDTO, withdrawOrderDO);
		createUserBillDTO.setTxTm(withdrawOrderDO.getOrderTm());
		createUserBillDTO.setOrderChannel(withdrawOrderDO.getBusCnl());
		createUserBillDTO.setPayerId(withdrawOrderDO.getUserId());
		createUserBillDTO.setOrderAmt(withdrawOrderDO.getWcApplyAmt());
		createUserBillDTO.setFee(withdrawOrderDO.getFeeAmt());
		createUserBillDTO.setGoodsInfo(desc);
		billSyncHandler.createBill(createUserBillDTO);
        logger.info("同步账单数据完成");
		WithdrawRspDTO withdrawRspDTO = new WithdrawRspDTO();
		withdrawRspDTO.setOrderNo(withdrawOrderDO.getOrderNo());
		withdrawRspDTO.setOrderStatus(withdrawOrderDO.getOrderStatus());
		return withdrawRspDTO;
	}

	/**
	 * 处理提现订单结果
	 * 
	 * @param genericWithdrawResultDTO
	 */
	@Override
	public WithdrawRspDTO completeOrder(GenericDTO<WithdrawResultDTO> genericWithdrawResultDTO) {

		WithdrawResultDTO withdrawResultDTO = genericWithdrawResultDTO.getBody();
		WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
		BeanUtils.copyProperties(withdrawOrderDO, withdrawResultDTO);
		// 校验订单是否存在
		WithdrawOrderDO queryWithdrawOrderDO = withdrawOrderTransactionalService.query(withdrawOrderDO.getOrderNo());
		withdrawOrderDO.setOrderTm(queryWithdrawOrderDO.getOrderTm());
		withdrawOrderDO.setFeeAmt(queryWithdrawOrderDO.getFeeAmt());
		withdrawOrderDO.setWcTotalAmt(withdrawOrderDO.getWcActAmt().add(withdrawOrderDO.getFeeAmt()));
		// 判断订单状态为'S1'，则修改订单成功时间
		if (JudgeUtils.equals(PwmConstants.WITHDRAW_ORD_S1, withdrawOrderDO.getOrderStatus())) {
			withdrawOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
			withdrawOrderDO.setRspSuccTm(DateTimeUtils.getCurrentLocalDateTime());
		}
		// 若订单状态为'F1'失败，则做账，并把手续费退了
		if (JudgeUtils.equals(PwmConstants.WITHDRAW_ORD_F1, withdrawOrderDO.getOrderStatus())) {
			//账务做失败账务处理   提现
			/*String actNo=queryWithdrawOrderDO.getUserId();
			logger.info("提现失败   账务处理  用户"+actNo);
			 GenericRspDTO<AccDataListDTO> userAccAcsFail=UserAccAcsFail(queryWithdrawOrderDO,actNo);
			if(JudgeUtils.isNotSuccess(userAccAcsFail.getMsgCd())){
	        	   logger.info("回滚账务失败");
	           }*/
		}
		withdrawOrderDO.setAcTm(LemonUtils.getAccDate());
		
		 //累计风控
        logger.info("累计风控校验");
        GenericDTO<RiskDataDTO> genericAccumDTO = new GenericDTO<>();
        RiskDataDTO riskDataAccumDTO = new RiskDataDTO();
        Map<String,Object> accumMap =new HashMap<>();
        riskDataAccumDTO.setRiskType(RiskConstants.RISK_TYPE_ACCUMULATE);
        //对象规则
        accumMap.put("RULE_ROLE",RiskConstants.RULE_ROLE_REAL_USER);
        accumMap.put("USER_USR_NO",queryWithdrawOrderDO.getUserId());
        accumMap.put("TX_ORD_AMT",queryWithdrawOrderDO.getWcApplyAmt());
        accumMap.put("TX_TYP",RiskConstants.TX_TYP_TX);
        
        accumMap.put("TX_ORD_NO",withdrawOrderDO.getOrderNo());
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
		String actDate = df.format(genericWithdrawResultDTO.getAccDate());
		accumMap.put("TX_ORD_DT",actDate);
	    String crdNoEnc=card(queryWithdrawOrderDO.getCapCardNo());
        //加密银行卡号
	    accumMap.put("CARD_NO",crdNoEnc);
	    accumMap.put("CARD_TYP",RiskConstants.CARD_TYP_DEBIT);
	    accumMap.put("PAY_TYP",RiskConstants.PAY_TYP_BAL_PAY);
        riskDataAccumDTO.setRiskDataMap(accumMap);
        genericAccumDTO.setBody(riskDataAccumDTO);
        GenericRspDTO<RiskDataDTO> riskAccDTO = riskCheckClient.xxRiskHandle(genericAccumDTO);
        if(JudgeUtils.isNotSuccess(riskAccDTO.getMsgCd())){
        	logger.info("风控累计失败");
        	LemonException.throwBusinessException(riskAccDTO.getMsgCd());
        }
     
        
		// 若更新提现单据状态及相关信息
		withdrawOrderTransactionalService.updateOrder(withdrawOrderDO);

		// 同步账单数据
		UpdateUserBillDTO updateUserBillDTO = new UpdateUserBillDTO();
		BeanUtils.copyProperties(updateUserBillDTO, withdrawOrderDO);
		if (JudgeUtils.isNotNull(withdrawOrderDO.getWcRemark())) {
			updateUserBillDTO.setRemark(withdrawOrderDO.getWcRemark());
		}
		billSyncHandler.updateBill(updateUserBillDTO);

		withdrawOrderDO.setUserId(queryWithdrawOrderDO.getUserId());
		// 订单成功或者失败时，做消息推送
		if (JudgeUtils.equals(PwmConstants.WITHDRAW_ORD_S1, withdrawOrderDO.getOrderStatus())) {
			if (JudgeUtils.isNotBlank(withdrawResultDTO.getCardNoLast())) {
				sendMessage(withdrawOrderDO, withdrawResultDTO.getCardNoLast());
			} else {
				sendMessage(withdrawOrderDO, null);
			}
		}
		if (JudgeUtils.equals(PwmConstants.WITHDRAW_ORD_F1, withdrawOrderDO.getOrderStatus())) {
			sendMessage(withdrawOrderDO, "");
		}

		WithdrawRspDTO withdrawRspDTO = new WithdrawRspDTO();
		withdrawRspDTO.setOrderNo(withdrawOrderDO.getOrderNo());
		withdrawRspDTO.setOrderStatus(withdrawOrderDO.getOrderStatus());
		return withdrawRspDTO;
	}

    /**
     * 查询交易费率
     * @param withdrawRateDTO
     * @return
     */
    @Override
    public GenericRspDTO<WithdrawRateResultDTO> queryRate(WithdrawRateDTO withdrawRateDTO) {

        TradeRateReqDTO tradeRateReqDTO = new TradeRateReqDTO();
        BeanUtils.copyProperties(tradeRateReqDTO, withdrawRateDTO);
        GenericDTO genericDTO = new GenericDTO();
        genericDTO.setBody(tradeRateReqDTO);
        GenericRspDTO genericRspDTO = tfmServerClient.tradeRate(genericDTO);
        if(JudgeUtils.isNull(genericRspDTO.getBody())){
            LemonException.throwBusinessException("PWM30011");
        }
        WithdrawRateResultDTO withdrawRateResultDTO = new WithdrawRateResultDTO();
        BeanUtils.copyProperties(withdrawRateResultDTO, genericRspDTO.getBody());
        genericRspDTO.setBody(withdrawRateResultDTO);
        return genericRspDTO;
    }

	/**
	 * 查询提现银行
	 * 
	 * @return
	 */
	@Override
	public List<WithdrawBankRspDTO> queryBank(GenericDTO genericDTO) {

		// List<WithdrawCardInfoDO> withdrawCardInfoDO =
		// withdrawCardInfoDao.query();
		// 查询路由
		RouteRspDTO routeRspDTO = routeClient.queryEffCapOrgInfo(CorpBusTyp.WITHDRAW, CorpBusSubTyp.PER_WITHDRAW)
				.getBody();
		List<WithdrawBankRspDTO> withdrawBankRspDTO = new ArrayList();
		List<RouteRspDTO.RouteDTO> list = null;
		if (JudgeUtils.isNotNull(routeRspDTO)) {
			list = routeRspDTO.getList();

			for (RouteRspDTO.RouteDTO routeDTO : list) {
				WithdrawBankRspDTO withdrawBankRspDTO1 = new WithdrawBankRspDTO();
				withdrawBankRspDTO1.setCapCorg(routeDTO.getCrdCorpOrg());
				withdrawBankRspDTO1.setBankName(routeDTO.getCorpOrgNm());
				withdrawBankRspDTO1.setCardAcType(routeDTO.getCrdAcTyp());
				withdrawBankRspDTO.add(withdrawBankRspDTO1);
			}
		}

		return withdrawBankRspDTO;
	}

    /**
     * 添加提现银行卡
     * @param genericWithdrawCardBindDTO
     * @return
     */
    @Override
    public GenericRspDTO addCard(GenericDTO<WithdrawCardBindDTO> genericWithdrawCardBindDTO) {
        WithdrawCardBindDTO withdrawCardBindDTO = genericWithdrawCardBindDTO.getBody();
        WithdrawCardBindDO withdrawCardBindDO = new WithdrawCardBindDO();
        BeanUtils.copyProperties(withdrawCardBindDO, withdrawCardBindDTO);
        String cardNo = withdrawCardBindDO.getCardNo().trim();
        String cardNoEnc = null;
        //加密银行卡
        CommonEncryptReqDTO commonEncryptReqDTO = new CommonEncryptReqDTO();
        commonEncryptReqDTO.setData(cardNo);
        commonEncryptReqDTO.setType("encrypt");
        GenericDTO genericDTO = new GenericDTO();
        genericDTO.setBody(commonEncryptReqDTO);
        //银行卡加密
        GenericRspDTO<CommonEncryptRspDTO> genericRspDTO = cmmServerClient.encrypt(genericDTO);
        CommonEncryptRspDTO commonEncryptRspDTO = genericRspDTO.getBody();
        if(JudgeUtils.isNotNull(commonEncryptRspDTO)) {
            cardNoEnc = commonEncryptRspDTO.getData();
        }
        String userId = withdrawCardBindDTO.getUserId();
        Integer withdrawCardBindDO1 = withdrawCardBindDao.queryCount(cardNoEnc, userId);
        //初始化需要返回的卡信息
        WithdrawCardQueryDTO withdrawCardQueryDTO = new WithdrawCardQueryDTO();
        //判断提现银行卡是否存在
        if(Integer.compare(0,withdrawCardBindDO1)<0){
            LemonException.throwBusinessException("PWM30012");
            //判断提现银行卡状态是否失效
//            if(JudgeUtils.equals(PwmConstants.WITHDRAW_CARD_STAT_EFF,withdrawCardBindDO1.getCardStatus())){
//                LemonException.throwBusinessException("PWM30012");
//            }
//            if(JudgeUtils.equals(PwmConstants.WITHDRAW_CARD_STAT_FAIL,withdrawCardBindDO1.getCardStatus())){
//                //失效则更新状态
//                WithdrawCardBindDO withdrawCardBindDO2 = new WithdrawCardBindDO();
//                withdrawCardBindDO2.setCardNo(cardNoEnc);
//                withdrawCardBindDO2.setCardStatus(PwmConstants.WITHDRAW_CARD_STAT_EFF);
//                withdrawCardBindDO2.setEftTm(DateTimeUtils.getCurrentLocalDateTime());
//                withdrawCardBindDO2.setFailTm("");
//                withdrawCardBindDO2.setCardId(withdrawCardBindDO1.getCardId());
//                withdrawOrderTransactionalService.updateCard(withdrawCardBindDO2);
//                BeanUtils.copyProperties(withdrawCardQueryDTO, withdrawCardBindDO1);
//            }
        }else{
            //提现银行卡不存在，则填充数据，添加入库
            String ymd= DateTimeUtils.getCurrentDateStr();
            String orderNo= IdGenUtils.generateId(PwmConstants.W_CRD_GEN_PRE+ymd,15);
            withdrawCardBindDO.setCardId(ymd+orderNo);
            withdrawCardBindDO.setEftTm(DateTimeUtils.getCurrentLocalDateTime());
            withdrawCardBindDO.setCardNoLast(cardNo.substring(cardNo.length()-4));
            withdrawCardBindDO.setCardNo(cardNoEnc);
            withdrawOrderTransactionalService.addCard(withdrawCardBindDO);
            BeanUtils.copyProperties(withdrawCardQueryDTO, withdrawCardBindDO);
        }
        //更新成功则返回改卡信息
        return GenericRspDTO.newSuccessInstance(withdrawCardQueryDTO);
    }

    /**
     * 查询已添加提现银行卡
     * @param genericDTO
     * @return
     */
    @Override
    public List<WithdrawCardQueryDTO> queryCard(GenericDTO genericDTO) {
        List<WithdrawCardBindDO> withdrawCardBindDO = withdrawCardBindDao.queryCardList(LemonUtils.getUserId());
        List<WithdrawCardQueryDTO> withdrawCardQueryDTO = new ArrayList();
        for(WithdrawCardBindDO withdrawCardBindDO1 : withdrawCardBindDO) {
            WithdrawCardQueryDTO withdrawCardQueryDTO1 = new WithdrawCardQueryDTO();
            BeanUtils.copyProperties(withdrawCardQueryDTO1, withdrawCardBindDO1);
            withdrawCardQueryDTO.add(withdrawCardQueryDTO1);
        }
        return withdrawCardQueryDTO;
    }

    /**
     * 删除提现银行卡
     * @param genericWithdrawCardDelDTO
     * @return
     */
    @Override

    public GenericRspDTO<WithdrawCardDelRspDTO> delCard(GenericDTO<WithdrawCardDelDTO> genericWithdrawCardDelDTO) {
        WithdrawCardDelDTO withdrawCardDelDTO = genericWithdrawCardDelDTO.getBody();
        WithdrawCardBindDO withdrawCardBindDO = withdrawCardBindDao.get(withdrawCardDelDTO.getCardId());
        if(JudgeUtils.isNull(withdrawCardBindDO)){
            LemonException.throwBusinessException("PWM30013");
        }
        if(JudgeUtils.isNotNull(withdrawCardBindDO) && JudgeUtils.equals(PwmConstants.WITHDRAW_CARD_STAT_FAIL, withdrawCardBindDO.getCardStatus())){
            LemonException.throwBusinessException("PWM30014");
        }
        WithdrawCardBindDO withdrawCardBindDO1 = new WithdrawCardBindDO();
        withdrawCardBindDO1.setCardId(withdrawCardBindDO.getCardId());
        withdrawCardBindDO1.setCardStatus(PwmConstants.WITHDRAW_CARD_STAT_FAIL);
        withdrawCardBindDO1.setFailTm(DateTimeUtils.getCurrentDateTimeStr("yyyy-MM-dd HH:mm:ss"));
        withdrawOrderTransactionalService.updateCard(withdrawCardBindDO1);
        withdrawCardBindDO1.setCardNoLast(withdrawCardBindDO.getCardNoLast());
        WithdrawCardDelRspDTO withdrawCardDelRspDTO = new WithdrawCardDelRspDTO();
        BeanUtils.copyProperties(withdrawCardDelRspDTO, withdrawCardBindDO1);
        return GenericRspDTO.newSuccessInstance(withdrawCardDelRspDTO);
    }

    /**
     * 消息推送
     *
     * @param withdrawOrderDO
     */
    public void sendMessage(WithdrawOrderDO withdrawOrderDO, String cardNoLast) {

        logger.info("message send to User : " + withdrawOrderDO.getUserId());

        String language = LemonUtils.getLocale().getLanguage();
        if (JudgeUtils.isBlank(language)) {
            language = "en";
            logger.error("language  ======  " + language);
        }

        GenericDTO<MessageSendReqDTO> messageReqDTO = new GenericDTO<MessageSendReqDTO>();
        MessageSendReqDTO messageReq = new MessageSendReqDTO();
        messageReq.setUserId(withdrawOrderDO.getUserId());
        messageReq.setMessageLanguage(language);
        Map<String, String> map = new HashMap<>();
        if(JudgeUtils.equals("",cardNoLast)) {
            messageReq.setMessageTemplateId(PwmConstants.WITHDRAW_FAIL_TEMPL);
            map.put("amount",withdrawOrderDO.getWcActAmt().toString());
            map.put("date",DateTimeUtils.formatLocalDate(withdrawOrderDO.getOrderTm().toLocalDate(), "yyyy-MM-dd"));
        }else {
            messageReq.setMessageTemplateId(PwmConstants.WITHDRAW_SUCC_TEMPL);
            map.put("amount", withdrawOrderDO.getWcActAmt().toString());
            if(JudgeUtils.isNotBlank(cardNoLast)) {
                map.put("cardNoLast", cardNoLast);
            }else {
                map.put("cardNoLast", "");
            }
            map.put("date",DateTimeUtils.formatLocalDate(withdrawOrderDO.getOrderTm().toLocalDate(), "yyyy-MM-dd"));
        }
        messageReq.setReplaceFieldMap(map);
        messageReqDTO.setBody(messageReq);
        GenericRspDTO<NoBody> rspDto = cmmServerClient.messageSend(messageReqDTO);
        if(JudgeUtils.isNotSuccess(rspDto.getMsgCd())){
            logger.error("个人提现推送订单号："+ withdrawOrderDO.getOrderNo()+"信息失败。");
        }
    }

    /**
     * 国际化商品描述信息
     * @param capCorg
     * @param args
     * @return
     */
    public String getViewOrderInfo(String capCorg,Object[] args){
        try{
            String key="view.withdraw."+capCorg;
            return localeMessageSource.getMessage(key,args);
        }catch (Exception e){

        }
        return  null;
    }

    /**
     * 提现对账差错处理
     * @param genericWithdrawErrorHandleDTO
     * @return
     */
    @Override
    public GenericRspDTO withdrawErrorHandler(GenericDTO<WithdrawErrorHandleDTO> genericWithdrawErrorHandleDTO) {
        WithdrawErrorHandleDTO withdrawErrorHandleDTO = genericWithdrawErrorHandleDTO.getBody();
        //查询订单是否存在
        WithdrawOrderDO queryWithdrawOrderDO = withdrawOrderTransactionalService.query(withdrawErrorHandleDTO.getOrderNo());
        WithdrawOrderDO withdrawOrderDO2 = new WithdrawOrderDO();
        withdrawOrderDO2.setOrderNo(withdrawErrorHandleDTO.getOrderNo());
        //设置需要修改的订单状态
        //判断订单状态为'S1'，则修改订单成功时间
        if(JudgeUtils.equals("0703",withdrawErrorHandleDTO.getChkBusSubTyp())){
            withdrawOrderDO2.setOrderStatus(PwmConstants.WITHDRAW_ORD_S1);
            withdrawOrderDO2.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
            withdrawOrderDO2.setRspSuccTm(DateTimeUtils.getCurrentLocalDateTime());
        }
        if(JudgeUtils.equals("0704",withdrawErrorHandleDTO.getChkBusSubTyp())){
            withdrawOrderDO2.setOrderStatus(PwmConstants.WITHDRAW_ORD_F1);
        }
        //若订单状态为'F1'失败，则做账，并把手续费退了
        if(JudgeUtils.equals(PwmConstants.WITHDRAW_ORD_F1, withdrawOrderDO2.getOrderStatus())){
            //账务处理
           /* String userId = queryWithdrawOrderDO.getUserId();
            GenericRspDTO<AccDataListDTO> userAccAcsFail=UserAccAcsFail(queryWithdrawOrderDO,userId);
            if(JudgeUtils.isNotSuccess(userAccAcsFail.getMsgCd())){
         	   logger.info("提现对账差错处理账务失败");
            }*/
        }
        withdrawOrderDO2.setAcTm(LemonUtils.getAccDate());
        //若更新提现单据状态及相关信息
        int num = withdrawOrderTransactionalService.updateOrder(withdrawOrderDO2);
        if(num != 1 ){
            LemonException.throwBusinessException("PWM20005");
        }else {
            //同步账单数据
            UpdateUserBillDTO updateUserBillDTO = new UpdateUserBillDTO();
            BeanUtils.copyProperties(updateUserBillDTO, withdrawOrderDO2);
            billSyncHandler.updateBill(updateUserBillDTO);
        }
        return GenericRspDTO.newSuccessInstance();
    }

 

    /**
     * 校验支付密码
     * @param userId
     * @param password
     * @param check
     * @param validateRandom
     */
    protected void checkPayPassword(String userId,String password,boolean check,String validateRandom){
        if(check){
            if(StringUtils.isBlank(password)){
                LemonException.throwBusinessException("CSH20076");
            }
            CheckPayPwdDTO checkPayPwdDTO=new CheckPayPwdDTO();

            if(StringUtils.isBlank(userId)){
                checkPayPwdDTO.setUserId(LemonUtils.getUserId());
            }else{
                checkPayPwdDTO.setUserId(userId);
            }
            checkPayPwdDTO.setPayPwdRandom(validateRandom);
            checkPayPwdDTO.setPayPwd(password);
            GenericDTO ckPwdDto=new GenericDTO();
            ckPwdDto.setBody(checkPayPwdDTO);
            GenericRspDTO rspDTO=userAuthenticationClient.checkPayPwd(ckPwdDto);

            if(JudgeUtils.isNotSuccess(rspDTO.getMsgCd())){
                logger.error("验证支付密码失败："+rspDTO.getMsgCd());
                if(rspDTO.getMsgCd().startsWith("SYS"))
                {
                    LemonException.throwBusinessException("PWM30004");
                }else{
                    LemonException.throwBusinessException(rspDTO.getMsgCd());
                }

            }
        }
    }

   /* *//**
     * 根据用户id检查用户状态
     * @param userId
     * @throws LemonException
     *//*
    private void checkUserStatus(String userId) throws LemonException {
        RiskCheckUserStatusReqDTO reqDTO = new RiskCheckUserStatusReqDTO();
        reqDTO.setId(userId);
        reqDTO.setIdTyp("01"); // 证件类型 01用户号/商户号 02银行卡 03身份证
        reqDTO.setTxTyp("04"); // 交易类型
        GenericRspDTO<NoBody> rspDTO = riskCheckClient.checkUserStatus(reqDTO);
        if (JudgeUtils.isNotSuccess(rspDTO.getMsgCd())) {
            // 用户状态检查失败
            logger.info("user :" + userId + " status check failure!");
            LemonException.throwBusinessException(rspDTO.getMsgCd());
        }
    }*/

/*    *//**
     * 根据用户手机号查询用户信息
     * @param mblNo
     * @return
     * @throws LemonException
     *//*
    private UserBasicInfDTO getUserBasicInfo(String mblNo) throws LemonException {
        GenericRspDTO<UserBasicInfDTO> rspDTO = userBasicInfClient.queryUserByLoginId(mblNo);
        if (JudgeUtils.isNotSuccess(rspDTO.getMsgCd())) {
            // 用户基本信息查询失败
            logger.info("mblNo :" + mblNo + " get basic info failure!");
            LemonException.throwBusinessException(rspDTO.getMsgCd());
        }
        return rspDTO.getBody();
    }*/

    private TradeFeeCaculateRspDTO caculateHallWithdrawFee(BigDecimal orderAmt){
        if(JudgeUtils.isNull(orderAmt)){
            throw new LemonException("PWM10006");
        }
        TradeFeeCaculateReqDTO tradeFeeCaculateReqDTO = new TradeFeeCaculateReqDTO();
        tradeFeeCaculateReqDTO.setBusType(PwmConstants.BUS_TYPE_WITHDRAW_HALL);
        tradeFeeCaculateReqDTO.setCcy(PwmConstants.HALL_PAY_CCY);
        tradeFeeCaculateReqDTO.setTradeAmt(orderAmt);

        GenericDTO<TradeFeeCaculateReqDTO> genericTradeFeeCaculateReqDTO = new GenericDTO<>();
        genericTradeFeeCaculateReqDTO.setBody(tradeFeeCaculateReqDTO);

        GenericRspDTO<TradeFeeCaculateRspDTO> tradeFeeCaculateResult = fmServerClient.tradeFeeCaculate(genericTradeFeeCaculateReqDTO);

        if(JudgeUtils.isNotSuccess(tradeFeeCaculateResult.getMsgCd())){
            LemonException.throwBusinessException(tradeFeeCaculateResult.getMsgCd());
        }
        TradeFeeCaculateRspDTO tradeFeeCaculateRspDTO = tradeFeeCaculateResult.getBody();
        return tradeFeeCaculateRspDTO;
    }

  

    /**
     * 同步账单
     * @param merchantId
     * @param merchantName
     * @param desc
     * @param withdrawOrderDO
     */
    public void syncHallWithdrawBil(String merchantId,String merchantName,String desc,WithdrawOrderDO withdrawOrderDO){
        CreateUserBillDTO createUserBillDTO = new CreateUserBillDTO();
        BeanUtils.copyProperties(createUserBillDTO, withdrawOrderDO);
        createUserBillDTO.setTxTm(withdrawOrderDO.getOrderTm());
        createUserBillDTO.setOrderChannel(withdrawOrderDO.getBusCnl());
        createUserBillDTO.setPayerId(withdrawOrderDO.getUserId());
        createUserBillDTO.setOrderAmt(withdrawOrderDO.getWcApplyAmt());
        createUserBillDTO.setFee(withdrawOrderDO.getFeeAmt());
        createUserBillDTO.setPayeeId(merchantId);
        createUserBillDTO.setOrderStatus(withdrawOrderDO.getOrderStatus());
        createUserBillDTO.setGoodsInfo(desc);
        createUserBillDTO.setMercName(merchantName);
        billSyncHandler.createBill(createUserBillDTO);
    }

    /**
     * 根据订单状态更新提现订单数据
     * @param orderNo
     * @param orderStatus
     */
    public WithdrawOrderDO updateWithdraw(String orderNo,String orderStatus){
        WithdrawOrderDO updateOrderDO = new WithdrawOrderDO();
        updateOrderDO.setOrderNo(orderNo);
        updateOrderDO.setOrderStatus(orderStatus);
        updateOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
        updateOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
        updateOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
        this.withdrawOrderDao.update(updateOrderDO);
        return updateOrderDO;
    }
  
    
	/**
	 * 用户提现账务处理 UserAccAcsDeal 账务处理 crossAcNo对手方账户 acNo 主账户
	 */
    private GenericRspDTO<AccDataListDTO> UserAccAcsDeal(WithdrawOrderDO withdrawOrderDO,String acNo){
    	GenericRspDTO<AccDataListDTO> GenericRspDTO=new GenericRspDTO<>();
    	try {
			GenericDTO<AccDataListDTO> genericReqDTO = new GenericDTO<>();
			AccDataListDTO accDataListDTO=new  AccDataListDTO();
			List<Map<String,Object>> accDataMapList=new ArrayList<>();
			String acmJrnNo = IdGenUtils.generateIdWithDate(PwmConstants.W_ORD_GEN_PRE, 14);
			acmJrnNo = withdrawOrderDO.getBusType() + acmJrnNo;
			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
			String actDate = df.format(withdrawOrderDO.getAcTm());
			// 提现总金额
			BigDecimal amount = withdrawOrderDO.getWcTotalAmt();
				//借：个人账户
			Map<String, Object> accDataMap1 = AcmComponent.getUserAccDataListDTO(AccConstants.USR_ACC_DR, acNo, AccConstants.AC_TYP_USR_CASH,actDate, AccConstants.CAP_TYP_CASH, amount.floatValue(),
					AccConstants.DC_FLG_D, "1401","01", "4040004", "个人账户", amount.floatValue(),"个人提现",
					"04", withdrawOrderDO.getOrderNo(), "1", AccConstants.NOT_TX_FLG_NOT_TX);
			accDataMapList.add(accDataMap1);
				
				//贷：应付账款-待结算款-用户提现
				Map<String, Object> accDataMap2 = AcmComponent.getInnerAccDataListDTO(AccConstants.INNER_ACC_CR,"4010002", "应付账款-待结算款-用户提现", "401",actDate, AccConstants.CAP_TYP_CASH ,amount.floatValue(), AccConstants.DC_FLG_C,
						"1401","01",acNo, "个人账户", amount.floatValue(), "04",
						withdrawOrderDO.getOrderNo(),"提现","2", AccConstants.NOT_TX_FLG_NOT_TX);	
				accDataMapList.add(accDataMap2);
				
				// 有手续费需要做手续费的账务
				if (withdrawOrderDO.getFeeAmt().compareTo(BigDecimal.valueOf(0)) > 0) {
					// 借：个人账户
					Map<String, Object> accDataMap3 = AcmComponent.getUserAccDataListDTO(AccConstants.USR_ACC_DR, acNo,
							AccConstants.AC_TYP_USR_CASH, actDate, AccConstants.CAP_TYP_CASH,withdrawOrderDO.getFeeAmt().floatValue(),
							AccConstants.DC_FLG_D, "1401", "01", "4040004", "个人账户", withdrawOrderDO.getFeeAmt().floatValue(), "个人提现",
							"04", withdrawOrderDO.getOrderNo(), "3", AccConstants.NOT_TX_FLG_NOT_TX);
					accDataMapList.add(accDataMap3);

					// 贷：其他应付款-暂收款项-手续费
					Map<String, Object> accDataMap4 = AcmComponent.getInnerAccDataListDTO(AccConstants.INNER_ACC_CR, "4040004",
							"其他应付款-暂收款项-手续费", "404", actDate, AccConstants.CAP_TYP_CASH, withdrawOrderDO.getFeeAmt().floatValue(),
							AccConstants.DC_FLG_C, "1401", "01", acNo, "其他应付款-暂收款项-手续费",withdrawOrderDO.getFeeAmt().floatValue(),
							"04", withdrawOrderDO.getOrderNo(), "个人提现", "4", AccConstants.NOT_TX_FLG_NOT_TX);
					accDataMapList.add(accDataMap4);
				}	
			System.out.println("账务请求报文："+accDataMapList);
			logger.info("用户转账账务处理请求报文:"+accDataMapList);
			accDataListDTO.setAccDataMapList(accDataMapList);
			accDataListDTO.setMainTxTyp("14");
			accDataListDTO.setMiniTxTyp("1401");
			accDataListDTO.setRvsTxFlg(AccConstants.RVS_TX_FLG_N);//正常交易
			genericReqDTO.setBody(accDataListDTO);
			GenericRspDTO=xxAccService.xxAccHandle(genericReqDTO);
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return GenericRspDTO;
	}
    
    
    
   /* *//**
     * 用户提现失败账务处理
     * UserAccAcsDeal 账务处理
     * crossAcNo对手方账户
     * acNo  主账户
     *//*
    private GenericRspDTO<AccDataListDTO> UserAccAcsFail(WithdrawOrderDO withdrawOrderDO,String acNo){
    	GenericRspDTO<AccDataListDTO> GenericRspDTO=new GenericRspDTO<>();
    	try {
			GenericDTO<AccDataListDTO> genericReqDTO = new GenericDTO<>();
			AccDataListDTO accDataListDTO=new  AccDataListDTO();
			List<Map<String,Object>> accDataMapList=new ArrayList<>();
			String acmJrnNo = IdGenUtils.generateIdWithDate(PwmConstants.W_ORD_GEN_PRE, 14);
			acmJrnNo = withdrawOrderDO.getBusType() + acmJrnNo;
			DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMdd");
			String actDate = df.format(withdrawOrderDO.getAcTm());
			// 提现总金额
			BigDecimal amount = withdrawOrderDO.getWcTotalAmt();
				//待：个人账户
			Map<String, Object> accDataMap1 = AcmComponent.getUserAccDataListDTO(AccConstants.USR_ACC_CR, acNo, AccConstants.AC_TYP_USR_CASH,actDate, AccConstants.CAP_TYP_CASH, amount.floatValue(),
					AccConstants.DC_FLG_C, "1401","14", "2181010002", "0401", amount.floatValue(),"个人提现",
					"04", withdrawOrderDO.getOrderNo(), "1", AccConstants.NOT_TX_FLG_NOT_TX);
			accDataMapList.add(accDataMap1);
				
				//借：应付账款-待结算款-用户提现
				Map<String, Object> accDataMap2 = AcmComponent.getInnerAccDataListDTO(AccConstants.INNER_ACC_DR,"4010002", "应付账款-待结算款-用户提现", "401",actDate, AccConstants.CAP_TYP_CASH ,amount.floatValue(), AccConstants.DC_FLG_D,
						"1401","14","212101", "应付账款-待结算款-用户提现", amount.floatValue(), "04",
						withdrawOrderDO.getOrderNo(),"提现","2", AccConstants.NOT_TX_FLG_NOT_TX);	
				accDataMapList.add(accDataMap2);
				
				// 有手续费需要做手续费的账务
				if (withdrawOrderDO.getFeeAmt().compareTo(BigDecimal.valueOf(0)) > 0) {
					// 贷：个人账户
					Map<String, Object> accDataMap3 = AcmComponent.getUserAccDataListDTO(AccConstants.USR_ACC_CR, acNo,
							AccConstants.AC_TYP_USR_CASH, actDate, AccConstants.CAP_TYP_CASH,withdrawOrderDO.getFeeAmt().floatValue(),
							AccConstants.DC_FLG_C, "1401", "14", "2181010002", "0401", withdrawOrderDO.getFeeAmt().floatValue(), "个人提现",
							"04", withdrawOrderDO.getOrderNo(), "3", AccConstants.NOT_TX_FLG_NOT_TX);
					accDataMapList.add(accDataMap3);

					// 借：其他应付款-暂收款项-手续费
					Map<String, Object> accDataMap4 = AcmComponent.getInnerAccDataListDTO(AccConstants.INNER_ACC_DR, "4040004",
							"其他应付款-暂收款项-手续费", "404", actDate, AccConstants.CAP_TYP_CASH, withdrawOrderDO.getFeeAmt().floatValue(),
							AccConstants.DC_FLG_D, "1401", "14", "218104", "其他应付款-暂收款项-手续费",withdrawOrderDO.getFeeAmt().floatValue(),
							"04", withdrawOrderDO.getOrderNo(), "个人提现", "4", AccConstants.NOT_TX_FLG_NOT_TX);
					accDataMapList.add(accDataMap4);
				}	
			System.out.println("账务请求报文："+accDataMapList);
			logger.info("用户转账账务处理请求报文:"+accDataMapList);
			accDataListDTO.setAccDataMapList(accDataMapList);
			accDataListDTO.setMainTxTyp("14");
			accDataListDTO.setMiniTxTyp("1401");
			accDataListDTO.setRvsTxFlg(AccConstants.RVS_TX_FLG_N);//正常交易
			genericReqDTO.setBody(accDataListDTO);
			GenericRspDTO=xxAccService.xxAccHandle(genericReqDTO);
    	} catch (Exception e) {
			e.printStackTrace();
		}
		return GenericRspDTO;
	}*/
}
