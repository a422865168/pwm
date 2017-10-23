package com.hisun.lemon.pwm.controller;

/**
 * @author leon
 * @date 2017/7/14
 * @time 16:52
 */
//@RunWith(SpringRunner.class)
//@SpringBootTest
public class WithdrawOrderControllerTest {

   // @Autowired
   // private WebApplicationContext context;


   // @Before
   // public void setupMockMvc() throws Exception {

   // }

    /**
     * 测试生成提现订单
     * @throws Exception
     */
//    @Test
//    @Ignore
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
//    @Test
//    @Ignore
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


//    @Test
//    @Ignore
    public void hcouponTest(){
//        String descStr=getViewOrderInfo(PwmConstants.BUS_TYPE_HCOUPON,args);
//        System.out.println(descStr);
 //       System"0501".substring(2);
    }
}
