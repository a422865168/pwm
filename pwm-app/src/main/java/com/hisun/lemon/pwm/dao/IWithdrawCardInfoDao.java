/*
 * @ClassName IWithdrawCardInfoDao
 * @Description 
 * @version 1.0
 * @Date 2017-08-15 16:58:53
 */
package com.hisun.lemon.pwm.dao;

import com.hisun.lemon.framework.dao.BaseDao;
import com.hisun.lemon.pwm.entity.WithdrawCardInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface IWithdrawCardInfoDao extends BaseDao<WithdrawCardInfoDO> {
    public List<WithdrawCardInfoDO> query();
}