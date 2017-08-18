package com.hisun.lemon.pwm.constants;

public class PwmConstants {
	
	/**
	 * 生成充海币订单号的前缀
	 */
	public static final String R_SEA_GEN_PRE="pwmRechangeSeaNo"; 
	/**
	 * 生成充值订单号的前缀
	 */
	public static final String R_ORD_GEN_PRE="pwmRechangeOrdNo"; 
	
	/**
	 * 生成提现订单号的前缀
	 */
	public static final String W_ORD_GEN_PRE="pwmWithdrawOrdNo";

    /**
     * 生成提现银行卡主键的前缀
     */
    public static final String W_CRD_GEN_PRE="pwmWithdrawCardNo";

    /**
	 * 交易类型：充值
	 */
	public static final String TX_TYPE_RECHANGE="01";
	/**
	 * 业务类型：充值--快捷充值
	 */
	public static final String BUS_TYPE_RECHARGE_QP ="0101";
	/**
	 * 业务类型：充值--线下充值
	 */
	public static final String BUS_TYPE_RECHARGE_OFL ="0102";
	/**
	 * 业务类型：充值--营业厅充值
	 */
	public static final String BUS_TYPE_RECHARGE_HALL ="0103";
	/**
	 * 业务类型：充值--企业网银充值
	 */
	public static final String BUS_TYPE_RECHARGE_BNB ="0104";
	
	
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
	public static final String RECHARGE_ORD_W ="W";
	/**
	 * 充值订单状态：充值成功
	 */
	public static final String RECHARGE_ORD_S ="S";
	
	/**
	 * 充值订单状态：交易逾期
	 */
	public static final String RECHARGE_ORD_TO ="T";
	
	/**
	 * 充值订单状态：交易失败
	 */
	public static final String RECHARGE_ORD_F ="F";

	/**
	 * 充值订单状态：冲正撤销
	 */
	public static final String RECHARGE_ORD_C ="C";

	/**
	 * 提现订单状态W1：系统受理中
	 */
	public static final String WITHDRAW_ORD_W1="W1";

	/**
	 * 提现订单状态W2：资金流出已受理
	 */
	public static final String WITHDRAW_ORD_W2="W2";

	/**
	 * 提现订单状态S1：付款成功
	 */
	public static final String WITHDRAW_ORD_S1="S1";

	/**
	 * 提现订单状态F1：付款失败
	 */
	public static final String WITHDRAW_ORD_F1="F1";

	/**
	 * 提现订单状态F2：付款核销
	 */
	public static final String WITHDRAW_ORD_F2="F2";

	/**
	 * 提现订单状态R9：审批拒绝
	 */
	public static final String WITHDRAW_ORD_R9="R9";

	/**
	 * 营业厅充值操作状态：充值申请
	 */
	public static final String RECHARGE_OPR_A = "A";
	/**
	 * 营业厅充值操作状态：充值确认
	 */
	public static final String RECHARGE_OPR_O = "O";
	/**
	 * 营业厅充值操作状态：充值撤销
	 */
	public static final String RECHARGE_OPR_C = "C";
	/**
	 * 订单来源渠道 ：WEB WEB站点
	 */
	public static final String ORD_SYSCHANNEL_WEB = "WEB";
	/**
	 * 订单来源渠道 ：APP 手机APP
	 */
	public static final String ORD_SYSCHANNEL_APP = "APP";
	/**
	 * 订单来源渠道 ：HALL 营业厅
	 */
	public static final String ORD_SYSCHANNEL_HALL = "HALL";
	/**
	 * 订单来源渠道 ：OTHER 其他渠道
	 */
	public static final String ORD_SYSCHANNEL_OTHER = "OTHER";
	
	/**
	 * 交易类型：充海币
	 */
	public static final String TX_TYPE_HCOUPON="05";
	
	/**
	 * 业务类型：充海币
	 */
	public static final String BUS_TYPE_HCOUPON="0501";

	/**
	 * 营业厅充值币种：USD美元
	 */
	public static final String HALL_PAY_CCY="USD";
	
	/**
	 * 内部科目
	 * 其他应付款-暂收-收银台
	 */
	public static final String AC_ITEM_CSH_PAY="2241030001";
	
	/**
	 * 内部科目
	 * 其他应付款-中转挂账-海币
	 */
	public static final String AC_ITEM_HCOUPONE="2241040001";

	/**
	 * 内部科目
	 * 银行存款-备付金账户-XX银行
	 */
	public static final String AC_ITEM_DEP_ACC_BNK="1002010001";
	
	/**
	 * 海币换算美元比率
	 */
	public static final double H_USD_RATE=0.01;

	/**
	 * 营业厅查询用户类型： U 用户
	 */
	public static final String HALL_QUERY_TYPE_U = "U";

	/**
	 * 营业厅查询用户类型： M 商户
	 */
	public static final String HALL_QUERY_TYPE_M = "M";

	/**
	 * 线下汇款订单状态：待审核
	 */
	public static final String OFFLINE_RECHARGE_ORD_W="W";

	/**
	 * 线下汇款订单状态：审核已提交
	 */
	public static final String OFFLINE_RECHARGE_ORD_W1="W1";

	/**
	 * 线下汇款订单状态：审核拒绝
	 */
	public static final String OFFLINE_RECHARGE_ORD_F="F";

	/**
	 * 线下汇款订单状态：审核通过
	 */
	public static final String OFFLINE_RECHARGE_ORD_S="S";

    /**
     * 内部科目
     * 其他应付款-支付账户-现金账户
     */
    public static final String AC_ITEM_PAY_CASH_ACNO="2241010001";

    /**
     * 内部科目
     * 应付账款-待结算款-批量付款
     */
    public static final String AC_ITEM_FOR_PAY="2202010001";

    /**
     * 内部科目
     * 手续费收入-支付账户-提现
     */
    public static final String AC_ITEM_FEE_PAY_WIDR="6021010002";

	/**
	 * 手机号国家编码
	 * 柬埔寨：
	 */
	public static final String COUNTRY_CODE_KHM="855";

	/**
	 * 提现银行卡状态"1"：生效
	 */
	public static final String WITHDRAW_CARD_STAT_EFF="1";

	/**
	 * 提现银行卡状态"0"：失效
	 */
	public static final String WITHDRAW_CARD_STAT_FAIL="0";
}
