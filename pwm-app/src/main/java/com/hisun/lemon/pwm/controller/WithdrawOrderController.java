package com.hisun.lemon.pwm.controller;

import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import com.hisun.lemon.pwm.service.IWithdrawOrderService;
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
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

import javax.annotation.Resource;
import java.math.BigDecimal;


@Api(value="处理提现")
@RestController
@RequestMapping(value="/pwm/withdraw")
public class WithdrawOrderController {
    private static final Logger logger = LoggerFactory.getLogger(WithdrawOrderController.class);

    @Resource
    private IWithdrawOrderService withdrawOrderService;

	/**
	 * 提现申请：生成提现订单
	 * @param cardNo
	 * @param amount
	 * @param orderCcy
	 * @param withdrawMode
	 * @param urgeFlag
	 * @param capCorgNo
	 * @param remark
	 * @param payPassword
	 * @param sysChannel
	 */
	@ApiOperation(value="申请提现", notes="生成提现订单")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "cardNo", value = "提现银行卡号", required = true, paramType="form",dataType = "String"),
		@ApiImplicitParam(name = "amount", value = "申请提现金额", required = true,paramType="form", dataType = "BigDecimal"),
		@ApiImplicitParam(name = "orderCcy", value = "币种", required = true, paramType="form",dataType = "String"),
		@ApiImplicitParam(name = "withdrawMode", value = "提现类型", required = true,paramType="form", dataType = "String"),
		@ApiImplicitParam(name = "urgeFlag", value = "加急标志", required = true, paramType="form",dataType = "String"),
		@ApiImplicitParam(name = "capCorgNo", value = "资金合作机构", required = true, paramType="form",dataType = "String"),
		@ApiImplicitParam(name = "remark", value = "提现备注", required = true, paramType="form",dataType = "String"),
		@ApiImplicitParam(name = "payPassword", value = "支付密码", required = true,paramType="form", dataType = "String"),
		@ApiImplicitParam(name = "sysChannel", value = "提现来源渠道 ", required = true,paramType="form", dataType = "String")
	})
	@ApiResponse(code = 200, message = "申请提现")
    @PostMapping(value = "/order")
    public void createOrder(@RequestParam String cardNo, @RequestParam BigDecimal amount, @RequestParam String orderCcy,
							@RequestParam String withdrawMode, @RequestParam String urgeFlag, @RequestParam String capCorgNo, @RequestParam String remark,
							@RequestParam String payPassword, @RequestParam String sysChannel) {
		/**
		 * 注入提现参数信息（少个userId）
		 */
		WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
		withdrawOrderDO.setCapCardNo(cardNo);
		withdrawOrderDO.setApplyAmount(amount);
		withdrawOrderDO.setOrderCcy(orderCcy);
		withdrawOrderDO.setWithdrawType(withdrawMode);
		withdrawOrderDO.setUrgeFlag(urgeFlag);
		withdrawOrderDO.setCapCorgNo(capCorgNo);
		withdrawOrderDO.setRemark(remark);
		withdrawOrderDO.setPayPassword(payPassword);
		withdrawOrderDO.setSysChannel(sysChannel);
		withdrawOrderService.createOrder(withdrawOrderDO);
    }

	/**
	 * 提现结果处理：等待异步通知，更新提现单信息
	 * @param withdrawResultDTO
	 * @return
	 */
	@ApiOperation(value="提现结果同步", notes="接收资金能力的处理结果通知") 
	@ApiImplicitParam(name = "withdrawResultDTO", value = "提现详细数据", required = true,paramType="body", dataType = "WithdrawResultDTO")
	@ApiResponse(code = 200, message = "申请提现结果")
	@PatchMapping(value = "/result")
	public GenericDTO completeOrder(@Validated @RequestBody WithdrawResultDTO withdrawResultDTO){
		withdrawOrderService.completeOrder(withdrawResultDTO);
		return null;
	} 
	
}
