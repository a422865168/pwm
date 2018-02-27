package com.hisun.lemon.pwm.utils;

import com.hisun.lemon.common.utils.DateTimeUtils;

import java.util.HashMap;
import java.util.Map;

public class AcsUtils {

    /**
     * 组织内部户借记对象
     * @param acNo 账号
     * @param acNm 内部账户名称
     * @param acTYP 账户类型
     * @param capTyp 资金种类
     * @param accAmt 记账金额
     * @param dcFlg 借贷标志
     * @param txTyp 细化交易类型
     * @param busTyp 系统类型
     * @param oppAcNo 对手账号
     * @param txOppNm 对手账户名称
     * @param txAmt 原始交易订单金额
     * @param ordTyp 订单种类
     * @param ordNo 原始交易订单号
     * @param ordJrnSeq 记账流水序号
     * @param notTxFlg 入账标志
     * @return
     */
    public static Map<String,Object> getAccDataListDTO(String apiNm,String acNo, String acNm, String acTYP, String capTyp , Float accAmt, String dcFlg,
                                                        String txTyp, String busTyp, String oppAcNo, String txOppNm, Float txAmt, String ordTyp,
                                                        String ordNo, String ordJrnSeq, String notTxFlg,String tx_desc){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("API_NM", apiNm);//记账组件名
        map.put("AC_NO",acNo);//账号
        map.put("AC_NM",acNm);//内部账户名称
        map.put("AC_TYP",acTYP);//账户类型传入为空时候默认取 AC_NO的前三位
        map.put("AC_DT", DateTimeUtils.getCurrentDateStr());//会计日期
        map.put("CAP_TYP",capTyp);//资金种类
        map.put("ACC_AMT",accAmt);//记账金额
        map.put("DC_FLG",dcFlg);//借贷标志
        map.put("TX_CD","");//交易码,非必输
        map.put("TX_TYP",txTyp);//细化交易类型,枚举值见会计分录表.xlsx
        map.put("BUS_TYP",busTyp);//系统类型
        map.put("OPP_AC_NO",oppAcNo);//对手账户号
        map.put("TX_OPP_NM",txOppNm);//对手账户名
        map.put("TX_AMT",txAmt);//原始交易订单号
        map.put("TX_DESC","");//交易描述,非必输
        map.put("ORD_TYP",ordTyp);//订单种类,(充值,提现,退款,转账)
        map.put("ORD_NO",ordNo);//原始交易订单号
        map.put("ORD_JRN_SEQ",ordJrnSeq);//记账流水序号
        map.put("NOT_TX_FLG",notTxFlg);//入账标志,默认为N
        map.put("TX_DESC",tx_desc);//原始交易描述
        return map;
    }

    /**
     * 根据银行英文缩写，获取银行备付金科目号
     */
    public static String switchBankItemNo(String rutCorg) {
        String bankAccount = null;
        switch(rutCorg) {
            case "ICBC":
                bankAccount = "1002010001";
                break;
            default:
                bankAccount = "1002010001";//默认取工行备付金科目号
                break;
        }
        return bankAccount;
    }
}
