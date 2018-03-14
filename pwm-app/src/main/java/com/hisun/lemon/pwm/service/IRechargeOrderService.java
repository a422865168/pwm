package com.hisun.lemon.pwm.service;

import com.hisun.lemon.csh.dto.cashier.CashierViewDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeRevokeDTO;
import com.hisun.lemon.pwm.dto.TransferenceReqDTO;
import com.hisun.lemon.pwm.dto.TransferenceRspDTO;


/**
 * @author ruan
 * @date 2018年3月6日
 * @time 上午10:13:58
 *
 */
public interface IRechargeOrderService {
	//充值下单
    public GenericRspDTO<CashierViewDTO> createOrder(GenericDTO<RechargeDTO> genRechargeDTO);
    
    /*
     * 圈存请求
     */
    public TransferenceRspDTO createTransference(GenericDTO<TransferenceReqDTO> genRechargeDTO);

    /**
     * 接收收银台的结果通知
     * @param resultDto
     */
    public void handleResult(GenericDTO<RechargeResultDTO> genericResultDTO);

   
   
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
