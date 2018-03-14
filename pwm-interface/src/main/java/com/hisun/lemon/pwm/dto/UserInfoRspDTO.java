package com.hisun.lemon.pwm.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModelProperty;

/**
 * 创建转账到用户对象
 * 
 * @author ruan
 * @date 2017年6月14日
 * @time 上午9:27:30
 *
 */
public class UserInfoRspDTO {
	/**
     * @Fields userId 内部用户号
     */
    @ApiModelProperty(name = "userId", value = "用户ID")
    private String userId;
    
    /**
     * mblNo 手机号
     */
    @ApiModelProperty(name = "mblNo", value = "用户手机号")
    private String mblNo;
    
    /**
     * displayNm 显示姓名
     */
    @ApiModelProperty(name = "displayNm", value = "显示姓名")
    private String displayNm;
    
    /**
     * avatarPath 头像路径
     */
    @ApiModelProperty(name = "avatarPath", value = "头像路径")
    private String avatarPath;

    /**
     * @Fields usrSts 用户状态 0:开户 1:销户
     */
    @ApiModelProperty(name = "usrSts", value = "用户状态0:开户 1:销户")
    private String usrSts;
    /**
     * @Fields 用户级别 0:普通用户 1:企业用户 2:个人商家 3：企业商家
     */
    @ApiModelProperty(name = "usrLvl", value = "用户级别 0:普通用户 1:企业用户 2:个人商家 3：企业商家")
    private String usrLvl;
    /**
     * @Fields idChkFlg 实名标志 0：非实名 1：实名
     */
    @ApiModelProperty(name = "idChkFlg", value = "实名标志0：非实名 1：实名")
    private String idChkFlg;
    /**
     * @Fields idType 证件类型
     */
    @ApiModelProperty(name = "idType", value = "证件类型")
    private String idType;
    /**
     * @Fields idNo 证件号码
     */
    @ApiModelProperty(name = "idNo", value = "证件号码")
    private String idNo;
    
    
    /**
     * @Fields usrNm 用户姓名
     */
    @ApiModelProperty(name = "usrNm", value = "用户姓名")
    private String usrNm;
    
    
    /**
     * @Fields usrGender 用户性别 M：男 F：女
     */
    @ApiModelProperty(name = "usrGender", value = "用户性别M：男 F：女")
    private String usrGender;
    /**
     * @Fields usrNation 用户归属国家
     */
    @ApiModelProperty(name = "usrNation", value = "用户归属国家")
    private String usrNation;
    /**
     * @Fields usrBirthDt 出生日期
     */
    @ApiModelProperty(name = "usrBirthDt", value = "出生日期")
    private String usrBirthDt;
    
    /**
     * @Fields usrNmHid 脱敏用户姓名
     */
    @ApiModelProperty(name = "usrNmHid", value = "脱敏用户姓名")
    @Length(max = 64)
    private String usrNmHid;
    
    /**
     * acNo 用户账户编号
     */
    @ApiModelProperty(name = "acNo", value = "用户账户编码")
    private String acNo;
    
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getMblNo() {
		return mblNo;
	}
	public void setMblNo(String mblNo) {
		this.mblNo = mblNo;
	}
	public String getDisplayNm() {
		return displayNm;
	}
	public void setDisplayNm(String displayNm) {
		this.displayNm = displayNm;
	}
	public String getAvatarPath() {
		return avatarPath;
	}
	public String getUsrNmHid() {
		return usrNmHid;
	}
	public void setUsrNmHid(String usrNmHid) {
		this.usrNmHid = usrNmHid;
	}
	public void setAvatarPath(String avatarPath) {
		this.avatarPath = avatarPath;
	}
	public String getUsrSts() {
		return usrSts;
	}
	public void setUsrSts(String usrSts) {
		this.usrSts = usrSts;
	}
	public String getUsrLvl() {
		return usrLvl;
	}
	public void setUsrLvl(String usrLvl) {
		this.usrLvl = usrLvl;
	}
	public String getIdChkFlg() {
		return idChkFlg;
	}
	public void setIdChkFlg(String idChkFlg) {
		this.idChkFlg = idChkFlg;
	}
	public String getIdType() {
		return idType;
	}
	public void setIdType(String idType) {
		this.idType = idType;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getUsrNm() {
		return usrNm;
	}
	public void setUsrNm(String usrNm) {
		this.usrNm = usrNm;
	}
	public String getUsrGender() {
		return usrGender;
	}
	public void setUsrGender(String usrGender) {
		this.usrGender = usrGender;
	}
	public String getUsrNation() {
		return usrNation;
	}
	public void setUsrNation(String usrNation) {
		this.usrNation = usrNation;
	}
	public String getUsrBirthDt() {
		return usrBirthDt;
	}
	public void setUsrBirthDt(String usrBirthDt) {
		this.usrBirthDt = usrBirthDt;
	}
	public String getAcNo() {
		return acNo;
	}
	public void setAcNo(String acNo) {
		this.acNo = acNo;
	}
	
}
