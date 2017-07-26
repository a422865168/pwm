package com.hisun.lemon.pwm.controller;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.hisun.lemon.common.utils.StringUtils;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.pwm.dto.HallQueryDTO;
import com.hisun.lemon.pwm.dto.HallQueryResultDTO;
import com.hisun.lemon.pwm.dto.HallRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.HallRechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponResultDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import com.hisun.lemon.pwm.entity.RechargeHCouponDO;
import com.hisun.lemon.pwm.service.IRechargeOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;


 
@Api(value="处理充值")
@RestController
@RequestMapping(value="/pwm/recharge")
public class RechargeOrderController {
    private static final Logger logger = LoggerFactory.getLogger(RechargeOrderController.class);
	 
    @Resource
	IRechargeOrderService service;
	
	@ApiOperation(value="充值下单", notes="生成充值订单，调用收银台")
	@ApiResponse(code = 200, message = "充值下单")
    @PostMapping(value = "/order")
    public GenericDTO createOrder(@Validated @ModelAttribute @RequestBody GenericDTO<RechargeDTO> genRechargeDTO) {
		String ip="";
		RechargeDTO rechargeDTO =genRechargeDTO.getBody();
		if(StringUtils.isBlank(rechargeDTO.getPayerId())){
			logger.debug("默认设置当前用户为的付款方");
			rechargeDTO.setPayerId(genRechargeDTO.getUserId());
		}
		return service.createOrder(rechargeDTO, ip);
    }

	@ApiOperation(value="充值处理结果通知", notes="接收收银台的处理结果通知")
	@ApiResponse(code = 200, message = "处理通知结果")
	@PatchMapping(value = "/result")
	public GenericDTO completeOrder(@Validated @RequestBody GenericDTO<RechargeResultDTO> genericResultDTO){
		service.handleResult(genericResultDTO);
		return GenericDTO.newSuccessInstance();
	}

	@ApiOperation(value="营业厅查询", notes="查询用户信息")
	@ApiResponse(code = 200, message = "查询到的用户信息")
	@GetMapping(value = "/hall/info")
	public GenericDTO queryUserInfo(@Validated GenericDTO<HallQueryDTO> genericResultDTO){
		HallQueryResultDTO resultDTO=service.queryUserInfo(genericResultDTO.getBody().getKey(),genericResultDTO.getBody().getAmount());
		return GenericDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value="营业厅充值申请", notes="接收营业厅的充值申请请求")
	@ApiResponse(code = 200, message = "营业厅充值申请结果")
	@PostMapping(value = "/hall/application")
	public GenericDTO hallRecharge(@Validated @RequestBody GenericDTO<HallRechargeApplyDTO> genericResultDTO){
		HallRechargeResultDTO resultDTO=service.hallRecharge(genericResultDTO.getBody());
		return GenericDTO.newSuccessInstance(resultDTO);
	}

	@ApiOperation(value="营业厅充值确认", notes="接收营业厅的充值确认请求")
	@ApiResponse(code = 200, message = "营业厅充值确认结果")
	@PatchMapping(value = "/hall/acknowledgement")
	public GenericDTO hallRechargeConfirm(@Validated @RequestBody GenericDTO<HallRechargeApplyDTO> genericResultDTO){
		HallRechargeResultDTO resultDTO=service.hallRechargeConfirm(genericResultDTO.getBody());
		return GenericDTO.newSuccessInstance(resultDTO);
	}
	
	@ApiOperation(value="海币充值下单", notes="生成充值订单，调用收银台")
	@ApiResponse(code = 200, message = "充值下单")
    @PostMapping(value = "/order/sea")
    public GenericDTO<RechargeHCouponDO> createHCouponOrder(@Validated @RequestBody GenericDTO<RechargeHCouponDTO> rechargeHCouponDTO) {
		RechargeHCouponDO rechargeSea=this.service.createHCouponOrder(rechargeHCouponDTO);
		GenericDTO dto = GenericDTO.newSuccessInstance(rechargeSea.getClass());
		dto.setBody(rechargeSea);
		return dto;
    }
	
	
	@ApiOperation(value="海币充值处理结果通知", notes="接收收银台的处理结果通知")
	@ApiResponse(code = 200, message = "处理通知结果")
	@PatchMapping(value = "/result/sea")
	public GenericDTO<NoBody> completeHCouponOrder(@Validated @RequestBody GenericDTO<RechargeHCouponResultDTO> rechargeHCouponDTO){
		service.handleHCouponResult(rechargeHCouponDTO);
		return GenericDTO.newSuccessInstance();
	}
}
