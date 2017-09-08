package com.hisun.lemon.pwm.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.framework.stream.MultiOutput;
import com.hisun.lemon.framework.stream.producer.Producer;
import com.hisun.lemon.framework.stream.producer.Producers;
import com.hisun.lemon.framework.utils.ObjectMapperHelper;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
import com.hisun.lemon.tfm.dto.TradeFeeReqDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;


/**
 * @author tone
 * @date 2017年8月18日
 * @time 下午2:54:28
 * @desc
 */
@Component
public class PaymentHandler {
    protected static final Logger logger = LoggerFactory.getLogger(PaymentHandler.class);

    @Resource
    ObjectMapper objectMapper;

    /**
     * 登记用户手续费
     */
    @Producers({
        @Producer(beanName="tradeFeeConsumer", channelName= MultiOutput.OUTPUT_THREE)
    })
    public TradeFeeReqDTO registUserFee(RechargeOrderDO orderDO){
        if(orderDO.getFee().compareTo(BigDecimal.ZERO)<=0){
            return null;
        }
        TradeFeeReqDTO tradeFeeReqDTO=new TradeFeeReqDTO();
        tradeFeeReqDTO.setCcy(orderDO.getOrderCcy());
        tradeFeeReqDTO.setUserId(orderDO.getPayerId());
        tradeFeeReqDTO.setBusOrderNo(orderDO.getOrderNo());
        tradeFeeReqDTO.setBusOrderTime(DateTimeUtils.getCurrentLocalDateTime());
        tradeFeeReqDTO.setTradeAmt(orderDO.getOrderAmt());
        tradeFeeReqDTO.setBusType(orderDO.getBusType());
        String data = ObjectMapperHelper.writeValueAsString(objectMapper, tradeFeeReqDTO, true);
        logger.info("登记用户手续费写入消息队列数据：" + data);
        return tradeFeeReqDTO;
    }
}
