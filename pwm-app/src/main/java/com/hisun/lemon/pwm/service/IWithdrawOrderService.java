package com.hisun.lemon.pwm.service;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.WithdrawRateDTO;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
import com.hisun.lemon.pwm.dto.WithdrawDTO;
import com.hisun.lemon.tfm.dto.TradeRateRspDTO;

/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:13:58
 *
 */
public interface IWithdrawOrderService {

    /**
     * 提现申请，生成提现订单
     * @param genericWithdrawDTO
     */
    public void createOrder(GenericDTO<WithdrawDTO> genericWithdrawDTO);

    /**
     * 提现结果处理：更新订单信息
     * @param genericWithdrawResultDTO
     */
    public void completeOrder(GenericDTO<WithdrawResultDTO> genericWithdrawResultDTO);

    /**
     * 查询交易费率
     * @param withdrawRateDTO
     */
    public GenericRspDTO<WithdrawResultDTO> queryRate(WithdrawRateDTO withdrawRateDTO);
}
