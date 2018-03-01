package com.hisun.lemon.pwm.controller;

import javax.annotation.Resource;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.framework.controller.BaseController;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dto.HallChkDTO;
import com.hisun.lemon.pwm.dto.HallOrderQueryResultDTO;
import com.hisun.lemon.pwm.dto.HallQueryDTO;
import com.hisun.lemon.pwm.dto.HallQueryResultDTO;
import com.hisun.lemon.pwm.dto.HallRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.HallRechargeErrorFundDTO;
import com.hisun.lemon.pwm.dto.HallRechargeMatchDTO;
import com.hisun.lemon.pwm.dto.HallRechargeResultDTO;
import com.hisun.lemon.pwm.dto.OfflineRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.OfflineRechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeRedoDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeRevokeDTO;
import com.hisun.lemon.pwm.dto.RemittanceUploadDTO;
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
	public GenericRspDTO createOrder(@Validated @RequestBody GenericDTO<RechargeDTO> genRechargeDTO) {
		RechargeDTO rechargeDTO = genRechargeDTO.getBody();
		return service.createOrder(rechargeDTO);
	}

	@ApiOperation(value = "充值处理结果通知", notes = "接收收银台的处理结果通知")
	@ApiResponse(code = 200, message = "处理通知结果")
	@PostMapping(value = "/result")
	public GenericRspDTO completeOrder(@Validated @RequestBody GenericDTO<RechargeResultDTO> genericResultDTO) {
		service.handleResult(genericResultDTO);
		return GenericRspDTO.newSuccessInstance();
	}

	@ApiOperation(value = "营业厅查询", notes = "查询用户账户信息")
	@ApiResponse(code = 200, message = "查询到的用户与账户信息")
	@PostMapping(value = "/hall/info")
	public GenericRspDTO<HallQueryResultDTO> queryUserInfo(
			@Validated @RequestBody GenericDTO<HallQueryDTO> genericResultDTO) {
		HallQueryResultDTO resultDTO = service.queryUserInfo(genericResultDTO);
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
	public GenericRspDTO<OfflineRechargeResultDTO> offlineRechargeApplication(
			@Validated @RequestBody GenericDTO<OfflineRechargeApplyDTO> genericDTO) {
		OfflineRechargeResultDTO resultDTO = service.offlineRechargeApplication(genericDTO);
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value = "上传汇款凭证进行汇款支付", notes = "接收线下汇款凭证进行汇款支付处理")
	@ApiResponse(code = 200, message = "线下汇款充值上传汇款凭证结果")
	@PostMapping(value = "/offline/pay")
	public GenericRspDTO<OfflineRechargeResultDTO> offlineRemittanceUpload(
			@Validated @RequestBody GenericDTO<RemittanceUploadDTO> genericDTO) {
		OfflineRechargeResultDTO resultDTO = service.offlineRemittanceUpload(genericDTO);
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value = "营业厅订单查询", notes = "查询充值订单详细信息")
	@ApiResponse(code = 200, message = "查询到订单详细信息")
	@GetMapping(value = "/order/info")
	public GenericRspDTO<HallOrderQueryResultDTO> queryOrderInfo(
			@Validated @RequestParam(value = "hallOrderNo") String hallOrderNo) {
		HallOrderQueryResultDTO resultDTO = service.queryOrderInfo(hallOrderNo);
		return GenericRspDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value = "营业厅差错处理", notes = "营业厅差错处理")
	@ApiResponse(code = 200, message = "与营业厅充值对账的差错处理")
	@PostMapping(value = "/hall/error/handler")
	public GenericRspDTO<NoBody> hallRechargeErrorHandler(
			@Validated @RequestBody GenericDTO<HallRechargeErrorFundDTO> genericDTO) {
		HallRechargeErrorFundDTO hallRechargeErrorFundDTO = genericDTO.getBody();
		String errorType = hallRechargeErrorFundDTO.getFundType();
		if (JudgeUtils.equals(errorType, PwmConstants.HALL_CHK_LONG_AMT)) {
			service.longAmtHandle(hallRechargeErrorFundDTO);
		} else if (JudgeUtils.equals(errorType, PwmConstants.HALL_CHK_SHORT_AMT)) {
			service.shortAmtHandle(hallRechargeErrorFundDTO);
		} else {
			LemonException.throwBusinessException("PWM20032");
		}

		return GenericRspDTO.newSuccessInstance();
	}

	@ApiOperation(value = "获取营业厅充值对账文件", notes = "处理营业对账文件上传服务器")
	@ApiResponse(code = 200, message = "获取营业厅对账文件结果")
	@PostMapping(value = "/chk/{type}/{date}/{filename}")
	public GenericRspDTO<NoBody> uploadHallChkFile(@Validated @RequestBody GenericDTO<HallChkDTO> genericDTO) {
		service.uploadHallRechargeChkFile(genericDTO);
		return GenericRspDTO.newSuccessInstance();
	}

	@ApiOperation(value = "营业厅充值对平金额处理", notes = "营业厅充值对平金额处理")
	@ApiResponse(code = 200, message = "营业厅充值对平金额处理")
	@PostMapping(value = "/hall/match")
	public GenericRspDTO<NoBody> hallRechargeMatchHandler(
			@Validated @RequestBody GenericDTO<HallRechargeMatchDTO> genericDTO) {
		service.hallRechargeMatchHandler(genericDTO);
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
