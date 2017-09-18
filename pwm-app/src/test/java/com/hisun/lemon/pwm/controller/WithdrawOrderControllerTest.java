package com.hisun.lemon.pwm.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dto.WithdrawResultDTO;
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


    @Before
    public void setupMockMvc() throws Exception {

    }

    /**
     * 测试生成提现订单
     * @throws Exception
     */
    @Test
    public void testCreateOrder() throws Exception{

/*        WithdrawResultDTO withdrawResultDTO = new WithdrawResultDTO();
        withdrawResultDTO.setUserId("userId");
        withdrawResultDTO.setPayPassWord("password2");
        withdrawResultDTO.setCapCardNo("666667888");
        withdrawResultDTO.setWcApplyAmt(BigDecimal.valueOf(33.44));
        withdrawResultDTO.setFeeAmt(BigDecimal.valueOf(2.66));
        withdrawResultDTO.setOrderCcy("RMB");
        withdrawResultDTO.setWcType("11");
        withdrawResultDTO.setPayUrgeFlg("0");
        withdrawResultDTO.setCapCorgNo("6123456");
        withdrawResultDTO.setWcRemark("is a remark");
        withdrawResultDTO.setBusCnl("edcba");
        withdrawResultDTO.setNtfMbl("19945678901");
        GenericDTO<WithdrawResultDTO> genericWithdrawResultDTO = new GenericDTO<WithdrawResultDTO>();
        genericWithdrawResultDTO.setBody(withdrawResultDTO);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(genericWithdrawResultDTO);
        RequestBuilder request = MockMvcRequestBuilders.post("/pwm/withdraw/order")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(requestJson);
        MvcResult mvcResult = mockMvc.perform(request).andReturn();
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();

        Assert.assertTrue("正确", status == 200);
        Assert.assertFalse("错误", status != 200);*/
    }


    /**
     * 测试提现结果处理
     * @throws Exception
     */
    @Test
    public void testCompleteOrder() throws Exception{

       /* WithdrawComplDTO withdrawComplDTO = new WithdrawComplDTO();
        withdrawComplDTO.setOrderNo("20170717000000000001501");
        withdrawComplDTO.setWcActAmt(new BigDecimal(36));
        withdrawComplDTO.setRspOrderNo("111110304958940");
        withdrawComplDTO.setOrderStatus(PwmConstants.WITHDRAW_ORD_S1);
        GenericDTO<WithdrawComplDTO> genericWithdrawComplDTO = new GenericDTO<WithdrawComplDTO>();
        genericWithdrawComplDTO.setBody(withdrawComplDTO);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        java.lang.String requestJson = ow.writeValueAsString(genericWithdrawComplDTO);
        RequestBuilder request = MockMvcRequestBuilders.patch("/pwm/withdraw/result")
                .contentType(MediaType.APPLICATION_JSON_UTF8).content(requestJson);
        MvcResult mvcResult = mockMvc.perform(request).andReturn();
        int status = mvcResult.getResponse().getStatus();
        String content = mvcResult.getResponse().getContentAsString();

        Assert.assertTrue("正确", status == 200);
        Assert.assertFalse("错误", status != 200);*/
    }


    @Test
    public void hcouponTest(){
        Object[] args=new Object[]{100};
//        String descStr=getViewOrderInfo(PwmConstants.BUS_TYPE_HCOUPON,args);
//        System.out.println(descStr);
 //       System"0501".substring(2);
    }
}
