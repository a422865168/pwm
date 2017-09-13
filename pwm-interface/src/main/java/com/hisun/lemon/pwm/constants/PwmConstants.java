package com.hisun.lemon.pwm.constants;

public class PwmConstants {
	
	/**
	 * 生成充海币订单号的前缀
	 */
	public static final String R_SEA_GEN_PRE="pwmRechangeSeaOrdNo"; 
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
	 * 业务类型：充值短款撤单退款
	 */
	public static final String BUS_TYPE_RECHARGE_SHORTAMT_REFUND ="0601";
	
	/**
	 * 交易类型：提现
	 */
	public static final String TX_TYPE_WITHDRAW="04";

	/**
	 * 交易类型：退款
	 */
	public static final String TX_TYPE_RECHARGE_REFUND="06";

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
	 * 充值订单状态：已退款
	 */
	public static final String RECHARGE_ORD_R ="R";

	/**
	 * 提现订单状态W1：系统受理中
	 */
	public static final String WITHDRAW_ORD_W1="W1";

	/**
	 * 提现订单状态W3：资金流出已受理
	 */
	public static final String WITHDRAW_ORD_W3="W3";

	/**
	 * 提现订单状态S1：付款成功
	 */
	public static final String WITHDRAW_ORD_S1="S1";

	/**
	 * 提现订单状态F1：付款失败
	 */
	public static final String WITHDRAW_ORD_F1="F1";

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
	 * 内部科目
	 * 手续费收入-支付账户-充值
	 */
	public static final String AC_ITEM_RECHARGE_FEE="6021010005";
	
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
	 * 应收账款-待结算款-营业厅
	 */
	public static final String AC_ITEM_PAY_HALL ="1122021001";

	/**
	 * 内部科目
	 * 应收账款-渠道充值-营业厅
	 */
	public static final String AC_ITEM_CNL_RECHARGE_HALL ="1122010001";

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

	/**
	 * 营业厅对账文件业务类型：提现
	 */
	public static final String HALL_CHK_TYPE_WC="WC";

	/**
	 * 营业厅对账文件业务类型：充值
	 */
	public static final String HALL_CHK_TYPE_RC="RC";

	/**
	 * 营业厅账务差错处理类型：长款
	 */
	public static final String HALL_CHK_LONG_AMT="L";

	/**
	 * 营业厅账务差错处理类型：短款
	 */
	public static final String HALL_CHK_SHORT_AMT="S";

	/**
	 * 提现成功消息推送模板
	 */
	public static final String WITHDRAW_SUCC_TEMPL="00000009";

	/**
	 * 提现失败消息推送模板
	 */
	public static final String WITHDRAW_FAIL_TEMPL="00000008";

	
	/**
	 * 支付方式
	 */
	public static final String BUS_PAY_TYPE="00100000";
	
	/**
	 * 海币充值订单状态 S
	 */
	public static final String ORD_STS_S="S";

	/**
	 * 充值成功消息推送模板
	 */
	public static final String RECHARGE_SUCC_TEMPL="00000010";

	/**
	 * 汇款充值失败消息推送模板
	 */
	public static final String RECHARGE_OFFLINE_BACK_TEMPL="00000011";

	/**
	 * 对账差错处理结果：成功(S)
	 */
	public static final String CHK_ERR_SUCCESS="S";

	/**
	 * 对账差错处理结果：失败(F)
	 */
	public static final String CHK_ERR_FAIL="F";
	
}
