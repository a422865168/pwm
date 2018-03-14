package com.hisun.lemon.pwm.client;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.pwm.dto.RechargeRedoDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeRevokeDTO;
import com.hisun.lemon.pwm.dto.TransferenceReqDTO;
import com.hisun.lemon.pwm.dto.TransferenceRspDTO;

/**
 * 充提  充值服务接口
 * @author ruan
 * @date 2018年3月7日
 * @time 下午3:06:23
 *
 */
@FeignClient("PWM")
public interface PwmRechargeClient {
	
	/**
	 * 圈存
	 * @param genRechargeDTO
	 * @return
	 */
	@PostMapping("/pwm/recharge/order/transference")
    public GenericRspDTO<TransferenceRspDTO> createTransference(@Validated @RequestBody GenericDTO<TransferenceReqDTO> genRechargeDTO);
	
	/**
     * 充值结果通知
     * @param rechargeResultDTO 通知数据
     * @return
     */
	@PostMapping("/pwm/recharge/result")
    public GenericRspDTO rechargeNotify(@Validated @RequestBody GenericDTO<RechargeResultDTO> rechargeResultDTO);
    

	/**
	 * 充值补单
	 * @param genericDTO
	 * @return
	 */
	@PatchMapping("/pwm/recharge/chk/error/redo")
	public GenericRspDTO<NoBody> errRepeatHandle(@Validated @RequestBody GenericDTO<RechargeRedoDTO> genericDTO);


	
	/**
	 * 充值撤单处理
	 * @param genericDTO
	 * @return
	 */
	@PostMapping(value = "/pwm/recharge/chk/error/revoke")
	public GenericRspDTO<NoBody> rechargeRevoke(@Validated @RequestBody GenericDTO<RechargeRevokeDTO> genericDTO);

}
