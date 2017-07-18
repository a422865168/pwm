package com.hisun.lemon.pwm.controller;

import javax.annotation.Resource;

import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hisun.lemon.common.utils.StringUtils;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.service.IRechargeOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

 
@Api(value="处理充值")
@RestController
@RequestMapping(value="/pwm/recharge")
public class RechargeOrderController<B> {
    private static final Logger logger = LoggerFactory.getLogger(RechargeOrderController.class);
	 
    @Resource
	IRechargeOrderService service;
	
	@ApiOperation(value="充值下单", notes="生成充值订单，调用收银台")
	@ApiImplicitParam(name = "genRechargeDTO", value = "业务模块传递的充值数据", required = true,paramType="body", dataType = "GenericDTO")
	@ApiResponse(code = 200, message = "充值下单")
    @PostMapping(value = "/order")
    public GenericDTO createOrder(@Validated @RequestBody GenericDTO<RechargeDTO> genRechargeDTO) {
		String ip="";
		RechargeDTO rechargeDTO =genRechargeDTO.getBody();
		if(StringUtils.isBlank(rechargeDTO.getPayerId())){
			rechargeDTO.setPayerId(genRechargeDTO.getUserId());
		}
		GenericDTO genericDTO=service.createOrder(rechargeDTO, ip);
		return genericDTO;
    }

	@ApiOperation(value="充值处理结果通知", notes="接收收银台的处理结果通知")
	@ApiImplicitParam(name = "genericResultDTO", value = "充值通知详细数据", required = true,paramType="body", dataType = "RechargeResultDTO")
	@ApiResponse(code = 200, message = "处理通知结果")
	@PatchMapping(value = "/result")
	public GenericDTO completeOrder(@Validated @RequestBody GenericDTO<RechargeResultDTO> genericResultDTO){
		service.handleResult(genericResultDTO);
		return GenericDTO.newSuccessInstance();
	}
}
