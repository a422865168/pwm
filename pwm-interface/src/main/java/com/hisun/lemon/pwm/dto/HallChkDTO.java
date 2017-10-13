package com.hisun.lemon.pwm.dto;

import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * Created by XianSky on 2017/10/13.
 */
public class HallChkDTO {

    /**
     * 业务类型 ： 充值 RC  提现
     */
    @ApiModelProperty(name = "type", value = "WC:提现；RC:充值")
    @NotBlank(message="PWM10056")
    private String type;

    @ApiModelProperty(name = "date", value = "营业厅指定平台获取的对账文件日期（8位年月日）")
    @NotBlank(message="PWM10057")
    private String date;

    @ApiModelProperty(name = "filename", value = "营业厅指定平台获取的对账文件名称")
    @NotBlank(message="PWM10058")
    private String filename;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
