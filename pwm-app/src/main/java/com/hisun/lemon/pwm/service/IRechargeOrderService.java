package com.hisun.lemon.pwm.service;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeSeaDTO;
import com.hisun.lemon.pwm.dto.RechargeSeaResultDTO;
import com.hisun.lemon.pwm.entity.RechargeSeaDO;


/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:13:58
 *
 */
public interface IRechargeOrderService {
    public GenericDTO createOrder(RechargeDTO rechargeDTO,String ipAddress);

    /**
     * 接收收银台的结果通知
     * @param resultDto
     */
    public void handleResult(GenericDTO resultDto);
    /**
     * 充海币下单
     * @param genRechargeSeaDTO
     * @return
     */
    public RechargeSeaDO createSeaOrder(GenericDTO<RechargeSeaDTO> genRechargeSeaDTO);
    
    /**
     * 海币充值的结果通知
     * @param resultDto
     */
    public void seaResult(GenericDTO<RechargeSeaDTO> rechargeSeaDTO);
}
