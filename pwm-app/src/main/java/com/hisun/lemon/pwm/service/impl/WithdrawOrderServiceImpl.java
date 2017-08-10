package com.hisun.lemon.pwm.service.impl;


import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.BeanUtils;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.cpo.dto.WithdrawReqDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.framework.utils.IdGenUtils;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dto.WithdrawRateDTO;
import com.hisun.lemon.pwm.dto.WithdrawRateResultDTO;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
import com.hisun.lemon.pwm.dto.WithdrawDTO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;
import com.hisun.lemon.tfm.client.TfmServerClient;
import com.hisun.lemon.tfm.dto.TradeRateReqDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.math.BigDecimal;


@Transactional
@Service("withdrawOrderService")
public class WithdrawOrderServiceImpl implements IWithdrawOrderService {

	@Resource
    private WithdrawOrderTransactionalService withdrawOrderTransactionalService;

	//@Resource
	//private WithdrawClient withdrawClient;

    @Resource
    private TfmServerClient tfmServerClient;
    /**
     * 生成提现订单
     * @param genericWithdrawDTO
     */
	@Override
	public void createOrder(GenericDTO<WithdrawDTO> genericWithdrawDTO) {

		WithdrawDTO withdrawDTO = genericWithdrawDTO.getBody();
		//校验申请提现金额是否不小于0
		if(withdrawDTO.getWcApplyAmt().compareTo(new BigDecimal(0)) <= 0) {
			LemonException.throwBusinessException("PWM10029");
		}
		//校验手续费是否不小于0
		if(withdrawDTO.getFeeAmt().compareTo(new BigDecimal(0)) <= 0) {
			LemonException.throwBusinessException("PWM10030");
		}
		//查询用户是否为黑名单
		//校验用户如为黑名单，则抛出异常信息
		if("在黑名单中".equals(withdrawDTO.getUserId())){
			LemonException.throwBusinessException("PWM30001");
		}
		BigDecimal balance = new BigDecimal(0);
		//查询用户账户余额，
		//balance = GetBalanceDao(withdrawDTO.getUserId());
		//校验提现余额加手续费大于用户账户余额,则抛出异常
		/*if(balance.compareTo(withdrawDTO.getActAmount()) == 1){
			LemonException.throwBusinessException("PWM30002");
		}*/
		//查询支付密码错误次数是否超过5次
		/*if("支付密码错误次数是否超过5".equals(withdrawOrderDO.getUserId())){
			LemonException.throwBusinessException("PWM30003");
		}*/
		//查询用户支付密码，校验支付密码，错误则抛异常
		/*if(!"用户账户支付密码".equals(withdrawOrderDO.getPayPassword())){
			LemonException.throwBusinessException("PWM30004");
		}*/
		//初始化提现订单数据
		WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
		BeanUtils.copyProperties(withdrawOrderDO, withdrawDTO);
		String ymd= DateTimeUtils.getCurrentDateStr();
		String orderNo= IdGenUtils.generateId(PwmConstants.W_ORD_GEN_PRE+ymd,15);
		withdrawOrderDO.setOrderNo(ymd+orderNo);
		//交易类型 04提现
		withdrawOrderDO.setTxType(PwmConstants.TX_TYPE_WITHDRAW);
		//业务类型 0401个人提现
		withdrawOrderDO.setBusType(PwmConstants.BUS_TYPE_WITHDRAW_P);
		withdrawOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		withdrawOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		//不确定用户名是查还是传
		withdrawOrderDO.setUserName("");
		withdrawOrderDO.setOrderStatus(PwmConstants.WITHDRAW_ORD_W1);
		//生成提现单据
		withdrawOrderTransactionalService.createOrder(withdrawOrderDO);
		//提现金额由账户余额转到提现银行卡
		//withdrawOrderTransactionalService.applyAmountTransfer();
		//调用资金能力的提现申请接口，等待人工线下付款到银行受理结果异步通知
		WithdrawReqDTO withdrawReqDTO = new WithdrawReqDTO();
		BeanUtils.copyProperties(withdrawReqDTO, withdrawOrderDO);
		//GenericDTO genericDTO = withdrawClient.createOrder(withdrawReqDTO);

	}

    /**
     * 处理提现订单结果
     * @param genericWithdrawResultDTO
     */
	public void completeOrder(GenericDTO<WithdrawResultDTO> genericWithdrawResultDTO) {

        WithdrawResultDTO withdrawResultDTO = genericWithdrawResultDTO.getBody();
        WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
        BeanUtils.copyProperties(withdrawOrderDO, withdrawResultDTO);
        // 校验订单是否存在
        withdrawOrderTransactionalService.query(withdrawOrderDO.getOrderNo());
        //判断订单状态为'S1'，则修改订单成功时间
        if(PwmConstants.WITHDRAW_ORD_S1.equals(withdrawOrderDO.getOrderStatus())){
            withdrawOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
            withdrawOrderDO.setRspSuccTm(DateTimeUtils.getCurrentLocalDateTime());
        }
        withdrawOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
        //若提现成功，则更新提现单据状态及相关信息
		withdrawOrderTransactionalService.updateOrder(withdrawOrderDO);
		//若提现失败，则将提现金额由银行卡退回账户余额
		//withdrawOrderTransactionalService.applyAmountBack();
	}

    @Override
    public GenericRspDTO<WithdrawResultDTO> queryRate(WithdrawRateDTO withdrawRateDTO) {

        TradeRateReqDTO tradeRateReqDTO = new TradeRateReqDTO();
        BeanUtils.copyProperties(tradeRateReqDTO, withdrawRateDTO);
        GenericDTO genericDTO = new GenericDTO();
        genericDTO.setBody(tradeRateReqDTO);
        GenericRspDTO genericRspDTO = tfmServerClient.tradeRate(genericDTO);
        WithdrawRateResultDTO withdrawRateResultDTO = new WithdrawRateResultDTO();
        BeanUtils.copyProperties(withdrawRateResultDTO, genericRspDTO.getBody());
        genericRspDTO.setBody(withdrawRateResultDTO);
        return genericRspDTO;
    }
}
