/*
 * @ClassName RechangeOrderDao
 * @Description 
 * @version 1.0
 * @Date 2017-07-12 16:42:59
 */
package com.hisun.lemon.pwm.dao;

import com.hisun.lemon.framework.dao.BaseDao;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface IRechargeOrderDao extends BaseDao<RechargeOrderDO> {

	public RechargeOrderDO getRechargeOrderByExtOrderNo(@Param("extOrderNo")String extOrderNo);

	public RechargeOrderDO getRechargeOrderByHallOrderNo(@Param("hallOrderNo")String hallOrderNo);

	public List<RechargeOrderDO> queryList(Map o);

}