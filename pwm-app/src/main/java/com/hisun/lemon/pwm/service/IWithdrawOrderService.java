package com.hisun.lemon.pwm.service;

import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;


/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:13:58
 *
 */
public interface IWithdrawOrderService {

    /**
     * 提现申请，生成提现订单
     * @param wdcOrderDO
     */
    public void createOrder(WithdrawOrderDO wdcOrderDO);

    /**
     * 提现结果处理：更新订单信息
     * @param withdrawResultDTO
     */
    public void completeOrder(WithdrawResultDTO withdrawResultDTO);
}
