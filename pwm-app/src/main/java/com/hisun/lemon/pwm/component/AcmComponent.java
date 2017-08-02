package com.hisun.lemon.pwm.component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.hisun.lemon.framework.data.GenericRspDTO;
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
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.urm.client.UserBasicInfClient;
import com.hisun.lemon.urm.dto.UserBasicInfDTO;

/**
 * 账务组件
 * 
 * 
 *
 */
@Component
public class AcmComponent {

	@Resource
	private AccountingTreatmentClient accountingTreatmentClient;

	@Resource
	private AccountManagementClient accountManagementClient;

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
		GenericDTO<UserAccountDTO> user=new GenericDTO<UserAccountDTO>();
		user.setBody(userDTO);
		GenericDTO<List<QueryAcBalRspDTO>> genericQueryAcBalRspDTO = accountManagementClient.queryAcBal(user);
		List<QueryAcBalRspDTO> acmAcBalInfList = genericQueryAcBalRspDTO.getBody();

		if (JudgeUtils.isNull(acmAcBalInfList) || JudgeUtils.isEmpty(acmAcBalInfList)) {
			throw new LemonException("TAM20007");
		}

		for (QueryAcBalRspDTO queryAcBalRspDTO : acmAcBalInfList) {
			if (StringUtils.equals(queryAcBalRspDTO.getCapTyp(), acTye)) {
				return queryAcBalRspDTO.getAcNo();
			}
		}
		return null;
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
			LemonException.throwBusinessException("TAM20008");
		}

		GenericDTO<List<AccountingReqDTO>> userAccDto = new GenericDTO<>();
		userAccDto.setBody(accList);
		GenericRspDTO<NoBody> accountingTreatment = accountingTreatmentClient.accountingTreatment(userAccDto);

		if (JudgeUtils.isNotSuccess(accountingTreatment.getMsgCd())) {
			throw new LemonException(accountingTreatment.getMsgCd());
		}
		return accountingTreatment;
	}

}
