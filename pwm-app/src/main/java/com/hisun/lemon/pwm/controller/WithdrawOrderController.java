package com.hisun.lemon.pwm.controller;

import javax.annotation.Resource;

import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.WithdrawRateDTO;
import com.hisun.lemon.pwm.dto.WithdrawRateResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.pwm.dto.WithdrawDTO;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;


@Api(value = "处理提现")
@RestController
@RequestMapping(value = "/pwm/withdraw")
public class WithdrawOrderController {
	private static final Logger logger = LoggerFactory.getLogger(WithdrawOrderController.class);

	@Resource
	private IWithdrawOrderService withdrawOrderService;

	/**
	 * 提现申请：生成提现订单
	 * 
	 * @param genericWithdrawDTO
	 */
	@ApiOperation(value = "申请提现", notes = "生成提现订单")
	@ApiResponse(code = 200, message = "申请提现")
	@PostMapping(value = "/order")
	public GenericRspDTO createOrder(@Validated @RequestBody GenericDTO<WithdrawDTO> genericWithdrawDTO) {

		withdrawOrderService.createOrder(genericWithdrawDTO);
		GenericRspDTO<WithdrawDTO> genericDTO = new GenericRspDTO<WithdrawDTO>();
		genericDTO.setMsgCd(LemonUtils.getSuccessMsgCd());
		return genericDTO;
	}

	/**
	 * 提现结果处理：等待异步通知，更新提现单信息
	 * 
	 * @param genericWithdrawResultDTO
	 * @return
	 */
	@ApiOperation(value = "提现结果同步", notes = "接收资金能力的处理结果通知")
	@ApiResponse(code = 200, message = "申请提现结果")
	@PatchMapping(value = "/result")
	public GenericRspDTO completeOrder(@Validated @RequestBody GenericDTO<WithdrawResultDTO> genericWithdrawResultDTO) {
		withdrawOrderService.completeOrder(genericWithdrawResultDTO);
		GenericRspDTO<WithdrawResultDTO> genericDTO = new GenericRspDTO<WithdrawResultDTO>();
		genericDTO.setMsgCd(LemonUtils.getSuccessMsgCd());
		return genericDTO;
	}

	/**
	 *	查询交易费率
	 */
    @ApiOperation(value = "查询交易费率", notes = "根据业务类型币种查询交易费率")
    @ApiResponse(code = 200, message = "查询交易费率")
    @GetMapping(value = "/rate")
	public GenericRspDTO<WithdrawRateResultDTO> queryWithdrawRate(@Validated WithdrawRateDTO withdrawRateDTO){
		GenericRspDTO genericDTO = withdrawOrderService.queryRate(withdrawRateDTO);
		genericDTO.setMsgCd(LemonUtils.getSuccessMsgCd());
		return genericDTO;
	}
}
