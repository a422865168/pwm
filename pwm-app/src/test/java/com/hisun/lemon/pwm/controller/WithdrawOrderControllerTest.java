package com.hisun.lemon.pwm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.constants.PwmConstants;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
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
@RunWith(SpringRunner.class)
@SpringBootTest
public class WithdrawOrderControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }
    @Test
    public void hcouponTest(){
        Object[] args=new Object[]{100};
//        String descStr=getViewOrderInfo(PwmConstants.BUS_TYPE_HCOUPON,args);
//        System.out.println(descStr);
        System.out.println("0501".substring(2));
    }
}
