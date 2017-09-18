package com.hisun.lemon.pwm.entity;

import com.hisun.lemon.framework.data.BaseDO;
import java.time.LocalDateTime;

public class WithdrawCardBindDO extends BaseDO {
    /**
     * @Fields cardId 主键
     */
    private String cardId;
    /**
     * @Fields cardNo 加密银行卡号
     */
    private String cardNo;
    /**
     * @Fields cardNoLast 银行卡后四位
     */
    private String cardNoLast;
    /**
     * @Fields branchName 支行名称
     */
    private String branchName;
    /**
     * @Fields userId 用户编号
     */
    private String userId;
    /**
     * @Fields capCorg 资金机构
     */
    private String capCorg;
    /**
     * @Fields cardStatus 卡状态 1生效 0失效
     */
    private String cardStatus;
    /**
     * @Fields eftTm 生效时间
     */
    private LocalDateTime eftTm;
    /**
     * @Fields failTm 失效时间
     */
    private String failTm;
    /**
     * @Fields remark 备注
     */
    private String remark;

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getCardNoLast() {
        return cardNoLast;
    }

    public void setCardNoLast(String cardNoLast) {
        this.cardNoLast = cardNoLast;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCapCorg() {
        return capCorg;
    }

    public void setCapCorg(String capCorg) {
        this.capCorg = capCorg;
    }

    public String getCardStatus() {
        return cardStatus;
    }

    public void setCardStatus(String cardStatus) {
        this.cardStatus = cardStatus;
    }

    public LocalDateTime getEftTm() {
        return eftTm;
    }

    public void setEftTm(LocalDateTime eftTm) {
        this.eftTm = eftTm;
    }

    public String getFailTm() {
        return failTm;
    }

    public void setFailTm(String failTm) {
        this.failTm = failTm;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}