package com.hisun.lemon.pwm.controller;

import javax.annotation.Resource;

import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.pwm.dto.*;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.service.IRechargeOrderService;

import java.math.BigDecimal;


@Api(value = "处理充值")
@RestController
@RequestMapping(value = "/pwm/recharge")
public class RechargeOrderController {
	private static final Logger logger = LoggerFactory.getLogger(RechargeOrderController.class);

	@Resource
	IRechargeOrderService service;

	@ApiOperation(value = "充值下单", notes = "生成充值订单，调用收银台")
	@ApiResponse(code = 200, message = "充值下单")
	@PostMapping(value = "/order")
	public GenericRspDTO createOrder(@Validated @RequestBody GenericDTO<RechargeDTO> genRechargeDTO) {
		RechargeDTO rechargeDTO = genRechargeDTO.getBody();
		return service.createOrder(rechargeDTO);
	}

	@ApiOperation(value = "充值处理结果通知", notes = "接收收银台的处理结果通知")
	@ApiResponse(code = 200, message = "处理通知结果")
	@PatchMapping(value = "/result")
	public GenericRspDTO completeOrder(@Validated @RequestBody GenericDTO<RechargeResultDTO> genericResultDTO) {
		service.handleResult(genericResultDTO);
		return GenericRspDTO.newSuccessInstance();
	}

	@ApiOperation(value = "营业厅查询", notes = "查询用户与充值订单信息")
	@ApiResponse(code = 200, message = "查询到的用户与订单信息")
	@GetMapping(value = "/hall/info")
	public GenericRspDTO<HallQueryResultDTO> queryUserInfo(@Validated @RequestParam(value = "userId") String userId,
														   @Validated @RequestParam(value = "hallOrderNo") String hallOrderNo,
														   @Validated @RequestParam(value = "amount",required = false) BigDecimal amount,
														   @Validated @RequestParam(value = "type") String type) {
		HallQueryResultDTO resultDTO = service.queryUserInfo(userId,hallOrderNo,amount,type);
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value = "营业厅充值申请", notes = "接收营业厅的充值申请请求")
	@ApiResponse(code = 200, message = "营业厅充值申请结果")
	@PostMapping(value = "/hall/application")
	public GenericRspDTO<HallRechargeResultDTO> hallRecharge(
			@Validated @RequestBody GenericDTO<HallRechargeApplyDTO> genericResultDTO) {
		HallRechargeResultDTO resultDTO = service.hallRecharge(genericResultDTO.getBody());
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value = "营业厅充值确认", notes = "接收营业厅的充值确认请求")
	@ApiResponse(code = 200, message = "营业厅充值确认结果")
	@PatchMapping(value = "/hall/acknowledgement")
	public GenericRspDTO<HallRechargeResultDTO> hallRechargeConfirm(
			@Validated @RequestBody GenericDTO<HallRechargeApplyDTO> genericResultDTO) {
		HallRechargeResultDTO resultDTO = service.hallRechargeConfirm(genericResultDTO.getBody());
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value = "海币充值下单", notes = "生成充值订单，调用收银台")
	@ApiResponse(code = 200, message = "充值下单")
	@PostMapping(value = "/order/sea")
	public GenericRspDTO createHCouponOrder(@Validated @RequestBody GenericDTO<RechargeHCouponDTO> rechargeHCouponDTO) {
		return this.service.createHCouponOrder(rechargeHCouponDTO);
	}

	@ApiOperation(value = "海币充值处理结果通知", notes = "接收收银台的处理结果通知")
	@ApiResponse(code = 200, message = "处理通知结果")
	@PatchMapping(value = "/result/sea")
	public GenericRspDTO completeHCouponOrder(
			@Validated @RequestBody GenericDTO<RechargeHCouponResultDTO> rechargeHCouponDTO) {
		service.handleHCouponResult(rechargeHCouponDTO);
		return GenericRspDTO.newSuccessInstance();
	}

	@ApiOperation(value = "营业厅充值撤销", notes = "接收营业厅的充值撤销请求")
	@ApiResponse(code = 200, message = "营业厅充值撤销结果")
	@PostMapping(value = "/hall/revocation")
	public GenericRspDTO<HallRechargeResultDTO> hallRechargeRevocation(
			@Validated @RequestBody GenericDTO<HallRechargeApplyDTO> genericResultDTO) {
		HallRechargeResultDTO resultDTO = service.hallRechargeRevocation(genericResultDTO.getBody());
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value = "线下汇款充值申请", notes = "接收线下汇款充值申请提交处理")
	@ApiResponse(code = 200, message = "线下汇款申请结果")
	@PostMapping(value = "/offline/application")
	public GenericRspDTO<OfflineRechargeResultDTO> offlineRechargeApplication(@Validated @RequestBody GenericDTO<OfflineRechargeApplyDTO> genericDTO) {
		OfflineRechargeResultDTO resultDTO = service.offlineRechargeApplication(genericDTO);
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value = "线下汇款上传汇款凭证进行汇款支付", notes = "接收线下汇款凭证进行汇款支付处理")
	@ApiResponse(code = 200, message = "线下汇款充值上传汇款凭证结果")
	@PatchMapping(value = "/offline/pay")
	public GenericRspDTO<OfflineRechargeResultDTO> offlineRemittanceUpload(@Validated @RequestBody GenericDTO<RemittanceUploadDTO> genericDTO) {
		OfflineRechargeResultDTO resultDTO = service.offlineRemittanceUpload(genericDTO);
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}
}
