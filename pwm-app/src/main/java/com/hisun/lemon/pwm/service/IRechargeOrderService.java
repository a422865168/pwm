package com.hisun.lemon.pwm.service;

import java.math.BigDecimal;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.HallQueryResultDTO;
import com.hisun.lemon.pwm.dto.HallRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.HallRechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponResultDTO;


/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:13:58
 *
 */
public interface IRechargeOrderService {
    public GenericRspDTO createOrder(RechargeDTO rechargeDTO);

    /**
     * 接收收银台的结果通知
     * @param resultDto
     */
    public void handleResult(GenericDTO resultDto);

    /**
     * 查询用户信息
     * @param userId
     * @param amount
     * @return
     */
    public HallQueryResultDTO queryUserInfo(String userId,BigDecimal amount);

    /**
     * 营业厅充值申请处理
     * @param dto
     * @return
     */
    public HallRechargeResultDTO hallRecharge(HallRechargeApplyDTO dto);

    /**
     * 营业厅充值确认处理
     * @param dto
     * @return
     */
    public HallRechargeResultDTO hallRechargeConfirm(HallRechargeApplyDTO dto);
    
    /**
     * 海币充值下单
     * @param rechargeHCouponDTO
     * @return
     */
    public GenericRspDTO createHCouponOrder(GenericDTO<RechargeHCouponDTO> rechargeHCouponDTO);
    
    /**
     * 海币充值结果处理
     * @param rechargeHCouponDTO
     */
    public void handleHCouponResult(GenericDTO<RechargeHCouponResultDTO> rechargeHCouponDTO);

    /**
     * 营业厅充值撤销处理
     * @param dto
     * @return
     */
    public HallRechargeResultDTO hallRechargeRevocation(HallRechargeApplyDTO dto);
}
