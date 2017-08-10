package com.hisun.lemon.pwm.dto;

import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.framework.validation.ClientValidated;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


/**
 * 交易费率查询请求传输对象
 * @author leon
 * @date 2017年8月9日
 * @time 上午19:27:30
 *
 */
@ClientValidated
@ApiModel("交易费率查询请求传输对象")
public class WithdrawRateDTO extends GenericDTO<NoBody>{
	/** 业务类型 */
	@ApiModelProperty(name = "busType", value = "业务类型", required = true)
	@NotNull(message = "PWM10038")
	@Length(max = 4)
	private String busType;
	/** 币种 */
	@ApiModelProperty(name = "ccy", value = "币种,KHR|USD|CNY", required = true)
	@Pattern(regexp = "KHR|USD|CNY", message = "PWM10017")
	private String ccy;

	public String getBusType() {
		return busType;
	}

	public void setBusType(String busType) {
		this.busType = busType;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}
}
