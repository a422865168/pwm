package com.hisun.lemon.pwm.service;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.*;

import java.util.List;

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
    public GenericRspDTO<WithdrawRateResultDTO> queryRate(WithdrawRateDTO withdrawRateDTO);

    /**
     * 查询提现银行
     * @return
     */
    public List<WithdrawBankRspDTO> queryBank(GenericDTO genericDTO);

    /**
     * 添加提现银行卡
     * @param genericWithdrawCardBindDTO
     */
    public GenericRspDTO addCard(GenericDTO<WithdrawCardBindDTO> genericWithdrawCardBindDTO);

    /**
     * 查询已添加的提现银行卡
     * @param genericDTO
     * @return
     */
    public List<WithdrawCardQueryDTO> queryCard(GenericDTO genericDTO);

    /**
     * 删除提现应行卡
     * @param genericWithdrawCardDelDTO
     * @return
     */
    public GenericRspDTO delCard(GenericDTO<WithdrawCardDelDTO> genericWithdrawCardDelDTO);

    /**
     * 提现差错处理
     * @param genericWithdrawErrorHandleDTO
     * @return
     */
    public GenericRspDTO withdrawErrorHandler(GenericDTO<WithdrawErrorHandleDTO> genericWithdrawErrorHandleDTO);
}
