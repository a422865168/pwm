package com.hisun.lemon.pwm.service;

import java.math.BigDecimal;

import com.hisun.lemon.csh.dto.cashier.CashierViewDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.*;


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
     * @param amount
     * @param type
     * @return
     */
    public HallQueryResultDTO queryUserInfo(String key,BigDecimal amount,String type);

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

    /**
     * 查询营业厅订单查询详细信息
     * @param hallOrderNo
     * @return
     */
    public HallOrderQueryResultDTO queryOrderInfo(String hallOrderNo);

    /**
     * 营业厅对账长款处理接口
     * @param genericDTO
     * @return
     */
    public HallRechargeFundRspDTO longAmtHandle(GenericDTO<HallRechargeFundRepDTO> genericDTO);

    /**
     * 营业厅对账短款处理接口
     * @param genericDTO
     * @return
     */
    public HallRechargeFundRspDTO shortAmtHandle(GenericDTO<HallRechargeFundRepDTO> genericDTO);

    /**
     * 获取营业厅对账文件
     * @param type
     * @param date
     * @param fileName
     */
    public void uploadHallRechargeChkFile(String type,String date,String fileName);
}
