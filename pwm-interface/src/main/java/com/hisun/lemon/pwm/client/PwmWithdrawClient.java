package com.hisun.lemon.pwm.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;

/**
 * 充提  提现服务接口
 * @author tone
 * @date 2017年6月27日
 * @time 下午3:06:23
 *
 */
@FeignClient("PWM")
public interface PwmWithdrawClient {
    @PatchMapping("/pwm/withdraw/result")
    public GenericDTO withdrawNotify(@Validated @RequestBody GenericDTO<WithdrawResultDTO> withdrawResultDTO);
}
