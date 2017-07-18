package com.hisun.lemon.pwm.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.common.utils.StringUtils;
import com.hisun.lemon.csh.client.CshOrderClient;
import com.hisun.lemon.csh.order.dto.InitCashierDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.utils.IdGenUtils;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
import com.hisun.lemon.pwm.service.IRechargeOrderService;

@Transactional
@Service
public class RechargeOrderServiceImpl implements IRechargeOrderService {

	@Resource
	RechargeOrderTransactionalService service;
    @Resource
    CshOrderClient cshOrderClient;


	@Override
	public GenericDTO createOrder(RechargeDTO rechargeDTO,String ipAddress) {
		if(!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_RECHANGE)){
			throw new LemonException("PWM20001");
		}
		
		String ymd=DateTimeUtils.getCurrentDateStr();
		String orderNo=IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE+ymd,15);  
		RechargeOrderDO rechargeOrderDO =new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(rechargeDTO.getBusType());
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setIpAddress(ipAddress);
		rechargeOrderDO.setOrderAmt(rechargeDTO.getAmount());
		rechargeOrderDO.setOrderCcy("USD");
		rechargeOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		rechargeOrderDO.setOrderNo(ymd+orderNo);
		rechargeOrderDO.setOrderStatus(PwmConstants.RECHANGE_ORD_W);
		rechargeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setPsnFlag(rechargeDTO.getPsnFlag());
		rechargeOrderDO.setRemark("");
		rechargeOrderDO.setSysChannel(rechargeDTO.getSysChannel());
		rechargeOrderDO.setTxType("01");
		service.initOrder(rechargeOrderDO);
		
		//调用收银
		InitCashierDTO initCashierDTO=new InitCashierDTO();
	  	initCashierDTO.setBusPaytype(null);
	  	initCashierDTO.setBusType(rechargeOrderDO.getBusType());
	  	initCashierDTO.setCbUrl(LemonUtils.getProperty("pwm.rechargeCbUrl"));
	  	initCashierDTO.setExtOrderNo(rechargeOrderDO.getOrderNo());
	  	initCashierDTO.setPayeeId(LemonUtils.getProperty("pwm.defaultPayeeId"));
	  	initCashierDTO.setSysChannel(rechargeDTO.getSysChannel());
	  	initCashierDTO.setPayerId("");
	  	initCashierDTO.setTxType(rechargeOrderDO.getTxType());
		initCashierDTO.setOrderAmt(rechargeDTO.getAmount());
		GenericDTO<InitCashierDTO> genericDTO=new GenericDTO<>();
		genericDTO.setBody(initCashierDTO);
		return cshOrderClient.initCashier(genericDTO);
	}

	@Override
	public void handleResult(GenericDTO resultDto) {
		RechargeResultDTO rechargeResultDTO =(RechargeResultDTO)resultDto.getBody();

		String orderNo = rechargeResultDTO.getOrderNo();

		RechargeOrderDO rechargeOrderDO =service.getRechangeOrderDao().get(orderNo);

		//未找到订单
		if(rechargeOrderDO ==null){
			throw new LemonException("PWM20002");
		}

		//订单已经成功
		if(StringUtils.equals(rechargeOrderDO.getOrderStatus(),PwmConstants.RECHANGE_ORD_S)){
			return;
		}

		//判断返回状态
		if(!StringUtils.equals(rechargeResultDTO.getStatus(),PwmConstants.RECHANGE_ORD_S)){
			RechargeOrderDO updOrderDO =new RechargeOrderDO();
			updOrderDO.setExtOrderNo(rechargeResultDTO.getExtOrderNo());
			updOrderDO.setOrderStatus(PwmConstants.RECHANGE_ORD_F);
			updOrderDO.setOrderCcy(rechargeResultDTO.getOrderCcy());
			updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
			updOrderDO.setOrderNo(orderNo);
			service.updateOrder(updOrderDO);
			return;
		}

		//比较金额
		BigDecimal amount= rechargeResultDTO.getAmount();
		if(rechargeOrderDO.getOrderAmt().compareTo(amount)!=0){
			throw new LemonException("PWM20003");
		}

		//账务处理

		//更新订单
		RechargeOrderDO updOrderDO =new RechargeOrderDO();
		updOrderDO.setAcTm(resultDto.getAccDate());
		updOrderDO.setExtOrderNo(rechargeResultDTO.getExtOrderNo());
		updOrderDO.setOrderStatus(PwmConstants.RECHANGE_ORD_S);
		updOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
		updOrderDO.setOrderCcy(rechargeResultDTO.getOrderCcy());
		updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		updOrderDO.setOrderNo(orderNo);
        service.updateOrder(updOrderDO);
	}

}
