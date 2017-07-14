package com.hisun.lemon.pwm.service.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

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
    @Resource
    private IRechangeOrderDao rechangeOrderDao;
    
    @Resource
    CshOrderClient cshOrderClient;

    public IRechangeOrderDao getUserDao() {
        return rechangeOrderDao;
    }

    public void setUserDao(IRechangeOrderDao rechangeOrderDao) {
        this.rechangeOrderDao = rechangeOrderDao;
    }

	@Override
	public GenericDTO createOrder(Double amount,String psnFlag,String busType,String sysChannel,String ipAddress) {
		// TODO Auto-generated method stub
		
		if(StringUtils.isBlank(busType)){
			busType="";
		}
		if(!StringUtils.isBlank(busType)&&!busType.startsWith(PwmConstants.TX_TYPE_RECHANGE)){
			throw new LemonException("001");
		}
		
		String ymd=DateTimeUtils.getCurrentDateStr();
		String orderNo=IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE+ymd,15);  
		RechangeOrderDO rechangeOrderDO=new RechangeOrderDO();
		rechangeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechangeOrderDO.setBusType(busType); 
		rechangeOrderDO.setModifyOpr("");
		rechangeOrderDO.setIpAddress(ipAddress);
		rechangeOrderDO.setOrderAmt(new BigDecimal(amount));
		rechangeOrderDO.setOrderCcy("USD");
		rechangeOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		rechangeOrderDO.setOrderNo(orderNo);
		rechangeOrderDO.setOrderStatus(PwmConstants.RECHANGE_ORD_W);
		rechangeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechangeOrderDO.setPsnFlag(psnFlag);
		rechangeOrderDO.setRemark("");
		rechangeOrderDO.setSysChannel(sysChannel);
		rechangeOrderDO.setTxType("01");
		rechangeOrderDao.insert(rechangeOrderDO);
		
		//调用收银
		InitCashierDTO initCashierDTO=new InitCashierDTO();
//		initCashierDTO.setBusPaytype(busPaytype);
//		GenericDTO genericDTO=new GenericDTO();
//		genericDTO.setBody(genericDTO); 
//		cshOrderClient.initCashier(genericDTO);
		return null;
	}

	@Override
	public void updateOrder(RechangeResultDTO rechangeResultDTO) {
		// TODO Auto-generated method stub
		
	}

}
