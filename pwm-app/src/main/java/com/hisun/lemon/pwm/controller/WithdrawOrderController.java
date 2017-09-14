package com.hisun.lemon.pwm.controller;

import javax.annotation.Resource;

import com.hisun.lemon.framework.controller.BaseController;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.pwm.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

import java.util.List;


@Api(value = "处理提现")
@RestController
@RequestMapping(value = "/pwm/withdraw")
public class WithdrawOrderController  extends BaseController {
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

		genericWithdrawDTO.getBody().setUserId(LemonUtils.getUserId());
		WithdrawRspDTO withdrawRspDTO = withdrawOrderService.createOrder(genericWithdrawDTO);
		return GenericRspDTO.newSuccessInstance(withdrawRspDTO);
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
		WithdrawRspDTO withdrawRspDTO = withdrawOrderService.completeOrder(genericWithdrawResultDTO);
		return GenericRspDTO.newSuccessInstance(withdrawRspDTO);
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

	/**
	 * 查询可提现银行
	 */
	@ApiOperation(value = "查询可提现银行", notes = "查询可提现银行列表")
	@ApiResponse(code = 200, message = "查询可提现银行结果")
	@GetMapping(value = "/bank")
	public GenericRspDTO<List<WithdrawBankRspDTO>> queryWithdrawBank(GenericDTO genericDTO){
		List<WithdrawBankRspDTO> withdrawBankRspDTO = withdrawOrderService.queryBank(genericDTO);
		return GenericRspDTO.newSuccessInstance(withdrawBankRspDTO);
	}

	/**
	 * 添加提现银行卡
	 */
	@ApiOperation(value = "添加提现银行卡", notes = "添加提现银行卡")
	@ApiResponse(code = 200, message = "添加提现银行卡")
	@PostMapping(value = "/add")
	public GenericRspDTO addWithdrawCard(@Validated @RequestBody GenericDTO<WithdrawCardBindDTO> genericWithdrawCardBindDTO){
		genericWithdrawCardBindDTO.getBody().setUserId(LemonUtils.getUserId());
		return withdrawOrderService.addCard(genericWithdrawCardBindDTO);
	}

    /**
     * 查询已添加的银行卡
     */
    @ApiOperation(value = "查询可提现银行", notes = "查询可提现银行列表")
    @ApiResponse(code = 200, message = "查询可提现银行结果")
    @GetMapping(value = "/card")
    public GenericRspDTO<List<WithdrawCardQueryDTO>> queryWithdrawCard(GenericDTO genericDTO){
        List<WithdrawCardQueryDTO> withdrawCardQueryDTO = withdrawOrderService.queryCard(genericDTO);
        return GenericRspDTO.newSuccessInstance(withdrawCardQueryDTO);
    }

	/**
	 * 删除提现银行卡
	 */
	@ApiOperation(value = "删除提现银行卡", notes = "删除提现银行卡")
	@ApiResponse(code = 200, message = "删除提现银行卡")
	@PutMapping(value = "/del")
	public GenericRspDTO<WithdrawCardDelRspDTO> delWithdrawCard(@Validated @RequestBody GenericDTO<WithdrawCardDelDTO> genericWithdrawCardDelDTO){
		return withdrawOrderService.delCard(genericWithdrawCardDelDTO);
	}
}
