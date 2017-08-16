package com.hisun.lemon.pwm.service.impl;


import com.hisun.lemon.acm.client.AccountManagementClient;
import com.hisun.lemon.acm.constants.ACMConstants;
import com.hisun.lemon.acm.constants.CapTypEnum;
import com.hisun.lemon.acm.dto.AccountingReqDTO;
import com.hisun.lemon.acm.dto.QueryAcBalRspDTO;
import com.hisun.lemon.acm.dto.UserAccountDTO;
import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.BeanUtils;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.cpo.client.WithdrawClient;
import com.hisun.lemon.cpo.dto.WithdrawReqDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.framework.utils.IdGenUtils;
import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.jcommon.encrypt.EncryptionUtils;
import com.hisun.lemon.jcommon.exception.EncryptException;
import com.hisun.lemon.pwm.component.AcmComponent;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dao.IWithdrawCardBindDao;
import com.hisun.lemon.pwm.dao.IWithdrawCardInfoDao;
import com.hisun.lemon.pwm.dto.*;
import com.hisun.lemon.pwm.entity.WithdrawCardBindDO;
import com.hisun.lemon.pwm.entity.WithdrawCardInfoDO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;
import com.hisun.lemon.rsm.client.RiskCheckClient;
import com.hisun.lemon.rsm.dto.req.riskJrn.JrnReqDTO;
import com.hisun.lemon.tfm.client.TfmServerClient;
import com.hisun.lemon.tfm.dto.TradeFeeReqDTO;
import com.hisun.lemon.tfm.dto.TradeFeeRspDTO;
import com.hisun.lemon.tfm.dto.TradeRateReqDTO;
import com.hisun.lemon.urm.client.UserAuthenticationClient;
import com.hisun.lemon.urm.dto.CheckPayPwdDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;


