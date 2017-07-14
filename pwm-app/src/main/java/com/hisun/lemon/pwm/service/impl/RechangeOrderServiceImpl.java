package com.hisun.lemon.pwm.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import com.hisun.lemon.pwm.dto.RechangeDTO;
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
import com.hisun.lemon.pwm.dao.IRechangeOrderDao;
import com.hisun.lemon.pwm.dto.RechangeResultDTO;
import com.hisun.lemon.pwm.entity.RechangeOrderDO;
import com.hisun.lemon.pwm.service.IRechangeOrderService;

@Transactional
@Service
public class RechangeOrderServiceImpl implements IRechangeOrderService {
	static final String PAYEE_ID="002002";

	@Resource
	RechangeOrderTransactionalService service;
    @Resource
    CshOrderClient cshOrderClient;


	@Override
	public GenericDTO createOrder(RechangeDTO rechangeDTO,String ipAddress) {
		// TODO Auto-generated method stub
		if(!rechangeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_RECHANGE)){
			throw new LemonException("001");
		}
		
		String ymd=DateTimeUtils.getCurrentDateStr();
		String orderNo=IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE+ymd,15);  
		RechangeOrderDO rechangeOrderDO=new RechangeOrderDO();
		rechangeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechangeOrderDO.setBusType(rechangeDTO.getBusType());
		rechangeOrderDO.setModifyOpr("");
		rechangeOrderDO.setIpAddress(ipAddress);
		rechangeOrderDO.setOrderAmt(rechangeDTO.getAmount());
		rechangeOrderDO.setOrderCcy("USD");
		rechangeOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		rechangeOrderDO.setOrderNo(ymd+orderNo);
		rechangeOrderDO.setOrderStatus(PwmConstants.RECHANGE_ORD_W);
		rechangeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechangeOrderDO.setPsnFlag(rechangeDTO.getPsnFlag());
		rechangeOrderDO.setRemark("");
		rechangeOrderDO.setSysChannel(rechangeDTO.getSysChannel());
		rechangeOrderDO.setTxType("01");
		service.initOrder(rechangeOrderDO);
		
		//调用收银
		InitCashierDTO initCashierDTO=new InitCashierDTO();
	  	initCashierDTO.setBusPaytype(null);
	  	initCashierDTO.setBusType(rechangeOrderDO.getBusType());
	  	initCashierDTO.setCbUrl("http://pwm.com");
	  	initCashierDTO.setExtOrderNo(rechangeOrderDO.getOrderNo());
	  	initCashierDTO.setPayeeId(PAYEE_ID);
	  	initCashierDTO.setSysChannel("web");
	  	initCashierDTO.setPayerId("");
	  	initCashierDTO.setTxType(rechangeOrderDO.getTxType());
		initCashierDTO.setOrderAmt(rechangeDTO.getAmount());
		GenericDTO<InitCashierDTO> genericDTO=new GenericDTO<InitCashierDTO>();
		genericDTO.setBody(initCashierDTO);
		return cshOrderClient.initCashier(genericDTO);
	}

	@Override
	public void updateOrder(RechangeResultDTO rechangeResultDTO) {
		// TODO Auto-generated method stub
		
	}

}
