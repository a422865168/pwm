/*
 * @ClassName RechargeHCouponDao
 * @Description 
 * @version 1.0
 * @Date 2017-07-21 17:27:24
 */
package com.hisun.lemon.pwm.dao;

import com.hisun.lemon.framework.dao.BaseDao;
import com.hisun.lemon.pwm.entity.RechargeHCouponDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface IRechargeHCouponDao extends BaseDao<RechargeHCouponDO> {

    public List<RechargeHCouponDO> queryList(Map o);
}