/*
 * @ClassName IWithdrawCardBindDao
 * @Description 
 * @version 1.0
 * @Date 2017-08-15 16:58:53
 */
package com.hisun.lemon.pwm.dao;

import com.hisun.lemon.framework.dao.BaseDao;
import com.hisun.lemon.pwm.entity.WithdrawCardBindDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface IWithdrawCardBindDao extends BaseDao<WithdrawCardBindDO> {
    //查询提现银行卡号是否存在
    public WithdrawCardBindDO query(String card_no);
}