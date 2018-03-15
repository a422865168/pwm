package com.hisun.lemon.pwm.mq;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.lemon.bil.constants.BilConstants;
import com.hisun.lemon.bil.dto.CreateUserBillDTO;
import com.hisun.lemon.bil.dto.UpdateUserBillDTO;
import com.hisun.lemon.framework.stream.MultiOutput;
import com.hisun.lemon.framework.stream.producer.Producer;
import com.hisun.lemon.framework.stream.producer.Producers;
import com.hisun.lemon.framework.utils.ObjectMapperHelper;

/**
 * @author tone
 * @date 2017年8月5日
 * @time 下午2:54:28
 *
 */
@Component
public class BillSyncHandler {
	protected static final Logger logger = LoggerFactory.getLogger(BillSyncHandler.class);
	@Resource
	ObjectMapper objectMapper;

	/**
	 * 生成用户商户订单信息
	 */
	@Producers({ @Producer(beanName = BilConstants.SYNC_CREATE_BEAN, channelName = MultiOutput.OUTPUT_SIX) // channelName
																											// 为将主题发送出去的通道名，如配置文件中的output
	})
	public CreateUserBillDTO createBill(CreateUserBillDTO createUserBillDTO) {
		String data = ObjectMapperHelper.writeValueAsString(objectMapper, createUserBillDTO, true);
		logger.info("创建订单同步账单写入消息队列：" + data);
		return createUserBillDTO;
	}

	/**
	 * 更新用户商户订单信息
	 */
	@Producers({ @Producer(beanName = BilConstants.SYNC_UPDATE_BEAN, channelName = MultiOutput.OUTPUT_SIX) })
	public UpdateUserBillDTO updateBill(UpdateUserBillDTO updateUserBillDTO) {
		String data = ObjectMapperHelper.writeValueAsString(objectMapper, updateUserBillDTO, true);
		logger.info("更新订单同步账单写入消息队列：" + data);
		return updateUserBillDTO;
	}

}
