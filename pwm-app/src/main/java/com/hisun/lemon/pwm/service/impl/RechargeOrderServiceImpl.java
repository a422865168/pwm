package com.hisun.lemon.pwm.service.impl;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.hisun.lemon.csh.order.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.lemon.acm.client.AccountManagementClient;
import com.hisun.lemon.acm.constants.ACMConstants;
import com.hisun.lemon.acm.constants.CapTypEnum;
import com.hisun.lemon.acm.dto.AccountingReqDTO;
import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.common.utils.StringUtils;
import com.hisun.lemon.csh.client.CshOrderClient;
import com.hisun.lemon.csh.constants.CshConstants;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.data.GenericRspDTO;
import com.hisun.lemon.framework.data.NoBody;
import com.hisun.lemon.framework.utils.IdGenUtils;
import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.mkm.client.MarketActivityClient;
import com.hisun.lemon.mkm.req.dto.RechargeMkmToolReqDTO;
import com.hisun.lemon.mkm.res.dto.RechargeMkmToolResDTO;
import com.hisun.lemon.pwm.component.AcmComponent;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dto.HallQueryResultDTO;
import com.hisun.lemon.pwm.dto.HallRechargeApplyDTO;
import com.hisun.lemon.pwm.dto.HallRechargeResultDTO;
import com.hisun.lemon.pwm.dto.RechargeDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponDTO;
import com.hisun.lemon.pwm.dto.RechargeHCouponResultDTO;
import com.hisun.lemon.pwm.dto.RechargeResultDTO;
import com.hisun.lemon.pwm.entity.RechargeHCouponDO;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
import com.hisun.lemon.pwm.service.IRechargeOrderService;
import com.hisun.lemon.tfm.client.TfmServerClient;
import com.hisun.lemon.tfm.dto.TradeRateReqDTO;
import com.hisun.lemon.tfm.dto.TradeRateRspDTO;
import com.hisun.lemon.urm.client.UserBasicInfClient;
import com.hisun.lemon.urm.dto.UserBasicInfDTO;

@Service
public class RechargeOrderServiceImpl implements IRechargeOrderService {

	private static final Logger logger = LoggerFactory.getLogger(RechargeOrderServiceImpl.class);
	@Resource
	RechargeOrderTransactionalService service;
	@Resource
	CshOrderClient cshOrderClient;
	@Resource
	private AcmComponent acmComponent;
	@Resource
	UserBasicInfClient userBasicInfClient;
	@Resource
	TfmServerClient fmServerClient;
	@Resource
	MarketActivityClient mkmClient;
	@Resource
	private AccountManagementClient accountManagementClient;
	/**
	 * 海币充值下单
	 */
	@Override
	public GenericRspDTO createHCouponOrder(GenericDTO<RechargeHCouponDTO> rechargeHCouponDTO) {
		RechargeHCouponDTO rechargeDTO = rechargeHCouponDTO.getBody();
		if (!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_HCOUPON)) {
			throw new LemonException("PWM20001");
		}

		RechargeHCouponDO rechargeDO = new RechargeHCouponDO();

