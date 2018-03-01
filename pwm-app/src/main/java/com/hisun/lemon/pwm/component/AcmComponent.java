package com.hisun.lemon.pwm.component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.hisun.lemon.acm.client.AccountManagementClient;
import com.hisun.lemon.acm.client.AccountingTreatmentClient;
import com.hisun.lemon.acm.constants.ACMConstants;
import com.hisun.lemon.acm.dto.AccountingReqDTO;
import com.hisun.lemon.acm.dto.QueryAcBalRspDTO;
import com.hisun.lemon.acm.dto.UserAccountDTO;
import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.common.utils.StringUtils;
import com.hisun.lemon.dto.AccDataListDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.xxka.client.XXAccHandleClient;

/**
 * 账务组件
 * 
 * 
 *
 */
@Component
public class AcmComponent {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AcmComponent.class);
	@Resource
	private XXAccHandleClient acsService;
	@Resource
	private AccountingTreatmentClient accountingTreatmentClient;

	@Resource
	private AccountManagementClient accountManagementClient;

	
	/**
	 * @param accDataListDTO
	 * @return
	 * @Title: QueryMainAccBal
	 * @Description: 主账户余额查询
	 * @return: String
	 */
	public AccDataListDTO queryAcBal(AccDataListDTO accDataListDTO) {
		AccDataListDTO AccDataList = null;
		try {
			GenericDTO genericDTO = new GenericDTO();
			genericDTO.setBody(accDataListDTO);
			GenericRspDTO<AccDataListDTO> rspDTO = acsService.xxAccHandle(genericDTO);
			if (JudgeUtils.isNotSuccess(rspDTO.getMsgCd())) {
				LemonException.throwLemonException(rspDTO.getMsgCd());
			}
			AccDataList = rspDTO.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return AccDataList;
	}
	/**
	 * 用户现金账户借贷对象
     * @param apiNm 记账组件名(UserDeposit)
     * @param acNo 账号
	 * @param acTYP 账户类型
	 * @param capTyp 资金种类
	 * @param accAmt 记账金额
	 * @param dcFlg 记账标志
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

	public static Map<String,Object> getUserAccDataListDTO(String apiNm,String acNo, String acTYP, String actDatte,String capTyp , Float accAmt, String dcFlg,
													   String txTyp, String busTyp, String oppAcNo, String txOppNm, Float txAmt,String txtDesc,String ordTyp,
													   String ordNo, String ordJrnSeq, String notTxFlg){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("API_NM", apiNm);//记账组件名
		map.put("AC_NO",acNo);//账号
		map.put("AC_TYP",acTYP);//账户类型传入为空时候默认取 AC_NO的前三位
        map.put("AC_DT",actDatte);//会计日期
		map.put("CAP_TYP",capTyp);//资金种类
		map.put("ACC_AMT",accAmt);//记账金额
		map.put("DC_FLG",dcFlg);//借贷标志
		map.put("TX_CD","");//交易码,非必输
		map.put("TX_TYP",txTyp);//细化交易类型,枚举值见会计分录表.xlsx
		map.put("BUS_TYP",busTyp);//系统类型
		map.put("OPP_AC_NO",oppAcNo);//对手账户号
		map.put("TX_OPP_NM",txOppNm);//对手账户名
		map.put("TX_AMT",txAmt);//原始交易订单号
		map.put("TX_DESC",txtDesc);//交易描述,非必输
		map.put("ORD_TYP",ordTyp);//订单种类,(充值,提现,退款,转账)
		map.put("ORD_NO",ordNo);//原始交易订单号
		map.put("ORD_JRN_SEQ",ordJrnSeq);//记账流水序号
		map.put("NOT_TX_FLG",notTxFlg);//入账标志,默认为N
		return map;
	}
	
	
	/**
	 * 内部账户借贷对象
     * @param apiNm 记账组件名
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
	public static Map<String,Object> getInnerAccDataListDTO(String apiNm,String acNo, String acNm, String acTYP,String actDate, String capTyp , Float accAmt, String dcFlg,
													   String txTyp, String busTyp, String oppAcNo, String txOppNm, Float txAmt, String ordTyp,String txDesc,
													   String ordNo, String ordJrnSeq, String notTxFlg){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("API_NM", apiNm);//记账组件名
		map.put("AC_NO",acNo);//账号
		map.put("AC_NM",acNm);//内部账户名称
		map.put("AC_TYP",acTYP);//账户类型传入为空时候默认取 AC_NO的前三位
		map.put("AC_DT", actDate);//会计日期
		map.put("CAP_TYP",capTyp);//资金种类
		map.put("ACC_AMT",accAmt);//记账金额
		map.put("DC_FLG",dcFlg);//借贷标志
		map.put("TX_CD","");//交易码,非必输
		map.put("TX_TYP",txTyp);//细化交易类型,枚举值见会计分录表.xlsx
		map.put("BUS_TYP",busTyp);//系统类型
		map.put("OPP_AC_NO",oppAcNo);//对手账户号
		map.put("TX_OPP_NM",txOppNm);//对手账户名
		map.put("TX_AMT",txAmt);//原始交易订单号
		map.put("TX_DESC",txDesc);//交易描述,非必输
		map.put("ORD_TYP",ordTyp);//订单种类,(充值,提现,退款,转账)
		map.put("ORD_NO",ordNo);//原始交易订单号
		map.put("ORD_JRN_SEQ",ordJrnSeq);//记账流水序号
		map.put("NOT_TX_FLG",notTxFlg);//入账标志,默认为N
		return map;
	}
	
	/**
	 * 生成账务请求对象---账户余额查询
	 *
	 * @param API_NM    记账组件名
	 * @param USR_NO    内部用户号
	 * @param AC_TYP    账户类型---商户现金账户-900,用户现金账户-100,用户卡账户-101
	 * @param CAP_TYP   资金种类---1-现金 2-预付卡 3-备付金
	 * @param AC_ORG    账户归属机构号---账户归属机构 10001:移动互联网 10002:银行卡收单 10003:预付卡支付
	 * @param mainTxTyp 主交易类型
	 * @param miniTxTyp 细化交易类型
	 * @param rvsTxFlg  交易冲正标志--N-正常交易,C-冲正交易
	 * @param CAP_TYP   1-现金 2-卡 3-备付金
	 * @return 账务请求信息对象:accDataListDTO
	 */
	public AccDataListDTO createAccountingReqDTO(String API_NM,String USR_NO,String AC_TYP,String AC_ORG,String CAP_TYP,
												 String mainTxTyp, String miniTxTyp, String rvsTxFlg,String msgCod, String msgInf) {
		AccDataListDTO accDataListDTO = new AccDataListDTO();
		List<Map<String, Object>> accDataMapList = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("API_NM", API_NM);
		map.put("USR_NO", USR_NO);
		map.put("AC_TYP", AC_TYP);
		map.put("AC_ORG", AC_ORG);
		map.put("CAP_TYP", CAP_TYP);
		accDataMapList.add(map);
		accDataListDTO.setAccDataMapList(accDataMapList);
		accDataListDTO.setMainTxTyp(mainTxTyp);
		accDataListDTO.setMiniTxTyp(miniTxTyp);
		accDataListDTO.setRvsTxFlg(rvsTxFlg);
		accDataListDTO.setMsgCod(msgCod);
		accDataListDTO.setMsgInf(msgInf);
		return accDataListDTO;
	}
	
	/**
	 *
	 * @param userId
	 *            用户ID
	 * @param acTye
	 *            账户类型 1 现金 2 待结算款
	 * @return
	 */
	public String getAcmAcNo(String userId, String acTye) {
		UserAccountDTO userDTO = new UserAccountDTO();
		userDTO.setUserId(userId);
		GenericDTO<UserAccountDTO> user=new GenericDTO<>();
		user.setBody(userDTO);
		GenericDTO<List<QueryAcBalRspDTO>> genericQueryAcBalRspDTO = accountManagementClient.queryAcBal(userDTO);
		List<QueryAcBalRspDTO> acmAcBalInfList = genericQueryAcBalRspDTO.getBody();

		if (JudgeUtils.isNull(acmAcBalInfList) || JudgeUtils.isEmpty(acmAcBalInfList)) {
			throw new LemonException("PWM40003");
		}

		for (QueryAcBalRspDTO queryAcBalRspDTO : acmAcBalInfList) {
			if (StringUtils.equals(queryAcBalRspDTO.getCapTyp(), acTye)) {
				return queryAcBalRspDTO.getAcNo();
			}
		}
		return null;
	}

	/**
	 *
	 * @param userId
	 *            用户ID
	 * @param acType
	 *            账户类型 1 现金 2 待结算款
	 * @return
	 */
	public BigDecimal getAccountBal(String userId,String acType){
		QueryAcBalRspDTO queryAcBalRspDTO=this.getAcmAcInfo(userId,acType);
		if(JudgeUtils.isNull(queryAcBalRspDTO)){
			LemonException.throwBusinessException("CSH20050");
		}
		return queryAcBalRspDTO.getAcCurBal();
	}

	/**
	 * 生成账务请求对象
	 * 
	 * @param orderNo
	 *            订单号
	 * @param txJrnNo
	 *            订单流水号
	 * @param txType
	 *            交易类型
	 * @param txSts
	 *            交易状态
	 * @param txAmt
	 *            交易金额
	 * @param acNo
	 *            账户号
	 * @param acType
	 *            账户类型
	 * @param capType
	 *            资金属性
	 * @param dcFlag
	 *            借贷标识
	 * @param itmNo
	 *            内部科目号
	 * @param oppAcNo
	 *            对手方账号
	 * @param oppCapType
	 *            对手方资金属性
	 * @param oppUsrId
	 *            对手方用户id
	 * @param oppUsrType
	 * @param remark
	 *            备注
	 * @return 账务请求信息对象
	 */
	public AccountingReqDTO createAccountingReqDTO(String orderNo, String txJrnNo, String txType, String txSts,
			BigDecimal txAmt, String acNo, String acType, String capType, String dcFlag, String itmNo, String oppAcNo,
			String oppCapType, String oppUsrId, String oppUsrType, String remark) {
		AccountingReqDTO accountReqDTO = new AccountingReqDTO();
		accountReqDTO.setTxTyp(txType);
		accountReqDTO.setTxSts(txSts);
		accountReqDTO.setTxAmt(txAmt);
		accountReqDTO.setTxJrnNo(txJrnNo);
		accountReqDTO.setTxOrdDt(DateTimeUtils.getCurrentLocalDate());
		accountReqDTO.setTxOrdTm(DateTimeUtils.getCurrentLocalTime());
		accountReqDTO.setTxOrdNo(orderNo);
		accountReqDTO.setAcNo(acNo);
		accountReqDTO.setAcTyp(acType);
		accountReqDTO.setCapTyp(capType);
		accountReqDTO.setDcFlg(dcFlag);
		accountReqDTO.setItmNo(itmNo);
		accountReqDTO.setOppAcNo(oppAcNo);
		accountReqDTO.setOppCapTyp(oppCapType);
		accountReqDTO.setOppUserId(oppUsrId);
		accountReqDTO.setOppUserTyp(oppUsrType);
		accountReqDTO.setRmk(remark);
		accountReqDTO.setUsrIpAdr(null);
		return accountReqDTO;
	}

	public GenericDTO<NoBody> requestAc(AccountingReqDTO... accountingReqDTOs) {
		BigDecimal dAmt = BigDecimal.ZERO;
		BigDecimal cAmt = BigDecimal.ZERO;
		List<AccountingReqDTO> accList = new ArrayList<>();
		for (AccountingReqDTO dto : accountingReqDTOs) {
			if (JudgeUtils.isNotNull(dto)) {
				accList.add(dto);
				if (JudgeUtils.equals(dto.getDcFlg(), ACMConstants.AC_D_FLG)) {
					dAmt = dAmt.add(dto.getTxAmt());
				} else {
					cAmt = cAmt.add(dto.getTxAmt());
				}
			}
		}

		// 借贷平衡校验
		if (cAmt.compareTo(dAmt) != 0) {
			LemonException.throwBusinessException("PWM40002");
		}

		GenericDTO<List<AccountingReqDTO>> userAccDto = new GenericDTO<>();
		userAccDto.setBody(accList);
		GenericRspDTO<NoBody> accountingTreatment = accountingTreatmentClient.accountingTreatment(userAccDto);
		if (JudgeUtils.isNotSuccess(accountingTreatment.getMsgCd())) {
			LemonException.throwBusinessException(accountingTreatment.getMsgCd());
		}
		return accountingTreatment;
	}

	private QueryAcBalRspDTO getAcmAcInfo(String userId, String acTye) {
		UserAccountDTO userDTO = new UserAccountDTO();
		userDTO.setUserId(userId);
		GenericDTO<UserAccountDTO> user=new GenericDTO<>();
		user.setBody(userDTO);
		GenericRspDTO<List<QueryAcBalRspDTO>> genericQueryAcBalRspDTO = accountManagementClient.queryAcBal(userDTO);
		if(JudgeUtils.isNotSuccess(genericQueryAcBalRspDTO.getMsgCd())){
			logger.error("查询账户信息失败："+userId);
			LemonException.throwBusinessException(genericQueryAcBalRspDTO.getMsgCd());
		}

		List<QueryAcBalRspDTO> acmAcBalInfList = genericQueryAcBalRspDTO.getBody();

		if (JudgeUtils.isNull(acmAcBalInfList) || JudgeUtils.isEmpty(acmAcBalInfList)) {
			throw new LemonException("CSH20050");
		}

		for (QueryAcBalRspDTO queryAcBalRspDTO : acmAcBalInfList) {
			if (StringUtils.equals(queryAcBalRspDTO.getCapTyp(), acTye)) {
				return queryAcBalRspDTO;
			}
		}
		return null;
	}

	public GenericRspDTO<NoBody> accountingTreatment(List<AccountingReqDTO> accountingReqDTOList) {
		BigDecimal dAmt = BigDecimal.ZERO;
		BigDecimal cAmt = BigDecimal.ZERO;
		for (AccountingReqDTO accountingReqDTO : accountingReqDTOList) {
			if (JudgeUtils.isNotNull(accountingReqDTO)) {
				if (JudgeUtils.equals(accountingReqDTO.getDcFlg(), ACMConstants.AC_D_FLG)) {
					dAmt = dAmt.add(accountingReqDTO.getTxAmt());
				} else {
					cAmt = cAmt.add(accountingReqDTO.getTxAmt());
				}
			}
		}
		// 借贷平衡校验
		if (cAmt.compareTo(dAmt) != 0) {
			LemonException.throwBusinessException("PWM40002");
		}
		GenericDTO<List<AccountingReqDTO>> userAccDto = new GenericDTO<>();
		userAccDto.setBody(accountingReqDTOList);
		GenericRspDTO<NoBody> genericRspDTO = accountingTreatmentClient.accountingTreatment(userAccDto);
		if (JudgeUtils.isNotSuccess(genericRspDTO.getMsgCd())) {
			LemonException.throwBusinessException(genericRspDTO.getMsgCd());
		}
		return genericRspDTO;
	}

}
