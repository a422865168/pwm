package com.hisun.lemon.pwm.mq;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.framework.data.GenericCmdDTO;
import com.hisun.lemon.framework.stream.MessageHandler;
import com.hisun.lemon.framework.utils.ObjectMapperHelper;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
import com.hisun.lemon.pwm.service.impl.WithdrawOrderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author xian
 * @date 2017年8月28日
 * @time 下午2:54:28
 *
 */
@Component(NoticeHandler.BEAN_NAME)
public class NoticeHandler implements MessageHandler<WithdrawResultDTO> {
    public static final String BEAN_NAME="noticeHandler";
    private static final Logger logger = LoggerFactory.getLogger(NoticeHandler.class);

    @Resource
    protected WithdrawOrderServiceImpl withdrawOrderService;

    @Resource
    ObjectMapper objectMapper;
    @Override
    public void onMessageReceive(GenericCmdDTO<WithdrawResultDTO> genericCmdDTO) {
        logger.info("可执行消息对象GenericCmdDTO：" + genericCmdDTO.toString());
        String data = ObjectMapperHelper.writeValueAsString(objectMapper, genericCmdDTO.getBody(), true);
        logger.info("接收cpo模块通知数据 {}", data);
        WithdrawResultDTO withdrawResultDTO = genericCmdDTO.getBody();
        if(JudgeUtils.isNotNull(withdrawResultDTO)){
            //调用通知处理接口
            withdrawOrderService.completeOrder(genericCmdDTO);
        }

    }

}