@Transactional
@Service("withdrawOrderService")
public class WithdrawOrderServiceImpl implements IWithdrawOrderService {

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
    /**
     * 生成提现订单
     * @param genericWithdrawDTO
     */
	@Override
	public WithdrawRspDTO createOrder(GenericDTO<WithdrawDTO> genericWithdrawDTO) {

        //生成订单号
        String ymd= DateTimeUtils.getCurrentDateStr();
        String orderNo= IdGenUtils.generateId(PwmConstants.W_ORD_GEN_PRE+ymd,15);
		WithdrawDTO withdrawDTO = genericWithdrawDTO.getBody();
        GenericRspDTO genericRspDTO = null;
        GenericDTO genericDTO = new GenericDTO();

        //调用风控接口 校验用户是否为黑名单
        JrnReqDTO jrnReqDTO = new JrnReqDTO();
        jrnReqDTO.setTxSts(PwmConstants.WITHDRAW_ORD_W1);
        jrnReqDTO.setTxCnl("app");
        jrnReqDTO.setTxDate(DateTimeUtils.getCurrentLocalDate());
        jrnReqDTO.setTxTime(DateTimeUtils.getCurrentLocalTime());
        jrnReqDTO.setTxJrnNo(ymd+orderNo);
        jrnReqDTO.setTxOrdNo(ymd+orderNo);
        jrnReqDTO.setStlUserId(genericWithdrawDTO.getUserId());
        genericRspDTO = riskCheckClient.riskControl(jrnReqDTO);
		if(JudgeUtils.isNull(genericRspDTO)){
		    LemonException.throwBusinessException("PWM30007");
        }
        //校验用户如为黑名单，则抛出异常信息
		if(JudgeUtils.equals("在黑名单中", genericRspDTO.getMsgCd())){
			LemonException.throwBusinessException("PWM30001");
		}

        //填充查询手续费数据
        TradeFeeReqDTO tradeFeeReqDTO = new TradeFeeReqDTO();
        tradeFeeReqDTO.setBusOrderNo(ymd+orderNo);
        tradeFeeReqDTO.setBusOrderTime(DateTimeUtils.getCurrentLocalDateTime());
        tradeFeeReqDTO.setBusType(PwmConstants.BUS_TYPE_WITHDRAW_P);
        tradeFeeReqDTO.setCcy(withdrawDTO.getOrderCcy());
        tradeFeeReqDTO.setTradeAmt(withdrawDTO.getWcApplyAmt());
        tradeFeeReqDTO.setUserId(withdrawDTO.getUserId());
        genericDTO.setBody(tradeFeeReqDTO);

        //调用tfm接口查询手续费
        GenericRspDTO<TradeFeeRspDTO> tradeGenericRspDTO = tfmServerClient.tradeFee(genericDTO);
        if(JudgeUtils.isNull(tradeGenericRspDTO)){
            LemonException.throwBusinessException("PWM30008");
        }
        //手续费
        BigDecimal fee = tradeGenericRspDTO.getBody().getTradeFee();
        //总提现金额
        BigDecimal totalAmt = tradeGenericRspDTO.getBody().getTradeTotalAmt();
        //校验前端传入的手续费与计算出的手续费是否一致
        if(!JudgeUtils.equals(fee, withdrawDTO.getFeeAmt())){
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
            if(JudgeUtils.equals(CapTypEnum.CAP_TYP_CASH,acBalRspDTO.getCapTyp())) {
                balance = acBalRspDTO.getAcCurBal();
            }
        }
		//校验提现余额加手续费大于用户账户余额,则抛出异常
		if(balance.compareTo(totalAmt) == 1){
			LemonException.throwBusinessException("PWM30002");
		}

		//查询支付密码错误次数是否超过5次
		/*if("支付密码错误次数是否超过5".equals(withdrawOrderDO.getUserId())){
			LemonException.throwBusinessException("PWM30003");
		}*/

		//查询用户支付密码，校验支付密码，错误则抛异常
        CheckPayPwdDTO checkPayPwdDTO =new CheckPayPwdDTO();
        checkPayPwdDTO.setUserId(withdrawDTO.getUserId());
        checkPayPwdDTO.setPayPwd(withdrawDTO.getPayPassWord());
        genericDTO.setBody(checkPayPwdDTO);
        genericRspDTO = userAuthenticationClient.checkPayPwd(genericDTO);
        if(JudgeUtils.isNull(genericRspDTO)){
            LemonException.throwBusinessException("PWM30010");
        }
		if(!JudgeUtils.equals("用户账户支付密码", genericRspDTO.getMsgCd())){
			LemonException.throwBusinessException("PWM30004");
		}

		//初始化提现订单数据
		WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
		BeanUtils.copyProperties(withdrawOrderDO, withdrawDTO);
		withdrawOrderDO.setOrderNo(ymd+orderNo);
		//交易类型 04提现
		withdrawOrderDO.setTxType(PwmConstants.TX_TYPE_WITHDRAW);
		//提现总金额=实际提现金额+手续费
        withdrawOrderDO.setWcTotalAmt(withdrawOrderDO.getWcActAmt().add(withdrawOrderDO.getFeeAmt()));
		//业务类型 0401个人提现
		withdrawOrderDO.setBusType(PwmConstants.BUS_TYPE_WITHDRAW_P);
		withdrawOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		withdrawOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		//不确定用户名是查还是传
		withdrawOrderDO.setUserName("");
		withdrawOrderDO.setOrderStatus(PwmConstants.WITHDRAW_ORD_W1);
        /**
         * 账务处理
         */
        //查询用户账号
        String acNo = acmComponent.getAcmAcNo(withdrawDTO.getUserId(), ACMConstants.USER_AC_TYP);
        //流水号
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
        //贷：手续费收入-支付账户-提现  2
        AccountingReqDTO feeItemReqDTO = acmComponent.createAccountingReqDTO(orderNo, jrnNo, withdrawOrderDO.getBusType(),
                ACMConstants.ACCOUNTING_NOMARL, withdrawOrderDO.getFeeAmt(), null, ACMConstants.ITM_AC_TYP, balCapType,
                ACMConstants.AC_C_FLG, PwmConstants.AC_ITEM_FEE_PAY_WIDR, null, null, null,
                null, null);
        //调用账务处理
        acmComponent.requestAc(userAccountReqDTO, payItemReqDTO, feeItemReqDTO);

		//生成提现单据
		withdrawOrderTransactionalService.createOrder(withdrawOrderDO);

		//调用资金能力的提现申请接口，等待结果通知
		WithdrawReqDTO withdrawReqDTO = new WithdrawReqDTO();
		BeanUtils.copyProperties(withdrawReqDTO, withdrawOrderDO);
		genericDTO.setBody(withdrawReqDTO);
		withdrawClient.createOrder(genericDTO);
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
        }else{//若订单失败，则做账，并把手续费退了
            /**
             * 账务处理
             */
            //查询用户账号
            String acNo = acmComponent.getAcmAcNo(genericWithdrawResultDTO.getUserId(), ACMConstants.USER_AC_TYP);
            //流水号
            String jrnNo = LemonUtils.getRequestId();
            //资金属性
            String balCapType = CapTypEnum.CAP_TYP_CASH.getCapTyp();
            //借：应付账款-待结算款-批量付款  100
            AccountingReqDTO payItemReqDTO = acmComponent.createAccountingReqDTO(withdrawOrderDO.getOrderNo(), jrnNo, PwmConstants.BUS_TYPE_WITHDRAW_P,
                    ACMConstants.ACCOUNTING_NOMARL, withdrawOrderDO.getWcActAmt(), null, ACMConstants.ITM_AC_TYP, balCapType,
                    ACMConstants.AC_D_FLG, PwmConstants.AC_ITEM_FOR_PAY, null, null, null,
                    null, null);

            //借：手续费收入-支付账户-提现  2
            AccountingReqDTO feeItemReqDTO = acmComponent.createAccountingReqDTO(withdrawOrderDO.getOrderNo(), jrnNo, PwmConstants.BUS_TYPE_WITHDRAW_P,
                    ACMConstants.ACCOUNTING_NOMARL, withdrawOrderDO.getFeeAmt(), null, ACMConstants.ITM_AC_TYP, balCapType,
                    ACMConstants.AC_D_FLG, PwmConstants.AC_ITEM_FEE_PAY_WIDR, null, null, null,
                    null, null);

            //贷：其他应付款-支付账户-现金账户  102
            AccountingReqDTO userAccountReqDTO = acmComponent.createAccountingReqDTO(withdrawOrderDO.getOrderNo(), jrnNo, PwmConstants.BUS_TYPE_WITHDRAW_P,
                    ACMConstants.ACCOUNTING_NOMARL, withdrawOrderDO.getWcTotalAmt(), acNo, ACMConstants.USER_AC_TYP, balCapType,
                    ACMConstants.AC_C_FLG, PwmConstants.AC_ITEM_PAY_CASH_ACNO, null, null, null,
                    null, null);

            //调用账务处理
            acmComponent.requestAc(userAccountReqDTO, payItemReqDTO, feeItemReqDTO);
        }
        withdrawOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
        //若更新提现单据状态及相关信息
		withdrawOrderTransactionalService.updateOrder(withdrawOrderDO);
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
    public GenericRspDTO<WithdrawResultDTO> queryRate(WithdrawRateDTO withdrawRateDTO) {

        TradeRateReqDTO tradeRateReqDTO = new TradeRateReqDTO();
        BeanUtils.copyProperties(tradeRateReqDTO, withdrawRateDTO);
        GenericDTO genericDTO = new GenericDTO();
        genericDTO.setBody(tradeRateReqDTO);
        GenericRspDTO genericRspDTO = tfmServerClient.tradeRate(genericDTO);
        if(JudgeUtils.isNull(genericRspDTO)){
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
    public WithdrawBankRspDTO queryBank() {

        WithdrawCardInfoDO withdrawCardInfoDO = withdrawCardInfoDao.query();
        WithdrawBankRspDTO withdrawBankRspDTO = new WithdrawBankRspDTO();
        BeanUtils.copyProperties(withdrawBankRspDTO, withdrawCardInfoDO);
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
        try {
            //银行卡加密
            cardNoEnc = EncryptionUtils.encrypt(cardNo);
        } catch (EncryptException e){
            return GenericRspDTO.newInstance(e.getMsgCd(), withdrawCardBindDTO);
        }
        WithdrawCardBindDO withdrawCardBindDO1 = withdrawCardBindDao.query(cardNoEnc);
        //判断提现银行卡是否存在
        if(JudgeUtils.isNotNull(withdrawCardBindDO1)){
            //判断提现银行卡状态是否失效
            if(JudgeUtils.equals("1",withdrawCardBindDO1.getCardStatus())){
                LemonException.throwBusinessException("PWM30012");
            }
            if(JudgeUtils.equals("0",withdrawCardBindDO1.getCardStatus())){
                //失效则更新状态
                WithdrawCardBindDO withdrawCardBindDO2 = new WithdrawCardBindDO();
                withdrawCardBindDO2.setCardNo(cardNoEnc);
                withdrawCardBindDO2.setCardStatus("1");
                withdrawCardBindDO2.setEftTm(DateTimeUtils.getCurrentLocalDateTime());
                withdrawCardBindDO2.setFailTm("");
                withdrawCardBindDO2.setCardId(withdrawCardBindDO1.getCardId());
                withdrawOrderTransactionalService.updateCard(withdrawCardBindDO2);
            }
        }else{
            //提现银行卡不存在，则添加入库
            String ymd= DateTimeUtils.getCurrentDateStr();
            String orderNo= IdGenUtils.generateId(PwmConstants.W_CRD_GEN_PRE+ymd,15);
            withdrawCardBindDO.setCardId(ymd+orderNo);
            withdrawCardBindDO.setEftTm(DateTimeUtils.getCurrentLocalDateTime());
            withdrawCardBindDO.setUserId(LemonUtils.getUserId());
            withdrawCardBindDO.setCardNoLast(cardNo.substring(cardNo.length()-4));
            withdrawOrderTransactionalService.addCard(withdrawCardBindDO);
        }
        return GenericRspDTO.newSuccessInstance();
    }
}
