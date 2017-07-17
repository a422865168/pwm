package com.hisun.lemon.pwm.service;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.dto.WithdrawComplDTO;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;

/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:13:58
 *
 */
public interface IWithdrawOrderService {

    /**
     * 提现申请，生成提现订单
     * @param genericWithdrawResultDTO
     */
    public void createOrder(GenericDTO<WithdrawResultDTO> genericWithdrawResultDTO);

    /**
     * 提现结果处理：更新订单信息
     * @param genericWithdrawComplDTO
     */
    public void completeOrder(GenericDTO<WithdrawComplDTO> genericWithdrawComplDTO);
}
