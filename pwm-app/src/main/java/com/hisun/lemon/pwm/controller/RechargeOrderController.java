package com.hisun.lemon.pwm.controller;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.csh.dto.cashier.BackstageViewDTO;
import com.hisun.lemon.csh.dto.cashier.CashierViewDTO;
import com.hisun.lemon.framework.controller.BaseController;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeRedoDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeRevokeDTO;
import com.hisun.lemon.pwm.dto.TransferenceReqDTO;
import com.hisun.lemon.pwm.dto.TransferenceRspDTO;
import com.hisun.lemon.pwm.service.IRechargeOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

@Api(value = "处理充值")
@RestController
@RequestMapping(value = "/pwm/recharge")
public class RechargeOrderController extends BaseController {

	@Resource
	IRechargeOrderService service;

	@ApiOperation(value = "充值下单", notes = "生成充值订单，调用收银台")
	@ApiImplicitParam(name = "x-lemon-usrid", value = "用户ID", paramType = "header")
	@ApiResponse(code = 200, message = "充值下单")
	@PostMapping(value = "/order")
	public GenericRspDTO<CashierViewDTO> createOrder(@Validated @RequestBody GenericDTO<RechargeDTO> genRechargeDTO) {
		return service.createOrder(genRechargeDTO);
	}
	
	
	@ApiOperation(value = "商户充值下单", notes = "生成商户充值订单，调用收银台")
	@ApiImplicitParam(name = "x-lemon-usrid", value = "用户ID", paramType = "header")
	@ApiResponse(code = 200, message = "商户充值下单")
	@PostMapping(value = "/order/mer")
	public GenericRspDTO<BackstageViewDTO> createOrderMer(@Validated @RequestBody GenericDTO<RechargeDTO> genRechargeDTO) {
		return service.createOrderMer(genRechargeDTO);
	}
	
	@ApiOperation(value = "圈存", notes = "圈存")
	@ApiResponse(code = 200, message = "圈存下单")
	@ApiImplicitParam(name = "x-lemon-usrid", value = "用户ID", paramType = "header")
	@PostMapping(value = "/order/transference")
	public GenericRspDTO<TransferenceRspDTO> createTransference(@Validated @RequestBody GenericDTO<TransferenceReqDTO> genRechargeDTO) {
		TransferenceRspDTO transferenceRspDTO=service.createTransference(genRechargeDTO);
		return GenericRspDTO.newSuccessInstance(transferenceRspDTO);
	}
	

	@ApiOperation(value = "充值处理结果通知", notes = "接收收银台的处理结果通知")
	@ApiResponse(code = 200, message = "处理通知结果")
	@PostMapping(value = "/result")
	public GenericRspDTO<NoBody> completeOrder(@Validated @RequestBody GenericDTO<RechargeResultDTO> genericResultDTO) {
		service.handleResult(genericResultDTO);
		return GenericRspDTO.newSuccessInstance();
	}


	@ApiOperation(value = "充值撤单处理", notes = "充值撤单处理")
	@ApiResponse(code = 200, message = "充值撤单处理结果")
	@PostMapping(value = "/chk/error/revoke")
	public GenericRspDTO<NoBody> rechargeRevoke(@Validated @RequestBody GenericDTO<RechargeRevokeDTO> genericDTO) {
		service.rechargeRevoke(genericDTO);
		return GenericRspDTO.newSuccessInstance();
	}

	@ApiOperation(value = "充值补单", notes = "充值补单")
	@ApiResponse(code = 200, message = "充值补单")
	@PatchMapping(value = "/chk/error/redo")
	public GenericRspDTO<NoBody> errRepeatHandle(@Validated @RequestBody GenericDTO<RechargeRedoDTO> genericDTO) {
		RechargeRedoDTO rechargeRedoDTO = genericDTO.getBody();
		if (JudgeUtils.isNull(rechargeRedoDTO)) {
			LemonException.throwBusinessException("PWM20037");
		}
		String orderNo = rechargeRedoDTO.getOrderNo();
		service.repeatResultHandle(orderNo);
		return GenericRspDTO.newSuccessInstance();
	}
}
