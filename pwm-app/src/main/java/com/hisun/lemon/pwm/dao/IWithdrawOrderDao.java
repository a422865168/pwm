package com.hisun.lemon.pwm.dao;

import com.hisun.lemon.framework.dao.BaseDao;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午1:54:26
 *
 */
@Mapper
public interface IWithdrawOrderDao extends BaseDao<WithdrawOrderDO>{

    /**
     * 插入记录
     * @param wdcOrderDO
     * @return
     */
    int insert(WithdrawOrderDO wdcOrderDO);
}
