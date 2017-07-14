package com.hisun.lemon.pwm.service.impl;

import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.pwm.dao.IRechangeOrderDao;
import com.hisun.lemon.pwm.entity.RechangeOrderDO;
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
public class RechangeOrderTransactionalService {

    @Resource
    private IRechangeOrderDao rechangeOrderDao;
    public IRechangeOrderDao getUserDao() {
        return rechangeOrderDao;
    }

    public void setUserDao(IRechangeOrderDao rechangeOrderDao) {
        this.rechangeOrderDao = rechangeOrderDao;
    }

    public void initOrder(RechangeOrderDO rechangeOrderDO){
        int nums=rechangeOrderDao.insert(rechangeOrderDO);
        if(nums!=1){
            throw new LemonException("PWM20006");
        }
    }

}
