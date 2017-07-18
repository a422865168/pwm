package com.hisun.lemon.pwm.service.impl;


import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.BeanUtils;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.utils.IdGenUtils;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dto.WithdrawComplDTO;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.math.BigDecimal;


@Transactional
@Service("withdrawOrderService")
public class WithdrawOrderServiceImpl implements IWithdrawOrderService {

	@Resource
    private WithdrawOrderTransactionalService withdrawOrderTransactionalService;

	@Override
	public void createOrder(GenericDTO<WithdrawResultDTO> genericWithdrawResultDTO) {

		WithdrawResultDTO withdrawResultDTO = genericWithdrawResultDTO.getBody();
		//校验申请提现金额是否不小于0
		if(withdrawResultDTO.getWcApplyAmt().compareTo(new BigDecimal(0)) <= 0) {
			LemonException.throwBusinessException("PWM10006");
		}
		//校验手续费是否不小于0
		if(withdrawResultDTO.getFeeAmt().compareTo(new BigDecimal(0)) <= 0) {
			LemonException.throwBusinessException("PWM10006");
		}
		//查询用户是否为黑名单
		//校验用户如为黑名单，则抛出异常信息
		if("在黑名单中".equals(withdrawResultDTO.getUserId())){
			LemonException.throwBusinessException("PWM10005");
		}
		BigDecimal balance = new BigDecimal(0);
		//查询用户账户余额，
		//balance = GetBalanceDao(withdrawResultDTO.getUserId());
		//校验提现余额加手续费大于用户账户余额,则抛出异常
		/*if(balance.compareTo(withdrawResultDTO.getActAmount()) == 1){
			LemonException.throwBusinessException("PWM10004");
		}*/
		//查询支付密码错误次数是否超过5次
		/*if("支付密码错误次数是否超过5".equals(withdrawOrderDO.getUserId())){
			LemonException.throwBusinessException("PWM10003");
		}*/
		//查询用户支付密码，校验支付密码，错误则抛异常
		/*if(!"用户账户支付密码".equals(withdrawOrderDO.getPayPassword())){
			LemonException.throwBusinessException("PWM10002");
		}*/
		//初始化提现订单数据
		WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
		BeanUtils.copyProperties(withdrawOrderDO, withdrawResultDTO);
		String ymd= DateTimeUtils.getCurrentDateStr();
		String orderNo= IdGenUtils.generateId(PwmConstants.W_ORD_GEN_PRE+ymd,15);
		withdrawOrderDO.setOrderNo(ymd+orderNo);
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


	}

	public void completeOrder(GenericDTO<WithdrawComplDTO> genericWithdrawComplDTO) {

        WithdrawComplDTO withdrawComplDTO = genericWithdrawComplDTO.getBody();
        WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
        BeanUtils.copyProperties(withdrawOrderDO, withdrawComplDTO);
        //如果订单状态为'S1'，则修改订单成功时间
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

}