		if (!JudgeUtils.isNull(rechargeDTO.getOrderCcy())) {

			rechargeDO.setOrderCcy(rechargeDTO.getOrderCcy());
		}
		rechargeDO.setOrderCcy("USD");
		// 会计日期
		rechargeDO.setAcTm(rechargeHCouponDTO.getAccDate());
		rechargeDO.setOrderStatus(PwmConstants.RECHARGE_ORD_W);
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = IdGenUtils.generateId(PwmConstants.R_SEA_GEN_PRE + ymd, 15);
		// 1:100的充值比例
		BigDecimal amount=rechargeDTO.getOrderAmt();
		BigDecimal hCouponAmt = amount.multiply(BigDecimal.valueOf(PwmConstants.H_USD_RATE)).setScale(2, BigDecimal.ROUND_DOWN);
		rechargeDO.sethCouponAmt(hCouponAmt);
		rechargeDO.setOrderNo(ymd + orderNo);
		rechargeDO.setOrderAmt(rechargeDTO.getOrderAmt());
		// 交易时间
		rechargeDO.setTxTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeDO.setTxType(rechargeDTO.getTxType());
		rechargeDO.setBusType(rechargeDTO.getBusType());
		rechargeDO.setUserId(rechargeDTO.getUserId());
		// 生成海币充值订单
		this.service.initSeaOrder(rechargeDO);
		// 调用收银
		InitCashierDTO initCashierDTO = new InitCashierDTO();
		initCashierDTO.setBusPaytype(null);
		initCashierDTO.setBusType(rechargeDO.getBusType());
		initCashierDTO.setExtOrderNo(rechargeDO.getOrderNo());
		initCashierDTO.setSysChannel("APP");
		initCashierDTO.setPayerId("");
		initCashierDTO.setAppCnl(LemonUtils.getApplicationName());
		initCashierDTO.setTxType(rechargeDO.getTxType());
		initCashierDTO.setOrderAmt(rechargeDO.getOrderAmt());
		GenericDTO<InitCashierDTO> genericDTO = new GenericDTO<>();
		genericDTO.setBody(initCashierDTO);
		logger.info("订单：" + rechargeDO.getOrderNo() + " 请求收银台");
		return cshOrderClient.initCashier(genericDTO);
	}

	/**
	 * 海币充值下单结果通知
	 */
	@Override
	public void handleHCouponResult(GenericDTO<RechargeHCouponResultDTO> rechargeHCouponDTO) {
		RechargeHCouponResultDTO rechargSeaDTO = rechargeHCouponDTO.getBody();
		RechargeHCouponDO rechargeSeaDO = this.service.getHCoupon(rechargSeaDTO.getOrderNo());
		// 原订单不存在
		if (JudgeUtils.isNull(rechargeSeaDO)) {
			throw new LemonException("PWM20008");
		}
		// 订单已经成功
		if (StringUtils.equals(rechargeSeaDO.getOrderStatus(), PwmConstants.RECHARGE_ORD_S)) {
			return;
		}
		// 订单失败
		if (!StringUtils.equals(rechargSeaDTO.getOrderStatus(), PwmConstants.RECHARGE_ORD_S)) {
			RechargeHCouponDO updateSeaDTO = new RechargeHCouponDO();
			updateSeaDTO.setOrderCcy(rechargSeaDTO.getOrderCcy());
			updateSeaDTO.setOrderNo(rechargSeaDTO.getOrderNo());
			updateSeaDTO.setAcTm(rechargeHCouponDTO.getAccDate());
			updateSeaDTO.setOrderStatus(PwmConstants.RECHARGE_ORD_F);
			this.service.updateSeaOrder(updateSeaDTO);
			return;
		}

		// 比较金额
		BigDecimal amount = rechargSeaDTO.getOrderAmt();
		if (rechargeSeaDO.getOrderAmt().compareTo(amount) != 0) {
			throw new LemonException("PWM20009");
		}

		// 账务处理
		AccountingReqDTO userAccountReqDTO = null; // xx用户海币账户
		AccountingReqDTO cshItemReqDTO = null; // 暂收收银台账务对象
		//流水号
		String payJrnNo=LemonUtils.getRequestId();
		// 查询用户帐号
		String balCapType = CapTypEnum.CAP_TYP_CASH.getCapTyp();
		//先静静
		String balAcNo = acmComponent.getAcmAcNo(rechargeHCouponDTO.getUserId(), balCapType);
		//借：其他应付款-暂收-收银台         100
		cshItemReqDTO=acmComponent.createAccountingReqDTO(
					rechargeSeaDO.getOrderNo(),
					payJrnNo, 
					rechargeSeaDO.getTxType(), 
					ACMConstants.ACCOUNTING_NOMARL, 
					rechargeSeaDO.getOrderAmt(),
					null, 
					ACMConstants.ITM_AC_TYP, 
					balCapType, 
					ACMConstants.AC_D_FLG, 
					CshConstants.AC_ITEM_CSH_PAY,
					null, 
					null, 
					null, 
					null, 
					null);  
						
		//贷：其他应付款-中转挂账-海币       100
		userAccountReqDTO=acmComponent.createAccountingReqDTO(
					rechargeSeaDO.getOrderNo(),
					payJrnNo, 
					rechargeSeaDO.getTxType(), 
					ACMConstants.ACCOUNTING_NOMARL, 
					rechargeSeaDO.getOrderAmt(),
					balAcNo, 
					ACMConstants.USER_AC_TYP,
					balCapType,
					ACMConstants.AC_C_FLG, 
					"", 
					CshConstants.AC_ITEM_HCOUPON, 
					null, 
					null, 
					null, 
					null);	
		acmComponent.requestAc(cshItemReqDTO,userAccountReqDTO);	
		// 账务更新成功  调用海币充值接口
		RechargeMkmToolReqDTO mkmReqDTO=new RechargeMkmToolReqDTO();
		mkmReqDTO.setSeq(rechargeSeaDO.getOrderNo());
		mkmReqDTO.setType("00");
		mkmReqDTO.setMkTool("02");
		String userId=rechargeSeaDO.getUserId();
		mkmReqDTO.setUserId(userId);
		String mblNo=accountManagementClient.queryAcNo(userId).getBody();
		String mobile=mblNo;
		mkmReqDTO.setMobile(mobile);
		BigDecimal hCouponAmt=rechargeSeaDO.gethCouponAmt();
		Integer count=hCouponAmt.intValue();
		mkmReqDTO.setRechargeTm(rechargeSeaDO.getTxTm());
		mkmReqDTO.setCount(count);
		GenericDTO<RechargeMkmToolReqDTO> rechangeDTO=new GenericDTO<RechargeMkmToolReqDTO>();
		rechangeDTO.setBody(mkmReqDTO);
		GenericRspDTO<RechargeMkmToolResDTO> mkmRsp=mkmClient.getSeaCyy(rechangeDTO);
		if(JudgeUtils.isNotNull(mkmRsp)){
			if(StringUtils.equals(mkmRsp.getBody().getResult(), "1")){
				RechargeHCouponDO update=new RechargeHCouponDO();
			    update.setAcTm(rechargeHCouponDTO.getAccDate());
			    update.sethCouponAmt(hCouponAmt);
			    update.setOrderAmt(rechargSeaDTO.getOrderAmt());
				update.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
				update.setOrderCcy(rechargSeaDTO.getOrderCcy());
				update.setOrderNo(rechargSeaDTO.getOrderNo());
				service.updateSeaOrder(update);
			}else{
				throw new LemonException("PWM40001");
			}
		}else{
			throw new LemonException("PWM40004");
		}
	}

	@Override
	public GenericRspDTO createOrder(RechargeDTO rechargeDTO) {
		if (!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_RECHANGE)) {
			throw new LemonException("PWM20001");
		}

		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 15);
		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(rechargeDTO.getBusType());
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setOrderAmt(rechargeDTO.getAmount());
		rechargeOrderDO.setOrderCcy("USD");
		rechargeOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		rechargeOrderDO.setOrderNo(ymd + orderNo);
		rechargeOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_W);
		rechargeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setPsnFlag(rechargeDTO.getPsnFlag());
		rechargeOrderDO.setRemark("");
		rechargeOrderDO.setSysChannel(rechargeDTO.getSysChannel());
		rechargeOrderDO.setTxType("01");
		service.initOrder(rechargeOrderDO);

		logger.info("登记充值订单成功，订单号："+rechargeOrderDO.getOrderNo());
		//调用收银
		InitCashierDTO initCashierDTO=new InitCashierDTO();
	  	initCashierDTO.setBusPaytype(null);
	  	initCashierDTO.setBusType(rechargeOrderDO.getBusType());
	  	initCashierDTO.setExtOrderNo(rechargeOrderDO.getOrderNo());
	  	initCashierDTO.setSysChannel(rechargeDTO.getSysChannel());
	  	initCashierDTO.setPayerId("");
		initCashierDTO.setAppCnl(LemonUtils.getApplicationName());
	  	initCashierDTO.setTxType(rechargeOrderDO.getTxType());
		initCashierDTO.setOrderAmt(rechargeDTO.getAmount());
		GenericDTO<InitCashierDTO> genericDTO = new GenericDTO<>();
		genericDTO.setBody(initCashierDTO);
		logger.info("订单："+rechargeOrderDO.getOrderNo()+" 请求收银台");
		return cshOrderClient.initCashier(genericDTO);
	}

	@Override
	public void handleResult(GenericDTO resultDto) {
		RechargeResultDTO rechargeResultDTO = (RechargeResultDTO) resultDto.getBody();

		String orderNo = rechargeResultDTO.getOrderNo();

		RechargeOrderDO rechargeOrderDO = service.getRechangeOrderDao().get(orderNo);

		// 未找到订单
		if (rechargeOrderDO == null) {
			throw new LemonException("PWM20002");
		}

		// 订单已经成功
//		if (StringUtils.equals(rechargeOrderDO.getOrderStatus(), PwmConstants.RECHARGE_ORD_S)) {
//			return;
//		}

		// 判断返回状态
		if (!StringUtils.equals(rechargeResultDTO.getStatus(), PwmConstants.RECHARGE_ORD_S)) {
			RechargeOrderDO updOrderDO = new RechargeOrderDO();
			updOrderDO.setExtOrderNo(rechargeResultDTO.getExtOrderNo());
			updOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_F);
			updOrderDO.setOrderCcy(rechargeResultDTO.getOrderCcy());
			updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
			updOrderDO.setOrderNo(orderNo);
			service.updateOrder(updOrderDO);
			return;
		}

		// 比较金额
		BigDecimal amount = rechargeResultDTO.getAmount();
		if (rechargeOrderDO.getOrderAmt().compareTo(amount) != 0) {
			throw new LemonException("PWM20003");
		}

		// 账务处理,根据不同业务进行不同处理
		String busType = rechargeResultDTO.getBusType();
		AccountingReqDTO userAccountReqDTO=null;     //用户现金账户账务对象
		AccountingReqDTO cshItemReqDTO=null;         //暂收收银台账务对象
		AccountingReqDTO couponItemReqDTO=null;      //优惠账务对象
		AccountingReqDTO crdItemReqDTO=null;         //补款账务对象
		AccountingReqDTO cnlRechargeBnkReqDTO=null;  //充值渠道银行账务对象
		AccountingReqDTO depositAccountBnkReqDTO=null; //备付金账户银行账务对象

		String balCapType= CapTypEnum.CAP_TYP_CASH.getCapTyp();
		String balAcNo=acmComponent.getAcmAcNo(rechargeResultDTO.getPayerId(), balCapType);

		switch (busType) {
			//个人快捷支付账户充值
			case PwmConstants.BUS_TYPE_RECHARGE_QP:
				// 借：其他应付款-暂收-收银台
				cshItemReqDTO=acmComponent.createAccountingReqDTO(
						rechargeOrderDO.getExtOrderNo(),
						rechargeResultDTO.getTxJrnNo(),
						rechargeOrderDO.getTxType(),
						ACMConstants.ACCOUNTING_NOMARL,
						rechargeOrderDO.getOrderAmt(),
						balAcNo,
						ACMConstants.USER_AC_TYP,
						balCapType,
						ACMConstants.AC_D_FLG,
						CshConstants.AC_ITEM_CSH_PAY,
						null,
						null,
						null,
						null,
						null);

				// 贷：其他应付款-支付账户-xx用户现金账户
				userAccountReqDTO=acmComponent.createAccountingReqDTO(
						rechargeOrderDO.getExtOrderNo(),
						rechargeResultDTO.getTxJrnNo(),
						rechargeOrderDO.getTxType(),
						ACMConstants.ACCOUNTING_NOMARL,
						rechargeOrderDO.getOrderAmt(),
						null,
						ACMConstants.USER_AC_TYP,
						balCapType,
						ACMConstants.AC_C_FLG,
						CshConstants.AC_ITEM_CSH_BAL,
						balAcNo,
						null,
						null,
						null,
						null);
				acmComponent.requestAc(cshItemReqDTO,userAccountReqDTO);
				break;
			case PwmConstants.BUS_TYPE_RECHARGE_OFL:
				AccountingReqDTO cshItemReqDTO1=null;
//				借：银行存款-备付金账户-XX银行
				depositAccountBnkReqDTO=acmComponent.createAccountingReqDTO(
						rechargeOrderDO.getExtOrderNo(),
						rechargeResultDTO.getTxJrnNo(),
						rechargeOrderDO.getTxType(),
						ACMConstants.ACCOUNTING_NOMARL,
						rechargeOrderDO.getOrderAmt(),
						balAcNo,
						ACMConstants.ITM_AC_TYP,
						balCapType,
						ACMConstants.AC_D_FLG,
						PwmConstants.AC_ITEM_DEP_ACC_BNK,
						null,
						null,
						null,
						null,
						null);
//				贷：其他应付款-暂收-收银台
				cshItemReqDTO1=acmComponent.createAccountingReqDTO(
						rechargeOrderDO.getExtOrderNo(),
						rechargeResultDTO.getTxJrnNo(),
						rechargeOrderDO.getTxType(),
						ACMConstants.ACCOUNTING_NOMARL,
						rechargeOrderDO.getOrderAmt(),
						balAcNo,
						ACMConstants.ITM_AC_TYP,
						balCapType,
						ACMConstants.AC_D_FLG,
						CshConstants.AC_ITEM_CSH_PAY,
						null,
						null,
						null,
						null,
						null);
				acmComponent.requestAc(depositAccountBnkReqDTO,cshItemReqDTO);
				break;
//			case PwmConstants.BUS_TYPE_RECHARGE_HALL:
//				break;
			case PwmConstants.BUS_TYPE_RECHARGE_BNB:
				break;
			case PwmConstants.BUS_TYPE_WITHDRAW_P:
				break;
			case PwmConstants.BUS_TYPE_WITHDRAW_M:
				break;
			default:
				break;
		}

		// 更新订单
		RechargeOrderDO updOrderDO = new RechargeOrderDO();
		updOrderDO.setAcTm(resultDto.getAccDate());
		updOrderDO.setExtOrderNo(rechargeResultDTO.getExtOrderNo());
		updOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
		updOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
		updOrderDO.setOrderCcy(rechargeResultDTO.getOrderCcy());
		updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		updOrderDO.setOrderNo(orderNo);
		service.updateOrder(updOrderDO);
	}

	@Override
	public HallQueryResultDTO queryUserInfo(String userId, BigDecimal amount) {
		//根据手机号查询平台用户基本信息
		GenericDTO<UserBasicInfDTO> genericUserBasicInfDTO=  userBasicInfClient.queryUserByLoginId(userId);
		UserBasicInfDTO userBasicInfDTO = genericUserBasicInfDTO.getBody();

		TradeRateReqDTO tradeFeeReqDTO = new TradeRateReqDTO();
		tradeFeeReqDTO.setCcy(PwmConstants.HALL_PAY_CCY);
		tradeFeeReqDTO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_HALL);

		GenericDTO<TradeRateReqDTO> genericTradeRateReqDTO = new GenericDTO<>();
		genericTradeRateReqDTO.setBody(tradeFeeReqDTO);
		GenericRspDTO<TradeRateRspDTO> genericTradeFeeRspDTO = fmServerClient.tradeRate(genericTradeRateReqDTO);
		TradeRateRspDTO tradeRateRspDTO = genericTradeFeeRspDTO.getBody();

		//费率
		BigDecimal tradeFee = tradeRateRspDTO.getTradeRate();
		HallQueryResultDTO hallQueryResultDTO = new HallQueryResultDTO();
		hallQueryResultDTO.setFee(tradeFee.multiply(amount));
		hallQueryResultDTO.setUmId(userBasicInfDTO.getUserId());
		hallQueryResultDTO.setUmName(userBasicInfDTO.getUsrNm());
		return hallQueryResultDTO;
	}

	@Override
	public HallRechargeResultDTO hallRecharge(HallRechargeApplyDTO dto) {

		String merchantId = dto.getMerchantId();
		String applySign = dto.getSign();
		// 签名密钥
		String key = LemonUtils.getProperty("pwm.recharge.HALLKEY");
		HallRechargeApplyDTO.BussinessBody bussinessBody = dto.getBody();
		String md5 = md5Str(bussinessBody, key);

		// 签名校验
		if (JudgeUtils.isNull(md5) || !JudgeUtils.equals(applySign, md5.toUpperCase())) {
			throw new LemonException("PWM10036");
		}
		// 解析校验,状态
		if (!JudgeUtils.equals(bussinessBody.getStatus(), PwmConstants.RECHARGE_OPR_O)) {
			throw new LemonException("PWM10037");
		}
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = ymd + IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 15);
		// 生成充值订单
		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_HALL);
		rechargeOrderDO.setExtOrderNo(bussinessBody.getHallOrderNo());
		rechargeOrderDO.setOrderAmt(bussinessBody.getAmount());
		rechargeOrderDO.setOrderCcy(bussinessBody.getCcy());
		rechargeOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		rechargeOrderDO.setOrderNo(orderNo);
		rechargeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setOrderStatus(bussinessBody.getStatus());
		rechargeOrderDO.setIpAddress("");
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setOrderSuccTm(null);
		rechargeOrderDO.setPsnFlag(bussinessBody.getPsnFlag());
		rechargeOrderDO.setSysChannel(PwmConstants.ORD_SYSCHANNEL_HALL);
		rechargeOrderDO.setTxType(PwmConstants.TX_TYPE_RECHANGE);
		rechargeOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setRemark("");

		this.service.initOrder(rechargeOrderDO);

		// 调用收银,生成收银订单
		InitCashierDTO initCashierDTO = new InitCashierDTO();
		initCashierDTO.setBusPaytype(PwmConstants.BUS_TYPE_RECHARGE_HALL);
		initCashierDTO.setBusType(rechargeOrderDO.getBusType());
		initCashierDTO.setExtOrderNo(rechargeOrderDO.getOrderNo());
		initCashierDTO.setPayeeId(dto.getMerchantId());
		initCashierDTO.setSysChannel(PwmConstants.ORD_SYSCHANNEL_HALL);
		initCashierDTO.setPayerId(bussinessBody.getPayerId());
		initCashierDTO.setTxType(rechargeOrderDO.getTxType());
		initCashierDTO.setOrderAmt(bussinessBody.getAmount());
		initCashierDTO.setFee(BigDecimal.valueOf(bussinessBody.getFee()));
		initCashierDTO.setTxType(PwmConstants.TX_TYPE_RECHANGE);
		initCashierDTO.setOrderCcy(PwmConstants.HALL_PAY_CCY);
		initCashierDTO.setPayerName("");
		GenericDTO<InitCashierDTO> genericDTO = new GenericDTO<>();
		genericDTO.setBody(initCashierDTO);
		GenericDTO<CashierViewDTO> genericCashierViewDTO = cshOrderClient.initCashier(genericDTO);
		CashierViewDTO cashierViewDTO = genericCashierViewDTO.getBody();

		// 返回
		HallRechargeResultDTO hallRechargeResultDTO = new HallRechargeResultDTO();
		hallRechargeResultDTO.setAmount(bussinessBody.getAmount());
		hallRechargeResultDTO.setStatus(PwmConstants.RECHARGE_ORD_W);
		hallRechargeResultDTO.setFee(BigDecimal.valueOf(bussinessBody.getFee()));
		hallRechargeResultDTO.setHallOrderNo(bussinessBody.getHallOrderNo());
		hallRechargeResultDTO.setOrderNo(orderNo);
		hallRechargeResultDTO.setCashierOrderNo(cashierViewDTO.getOrderNo());
		return hallRechargeResultDTO;
	}

	@Override
	public HallRechargeResultDTO hallRechargeConfirm(HallRechargeApplyDTO dto) {
		HallRechargeApplyDTO.BussinessBody busBody= dto.getBody();
		//检查原订单
		String oriHallOrder = busBody.getHallOrderNo();
		if(JudgeUtils.isNull(oriHallOrder)) {
			throw new LemonException("PWM10011");
		}
		RechargeOrderDO rechargeOrderDO = this.service.getRechargeOrderByExtOrderNo(oriHallOrder);
		if(JudgeUtils.isNull(rechargeOrderDO)) {
			throw new LemonException("PWM20010");
		}
		if(JudgeUtils.equals(rechargeOrderDO.getOrderStatus(),PwmConstants.RECHARGE_ORD_W)) {
			throw new LemonException("PWM20011");
		}
		//状态 金额校验
		BigDecimal amount = busBody.getAmount();
		String status = busBody.getStatus();
		if(!JudgeUtils.equals(status, PwmConstants.RECHARGE_OPR_O)) {
			throw new LemonException("PWM10037");
		}
		if(JudgeUtils.equals(amount, rechargeOrderDO.getOrderAmt())) {
			throw new LemonException("PWM20003");
		}

		//调用收银台线下收款接口，完成用户线下充值
		HallRechargePaymentDTO hallRechargePaymentDTO = new HallRechargePaymentDTO();
		hallRechargePaymentDTO.setOrderNo(busBody.getCashierOrderNo());
		hallRechargePaymentDTO.setHallOrderNo(oriHallOrder);
		hallRechargePaymentDTO.setOrderCcy(PwmConstants.HALL_PAY_CCY);
		hallRechargePaymentDTO.setOrderAmt(busBody.getAmount());

		GenericDTO<HallRechargePaymentDTO> genericHallRechargePaymentDTO = new GenericDTO<>();
		genericHallRechargePaymentDTO.setBody(hallRechargePaymentDTO);
		GenericRspDTO<HallRechargePaymentResultDTO> GenericRspHallPaymentResult = cshOrderClient.hallRechargePayment(genericHallRechargePaymentDTO);

		HallRechargePaymentResultDTO hallRechargePaymentResult = GenericRspHallPaymentResult.getBody();
		//收银台收款失败
		if(!JudgeUtils.isSuccess(GenericRspHallPaymentResult.getMsgCd())) {
			throw new LemonException("PWM20013");
		}

		//账务处理
		AccountingReqDTO cshItemReqDTO=null;         //暂收收银台账务对象
		AccountingReqDTO userAccountReqDTO=null;

		//个人账户
		String balCapType= CapTypEnum.CAP_TYP_CASH.getCapTyp();
		String balAcNo=acmComponent.getAcmAcNo(busBody.getPayerId(), balCapType);
//		借：其他应付款-暂收-收银台
		cshItemReqDTO=acmComponent.createAccountingReqDTO(
				hallRechargePaymentResult.getCashierOrderNo(),
				hallRechargePaymentResult.getTxJrnNo(),
				PwmConstants.TX_TYPE_RECHANGE,
				ACMConstants.ACCOUNTING_NOMARL,
				hallRechargePaymentResult.getAmount(),
				balAcNo,
				ACMConstants.ITM_AC_TYP,
				balCapType,
				ACMConstants.AC_D_FLG,
				CshConstants.AC_ITEM_CSH_PAY,
				null,
				null,
				null,
				null,
				null);
//		贷：其他应付款-支付账户-现金账户
		userAccountReqDTO=acmComponent.createAccountingReqDTO(
				hallRechargePaymentResult.getCashierOrderNo(),
				hallRechargePaymentResult.getTxJrnNo(),
				PwmConstants.TX_TYPE_RECHANGE,
				ACMConstants.ACCOUNTING_NOMARL,
				hallRechargePaymentResult.getAmount(),
				balAcNo,
				ACMConstants.ITM_AC_TYP,
				balCapType,
				ACMConstants.AC_C_FLG,
				CshConstants.AC_ITEM_CSH_BAL,
				null,
				null,
				null,
				null,
				null);
		acmComponent.requestAc(userAccountReqDTO,cshItemReqDTO);
		//更新充值订单
		RechargeOrderDO updOrderDO = new RechargeOrderDO();
		updOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		updOrderDO.setExtOrderNo(rechargeOrderDO.getExtOrderNo());
		updOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
		updOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
		updOrderDO.setOrderCcy(rechargeOrderDO.getOrderCcy());
		updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		updOrderDO.setOrderNo(rechargeOrderDO.getOrderNo());
		updOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		service.updateOrder(updOrderDO);
		//调用平台短信能力通知充值到账

		//返回
		HallRechargeResultDTO hallRechargeResultDTO = new HallRechargeResultDTO();
		hallRechargeResultDTO.setAmount(busBody.getAmount());
		hallRechargeResultDTO.setStatus(PwmConstants.RECHARGE_ORD_S);
		hallRechargeResultDTO.setFee(BigDecimal.valueOf(busBody.getFee()));
		hallRechargeResultDTO.setHallOrderNo(busBody.getHallOrderNo());
		hallRechargeResultDTO.setOrderNo(rechargeOrderDO.getOrderNo());
		return hallRechargeResultDTO;
	}

	@Override
	public HallRechargeResultDTO hallRechargeRevocation(HallRechargeApplyDTO dto) {
		HallRechargeApplyDTO.BussinessBody busBody= dto.getBody();

		//充值原订单校验
		String oriHallOrder = busBody.getHallOrderNo();
		if(JudgeUtils.isNull(oriHallOrder)) {
			throw new LemonException("PWM10011");
		}
		if(!JudgeUtils.equals(busBody.getStatus(),PwmConstants.RECHARGE_OPR_C)) {
			throw new LemonException("PWM10037");
		}
		RechargeOrderDO rechargeOrderDO = this.service.getRechargeOrderByExtOrderNo(oriHallOrder);
		if(JudgeUtils.isNull(rechargeOrderDO)) {
			throw new LemonException("PWM20010");
		}
		if(JudgeUtils.equals(rechargeOrderDO.getOrderStatus(),PwmConstants.RECHARGE_ORD_W)) {
			throw new LemonException("PWM20011");
		}

		//更新订单状态
		RechargeOrderDO updOrderDO = new RechargeOrderDO();
		updOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		updOrderDO.setExtOrderNo(rechargeOrderDO.getExtOrderNo());
		updOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_F);
		updOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
		updOrderDO.setOrderCcy(rechargeOrderDO.getOrderCcy());
		updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		updOrderDO.setOrderNo(rechargeOrderDO.getOrderNo());
		updOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		service.updateOrder(updOrderDO);

		//更新收银订单状态
		HallRechargeOrderDTO hallRechargeOrderDTO = new HallRechargeOrderDTO();
		hallRechargeOrderDTO.setCshOrderNo(busBody.getCashierOrderNo());
		hallRechargeOrderDTO.setOrderStatus(CshConstants.ORD_STS_C);

		GenericDTO<HallRechargeOrderDTO> genericHallRechargeOrderDTO = new GenericDTO<>();
		genericHallRechargeOrderDTO.setBody(hallRechargeOrderDTO);
		GenericRspDTO<NoBody> cashierOrderUpdateResult = cshOrderClient.hallRevocationOrderUpdate(genericHallRechargeOrderDTO);
		if(!JudgeUtils.isSuccess(cashierOrderUpdateResult.getMsgCd())) {
			throw new LemonException("PWM20012");
		}

		//返回处理结果
		HallRechargeResultDTO hallRechargeResultDTO = new HallRechargeResultDTO();
		hallRechargeResultDTO.setAmount(busBody.getAmount());
		hallRechargeResultDTO.setStatus(PwmConstants.RECHARGE_ORD_F);
		hallRechargeResultDTO.setFee(BigDecimal.valueOf(busBody.getFee()));
		hallRechargeResultDTO.setHallOrderNo(busBody.getHallOrderNo());
		hallRechargeResultDTO.setOrderNo(rechargeOrderDO.getOrderNo());
		return hallRechargeResultDTO;

	}

	private String md5Str(HallRechargeApplyDTO.BussinessBody busBody, String key) {
		ObjectMapper om = new ObjectMapper();
		String retStr = "";
		try {
			Map<String,Object> oriMap = new LinkedHashMap<>();
			oriMap.put("amount",busBody.getAmount().setScale(2));
			oriMap.put("ccy",busBody.getCcy());
			oriMap.put("fee",BigDecimal.valueOf(busBody.getFee()).setScale(2));
			oriMap.put("hallOrderNo",busBody.getHallOrderNo());
			oriMap.put("payerId",busBody.getPayerId());
			oriMap.put("status",busBody.getStatus());
			retStr = om.writeValueAsString(oriMap);

			Md5PasswordEncoder md5 = new Md5PasswordEncoder();
			retStr = md5.encodePassword(retStr+key,null);
		} catch (JsonProcessingException e) {

		}
		return retStr;
	}

}
