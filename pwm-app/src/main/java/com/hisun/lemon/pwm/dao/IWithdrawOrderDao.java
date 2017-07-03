package com.hisun.lemon.pwm.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.hisun.lemon.pwm.entity.WithdrawOrderDO;

/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午1:54:26
 *
 */
@Mapper
public interface IWithdrawOrderDao {
    
    @Insert("INSERT INTO USER(USER_ID, NAME, SEX, BIRTHDAY, AGE, CREATE_TIME, MODIFY_TIME) VALUES(#{userId}, #{name}, #{sex}, #{birthday, javaType=java.time.LocalDate,jdbcType=DATE}, #{age}, #{createTime, javaType=java.time.LocalDateTime,jdbcType=TIMESTAMP}, #{modifyTime, javaType=java.time.LocalDateTime,jdbcType=TIMESTAMP})")
    int insert(WithdrawOrderDO wdcOrderDO);
    
}
