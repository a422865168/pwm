package com.hisun.lemon.pwm.dao;

import com.hisun.lemon.framework.dao.BaseDao;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午1:54:26
 *
 */
@Mapper
@Component
public interface IWithdrawOrderDao extends BaseDao<WithdrawOrderDO>{
    public List<WithdrawOrderDO> queryList(Map o);
}
