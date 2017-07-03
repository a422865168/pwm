package com.hisun.lemon.pwm.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.dto.RechangeResultDTO;

/**
 * 充提  充值服务接口
 * @author tone
 * @date 2017年6月27日
 * @time 下午3:06:23
 *
 */
@FeignClient("pwm")
public interface PwmRechangeClient {
    @GetMapping("/pwm/recharge/result")
    public GenericDTO addUser(@Validated @RequestBody RechangeResultDTO rechangeResultDTO);
}
