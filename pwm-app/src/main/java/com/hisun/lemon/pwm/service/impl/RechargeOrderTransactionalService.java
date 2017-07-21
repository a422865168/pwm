package com.hisun.lemon.pwm.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.pwm.dao.IRechargeHCouponDao;
import com.hisun.lemon.pwm.dao.IRechargeOrderDao;
import com.hisun.lemon.pwm.entity.RechargeHCouponDO;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;

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
	private IRechargeHCouponDao rechargeHCouponDao;

	public void initOrder(RechargeOrderDO rechargeOrderDO) {
		int nums = rechangeOrderDao.insert(rechargeOrderDO);
		if (nums != 1) {
			throw new LemonException("PWM20004");
		}
	}

	public void updateOrder(RechargeOrderDO rechargeOrderDO) {
		int nums = rechangeOrderDao.update(rechargeOrderDO);
		if (nums != 1) {
			throw new LemonException("PWM20005");
		}
	}

	/**
	 * 生成海币充值订单
	 * 
	 * @param rechargeSeaDO
	 */
	public void initSeaOrder(RechargeHCouponDO rechargeHCouponDO) {
		int nums = rechargeHCouponDao.insert(rechargeHCouponDO);
		if (nums != 1) {
			throw new LemonException("PWM20006");
		}
	}

	/**
	 * 海币充值结果处理
	 * 
	 * @param rechargeSeaDO
	 */
	public void updateSeaOrder(RechargeHCouponDO rechargeHCouponDO) {
		int nums = rechargeHCouponDao.update(rechargeHCouponDO);
		if (nums != 1) {
			throw new LemonException("PWM20007");
		}
	}

	/**
	 * 查询海币充值订单
	 * 
	 * @return
	 */
  public  RechargeHCouponDO getHCoupon(String orderNo)
  {
	  RechargeHCouponDO rechargeHCouponDO=this.rechargeHCouponDao.get(orderNo);
	  return rechargeHCouponDO;
  }
	public IRechargeOrderDao getRechangeOrderDao() {
		return rechangeOrderDao;
	}

	public void setRechangeOrderDao(IRechargeOrderDao rechangeOrderDao) {
		this.rechangeOrderDao = rechangeOrderDao;
	}

	public IRechargeHCouponDao getRechargeHCouponDao() {
		return rechargeHCouponDao;
	}

	public void setRechargeHCouponDao(IRechargeHCouponDao rechargeHCouponDao) {
		this.rechargeHCouponDao = rechargeHCouponDao;
	}
}
