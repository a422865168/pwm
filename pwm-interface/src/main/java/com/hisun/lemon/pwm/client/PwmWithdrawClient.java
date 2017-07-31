package com.hisun.lemon.pwm.client;

import com.hisun.lemon.pwm.dto.WithdrawDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;

/**
 * 提现服务接口
 * @author leon
 * @date 2017年7月31日
 * @time 上午11:06:23
 *
 */
@FeignClient("PWM")
public interface PwmWithdrawClient {

    @PostMapping("/pwm/withdraw/order")
    public GenericDTO createOrder(@Validated @RequestBody GenericDTO<WithdrawDTO> genericWithdrawDTO);

    @PatchMapping("/pwm/withdraw/result")
    public GenericDTO completeOrder(@Validated @RequestBody GenericDTO<WithdrawResultDTO> withdrawResultDTO);
}
