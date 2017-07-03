package com.hisun.lemon.pwm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.dto.RechangeResultDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

 
@Api(value="处理充值")
@RestController
@RequestMapping(value="/pwm/recharge")
public class RechargeOrderController {
    private static final Logger logger = LoggerFactory.getLogger(RechargeOrderController.class);
	 
	
	@ApiOperation(value="充值下单", notes="生成充值订单，调用收银台")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "amount", value = "充值金额", required = true, paramType="form", dataType = "Double"),
		@ApiImplicitParam(name = "psnFlag", value = "对公对私标志", required = true, paramType="form", dataType = "String"),
		@ApiImplicitParam(name = "sysChannel", value = "订单来源渠道", required = true, paramType="form", dataType = "String")
	})
	@ApiResponse(code = 200, message = "充值下单结果")
    @PostMapping(value = "/order")
    public void createOrder(@RequestParam Double amount, @RequestParam String psnFlag, @RequestParam String sysChannel) {
    
        
    }

	@ApiOperation(value="充值处理结果通知", notes="接收收银台的处理结果通知")
//	@ApiImplicitParams({
//		@ApiImplicitParam(name = "orderNo", value = "充值订单号", required = true, dataType = "String"),
//		@ApiImplicitParam(name = "amount", value = "充值金额", required = true, dataType = "Double"),
//		@ApiImplicitParam(name = "orderCcy", value = "币种", required = true, dataType = "String"),
//		@ApiImplicitParam(name = "mobileNo", value = "手机号", required = false, dataType = "String"),
//		@ApiImplicitParam(name = "capCorgNo", value = "资金合作机构", required = true, dataType = "String"),
//		@ApiImplicitParam(name = "rutCorgNo", value = "路径合作机构", required = true, dataType = "String"),
//		@ApiImplicitParam(name = "corpBusType", value = "业务合作类型", required = true, dataType = "String"),
//		@ApiImplicitParam(name = "corpBusSubType", value = "业务合作子类型", required = true, dataType = "String"),
//		@ApiImplicitParam(name = "capCardType", value = "银行卡类型", required = false, dataType = "String"),
//		@ApiImplicitParam(name = "cardNoHide", value = "脱敏银行卡号", required = true, dataType = "String"),
//		@ApiImplicitParam(name = "cardNoLast", value = "卡末四位", required = true, dataType = "String"),
//		@ApiImplicitParam(name = "cardUserNameHide", value = "脱敏用户名", required = true, dataType = "String"),
//		@ApiImplicitParam(name = "idType", value = "证件类型", required = true, dataType = "String"),
//		@ApiImplicitParam(name = "idNoHide", value = "脱敏证件号", required = true, dataType = "String")
//	})
	@ApiImplicitParam(name = "rechangeResultDTO", value = "充值通知详细数据", required = true,paramType="body", dataType = "RechangeResultDTO")
	@ApiResponse(code = 200, message = "处理通知结果")
	@PatchMapping(value = "/result")
	public GenericDTO completeOrder(@Validated @RequestBody RechangeResultDTO rechangeResultDTO){
		 
		return null;
	}
}
