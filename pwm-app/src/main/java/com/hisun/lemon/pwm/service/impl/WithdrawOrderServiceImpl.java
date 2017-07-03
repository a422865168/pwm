package com.hisun.lemon.pwm.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hisun.lemon.pwm.dao.IRechangeOrderDao;
import com.hisun.lemon.pwm.dto.RechangeResultDTO;
import com.hisun.lemon.pwm.entity.RechangeOrderDO;
import com.hisun.lemon.pwm.service.IRechangeOrderService;

@Transactional
@Service
public class WithdrawOrderServiceImpl implements IRechangeOrderService {
    @Resource
    private IRechangeOrderDao rechangeOrderDao;
    
     

    public IRechangeOrderDao getUserDao() {
        return rechangeOrderDao;
    }

    public void setUserDao(IRechangeOrderDao rechangeOrderDao) {
        this.rechangeOrderDao = rechangeOrderDao;
    }

	@Override
	public void createOrder(RechangeOrderDO PpdOrderDO) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateOrder(RechangeResultDTO ppdOrderResultDTO) {
		// TODO Auto-generated method stub
		
	}

}
