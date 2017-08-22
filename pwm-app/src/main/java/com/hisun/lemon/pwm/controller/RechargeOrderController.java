package com.hisun.lemon.pwm.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hisun.lemon.csh.dto.cashier.CashierViewDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.HallQueryResultDTO;
import com.hisun.lemon.pwm.dto.HallRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.HallRechargeResultDTO;
import com.hisun.lemon.pwm.dto.OfflineRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.OfflineRechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponResultDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import com.hisun.lemon.pwm.dto.RemittanceUploadDTO;
import com.hisun.lemon.pwm.service.IRechargeOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;


@Api(value = "处理充值")
@RestController
@RequestMapping(value = "/pwm/recharge")
public class RechargeOrderController {
	private static final Logger logger = LoggerFactory.getLogger(RechargeOrderController.class);

	@Resource
	IRechargeOrderService service;

	@ApiOperation(value = "充值下单", notes = "生成充值订单，调用收银台")
	@ApiImplicitParam(name = "x-lemon-usrid", value = "用户ID", paramType = "header")
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
	public GenericRspDTO<HallQueryResultDTO> queryUserOrOrderInfo(@Validated @RequestParam(value = "key",required = false) String key,
														   @Validated @RequestParam(value = "hallOrderNo") String hallOrderNo,
														   @Validated @RequestParam(value = "amount",required = false) BigDecimal amount,
														   @Validated @RequestParam(value = "type",required = false) String type) {
		HallQueryResultDTO resultDTO = service.queryUserOrOrderInfo(key,hallOrderNo,amount,type);
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value = "营业厅充值", notes = "接收营业厅的充值请求")
	@ApiResponse(code = 200, message = "营业厅充值结果")
	@PostMapping(value = "/hall")
	public GenericRspDTO<HallRechargeResultDTO> hallRecharge(
			@Validated @RequestBody GenericDTO<HallRechargeApplyDTO> genericResultDTO) {
		HallRechargeResultDTO resultDTO = service.hallRechargePay(genericResultDTO.getBody());
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value = "海币充值下单", notes = "生成充值订单，调用收银台")
	@ApiResponse(code = 200, message = "充值下单")
	@PostMapping(value = "/order/sea")
	@ApiImplicitParam(name = "x-lemon-usrid", value = "用户ID", paramType = "header")
	public GenericRspDTO<CashierViewDTO> createHCouponOrder(@Validated @RequestBody GenericDTO<RechargeHCouponDTO> rechargeHCouponDTO) {
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

	@ApiOperation(value = "营业厅充值撤销冲正", notes = "接收营业厅的充值撤销请求")
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

	@ApiOperation(value = "上传汇款凭证进行汇款支付", notes = "接收线下汇款凭证进行汇款支付处理")
	@ApiResponse(code = 200, message = "线下汇款充值上传汇款凭证结果")
	@PatchMapping(value = "/offline/pay")
	public GenericRspDTO<OfflineRechargeResultDTO> offlineRemittanceUpload(@Validated @RequestBody GenericDTO<RemittanceUploadDTO> genericDTO) {
		OfflineRechargeResultDTO resultDTO = service.offlineRemittanceUpload(genericDTO);
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}
}
