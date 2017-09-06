/*
 * @ClassName WithdrawCardInfoDO
 * @Description 
 * @version 1.0
 * @Date 2017-08-15 16:58:53
 */
package com.hisun.lemon.pwm.entity;

import com.hisun.lemon.framework.data.BaseDO;
import java.time.LocalDateTime;

public class WithdrawCardInfoDO extends BaseDO {
    /**
     * @Fields binId 主键
     */
    private String binId;
    /**
     * @Fields cardBin 卡bin
     */
    private String cardBin;
    /**
     * @Fields capCorg 资金机构
     */
    private String capCorg;
    /**
     * @Fields bankName 提现银行
     */
    private String bankName;
    /**
     * @Fields cardAcType 卡类型，D借记卡，C贷记卡
     */
    private String cardAcType;
    /**
     * @Fields cardLength 卡长度
     */
    private Integer cardLength;
    /**
     * @Fields oprId 操作员
     */
    private String oprId;

    public String getBinId() {
        return binId;
    }

    public void setBinId(String binId) {
        this.binId = binId;
    }

    public String getCardBin() {
        return cardBin;
    }

    public void setCardBin(String cardBin) {
        this.cardBin = cardBin;
    }

    public String getCapCorg() {
        return capCorg;
    }

    public void setCapCorg(String capCorg) {
        this.capCorg = capCorg;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCardAcType() {
        return cardAcType;
    }

    public void setCardAcType(String cardAcType) {
        this.cardAcType = cardAcType;
    }

    public Integer getCardLength() {
        return cardLength;
    }

    public void setCardLength(Integer cardLength) {
        this.cardLength = cardLength;
    }

    public String getOprId() {
        return oprId;
    }

    public void setOprId(String oprId) {
        this.oprId = oprId;
    }

}