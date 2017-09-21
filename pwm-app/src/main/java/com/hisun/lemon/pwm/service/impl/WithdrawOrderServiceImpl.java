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
import com.hisun.lemon.cpo.client.WithdrawClient;
import com.hisun.lemon.cpo.dto.WithdrawReqDTO;
import com.hisun.lemon.cpo.enums.CorpBusSubTyp;
import com.hisun.lemon.cpo.enums.CorpBusTyp;
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
import com.hisun.lemon.pwm.dto.*;
import com.hisun.lemon.pwm.entity.WithdrawCardBindDO;
import com.hisun.lemon.pwm.entity.WithdrawCardInfoDO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import com.hisun.lemon.pwm.mq.BillSyncHandler;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;
import com.hisun.lemon.rsm.Constants;
import com.hisun.lemon.rsm.client.RiskCheckClient;
import com.hisun.lemon.rsm.dto.req.riskJrn.JrnReqDTO;
import com.hisun.lemon.tfm.client.TfmServerClient;
import com.hisun.lemon.tfm.dto.TradeFeeReqDTO;
import com.hisun.lemon.tfm.dto.TradeFeeRspDTO;
import com.hisun.lemon.tfm.dto.TradeRateReqDTO;
import com.hisun.lemon.urm.client.UserAuthenticationClient;
import com.hisun.lemon.urm.dto.CheckPayPwdDTO;
import org.aspectj.apache.bcel.classfile.Constant;
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
    /**
     * 生成提现订单
     * @param genericWithdrawDTO
     */
	@Override
	public WithdrawRspDTO createOrder(GenericDTO<WithdrawDTO> genericWithdrawDTO) {

        //生成订单号
        String ymd= DateTimeUtils.getCurrentDateStr();
        String orderNo= IdGenUtils.generateId(PwmConstants.W_ORD_GEN_PRE+ymd,15);
        orderNo = PwmConstants.BUS_TYPE_WITHDRAW_P+ymd+orderNo;
		WithdrawDTO withdrawDTO = genericWithdrawDTO.getBody();
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
        jrnReqDTO.setStlUserId(withdrawDTO.getUserId());
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
            LemonException.throwLemonException(genericRspDTO.getMsgCd(),genericRspDTO.getMsgInfo());
        }

        //填充查询手续费数据
        TradeFeeReqDTO tradeFeeReqDTO = new TradeFeeReqDTO();
        tradeFeeReqDTO.setBusOrderNo(orderNo);
        tradeFeeReqDTO.setBusOrderTime(DateTimeUtils.getCurrentLocalDateTime());
        tradeFeeReqDTO.setBusType(PwmConstants.BUS_TYPE_WITHDRAW_P);
        tradeFeeReqDTO.setCcy(withdrawDTO.getOrderCcy());
        tradeFeeReqDTO.setTradeAmt(withdrawDTO.getWcApplyAmt());
        tradeFeeReqDTO.setUserId(withdrawDTO.getUserId());
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
        userAccountDTO.setUserId(withdrawDTO.getUserId());
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
        checkPayPwdDTO.setUserId(withdrawDTO.getUserId());
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
        String acNo = accountManagementClient.queryAcNo(withdrawDTO.getUserId()).getBody();
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
        WithdrawCardBindDO withdrawCardBindDO = withdrawCardBindDao.query(withdrawOrderDO.getCapCardNo());
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
		withdrawOrderTransactionalService.updateOrder(withdrawOrderDO);

		//同步账单数据
        UpdateUserBillDTO updateUserBillDTO = new UpdateUserBillDTO();
        BeanUtils.copyProperties(updateUserBillDTO, withdrawOrderDO);
        billSyncHandler.updateBill(updateUserBillDTO);

        //订单成功或者失败时，做消息推送
        if(JudgeUtils.equals(PwmConstants.WITHDRAW_ORD_S1, withdrawOrderDO.getOrderStatus())) {
            withdrawOrderDO.setAcTm(withdrawResultDTO.getAcTm());
            sendMessage(withdrawOrderDO, withdrawResultDTO.getCardNoLast());
        }
        if(JudgeUtils.equals(PwmConstants.WITHDRAW_ORD_F1, withdrawOrderDO.getOrderStatus())) {
            withdrawOrderDO.setAcTm(withdrawResultDTO.getAcTm());
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
     * @return
     */
    @Override
    public List<WithdrawBankRspDTO> queryBank(GenericDTO genericDTO) {

        List<WithdrawCardInfoDO> withdrawCardInfoDO = withdrawCardInfoDao.query();
        List<WithdrawBankRspDTO> withdrawBankRspDTO = new ArrayList();
        for(WithdrawCardInfoDO withdrawCardInfoDO1 : withdrawCardInfoDO) {
            WithdrawBankRspDTO withdrawBankRspDTO1 = new WithdrawBankRspDTO();
            BeanUtils.copyProperties(withdrawBankRspDTO1, withdrawCardInfoDO1);
            withdrawBankRspDTO.add(withdrawBankRspDTO1);
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
        WithdrawCardBindDO withdrawCardBindDO1 = withdrawCardBindDao.query(cardNoEnc);
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
        withdrawOrderTransactionalService.updateOrder(withdrawOrderDO2);

        //同步账单数据
        UpdateUserBillDTO updateUserBillDTO = new UpdateUserBillDTO();
        BeanUtils.copyProperties(updateUserBillDTO, withdrawOrderDO2);
        billSyncHandler.updateBill(updateUserBillDTO);
        return GenericRspDTO.newSuccessInstance();
    }
}
