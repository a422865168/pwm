package com.hisun.lemon.pwm.service.impl;

import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.pwm.dao.IRechargeOrderDao;
import com.hisun.lemon.pwm.dao.IRechargeSeaDao;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
import com.hisun.lemon.pwm.entity.RechargeSeaDO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author leon
 * @date 2017/7/11
 * @time 11:28
 */
@Transactional
@Service
public class RechargeOrderTransactionalService {

    @Resource
    private IRechargeOrderDao rechangeOrderDao;
    @Resource
    private IRechargeSeaDao rechangeSeaDao;

    public void initOrder(RechargeOrderDO rechargeOrderDO){
        int nums=rechangeOrderDao.insert(rechargeOrderDO);
        if(nums!=1){
            throw new LemonException("PWM20004");
        }
    }

    public void updateOrder(RechargeOrderDO rechargeOrderDO){
        int nums=rechangeOrderDao.update(rechargeOrderDO);
        if(nums!=1){
            throw new LemonException("PWM20005");
        }
    }
    
    /**
     * 生成海币充值订单
     * @param rechargeSeaDO
     */
    public void initSeaOrder(RechargeSeaDO rechargeSeaDO){
        int nums=rechangeSeaDao.insert(rechargeSeaDO);
        if(nums!=1){
            throw new LemonException("PWM20006");
        }
    }
     
    /**
     * 海币充值结果处理
     * @param rechargeSeaDO
     */
    public void updateSeaOrder(RechargeSeaDO rechargeSeaDO){
        int nums=rechangeSeaDao.update(rechargeSeaDO);
        if(nums!=1){
            throw new LemonException("PWM20007");
        }
    }
    
    /**
     * 查询海币充值订单
     * @return
     */
    
    public RechargeSeaDO getRechangeSea(String orderNo) {
    	RechargeSeaDO rechargeSeaDO=rechangeSeaDao.get(orderNo);
        return rechargeSeaDO;
    }
    
    public IRechargeOrderDao getRechangeOrderDao() {
        return rechangeOrderDao;
    }

    public void setRechangeOrderDao(IRechargeOrderDao rechangeOrderDao) {
        this.rechangeOrderDao = rechangeOrderDao;
    }

	public IRechargeSeaDao getRechangeSeaDao() {
		return rechangeSeaDao;
	}

	public void setRechangeSeaDao(IRechargeSeaDao rechangeSeaDao) {
		this.rechangeSeaDao = rechangeSeaDao;
	}
    
    
}
