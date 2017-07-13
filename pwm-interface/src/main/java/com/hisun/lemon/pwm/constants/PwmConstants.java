package com.hisun.lemon.pwm.constants;

public class PwmConstants {
	
	/**
	 * 生成充值订单号的前缀
	 */
	public static final String R_ORD_GEN_PRE="pwmRechangeOrdNo"; 
	
	/**
	 * 生成充值订单号的前缀
	 */
	public static final String W_ORD_GEN_PRE="pwmWithdrawOrdNo"; 
	
	/**
	 * 交易类型：充值
	 */
	public static final String TX_TYPE_RECHANGE="01";
	/**
	 * 业务类型：充值--快捷充值
	 */
	public static final String BUS_TYPE_RECHANGE_QP="0101";
	/**
	 * 业务类型：充值--线下充值
	 */
	public static final String BUS_TYPE_RECHANGE_OFL="0102";
	/**
	 * 业务类型：充值--营业厅充值
	 */
	public static final String BUS_TYPE_RECHANGE_HALL="0103";
	/**
	 * 业务类型：充值--企业网银充值
	 */
	public static final String BUS_TYPE_RECHANGE_BNB="0104";
	
	
	/**
	 * 交易类型：提现
	 */
	public static final String TX_TYPE_WITHDRAW="04";
	
	/**
	 * 业务类型：提现--个人提现
	 */
	public static final String BUS_TYPE_WITHDRAW_P="0401";
	/**
	 * 业务类型：提现--商户提现
	 */
	public static final String BUS_TYPE_WITHDRAW_M="0402";
	
	/**
	 * 充值订单状态：等待充值
	 */
	public  static final String RECHANGE_ORD_W="W";
	/**
	 * 充值订单状态：充值成功
	 */
	public  static final String RECHANGE_ORD_S="S";
	
	/**
	 * 充值订单状态：交易逾期
	 */
	public  static final String RECHANGE_ORD_TO="T";
	
	/**
	 * 充值订单状态：交易失败
	 */
	public  static final String RECHANGE_ORD_F="F";
}
