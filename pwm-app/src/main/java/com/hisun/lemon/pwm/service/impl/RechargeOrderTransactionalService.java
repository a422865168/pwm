package com.hisun.lemon.pwm.service.impl;

import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.pwm.dao.IRechargeOrderDao;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
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

    public IRechargeOrderDao getRechangeOrderDao() {
        return rechangeOrderDao;
    }

    public void setRechangeOrderDao(IRechargeOrderDao rechangeOrderDao) {
        this.rechangeOrderDao = rechangeOrderDao;
    }
}
