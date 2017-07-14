package com.hisun.lemon.pwm.service;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.dto.RechangeDTO;
import com.hisun.lemon.pwm.dto.RechangeResultDTO;


/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:13:58
 *
 */
public interface IRechangeOrderService {
    public GenericDTO createOrder(RechangeDTO rechangeDTO,String ipAddress);
  
    public void updateOrder(RechangeResultDTO ppdOrderResultDTO);
}
