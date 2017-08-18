package com.hisun.lemon.pwm.service.impl;

import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.pwm.dao.IWithdrawCardBindDao;
import com.hisun.lemon.pwm.dao.IWithdrawOrderDao;
import com.hisun.lemon.pwm.entity.WithdrawCardBindDO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
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
public class WithdrawOrderTransactionalService {

    @Resource
    private IWithdrawOrderDao withdrawOrderDao;

    @Resource
    private IWithdrawCardBindDao withdrawCardBindDao;
    /**
     * 提现申请，生成订单
     * @param withdrawOrderDO
     */
    public void createOrder(WithdrawOrderDO withdrawOrderDO){

        int num = withdrawOrderDao.insert(withdrawOrderDO);
        if(num != 1){
            LemonException.throwBusinessException("PWM20004");
        }
    }

    /**
     * 提现成功，更新提现单据信息
     */
    public void updateOrder(WithdrawOrderDO withdrawOrderDO){

        int num = withdrawOrderDao.update(withdrawOrderDO);
        if(num != 1){
            LemonException.throwBusinessException("PWM20005");
        }
    }

    /**
     * 校验订单是否存在
     */
    public WithdrawOrderDO query(String orderNo){

        WithdrawOrderDO withdrawOrderDO = withdrawOrderDao.get(orderNo);
        //订单号不存在，则抛异常
        if(JudgeUtils.isNull(withdrawOrderDO)){
            LemonException.throwBusinessException("PWM30005");
        }
        return withdrawOrderDO;
    }

    /**
     * 更新银行卡状态
     * @param withdrawCardBindDO
     */
    public void updateCard(WithdrawCardBindDO withdrawCardBindDO) {
        int num = withdrawCardBindDao.update(withdrawCardBindDO);
        if(num != 1){
            LemonException.throwBusinessException("PWM20019");
        }
    }

    /**
     * 添加提现银行卡
     */
    public void addCard(WithdrawCardBindDO withdrawCardBindDO){

        int num = withdrawCardBindDao.insert(withdrawCardBindDO);
        if(num != 1){
            LemonException.throwBusinessException("PWM20020");
        }
    }
}
