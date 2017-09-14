package com.hisun.lemon.pwm.service.impl;

import javax.annotation.Resource;

import com.hisun.lemon.framework.service.BaseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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
public class RechargeOrderTransactionalService extends BaseService {

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
	 * @param rechargeHCouponDO
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
	 * @param rechargeHCouponDO
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
	@Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
	public RechargeHCouponDO getHCoupon(String orderNo) {
		RechargeHCouponDO rechargeHCouponDO = this.rechargeHCouponDao.get(orderNo);
		return rechargeHCouponDO;
	}

	/**
	 * 根据外部收银订订单号与订单状态查询充值订单
	 * @param extOrderNo 外部充值订单号
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
	public RechargeOrderDO getRechargeOrderByExtOrderNo(String extOrderNo) {
		RechargeOrderDO rechargeOrderDO = this.rechangeOrderDao.getRechargeOrderByExtOrderNo(extOrderNo);
		return rechargeOrderDO;
	}

	/**
	 * 根据营业厅充值订单号查询充值订单信息
	 * @param hallOrderNo
	 * @return
	 */
	@Transactional(propagation = Propagation.SUPPORTS,readOnly = true)
	public RechargeOrderDO getRechargeOrderByHallOrderNo(String hallOrderNo) {
		RechargeOrderDO rechargeOrderDO = this.rechangeOrderDao.getRechargeOrderByHallOrderNo(hallOrderNo);
		return rechargeOrderDO;
	}

	/**
	 * 查询订单信息
	 * 
	 * @param orderNo
	 * @return
	 */
	@Transactional(readOnly = true)
	public RechargeHCouponDO rechargeInfo(String orderNo) {
		return this.getHCoupon(orderNo);
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
