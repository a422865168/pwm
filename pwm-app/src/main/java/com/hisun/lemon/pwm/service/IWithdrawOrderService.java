package com.hisun.lemon.pwm.service;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.*;
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
    public WithdrawRspDTO createOrder(GenericDTO<WithdrawDTO> genericWithdrawDTO);

    /**
     * 提现结果处理：更新订单信息
     * @param genericWithdrawResultDTO
     */
    public WithdrawRspDTO completeOrder(GenericDTO<WithdrawResultDTO> genericWithdrawResultDTO);

    /**
     * 查询交易费率
     * @param withdrawRateDTO
     */
    public GenericRspDTO<WithdrawResultDTO> queryRate(WithdrawRateDTO withdrawRateDTO);

    /**
     * 查询提现银行
     * @return
     */
    public WithdrawBankRspDTO queryBank();

    /**
     * 添加提现银行卡
     * @param genericWithdrawCardBindDTO
     */
    public GenericRspDTO addCard(GenericDTO<WithdrawCardBindDTO> genericWithdrawCardBindDTO);
}
