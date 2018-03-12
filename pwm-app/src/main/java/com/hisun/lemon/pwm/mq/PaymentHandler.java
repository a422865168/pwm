package com.hisun.lemon.pwm.mq;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.common.utils.StringUtils;
import com.hisun.lemon.framework.stream.MultiOutput;
import com.hisun.lemon.framework.stream.producer.Producer;
import com.hisun.lemon.framework.stream.producer.Producers;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import com.hisun.lemon.tfm.dto.TradeFeeReqDTO;


/**
 * @author tone
 * @date 2017年8月18日
 * @time 下午2:54:28
 * @desc
 */
@Component
public class PaymentHandler {
    protected static final Logger logger = LoggerFactory.getLogger(PaymentHandler.class);

 //   @Resource
  //  LemonObjectMapper objectMapper;

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
      //  String data = LemonObjectMapper.writeValueAsString(objectMapper, tradeFeeReqDTO, true);
       // logger.info("登记用户手续费写入消息队列数据：" + data);
        return tradeFeeReqDTO;
    }

    /**
     * 登记商户手续费
     */
    @Producers({
            @Producer(beanName= "merchantTradeFeeConsumer", channelName= MultiOutput.OUTPUT_THREE)
    })
    public TradeFeeReqDTO registMerChantFee(RechargeOrderDO orderDO,String payeeId){
        if(StringUtils.equals(orderDO.getBusType(), PwmConstants.BUS_TYPE_RECHARGE_HALL)){
            TradeFeeReqDTO tradeFeeReqDTO=new TradeFeeReqDTO();
            tradeFeeReqDTO.setCcy(orderDO.getOrderCcy());
            tradeFeeReqDTO.setUserId(payeeId);
            tradeFeeReqDTO.setBusOrderNo(orderDO.getOrderNo());
            tradeFeeReqDTO.setBusOrderTime(DateTimeUtils.getCurrentLocalDateTime());
            tradeFeeReqDTO.setTradeAmt(orderDO.getOrderAmt());
            tradeFeeReqDTO.setBusType(orderDO.getBusType());
         //   String data = ObjectMapperHelper.writeValueAsString(objectMapper, tradeFeeReqDTO, true);
         //   logger.info("登记商户id为:" + payeeId + "手续费写入消息队列数据：" + data);
            return tradeFeeReqDTO;
        }
        logger.info("非营业厅充值订单，不登记商户手续费：" + orderDO.getOrderNo());
        return null;
    }


    /**
     * 登记用户提现手续费
     */
    @Producers({
            @Producer(beanName="tradeFeeConsumer", channelName= MultiOutput.OUTPUT_THREE)
    })
    public TradeFeeReqDTO registUserWithdrawFee(WithdrawOrderDO orderDO){
        if(orderDO.getFeeAmt().compareTo(BigDecimal.ZERO)<=0){
            return null;
        }
        TradeFeeReqDTO tradeFeeReqDTO=new TradeFeeReqDTO();
        tradeFeeReqDTO.setCcy(orderDO.getOrderCcy());
        tradeFeeReqDTO.setUserId(orderDO.getUserId());
        tradeFeeReqDTO.setBusOrderNo(orderDO.getOrderNo());
        tradeFeeReqDTO.setBusOrderTime(DateTimeUtils.getCurrentLocalDateTime());
        tradeFeeReqDTO.setTradeAmt(orderDO.getWcApplyAmt());
        tradeFeeReqDTO.setBusType(orderDO.getBusType());
      //  String data = ObjectMapperHelper.writeValueAsString(objectMapper, tradeFeeReqDTO, true);
     //   logger.info("登记用户手续费写入消息队列数据：" + data);
        return tradeFeeReqDTO;
    }

    @Producers({
            @Producer(beanName= "merchantTransferTradeFeeConsumer", channelName= MultiOutput.OUTPUT_THREE)
    })
    public TradeFeeReqDTO registMerChantWithdrawFee(WithdrawOrderDO orderDO,String payeeId){
        if(StringUtils.equals(orderDO.getBusType(), PwmConstants.BUS_TYPE_WITHDRAW_HALL)){
            TradeFeeReqDTO tradeFeeReqDTO=new TradeFeeReqDTO();
            tradeFeeReqDTO.setCcy(orderDO.getOrderCcy());
            tradeFeeReqDTO.setUserId(payeeId);
            tradeFeeReqDTO.setBusOrderNo(orderDO.getOrderNo());
            tradeFeeReqDTO.setBusOrderTime(DateTimeUtils.getCurrentLocalDateTime());
            tradeFeeReqDTO.setTradeAmt(orderDO.getWcApplyAmt());
            //tfm计费模块已经定义0405为营业厅取现
            tradeFeeReqDTO.setBusType("0405");
           // String data = ObjectMapperHelper.writeValueAsString(objectMapper, tradeFeeReqDTO, true);
          //  logger.info("登记商户id为:" + payeeId + "手续费写入消息队列数据：" + data);
            return tradeFeeReqDTO;
        }
        logger.info("非营业厅提现订单，不登记商户手续费：" + orderDO.getOrderNo());
        return null;
    }

}
