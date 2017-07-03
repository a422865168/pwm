package com.hisun.lemon.pwm.service;

import com.hisun.lemon.pwm.dto.RechangeResultDTO;
import com.hisun.lemon.pwm.entity.RechangeOrderDO;


/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:13:58
 *
 */
public interface IRechangeOrderService {
    public void createOrder(RechangeOrderDO PpdOrderDO);
    
    public void updateOrder(RechangeResultDTO ppdOrderResultDTO);
}
