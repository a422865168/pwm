package com.hisun.lemon.pwm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.framework.utils.IdGenUtils;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

/**
 * @author leon
 * @date 2017/7/14
 * @time 16:52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WithdrawOrderControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    /**
     * 测试生成提现订单
     * @throws Exception
     */
    @Test
    public void testCreateOrder() throws Exception{
        WithdrawOrderDO withdrawOrderDO = new WithdrawOrderDO();
        String ymd= DateTimeUtils.getCurrentDateStr();
        String orderNo= IdGenUtils.generateId("pwmOrdNo"+ymd,15);
        withdrawOrderDO.setOrderNo(ymd+orderNo);
        //withdrawOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
        //withdrawOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
        //withdrawOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
        withdrawOrderDO.setCapCardNo("238746724");
        withdrawOrderDO.setWcApplyAmt(new BigDecimal(12.34));
        withdrawOrderDO.setOrderCcy("RMB");
        withdrawOrderDO.setWcType("11");
        withdrawOrderDO.setPayUrgeFlg("0");
        withdrawOrderDO.setCapCorgNo("2334");
        withdrawOrderDO.setWcRemark("this is a message");
        withdrawOrderDO.setPayPassword("password");
        withdrawOrderDO.setBusCnl("abcde");

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(withdrawOrderDO);
        RequestBuilder request = MockMvcRequestBuilders.post("/pwm/withdraw/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(requestJson);
        MvcResult mvcResult = mockMvc.perform(request).andReturn();
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();

        Assert.assertTrue("正确", status == 200);
        Assert.assertFalse("错误", status != 200);
    }
}
