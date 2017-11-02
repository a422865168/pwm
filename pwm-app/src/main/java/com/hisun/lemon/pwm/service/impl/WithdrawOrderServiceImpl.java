package com.hisun.lemon.pwm.service.impl;


import com.hisun.lemon.acm.client.AccountManagementClient;
import com.hisun.lemon.acm.constants.ACMConstants;
import com.hisun.lemon.acm.constants.CapTypEnum;
import com.hisun.lemon.acm.dto.AccountingReqDTO;
import com.hisun.lemon.acm.dto.QueryAcBalRspDTO;
import com.hisun.lemon.acm.dto.UserAccountDTO;
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
import com.hisun.lemon.cpo.client.RouteClient;
import com.hisun.lemon.cpo.client.WithdrawClient;
import com.hisun.lemon.cpo.dto.RouteRspDTO;
import com.hisun.lemon.cpo.dto.WithdrawReqDTO;
import com.hisun.lemon.cpo.enums.CorpBusSubTyp;
import com.hisun.lemon.cpo.enums.CorpBusTyp;
import com.hisun.lemon.csh.enums.AcItem;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.framework.i18n.LocaleMessageSource;
import com.hisun.lemon.framework.service.BaseService;
import com.hisun.lemon.framework.utils.IdGenUtils;
import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.pwm.component.AcmComponent;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dao.IWithdrawCardBindDao;
import com.hisun.lemon.pwm.dao.IWithdrawCardInfoDao;
import com.hisun.lemon.pwm.dao.IWithdrawOrderDao;
import com.hisun.lemon.pwm.dto.*;
import com.hisun.lemon.pwm.entity.WithdrawCardBindDO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import com.hisun.lemon.pwm.mq.BillSyncHandler;
import com.hisun.lemon.pwm.mq.PaymentHandler;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;
import com.hisun.lemon.rsm.Constants;
import com.hisun.lemon.rsm.client.RiskCheckClient;
import com.hisun.lemon.rsm.dto.req.checkstatus.RiskCheckUserStatusReqDTO;
import com.hisun.lemon.rsm.dto.req.riskJrn.JrnReqDTO;
import com.hisun.lemon.tfm.client.TfmServerClient;
import com.hisun.lemon.tfm.dto.*;
import com.hisun.lemon.urm.client.UserAuthenticationClient;
import com.hisun.lemon.urm.client.UserBasicInfClient;
import com.hisun.lemon.urm.dto.CheckPayPwdDTO;
import com.hisun.lemon.urm.dto.UserBasicInfDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Transactional
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
    private AccountManagementClient accountManagementClient;

    @Resource
    private RiskCheckClient riskCheckClient;

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

    /**
     * 生成提现订单
     * @param genericWithdrawDTO
     */
	@Override
	public WithdrawRspDTO createOrder(GenericDTO<WithdrawDTO> genericWithdrawDTO) {

        //生成订单号
        WithdrawDTO withdrawDTO = genericWithdrawDTO.getBody();

        String userId = withdrawDTO.getUserId();
        String ymd= DateTimeUtils.getCurrentDateStr();
        String orderNo= IdGenUtils.generateId(PwmConstants.W_ORD_GEN_PRE+ymd,15);
        orderNo = PwmConstants.BUS_TYPE_WITHDRAW_P+ymd+orderNo;

        GenericRspDTO genericRspDTO = null;
        GenericDTO genericDTO = new GenericDTO();

        //调用风控接口 校验用户是否为黑名单
        JrnReqDTO jrnReqDTO = new JrnReqDTO();
        jrnReqDTO.setTxSts(PwmConstants.WITHDRAW_ORD_W1);
        jrnReqDTO.setTxCnl("app");
        jrnReqDTO.setTxDate(DateTimeUtils.getCurrentLocalDate());
        jrnReqDTO.setTxTime(DateTimeUtils.getCurrentLocalTime());
        jrnReqDTO.setTxJrnNo(orderNo);
        jrnReqDTO.setTxOrdNo(orderNo);
        jrnReqDTO.setStlUserId(userId);
        jrnReqDTO.setStlUserTyp(Constants.ID_TYP_USER_NO);
        jrnReqDTO.setTxTyp(Constants.TX_TYP_WITHDRAW);
        jrnReqDTO.setPayTyp(Constants.PAY_TYP_ACCOUNT);
        jrnReqDTO.setTxAmt(withdrawDTO.getWcApplyAmt());
        jrnReqDTO.setCcy(withdrawDTO.getOrderCcy());
        genericRspDTO = riskCheckClient.riskControl(jrnReqDTO);
		if(JudgeUtils.isNull(genericRspDTO)){
		    LemonException.throwBusinessException("PWM30007");
        }
        //校验用户如为黑名单，则抛出异常信息
		if(JudgeUtils.equals("RSM30002", genericRspDTO.getMsgCd())){
			LemonException.throwBusinessException("PWM30001");
		}
        if(JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())){
            if(JudgeUtils.isNotBlank(genericRspDTO.getMsgInfo())) {
                LemonException.throwLemonException(genericRspDTO.getMsgCd(), genericRspDTO.getMsgInfo());
            }else{
                LemonException.throwBusinessException(genericRspDTO.getMsgCd());
            }
        }

        //填充查询手续费数据
        TradeFeeReqDTO tradeFeeReqDTO = new TradeFeeReqDTO();
        tradeFeeReqDTO.setBusOrderNo(orderNo);
        tradeFeeReqDTO.setBusOrderTime(DateTimeUtils.getCurrentLocalDateTime());
        tradeFeeReqDTO.setBusType(PwmConstants.BUS_TYPE_WITHDRAW_P);
        tradeFeeReqDTO.setCcy(withdrawDTO.getOrderCcy());
        tradeFeeReqDTO.setTradeAmt(withdrawDTO.getWcApplyAmt());
        tradeFeeReqDTO.setUserId(userId);
        genericDTO.setBody(tradeFeeReqDTO);

        //调用tfm接口查询手续费
        GenericRspDTO<TradeFeeRspDTO> tradeGenericRspDTO = tfmServerClient.tradeFee(genericDTO);
        if(JudgeUtils.isNull(tradeGenericRspDTO.getBody())){
            LemonException.throwBusinessException("PWM30008");
        }
        //手续费
        BigDecimal fee = tradeGenericRspDTO.getBody().getTradeFee();
        //总提现金额
        BigDecimal totalAmt = tradeGenericRspDTO.getBody().getTradeTotalAmt();
        //校验前端传入的手续费与计算出的手续费是否一致
        if(fee.compareTo(withdrawDTO.getFeeAmt())!=0){
            LemonException.throwBusinessException("PWM30006");
        }

        BigDecimal balance = new BigDecimal(0);
        //查询用户账户余额
        UserAccountDTO userAccountDTO = new UserAccountDTO();
        userAccountDTO.setUserId(userId);
        //调用账户接口，查询账户余额
        genericRspDTO = accountManagementClient.queryAcBal(userAccountDTO);
        List<QueryAcBalRspDTO> queryAcBalRspDTO = (List<QueryAcBalRspDTO>) genericRspDTO.getBody();
        if(JudgeUtils.isNull(queryAcBalRspDTO)){
            LemonException.throwBusinessException("PWM30009");
        }
        //判断资金类型为现金，则填充账户余额
        for(QueryAcBalRspDTO acBalRspDTO: queryAcBalRspDTO) {
            if(JudgeUtils.equals(CapTypEnum.CAP_TYP_CASH.getCapTyp(),acBalRspDTO.getCapTyp())) {
                balance = acBalRspDTO.getAcCurBal();
            }
        }
		//校验提现金额加手续费大于用户账户余额,则抛出异常
		if(totalAmt.compareTo(balance) > 0){
			LemonException.throwBusinessException("PWM30002");
		}

		//查询用户支付密码，校验支付密码，错误则抛异常
        CheckPayPwdDTO checkPayPwdDTO =new CheckPayPwdDTO();
        checkPayPwdDTO.setUserId(userId);
        checkPayPwdDTO.setPayPwd(withdrawDTO.getPayPassWord());
        checkPayPwdDTO.setPayPwdRandom(withdrawDTO.getPayPassWordRand());
        genericDTO.setBody(checkPayPwdDTO);
        genericRspDTO = userAuthenticationClient.checkPayPwd(genericDTO);
        if(JudgeUtils.isNull(genericRspDTO)){
            LemonException.throwBusinessException("PWM30010");
        }
		if(JudgeUtils.equals("URM30005", genericRspDTO.getMsgCd())){
			LemonException.throwBusinessException("PWM30004");
		}
        //查询支付密码错误次数是否超过5次
        if(JudgeUtils.equals("URM30011", genericRspDTO.getMsgCd())){
            LemonException.throwBusinessException("PWM30017");
        }
        if(JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())){
            if(JudgeUtils.isNotBlank(genericRspDTO.getMsgInfo())) {
                LemonException.throwLemonException(genericRspDTO.getMsgCd(), genericRspDTO.getMsgInfo());
            }else{
                LemonException.throwBusinessException(genericRspDTO.getMsgCd());
            }
        }

		//初始化提现订单数据
		WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
		BeanUtils.copyProperties(withdrawOrderDO, withdrawDTO);
		withdrawOrderDO.setOrderNo(orderNo);
		//交易类型 04提现
		withdrawOrderDO.setTxType(PwmConstants.TX_TYPE_WITHDRAW);
		//提现总金额=实际提现金额+手续费
        withdrawOrderDO.setWcTotalAmt(totalAmt);
        withdrawOrderDO.setWcActAmt(totalAmt.subtract(fee));
		//业务类型 0401个人提现
		withdrawOrderDO.setBusType(PwmConstants.BUS_TYPE_WITHDRAW_P);
		withdrawOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		withdrawOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		//用户姓名
		withdrawOrderDO.setUserName(withdrawDTO.getCardUserName());
		withdrawOrderDO.setCapCardName(withdrawDTO.getCardUserName());
		withdrawOrderDO.setOrderStatus(PwmConstants.WITHDRAW_ORD_W1);
        //生成提现单据
        withdrawOrderTransactionalService.createOrder(withdrawOrderDO);

        //调用资金能力的提现申请接口，等待结果通知
        WithdrawReqDTO withdrawReqDTO = new WithdrawReqDTO();
        withdrawReqDTO.setCapTyp("1");
        withdrawReqDTO.setCapCorg(withdrawOrderDO.getCapCorgNo());
        withdrawReqDTO.setCcy(withdrawOrderDO.getOrderCcy());
        withdrawReqDTO.setCorpBusTyp(CorpBusTyp.WITHDRAW);
        withdrawReqDTO.setCorpBusSubTyp(CorpBusSubTyp.PER_WITHDRAW);
        withdrawReqDTO.setWcAplAmt(withdrawOrderDO.getWcApplyAmt());
        withdrawReqDTO.setUserNm(withdrawOrderDO.getUserName());
        withdrawReqDTO.setCrdNoEnc(withdrawOrderDO.getCapCardNo());
        withdrawReqDTO.setCapCrdNm(withdrawOrderDO.getUserName());
        withdrawReqDTO.setReqOrdNo(withdrawOrderDO.getOrderNo());
        withdrawReqDTO.setReqOrdDt(DateTimeUtils.getCurrentLocalDate());
        withdrawReqDTO.setReqOrdTm(DateTimeUtils.getCurrentLocalTime());
        withdrawReqDTO.setMblNo(withdrawOrderDO.getNtfMbl());
        genericDTO.setBody(withdrawReqDTO);
        genericRspDTO = withdrawClient.createOrder(genericDTO);
        if(JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())){
            if(JudgeUtils.isNotBlank(genericRspDTO.getMsgInfo())) {
                LemonException.throwLemonException(genericRspDTO.getMsgCd(), genericRspDTO.getMsgInfo());
            }else{
                LemonException.throwBusinessException(genericRspDTO.getMsgCd());
            }
        }

        /**
         * 账务处理
         */
        //查询用户账号
        String acNo = accountManagementClient.queryAcNo(userId).getBody();
        if(JudgeUtils.isEmpty(acNo)){
            LemonException.throwBusinessException("PWM40006");
        }
        //流水号s
        String jrnNo = LemonUtils.getRequestId();
        //资金属性
        String balCapType = CapTypEnum.CAP_TYP_CASH.getCapTyp();
		//借：其他应付款-支付账户-现金账户  102
        AccountingReqDTO userAccountReqDTO = acmComponent.createAccountingReqDTO(orderNo, jrnNo, withdrawOrderDO.getBusType(),
                ACMConstants.ACCOUNTING_NOMARL, withdrawOrderDO.getWcTotalAmt(), acNo, ACMConstants.USER_AC_TYP, balCapType,
                ACMConstants.AC_D_FLG, PwmConstants.AC_ITEM_PAY_CASH_ACNO, null, null, null,
                null, null);
        //贷：应付账款-待结算款-批量付款  100
        AccountingReqDTO payItemReqDTO = acmComponent.createAccountingReqDTO(orderNo, jrnNo, withdrawOrderDO.getBusType(),
                ACMConstants.ACCOUNTING_NOMARL, withdrawOrderDO.getWcActAmt(), null, ACMConstants.ITM_AC_TYP, balCapType,
                ACMConstants.AC_C_FLG, PwmConstants.AC_ITEM_FOR_PAY, null, null, null,
                null, null);
        //贷：手续费收入-支付账户-提现  2（如果手续费等于0则不做账）
        if(fee.compareTo(BigDecimal.ZERO)>0) {
            AccountingReqDTO feeItemReqDTO = acmComponent.createAccountingReqDTO(orderNo, jrnNo, withdrawOrderDO.getBusType(),
                    ACMConstants.ACCOUNTING_NOMARL, withdrawOrderDO.getFeeAmt(), null, ACMConstants.ITM_AC_TYP, balCapType,
                    ACMConstants.AC_C_FLG, PwmConstants.AC_ITEM_FEE_PAY_WIDR, null, null, null,
                    null, null);
            //调用账务处理
            acmComponent.requestAc(userAccountReqDTO, payItemReqDTO, feeItemReqDTO);
        }else {
            //调用账务处理
            acmComponent.requestAc(userAccountReqDTO, payItemReqDTO);
        }

        //国际化订单信息
        WithdrawCardBindDO withdrawCardBindDO = withdrawCardBindDao.query(withdrawOrderDO.getCapCardNo(), userId, withdrawOrderDO.getCapCorgNo());
        Object[] args=new Object[]{withdrawCardBindDO.getCardNoLast(), withdrawOrderDO.getWcApplyAmt()};
        String desc=getViewOrderInfo(withdrawOrderDO.getCapCorgNo(),args);

		//同步账单数据
        CreateUserBillDTO createUserBillDTO = new CreateUserBillDTO();
        BeanUtils.copyProperties(createUserBillDTO, withdrawOrderDO);
        createUserBillDTO.setTxTm(withdrawOrderDO.getOrderTm());
        createUserBillDTO.setOrderChannel(withdrawOrderDO.getBusCnl());
        createUserBillDTO.setPayerId(withdrawOrderDO.getUserId());
        createUserBillDTO.setOrderAmt(withdrawOrderDO.getWcApplyAmt());
        createUserBillDTO.setFee(withdrawOrderDO.getFeeAmt());
        createUserBillDTO.setGoodsInfo(desc);
        billSyncHandler.createBill(createUserBillDTO);

        WithdrawRspDTO withdrawRspDTO = new WithdrawRspDTO();
        withdrawRspDTO.setOrderNo(withdrawOrderDO.getOrderNo());
        withdrawRspDTO.setOrderStatus(withdrawOrderDO.getOrderStatus());
        return withdrawRspDTO;
	}

    /**
     * 处理提现订单结果
     * @param genericWithdrawResultDTO
     */
	public WithdrawRspDTO completeOrder(GenericDTO<WithdrawResultDTO> genericWithdrawResultDTO) {

        WithdrawResultDTO withdrawResultDTO = genericWithdrawResultDTO.getBody();
        WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
        BeanUtils.copyProperties(withdrawOrderDO, withdrawResultDTO);
        // 校验订单是否存在
        WithdrawOrderDO queryWithdrawOrderDO = withdrawOrderTransactionalService.query(withdrawOrderDO.getOrderNo());
        withdrawOrderDO.setFeeAmt(queryWithdrawOrderDO.getFeeAmt());
        withdrawOrderDO.setWcTotalAmt(withdrawOrderDO.getWcActAmt().add(withdrawOrderDO.getFeeAmt()));
        //判断订单状态为'S1'，则修改订单成功时间
        if(JudgeUtils.equals(PwmConstants.WITHDRAW_ORD_S1, withdrawOrderDO.getOrderStatus())){
            withdrawOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
            withdrawOrderDO.setRspSuccTm(DateTimeUtils.getCurrentLocalDateTime());
        }
        //若订单状态为'F1'失败，则做账，并把手续费退了
        if(JudgeUtils.equals(PwmConstants.WITHDRAW_ORD_F1, withdrawOrderDO.getOrderStatus())){
            /**
             * 账务处理
             */
            String userId = queryWithdrawOrderDO.getUserId();
            GenericRspDTO genericRspDTO = accountManagementClient.queryAcNo(userId);
            if(JudgeUtils.isNull(genericRspDTO)){
                LemonException.throwBusinessException("PWM40006");
            }
            //查询用户账号
            String acNo = (String)genericRspDTO.getBody();
            //流水号
            String jrnNo = LemonUtils.getRequestId();
            //资金属性
            String balCapType = CapTypEnum.CAP_TYP_CASH.getCapTyp();
            //借：应付账款-待结算款-批量付款  100
            AccountingReqDTO payItemReqDTO = acmComponent.createAccountingReqDTO(withdrawOrderDO.getOrderNo(), jrnNo, PwmConstants.BUS_TYPE_WITHDRAW_P,
                    ACMConstants.ACCOUNTING_NOMARL, withdrawOrderDO.getWcActAmt(), null, ACMConstants.ITM_AC_TYP, balCapType,
                    ACMConstants.AC_D_FLG, PwmConstants.AC_ITEM_FOR_PAY, null, null, null,
                    null, null);

            //贷：其他应付款-支付账户-现金账户  102
            AccountingReqDTO userAccountReqDTO = acmComponent.createAccountingReqDTO(withdrawOrderDO.getOrderNo(), jrnNo, PwmConstants.BUS_TYPE_WITHDRAW_P,
                    ACMConstants.ACCOUNTING_NOMARL, withdrawOrderDO.getWcTotalAmt(), acNo, ACMConstants.USER_AC_TYP, balCapType,
                    ACMConstants.AC_C_FLG, PwmConstants.AC_ITEM_PAY_CASH_ACNO, null, null, null,
                    null, null);

            if(withdrawOrderDO.getFeeAmt().compareTo(BigDecimal.ZERO)==1) {
                //借：手续费收入-支付账户-提现  2（如果手续费等于0则不做账）
                AccountingReqDTO feeItemReqDTO = acmComponent.createAccountingReqDTO(withdrawOrderDO.getOrderNo(), jrnNo, PwmConstants.BUS_TYPE_WITHDRAW_P,
                        ACMConstants.ACCOUNTING_NOMARL, withdrawOrderDO.getFeeAmt(), null, ACMConstants.ITM_AC_TYP, balCapType,
                        ACMConstants.AC_D_FLG, PwmConstants.AC_ITEM_FEE_PAY_WIDR, null, null, null,
                        null, null);

                //调用账务处理
                acmComponent.requestAc(userAccountReqDTO, payItemReqDTO, feeItemReqDTO);
            }else {
                //调用账务处理
                acmComponent.requestAc(userAccountReqDTO, payItemReqDTO);
            }
        }
        withdrawOrderDO.setAcTm(LemonUtils.getAccDate());
        //若更新提现单据状态及相关信息
		int num = withdrawOrderTransactionalService.updateOrder(withdrawOrderDO);
		if(num != 1 ){
            LemonException.throwBusinessException("PWM20005");
        }else {
            //同步账单数据
            UpdateUserBillDTO updateUserBillDTO = new UpdateUserBillDTO();
            BeanUtils.copyProperties(updateUserBillDTO, withdrawOrderDO);
            billSyncHandler.updateBill(updateUserBillDTO);

            withdrawOrderDO.setUserId(queryWithdrawOrderDO.getUserId());
            //订单成功或者失败时，做消息推送
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
     * @return
     */
    @Override
    public List<WithdrawBankRspDTO> queryBank(GenericDTO genericDTO) {

        //List<WithdrawCardInfoDO> withdrawCardInfoDO = withdrawCardInfoDao.query();
        //查询路由
        RouteRspDTO routeRspDTO = routeClient.queryEffCapOrgInfo(CorpBusTyp.WITHDRAW, CorpBusSubTyp.PER_WITHDRAW).getBody();
        List<WithdrawBankRspDTO> withdrawBankRspDTO = new ArrayList();
        List<RouteRspDTO.RouteDTO> list = null;
        if(JudgeUtils.isNotNull(routeRspDTO)){
            list = routeRspDTO.getList();

            for(RouteRspDTO.RouteDTO routeDTO : list) {
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
        WithdrawCardBindDO withdrawCardBindDO1 = withdrawCardBindDao.query(cardNoEnc, userId, withdrawCardBindDTO.getCapCorg());
        //初始化需要返回的卡信息
        WithdrawCardQueryDTO withdrawCardQueryDTO = new WithdrawCardQueryDTO();
        //判断提现银行卡是否存在
        if(JudgeUtils.isNotNull(withdrawCardBindDO1)){
            //判断提现银行卡状态是否失效
            if(JudgeUtils.equals(PwmConstants.WITHDRAW_CARD_STAT_EFF,withdrawCardBindDO1.getCardStatus())){
                LemonException.throwBusinessException("PWM30012");
            }
            if(JudgeUtils.equals(PwmConstants.WITHDRAW_CARD_STAT_FAIL,withdrawCardBindDO1.getCardStatus())){
                //失效则更新状态
                WithdrawCardBindDO withdrawCardBindDO2 = new WithdrawCardBindDO();
                withdrawCardBindDO2.setCardNo(cardNoEnc);
                withdrawCardBindDO2.setCardStatus(PwmConstants.WITHDRAW_CARD_STAT_EFF);
                withdrawCardBindDO2.setEftTm(DateTimeUtils.getCurrentLocalDateTime());
                withdrawCardBindDO2.setFailTm("");
                withdrawCardBindDO2.setCardId(withdrawCardBindDO1.getCardId());
                withdrawOrderTransactionalService.updateCard(withdrawCardBindDO2);
                BeanUtils.copyProperties(withdrawCardQueryDTO, withdrawCardBindDO1);
            }
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
            map.put("date",DateTimeUtils.formatLocalDate(withdrawOrderDO.getAcTm(), "yyyy-MM-dd"));
        }else {
            messageReq.setMessageTemplateId(PwmConstants.WITHDRAW_SUCC_TEMPL);
            map.put("amount", withdrawOrderDO.getWcActAmt().toString());
            if(JudgeUtils.isNotBlank(cardNoLast)) {
                map.put("cardNoLast", cardNoLast);
            }else {
                map.put("cardNoLast", "");
            }
            map.put("date",DateTimeUtils.formatLocalDate(withdrawOrderDO.getAcTm(), "yyyy-MM-dd"));
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
            /**
             * 账务处理
             */
            String userId = queryWithdrawOrderDO.getUserId();
            GenericRspDTO genericRspDTO = accountManagementClient.queryAcNo(userId);
            if(JudgeUtils.isNull(genericRspDTO)){
                LemonException.throwBusinessException("PWM40006");
            }
            //查询用户账号
            String acNo = (String)genericRspDTO.getBody();
            //流水号
            String jrnNo = LemonUtils.getRequestId();
            //资金属性
            String balCapType = CapTypEnum.CAP_TYP_CASH.getCapTyp();
            //借：应付账款-待结算款-批量付款  100
            AccountingReqDTO payItemReqDTO = acmComponent.createAccountingReqDTO(queryWithdrawOrderDO.getOrderNo(), jrnNo, PwmConstants.BUS_TYPE_WITHDRAW_P,
                    ACMConstants.ACCOUNTING_NOMARL, queryWithdrawOrderDO.getWcActAmt(), null, ACMConstants.ITM_AC_TYP, balCapType,
                    ACMConstants.AC_D_FLG, PwmConstants.AC_ITEM_FOR_PAY, null, null, null,
                    null, null);

            //贷：其他应付款-支付账户-现金账户  102
            AccountingReqDTO userAccountReqDTO = acmComponent.createAccountingReqDTO(queryWithdrawOrderDO.getOrderNo(), jrnNo, PwmConstants.BUS_TYPE_WITHDRAW_P,
                    ACMConstants.ACCOUNTING_NOMARL, queryWithdrawOrderDO.getWcTotalAmt(), acNo, ACMConstants.USER_AC_TYP, balCapType,
                    ACMConstants.AC_C_FLG, PwmConstants.AC_ITEM_PAY_CASH_ACNO, null, null, null,
                    null, null);

            if(queryWithdrawOrderDO.getFeeAmt().compareTo(BigDecimal.ZERO)==1) {
                //借：手续费收入-支付账户-提现  2（如果手续费等于0则不做账）
                AccountingReqDTO feeItemReqDTO = acmComponent.createAccountingReqDTO(queryWithdrawOrderDO.getOrderNo(), jrnNo, PwmConstants.BUS_TYPE_WITHDRAW_P,
                        ACMConstants.ACCOUNTING_NOMARL, queryWithdrawOrderDO.getFeeAmt(), null, ACMConstants.ITM_AC_TYP, balCapType,
                        ACMConstants.AC_D_FLG, PwmConstants.AC_ITEM_FEE_PAY_WIDR, null, null, null,
                        null, null);

                //调用账务处理
                acmComponent.requestAc(userAccountReqDTO, payItemReqDTO, feeItemReqDTO);
            }else {
                //调用账务处理
                acmComponent.requestAc(userAccountReqDTO, payItemReqDTO);
            }
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
     * 个人营业厅提现处理
     * @param genericWithdrawResultDTO
     * @return
     */
    @Override
    public HallWithdrawResultDTO handleHallWithdraw(GenericDTO<HallWithdrawApplyDTO> genericWithdrawResultDTO) {
        HallWithdrawApplyDTO hallApplyDTO = genericWithdrawResultDTO.getBody();
        String merchantId = hallApplyDTO.getMerchantId();
        String passwd = hallApplyDTO.getPayPassword();
        String passwdRand = hallApplyDTO.getPayPwdRandom();
        String merchantName = hallApplyDTO.getMerchantName();
        String hallWithdrawOrderNo = hallApplyDTO.getBusOrderNo();
        String mblNo = hallApplyDTO.getMblNo();
        BigDecimal withdrawAmt = hallApplyDTO.getWithdrawAmt();
        BigDecimal feeAmt = hallApplyDTO.getFeeAmt();
        //1 查询用户信息，风控
        UserBasicInfDTO userBasicInfo = getUserBasicInfo(mblNo);
        String userId = userBasicInfo.getUserId();

        checkUserStatus(userId);

        //2 校验支付密码
        checkPayPassword(userId,passwd,true,passwdRand);

        //3 校验订单,金额,重复下单
        if(withdrawAmt.compareTo(new BigDecimal(0)) <= 0) {
            LemonException.throwBusinessException("PWM10029");
        }
        WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
        withdrawOrderDO.setRspOrderNo(hallWithdrawOrderNo);
        if(this.withdrawOrderDao.getListByCondition(withdrawOrderDO).stream().filter(wd -> wd != null).count() > 0){
            LemonException.throwBusinessException("PWM20021");
        }
        //4 手续费处理
        TradeFeeCaculateRspDTO tradeFeeCaculateRspDTO = caculateHallWithdrawFee(withdrawAmt);
        String feeFlag = tradeFeeCaculateRspDTO.getCalculateMode();
        BigDecimal fee = tradeFeeCaculateRspDTO.getTradeFee();

        //校验前端传入的手续费与计算出的手续费是否一致
        if(feeAmt.compareTo(fee) != 0){
            LemonException.throwBusinessException("PWM30006");
        }
        String ymd= DateTimeUtils.getCurrentDateStr();
        String orderNo= IdGenUtils.generateId(PwmConstants.W_ORD_GEN_PRE+ymd,15);
        orderNo = PwmConstants.BUS_TYPE_WITHDRAW_P+ymd+orderNo;
        //5 新建提现记录
        withdrawOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
        withdrawOrderDO.setAgrNo("");
        withdrawOrderDO.setBusCnl("HALL");
        withdrawOrderDO.setCapCardName("");
        withdrawOrderDO.setCapCardNo("");
        withdrawOrderDO.setFeeAmt(feeAmt);
        withdrawOrderDO.setNtfMbl(mblNo);
        withdrawOrderDO.setOrderCcy("USD");
        withdrawOrderDO.setOrderExpTm(DateTimeUtils.getCurrentLocalDateTime().minusMinutes(15));
        withdrawOrderDO.setOrderNo(orderNo);
        withdrawOrderDO.setCapCardType("");
        withdrawOrderDO.setTxType(PwmConstants.TX_TYPE_WITHDRAW);
        withdrawOrderDO.setPayUrgeFlg("");
        withdrawOrderDO.setWcType("11");
        withdrawOrderDO.setUserId(userId);

        BigDecimal acUserAmt = new BigDecimal(0);
        //根据扣费标识判断手续费
        if(JudgeUtils.equals(PwmConstants.FEE_IN_MODE,feeFlag)){
            acUserAmt = withdrawAmt.subtract(fee);
        }else if(JudgeUtils.equals(PwmConstants.FEE_EX_MODE,feeFlag)){
            acUserAmt = withdrawAmt.add(fee);
        }
        withdrawOrderDO.setWcActAmt(acUserAmt);
        Object[] args=new Object[]{withdrawAmt};
        String desc=getViewOrderInfo(PwmConstants.ORD_SYSCHANNEL_HALL,args);
        withdrawOrderDO.setWcRemark(desc);
        withdrawOrderDO.setWcTotalAmt(tradeFeeCaculateRspDTO.getTradeToalAmt());
        withdrawOrderDO.setCreateTime(DateTimeUtils.getCurrentLocalDateTime());
        withdrawOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
        withdrawOrderDO.setRspSuccTm(null);
        withdrawOrderDO.setUserName(userBasicInfo.getUsrNm());
        withdrawOrderDO.setRspOrderNo(hallWithdrawOrderNo);
        withdrawOrderDO.setOrderStatus(PwmConstants.WITHDRAW_ORD_W1);
        withdrawOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
        withdrawOrderDO.setBusType(PwmConstants.BUS_TYPE_WITHDRAW_HALL);
        withdrawOrderDO.setWcApplyAmt(withdrawAmt);

        //6 账务处理
        //个人账户查询
        String balCapType= CapTypEnum.CAP_TYP_CASH.getCapTyp();
        String balAcNo=acmComponent.getAcmAcNo(userId, balCapType);
        if(JudgeUtils.isBlank(balAcNo)){
            throw new LemonException("PWM20022");
        }
        String tmpJrnNo = LemonUtils.getRequestId();
        AccountingReqDTO withdrawFeeReqDTO=null;//充值手续费账务处理
         //借：其他应付款-支付账户-现金账户
        AccountingReqDTO userAccountReqDTO=acmComponent.createAccountingReqDTO(
                 withdrawOrderDO.getOrderNo(),
                 tmpJrnNo,
                 PwmConstants.TX_TYPE_RECHANGE,
                 ACMConstants.ACCOUNTING_NOMARL,
                 acUserAmt,
                 balAcNo,
                 ACMConstants.USER_AC_TYP,
                 balCapType,
                 ACMConstants.AC_D_FLG,
                 AcItem.O_BAL.getValue(),
                 null,
                 null,
                 null,
                 null,
                 "营业厅提现$"+withdrawOrderDO.getWcApplyAmt());

        //贷：应收账款-渠道充值-营业厅
        AccountingReqDTO cnlWithdrawHallReqDTO=acmComponent.createAccountingReqDTO(
                withdrawOrderDO.getOrderNo(),
                tmpJrnNo,
                PwmConstants.TX_TYPE_RECHANGE,
                ACMConstants.ACCOUNTING_NOMARL,
                withdrawAmt,
                balAcNo,
                ACMConstants.ITM_AC_TYP,
                balCapType,
                ACMConstants.AC_C_FLG,
                AcItem.I_CNL_HALL.getValue(),
                null,
                null,
                null,
                null,
                null);
        //如果充值手续费大于0那么做手续费账务
        if(JudgeUtils.isNotNull(fee) && fee.compareTo(BigDecimal.valueOf(0))>0){
            //贷：手续费收入-支付账户-提现
            withdrawFeeReqDTO=acmComponent.createAccountingReqDTO(withdrawOrderDO.getOrderNo(), tmpJrnNo, withdrawOrderDO.getTxType(),
                    ACMConstants.ACCOUNTING_NOMARL, fee, balAcNo, ACMConstants.ITM_AC_TYP, balCapType, ACMConstants.AC_C_FLG,
                    PwmConstants.AC_ITEM_FEE_PAY_WIDR, null, null, null, null, "营业厅提现手续费$"+fee);
        }

        acmComponent.requestAc(userAccountReqDTO,cnlWithdrawHallReqDTO,withdrawFeeReqDTO);
        //7 修改订单状态
        withdrawOrderDO.setOrderStatus(PwmConstants.WITHDRAW_ORD_S1);
        withdrawOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
        withdrawOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
        withdrawOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
        this.withdrawOrderDao.insert(withdrawOrderDO);
        //8 登记手续费
        if(fee.compareTo(BigDecimal.ZERO)>0){
            paymentHandler.registUserWithdrawFee(withdrawOrderDO);
        }

        //登记营业厅商户手续费
        paymentHandler.registMerChantWithdrawFee(withdrawOrderDO,merchantId);

        //9 账单同步
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

        //10 处理返回
        HallWithdrawResultDTO hallWithdrawResultDTO = new HallWithdrawResultDTO();
        hallWithdrawResultDTO.setBusOrderNo(hallWithdrawOrderNo);
        hallWithdrawResultDTO.setFeeAmt(String.valueOf(fee));
        hallWithdrawResultDTO.setOrderNo(withdrawOrderDO.getOrderNo());
        hallWithdrawResultDTO.setTradeDate(DateTimeUtils.getCurrentLocalDate());
        hallWithdrawResultDTO.setTradeTime(withdrawOrderDO.getOrderTm().toLocalTime());
        return hallWithdrawResultDTO;
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

    /**
     * 根据用户id检查用户状态
     * @param userId
     * @throws LemonException
     */
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
    }

    /**
     * 根据用户手机号查询用户信息
     * @param mblNo
     * @return
     * @throws LemonException
     */
    private UserBasicInfDTO getUserBasicInfo(String mblNo) throws LemonException {
        GenericRspDTO<UserBasicInfDTO> rspDTO = userBasicInfClient.queryUserByLoginId(mblNo);
        if (JudgeUtils.isNotSuccess(rspDTO.getMsgCd())) {
            // 用户基本信息查询失败
            logger.info("mblNo :" + mblNo + " get basic info failure!");
            LemonException.throwBusinessException(rspDTO.getMsgCd());
        }
        return rspDTO.getBody();
    }

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
}
