package com.hisun.lemon.pwm.controller;

import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.dto.WithdrawDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

import javax.annotation.Resource;


@Api(value="处理提现")
@RestController
@RequestMapping(value="/pwm/withdraw")
public class WithdrawOrderController {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawOrderController.class);


    @Resource
    private IWithdrawOrderService withdrawOrderService;

	/**
	 * 提现申请：生成提现订单
	 * @param genericWithdrawDTO
	 */
	@ApiOperation(value="申请提现", notes="生成提现订单")
	@ApiImplicitParam(name = "genericWithdrawDTO", value = "提现通知详细数据", required = true,paramType="body", dataType = "GenericDTO<WithdrawDTO>")
	@ApiResponse(code = 200, message = "申请提现")
    @PostMapping(value = "/order")
    public GenericDTO createOrder(@Validated @RequestBody GenericDTO<WithdrawDTO> genericWithdrawDTO) {

		withdrawOrderService.createOrder(genericWithdrawDTO);
		GenericDTO<WithdrawDTO> genericDTO = new GenericDTO<WithdrawDTO>();
		genericDTO.setMsgCd(LemonUtils.getSuccessMsgCd());
		return genericDTO;
    }

	/**
	 * 提现结果处理：等待异步通知，更新提现单信息
	 * @param genericWithdrawResultDTO
	 * @return
	 */
	@ApiOperation(value="提现结果同步", notes="接收资金能力的处理结果通知") 
	@ApiImplicitParam(name = "genericWithdrawResultDTO", value = "提现详细数据", required = true,paramType="body", dataType = "GenericDTO<WithdrawResultDTO>")
	@ApiResponse(code = 200, message = "申请提现结果")
	@PatchMapping(value = "/result")
	public GenericDTO completeOrder(@Validated @RequestBody GenericDTO<WithdrawResultDTO> genericWithdrawResultDTO){
		withdrawOrderService.completeOrder(genericWithdrawResultDTO);
		GenericDTO<WithdrawResultDTO> genericDTO = new GenericDTO<WithdrawResultDTO>();
		genericDTO.setMsgCd(LemonUtils.getSuccessMsgCd());
		return genericDTO;
	} 
	
}
