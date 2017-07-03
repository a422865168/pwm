package com.hisun.lemon.pwm.service;

import com.hisun.lemon.pwm.dto.RechangeResultDTO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;


/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:13:58
 *
 */
public interface IWithdrawOrderService {
    public void createOrder(WithdrawOrderDO wdcOrderDO);
    
    public void updateOrder(RechangeResultDTO ppdOrderResultDTO);
}
