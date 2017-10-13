package com.hisun.lemon.pwm.service;

import com.hisun.lemon.csh.dto.cashier.CashierViewDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;


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
     * @param genericResultDTO
     * @return
     */
    public HallQueryResultDTO queryUserInfo(GenericDTO<HallQueryDTO> genericResultDTO);

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
     * 海币充值下单(对外接口)
     * @param rechargeHCouponDTO
     * @return
     */
    public GenericRspDTO<RechargeRspHCouponDTO> createOutHCouponOrder(GenericDTO<RechargeReqHCouponDTO> rechargeHCouponDTO);
    
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
     * @param hallRechargeErrorFundDTO
     * @return
     */
    public void longAmtHandle(HallRechargeErrorFundDTO hallRechargeErrorFundDTO);

    /**
     * 营业厅对账短款处理接口
     * @param hallRechargeErrorFundDTO
     * @return
     */
    public void shortAmtHandle(HallRechargeErrorFundDTO hallRechargeErrorFundDTO);

    /**
     * 获取营业厅对账文件
     */
    public void uploadHallRechargeChkFile(@Validated @RequestBody GenericDTO<HallChkDTO> genericDTO);

    /**
     * 营业厅充值对平账处理
     * @param genericDTO
     */
    public void hallRechargeMatchHandler(GenericDTO<HallRechargeMatchDTO> genericDTO);

    /**
     * 充值撤单处理
     * @param genericDTO
     */
    public void rechargeRevoke(GenericDTO<RechargeRevokeDTO> genericDTO);

    /**
     * 充值长款补单
     * @param orderNo
     */
    public void repeatResultHandle(String orderNo);

}
