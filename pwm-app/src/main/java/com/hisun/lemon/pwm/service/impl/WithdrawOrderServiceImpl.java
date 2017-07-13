package com.hisun.lemon.pwm.service.impl;


import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.math.BigDecimal;


@Transactional
@Service
public class WithdrawOrderServiceImpl implements IWithdrawOrderService {

	@Resource
	private WithdrawOrderTransactionalService withdrawOrderTransactionalService;

	@Override
	public void createOrder(WithdrawOrderDO withdrawOrderDO) {
		//校验提现金额是否正确
		if(withdrawOrderDO.getApplyAmount().compareTo(new BigDecimal(0)) <= 0) {
			throw new LemonException("PWM10006");
		}
		//查询用户是否为黑名单
		//校验用户如为黑名单，则抛出异常信息
		if("在黑名单中".equals(withdrawOrderDO.getUserId())){
			throw new LemonException("PWM10005");
		}
		BigDecimal balance = new BigDecimal(0);
		//查询用户账户余额，
		//balance = GetBalanceDao(withdrawOrderDO.getUserId());
		//调用内部接口计算提现手续费
		//withdrawOrderDO.setFeeAmount(countWithdrawFee());
		//申请提现余额加手续费
		withdrawOrderDO.setActAmount(withdrawOrderDO.getApplyAmount().add(withdrawOrderDO.getFeeAmount()));
		//校验提现余额加手续费大于用户账户余额,则抛出异常
		if(balance.compareTo(withdrawOrderDO.getActAmount()) == 1){
			throw new LemonException("PWM10004");
		}
		//查询支付密码错误次数是否超过5次
		if("支付密码错误次数是否超过5".equals(withdrawOrderDO.getUserId())){
			throw new LemonException("PWM10003");
		}
		//查询用户支付密码，校验支付密码，错误则抛异常
		if(!"用户账户支付密码".equals(withdrawOrderDO.getPayPassword())){
			throw new LemonException("PWM10002");
		}
		//生成提现单据,把订单状态置为'W1'
		withdrawOrderTransactionalService.createOrder(withdrawOrderDO);
		//提现金额由账户余额转到提现银行卡
		withdrawOrderTransactionalService.applyAmountTransfer();
		//调用资金能力的提现申请接口，等待人工线下付款到银行受理结果异步通知


	}
 
	public void completeOrder(WithdrawResultDTO withdrawResultDTO) {
		//若提现成功，则更新提现单据状态及相关信息
		withdrawOrderTransactionalService.updateOrder(withdrawResultDTO);
		//若提现失败，则将提现金额由银行卡退回账户余额
		withdrawOrderTransactionalService.applyAmountBack();
	}

}
