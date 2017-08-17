package com.hisun.lemon.pwm.service;

import java.math.BigDecimal;

import com.hisun.lemon.csh.dto.cashier.CashierViewDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.HallQueryResultDTO;
import com.hisun.lemon.pwm.dto.HallRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.HallRechargeResultDTO;
import com.hisun.lemon.pwm.dto.OfflineRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.OfflineRechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponResultDTO;
import com.hisun.lemon.pwm.dto.RemittanceUploadDTO;


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
     * 查询用户与订单信息
     * @param key
     * @param hallOrderNo
     * @param amount
     * @param type
     * @return
     */
    public HallQueryResultDTO queryUserOrOrderInfo(String key,String hallOrderNo,BigDecimal amount,String type);

    /**
     * 营业厅充值处理
     * @param dto
     * @return
     */
    public HallRechargeResultDTO hallRechargePay(HallRechargeApplyDTO dto);

    
    /**
     * 海币充值下单
     * @param rechargeHCouponDTO
     * @return
     */
    public GenericRspDTO<CashierViewDTO> createHCouponOrder(GenericDTO<RechargeHCouponDTO> rechargeHCouponDTO);
    
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

    /**
     * 线下汇款充值申请
     * @param genericDTO
     * @return
     */
    public OfflineRechargeResultDTO offlineRechargeApplication(GenericDTO<OfflineRechargeApplyDTO> genericDTO);

    /**
     * 线下汇款上传汇款凭证
     * @param genericDTO
     * @return
     */
    public OfflineRechargeResultDTO offlineRemittanceUpload(GenericDTO<RemittanceUploadDTO> genericDTO);
}
