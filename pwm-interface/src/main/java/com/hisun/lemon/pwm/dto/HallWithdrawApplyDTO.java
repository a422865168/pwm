package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 营业厅个人提现请求 传输对象
 * @author xian
 * @date 2017年11月1日
 * @time 上午9:27:30
 *
 */
@ApiModel(" 营业厅个人提现请求对象")
public class HallWithdrawApplyDTO {

    /** 商户号**/
    @ApiModelProperty(name = "merchantId", value = "营业厅商户id", required = true)
    @NotNull(message = "PWM10010")
    private String merchantId;

    /** 商户名**/
    @ApiModelProperty(name = "merchantName", value = "营业厅商户名称", required = true)
    private String merchantName;


    /** 用户手机号 */
    @ApiModelProperty(name = "mblNo", value = "提现用户手机号", required = true)
    @NotNull(message = "PWM10035")
    @Length(max = 20)
    private String mblNo;

    @ApiModelProperty(name = "orderCcy", value = "币种(USD:美元)")
    @NotEmpty(message="PWM10020")
    @Length(max =3)
    private String orderCcy;

    /** 结算金额 */
    @ApiModelProperty(name = "settleAmt", value = "提现金额", required = true)
    @NotNull(message = "PWM10041")
    @Min(value=0, message="PWM10029")
    private BigDecimal withdrawAmt;

    //提现手续费
    @ApiModelProperty(name = "feeAmt", value = "提现手续费")
    @Min(value=0, message="PWM10030")
    private BigDecimal feeAmt;

    /** 业务订单号 */
    @ApiModelProperty(name = "busOrderNo", value = "业务订单号(营业厅请求订单号)", required = true)
    @NotNull(message = "PWM10007")
    private String busOrderNo;

    /** 支付密码 */
    @ApiModelProperty(name = "payPassword", value = "支付密码", required = true)
    @NotNull(message = "PWM10034")
    private String payPassword;

    /** 支付密码随机数 */
    @ApiModelProperty(name = "payPwdRandom", value = "支付密码随机数", required = true)
    @NotNull(message = "PWM10054")
    private String payPwdRandom;

    public String getMblNo() {
        return mblNo;
    }

    public void setMblNo(String mblNo) {
        this.mblNo = mblNo;
    }

    public String getOrderCcy() {
        return orderCcy;
    }

    public void setOrderCcy(String orderCcy) {
        this.orderCcy = orderCcy;
    }

    public BigDecimal getWithdrawAmt() {
        return withdrawAmt;
    }

    public void setWithdrawAmt(BigDecimal withdrawAmt) {
        this.withdrawAmt = withdrawAmt;
    }

    public String getBusOrderNo() {
        return busOrderNo;
    }

    public void setBusOrderNo(String busOrderNo) {
        this.busOrderNo = busOrderNo;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }

    public String getPayPwdRandom() {
        return payPwdRandom;
    }

    public void setPayPwdRandom(String payPwdRandom) {
        this.payPwdRandom = payPwdRandom;
    }

    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}
