package com.hisun.lemon.pwm.dao;

import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import org.apache.ibatis.annotations.Update;

/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午1:54:26
 *
 */
@Mapper
public interface IWithdrawOrderDao {

    /**
     * 插入记录
     * @param wdcOrderDO
     * @return
     */
    @Insert("INSERT INTO PWM_WDC_ORD(USER_ID, NAME, SEX, BIRTHDAY, AGE, CREATE_TIME, MODIFY_TIME) VALUES(#{userId}, #{name}, #{sex}, #{birthday, javaType=java.time.LocalDate,jdbcType=DATE}, #{age}, #{createTime, javaType=java.time.LocalDateTime,jdbcType=TIMESTAMP}, #{modifyTime, javaType=java.time.LocalDateTime,jdbcType=TIMESTAMP})")
    int insert(WithdrawOrderDO wdcOrderDO);

    /**
     *  更新
     * @mbggenerated
     */
    @Update({
            "update pwm_card_bin",
            "set CRD_BIN = #{crdBin,jdbcType=VARCHAR},",
            "CAP_CORG = #{capCorg,jdbcType=VARCHAR},",
            "CRD_AC_TYP = #{crdAcTyp,jdbcType=CHAR},",
            "CRD_LTH = #{crdLth,jdbcType=INTEGER},",
            "OPR_ID = #{oprId,jdbcType=VARCHAR},",
            "CREATE_TIME = #{createTime,jdbcType=TIMESTAMP},",
            "MODIFY_TIME = #{modifyTime,jdbcType=TIMESTAMP},",
            "TM_SMP = #{tmSmp,jdbcType=TIMESTAMP}",
            "where BIN_ID = #{binId,jdbcType=VARCHAR}"
    })
    int update(WithdrawResultDTO record);
}
