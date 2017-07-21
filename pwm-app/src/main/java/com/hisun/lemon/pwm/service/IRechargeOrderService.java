package com.hisun.lemon.pwm.service;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.dto.HallQueryResultDTO;
import com.hisun.lemon.pwm.dto.HallRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.HallRechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeDTO;

import java.math.BigDecimal;


/**
 * @author tone
 * @date 2017年6月7日
 * @time 下午2:13:58
 *
 */
public interface IRechargeOrderService {
    public GenericDTO createOrder(RechargeDTO rechargeDTO,String ipAddress);

    /**
     * 接收收银台的结果通知
     * @param resultDto
     */
    public void handleResult(GenericDTO resultDto);

    /**
     * 查询用户信息
     * @param userId
     * @return
     */
    public HallQueryResultDTO queryUserInfo(String userId,BigDecimal amount);
    public HallRechargeResultDTO hallRecharge(HallRechargeApplyDTO dto);

    public HallRechargeResultDTO hallRechargeConfirm(HallRechargeApplyDTO dto);
}
