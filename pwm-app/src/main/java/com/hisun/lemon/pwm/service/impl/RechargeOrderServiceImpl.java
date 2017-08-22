package com.hisun.lemon.pwm.service.impl;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import com.hisun.lemon.common.utils.BeanUtils;
import com.hisun.lemon.cpi.client.RouteClient;
import com.hisun.lemon.cpi.dto.RouteRspDTO;
import com.hisun.lemon.cpi.enums.CorpBusSubTyp;
import com.hisun.lemon.cpi.enums.CorpBusTyp;
import com.hisun.lemon.csh.dto.cashier.CashierViewDTO;
import com.hisun.lemon.csh.dto.cashier.InitCashierDTO;
import com.hisun.lemon.csh.dto.payment.*;
import com.hisun.lemon.jcommon.phonenumber.PhoneNumberUtils;
import com.hisun.lemon.pwm.dto.*;
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
	@Resource
	private RouteClient routeClient;

	/**
	 * 海币充值下单
	 */
	@Override
	public GenericRspDTO<CashierViewDTO> createHCouponOrder(GenericDTO<RechargeHCouponDTO> rechargeHCouponDTO) {
		RechargeHCouponDTO rechargeDTO = rechargeHCouponDTO.getBody();
		if (!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_HCOUPON)) {
			throw new LemonException("PWM20001");
		}

		RechargeHCouponDO rechargeDO = new RechargeHCouponDO();

		if (!JudgeUtils.isNull(rechargeDTO.getOrderCcy())) {

			rechargeDO.setOrderCcy(rechargeDTO.getOrderCcy());
		}
		rechargeDO.setOrderCcy(PwmConstants.HALL_PAY_CCY);
		// 会计日期
		rechargeDO.setAcTm(rechargeHCouponDTO.getAccDate());
		rechargeDO.setOrderStatus(PwmConstants.RECHARGE_ORD_W);
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = PwmConstants.BUS_TYPE_RECHARGE_HALL + ymd + IdGenUtils.generateId(PwmConstants.BUS_TYPE_HCOUPON + ymd, 15);
		// 1:100的充值比例
		BigDecimal hCouponAmt=rechargeDTO.gethCouponAmt();
		BigDecimal amount = hCouponAmt.multiply(BigDecimal.valueOf(PwmConstants.H_USD_RATE)).setScale(2, BigDecimal.ROUND_DOWN);
		rechargeDO.sethCouponAmt(hCouponAmt);
		rechargeDO.setOrderNo(orderNo);
		rechargeDO.setOrderAmt(amount);
		// 交易时间
		rechargeDO.setTxTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeDO.setTxType(rechargeDTO.getTxType());
		rechargeDO.setBusType(rechargeDTO.getBusType());
		rechargeDO.setUserId(LemonUtils.getUserId());
		// 生成海币充值订单
		this.service.initSeaOrder(rechargeDO);
		// 调用收银
		logger.info("订单：" + rechargeDO.getOrderNo() + " 请求收银台");
		InitCashierDTO initCashierDTO = new InitCashierDTO();
		initCashierDTO.setBusPaytype(null);
		initCashierDTO.setBusType(PwmConstants.BUS_TYPE_HCOUPON);
		initCashierDTO.setExtOrderNo(rechargeDO.getOrderNo());
		initCashierDTO.setSysChannel("APP");
		initCashierDTO.setPayerId(LemonUtils.getUserId());
		initCashierDTO.setPayeeId(LemonUtils.getUserId());
		initCashierDTO.setAppCnl(LemonUtils.getApplicationName());
		initCashierDTO.setTxType(rechargeDO.getTxType());
		initCashierDTO.setOrderAmt(rechargeDO.getOrderAmt());
		initCashierDTO.setGoodsDesc("海币充值");
		GenericDTO<InitCashierDTO> genericDTO = new GenericDTO<>();
		genericDTO.setBody(initCashierDTO);
		GenericRspDTO<CashierViewDTO> rspDTO = new GenericRspDTO<CashierViewDTO>();
		rspDTO = cshOrderClient.initCashier(genericDTO);
		if (!JudgeUtils.isSuccess(rspDTO.getMsgCd())) {
			throw new LemonException(rspDTO.getMsgCd());
		}
		return rspDTO;
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
		String balAcNo = acmComponent.getAcmAcNo(LemonUtils.getUserId(), balCapType);
		//借：其他应付款-暂收-收银台         100
		cshItemReqDTO=acmComponent.createAccountingReqDTO(
					rechargeSeaDO.getOrderNo(),
					payJrnNo, 
					rechargeSeaDO.getTxType(), 
					ACMConstants.ACCOUNTING_NOMARL, 
					rechargeSeaDO.getOrderAmt(),
					balAcNo, 
					ACMConstants.ITM_AC_TYP, 
					balCapType, 
					ACMConstants.AC_D_FLG, 
					PwmConstants.AC_ITEM_CSH_PAY,
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
					null, 
					ACMConstants.USER_AC_TYP,
					balCapType,
					ACMConstants.AC_C_FLG, 
					PwmConstants.AC_ITEM_HCOUPONE, 
					balAcNo, 
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
			throw new LemonException(mkmRsp.getMsgCd());
		}
	}

	@Override
	public GenericRspDTO createOrder(RechargeDTO rechargeDTO) {
		if (!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_RECHANGE)) {
			throw new LemonException("PWM20001");
		}

		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 15);
		orderNo=rechargeDTO.getBusType()+ymd + orderNo;

		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(rechargeDTO.getBusType());
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setOrderAmt(rechargeDTO.getAmount());
		rechargeOrderDO.setOrderCcy("USD");
		rechargeOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		rechargeOrderDO.setOrderNo(orderNo);
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
		initCashierDTO.setPayeeId(LemonUtils.getUserId());
		initCashierDTO.setAppCnl(LemonUtils.getApplicationName());
	  	initCashierDTO.setTxType(rechargeOrderDO.getTxType());
		initCashierDTO.setOrderAmt(rechargeDTO.getAmount());
		initCashierDTO.setGoodsDesc("在线充值$"+rechargeDTO.getAmount());
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

		//账号资金属性：1 现金 8 待结算
		String balCapType= CapTypEnum.CAP_TYP_CASH.getCapTyp();
		String payerId = rechargeResultDTO.getPayerId();
		//现金账户
		String balAcNo=acmComponent.getAcmAcNo(payerId, balCapType);

		switch (busType) {
			//个人快捷支付账户充值
			case PwmConstants.BUS_TYPE_RECHARGE_QP:
				if(JudgeUtils.isBlank(balAcNo)){
					throw new LemonException("PWM20022");
				}
				String tmpJrnNo =  IdGenUtils.generateIdWithDate(PwmConstants.R_ORD_GEN_PRE,14);
				// 借：其他应付款-暂收-收银台
				cshItemReqDTO=acmComponent.createAccountingReqDTO(rechargeOrderDO.getOrderNo(), tmpJrnNo, rechargeOrderDO.getTxType(),
						ACMConstants.ACCOUNTING_NOMARL, rechargeOrderDO.getOrderAmt(), balAcNo, ACMConstants.ITM_AC_TYP, balCapType, ACMConstants.AC_D_FLG,
						CshConstants.AC_ITEM_CSH_PAY, null, null, null, null, null);

				// 贷：其他应付款-支付账户-xx用户现金账户
				userAccountReqDTO=acmComponent.createAccountingReqDTO(rechargeOrderDO.getOrderNo(), tmpJrnNo, rechargeOrderDO.getTxType(),
						ACMConstants.ACCOUNTING_NOMARL, rechargeOrderDO.getOrderAmt(), balAcNo, ACMConstants.USER_AC_TYP, balCapType, ACMConstants.AC_C_FLG,
						CshConstants.AC_ITEM_CSH_BAL, null, null, null, null, "快捷充值$"+rechargeOrderDO.getOrderAmt());
				acmComponent.requestAc(cshItemReqDTO,userAccountReqDTO);
				break;
			case PwmConstants.BUS_TYPE_RECHARGE_OFL:
				RechargeOrderDO updOrderDO = new RechargeOrderDO();
				// 借：其他应付款-暂收-收银台
				cshItemReqDTO=acmComponent.createAccountingReqDTO(rechargeOrderDO.getExtOrderNo(), rechargeResultDTO.getTxJrnNo(), rechargeOrderDO.getTxType(),
						ACMConstants.ACCOUNTING_NOMARL, rechargeOrderDO.getOrderAmt(), balAcNo, ACMConstants.ITM_AC_TYP, balCapType, ACMConstants.AC_D_FLG,
						CshConstants.AC_ITEM_CSH_PAY, null, null, null, null, null);

				// 贷：其他应付款-支付账户-xx用户现金账户
				userAccountReqDTO=acmComponent.createAccountingReqDTO(rechargeOrderDO.getExtOrderNo(), rechargeResultDTO.getTxJrnNo(), rechargeOrderDO.getTxType(),
						ACMConstants.ACCOUNTING_NOMARL, rechargeOrderDO.getOrderAmt(), balAcNo, ACMConstants.USER_AC_TYP, balCapType, ACMConstants.AC_C_FLG,
						CshConstants.AC_ITEM_CSH_BAL, null, null, null, null, null);
				acmComponent.requestAc(userAccountReqDTO,cshItemReqDTO);
				//更新充值订单状态
				updOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
				updOrderDO.setAcTm(resultDto.getAccDate());
				updOrderDO.setExtOrderNo(rechargeResultDTO.getExtOrderNo());
				updOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
				updOrderDO.setOrderCcy(rechargeResultDTO.getOrderCcy());
				updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
				updOrderDO.setOrderNo(orderNo);
				service.updateOrder(updOrderDO);
				return;
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
	public HallQueryResultDTO queryUserInfo(String key, BigDecimal amount, String type) {
		GenericRspDTO<UserBasicInfDTO> genericUserBasicInfDTO = null;
		HallQueryResultDTO hallQueryResultDTO = new HallQueryResultDTO();

		if(JudgeUtils.isNotBlank(key)){
			//判断是否是通过手机号查询
			boolean isPhone = PhoneNumberUtils.isValidNumber(key,PwmConstants.COUNTRY_CODE_KHM);
			if(isPhone){
				genericUserBasicInfDTO = userBasicInfClient.queryUserByLoginId(key);
			}else{
				//根据id查询
				genericUserBasicInfDTO = userBasicInfClient.queryUser(key);
			}
			if(JudgeUtils.isNotSuccess(genericUserBasicInfDTO.getMsgCd())) {
				LemonException.throwBusinessException(genericUserBasicInfDTO.getMsgCd());
			}

			UserBasicInfDTO userBasicInfDTO = genericUserBasicInfDTO.getBody();

			if(JudgeUtils.isNotNull(amount)){
				TradeRateReqDTO tradeFeeReqDTO = new TradeRateReqDTO();
				tradeFeeReqDTO.setCcy(PwmConstants.HALL_PAY_CCY);
				tradeFeeReqDTO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_HALL);
				GenericDTO<TradeRateReqDTO> genericTradeRateReqDTO = new GenericDTO<>();
				genericTradeRateReqDTO.setBody(tradeFeeReqDTO);

				GenericRspDTO<TradeRateRspDTO> genericTradeFeeRspDTO = fmServerClient.tradeRate(genericTradeRateReqDTO);
				TradeRateRspDTO tradeRateRspDTO = genericTradeFeeRspDTO.getBody();
				//费率
				BigDecimal tradeFee = tradeRateRspDTO.getRate();

				hallQueryResultDTO.setFee(amount.multiply(tradeFee));
			}

			hallQueryResultDTO.setUmId(userBasicInfDTO.getUserId());
			hallQueryResultDTO.setKey(key);

			if(JudgeUtils.equals(PwmConstants.HALL_QUERY_TYPE_U,type)) {
				hallQueryResultDTO.setUmName(userBasicInfDTO.getUsrNm());
			} else if (JudgeUtils.equals(PwmConstants.HALL_QUERY_TYPE_M,type)) {
				hallQueryResultDTO.setUmName(userBasicInfDTO.getMercName());
			}
		}

		return hallQueryResultDTO;
	}

	@Override
	public HallRechargeResultDTO hallRechargePay(HallRechargeApplyDTO dto) {
		HallRechargeApplyDTO.BussinessBody bussinessBody = dto.getBody();

		//签名校验
		signCheck(dto);
		// 解析校验,状态
		if (!JudgeUtils.equals(bussinessBody.getStatus(), PwmConstants.RECHARGE_OPR_A)) {
			throw new LemonException("PWM10037");
		}

		//重复下单校验
		RechargeOrderDO oriRechargeOrderDO = this.service.getRechargeOrderByHallOrderNo(bussinessBody.getHallOrderNo());
		if(JudgeUtils.isNotNull(oriRechargeOrderDO)){
			throw new LemonException("PWM20021");
		}

		TradeRateReqDTO tradeFeeReqDTO = new TradeRateReqDTO();
		tradeFeeReqDTO.setCcy(PwmConstants.HALL_PAY_CCY);
		tradeFeeReqDTO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_HALL);
		GenericDTO<TradeRateReqDTO> genericTradeRateReqDTO = new GenericDTO<>();
		genericTradeRateReqDTO.setBody(tradeFeeReqDTO);
		GenericRspDTO<TradeRateRspDTO> genericTradeFeeRspDTO = fmServerClient.tradeRate(genericTradeRateReqDTO);
		if(JudgeUtils.isNotSuccess(genericTradeFeeRspDTO.getMsgCd())){
			logger.error("营业厅充值查询费率失败");
			LemonException.throwBusinessException(genericTradeFeeRspDTO.getMsgCd());
		}
		TradeRateRspDTO tradeRateRspDTO = genericTradeFeeRspDTO.getBody();
		//费率
		BigDecimal tradeFee = tradeRateRspDTO.getRate();


		String ymd = DateTimeUtils.getCurrentDateStr();
		BigDecimal orderAmt = bussinessBody.getAmount();
		String orderNo = PwmConstants.BUS_TYPE_RECHARGE_HALL + ymd + IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 15);
		// 生成充值订单
		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_HALL);
		rechargeOrderDO.setExtOrderNo("");
		rechargeOrderDO.setOrderAmt(orderAmt);
		rechargeOrderDO.setOrderCcy(bussinessBody.getCcy());
		rechargeOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		rechargeOrderDO.setOrderNo(orderNo);
		rechargeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setOrderStatus(bussinessBody.getStatus());
		rechargeOrderDO.setIpAddress("");
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setOrderSuccTm(null);
		rechargeOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_W);
		rechargeOrderDO.setPsnFlag(bussinessBody.getPsnFlag());
		rechargeOrderDO.setSysChannel(PwmConstants.ORD_SYSCHANNEL_HALL);
		rechargeOrderDO.setTxType(PwmConstants.TX_TYPE_RECHANGE);
		rechargeOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setRemark("");
		rechargeOrderDO.setFee(orderAmt.multiply(tradeFee).setScale(2));
		this.service.initOrder(rechargeOrderDO);

		//调用收银台线下收款接口，完成用户线下充值
		HallRechargePaymentDTO hallRechargePaymentDTO = new HallRechargePaymentDTO();
		hallRechargePaymentDTO.setHallOrderNo(orderNo);
		hallRechargePaymentDTO.setOrderCcy(PwmConstants.HALL_PAY_CCY);
		hallRechargePaymentDTO.setOrderAmt(bussinessBody.getAmount());
		hallRechargePaymentDTO.setFee(BigDecimal.valueOf(bussinessBody.getFee()));
		hallRechargePaymentDTO.setPayerId(bussinessBody.getPayerId());

		GenericDTO<HallRechargePaymentDTO> genericHallRechargePaymentDTO = new GenericDTO<>();
		genericHallRechargePaymentDTO.setBody(hallRechargePaymentDTO);
		GenericRspDTO<HallRechargePaymentResultDTO> genericRspHallPaymentResult = cshOrderClient.hallRechargePayment(genericHallRechargePaymentDTO);
		HallRechargePaymentResultDTO hallPayResult = genericRspHallPaymentResult.getBody();

		if(JudgeUtils.isNotSuccess(genericRspHallPaymentResult.getMsgCd())) {
			//更新充值订单状态
			RechargeOrderDO updatOrderDO = new RechargeOrderDO();
			updatOrderDO.setOrderNo(orderNo);
			updatOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
			updatOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_F);
			updatOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
			this.service.updateOrder(updatOrderDO);
			LemonException.throwBusinessException(genericRspHallPaymentResult.getMsgCd());
		}
		//账务处理
		AccountingReqDTO cshItemReqDTO=null;         //暂收收银台账务对象
		AccountingReqDTO userAccountReqDTO=null;

		//个人账户
		String balCapType= CapTypEnum.CAP_TYP_CASH.getCapTyp();
		String balAcNo=acmComponent.getAcmAcNo(bussinessBody.getPayerId(), balCapType);
		BigDecimal addAcAmt = orderAmt.subtract(hallPayResult.getFee()).setScale(2);
		//借：其他应付款-暂收-收银台
		cshItemReqDTO=acmComponent.createAccountingReqDTO(
				rechargeOrderDO.getOrderNo(),
                rechargeOrderDO.getExtOrderNo(),
				PwmConstants.TX_TYPE_RECHANGE,
				ACMConstants.ACCOUNTING_NOMARL,
				hallPayResult.getAmount(),
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
		//贷：其他应付款-支付账户-现金账户
		userAccountReqDTO=acmComponent.createAccountingReqDTO(
				rechargeOrderDO.getOrderNo(),
				rechargeOrderDO.getExtOrderNo(),
				PwmConstants.TX_TYPE_RECHANGE,
				ACMConstants.ACCOUNTING_NOMARL,
				addAcAmt,
				balAcNo,
				ACMConstants.USER_AC_TYP,
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
		//更新收银订单号
		updOrderDO.setExtOrderNo(hallPayResult.getCashierOrderNo());
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
		hallRechargeResultDTO.setAmount(hallPayResult.getAmount());
		hallRechargeResultDTO.setStatus(PwmConstants.RECHARGE_ORD_S);
		hallRechargeResultDTO.setFee(hallPayResult.getFee());
		hallRechargeResultDTO.setHallOrderNo(hallPayResult.getHallOrderNo());
		hallRechargeResultDTO.setOrderNo(hallPayResult.getCashierOrderNo());
		return hallRechargeResultDTO;
	}


	@Override
	public HallRechargeResultDTO hallRechargeRevocation(HallRechargeApplyDTO dto) {
		HallRechargeApplyDTO.BussinessBody busBody= dto.getBody();
		//签名校验
		signCheck(dto);
		//充值原订单校验
		String oriHallOrder = busBody.getHallOrderNo();
		if(!JudgeUtils.equals(busBody.getStatus(),PwmConstants.RECHARGE_OPR_C)) {
			throw new LemonException("PWM10037");
		}
		RechargeOrderDO rechargeOrderDO = this.service.getRechargeOrderByHallOrderNo(oriHallOrder);
		if(JudgeUtils.isNull(rechargeOrderDO)) {
			throw new LemonException("PWM20010");
		}
		//查询充值订单状态
		if(JudgeUtils.equals(rechargeOrderDO.getOrderStatus(),PwmConstants.RECHARGE_ORD_S)){
			//营业厅冲正账务处理
			//借：其他应付款-支付账户-现金账户
			AccountingReqDTO cshItemReqDTO=acmComponent.createAccountingReqDTO(
					rechargeOrderDO.getOrderNo(),
					rechargeOrderDO.getExtOrderNo(),
					rechargeOrderDO.getTxType(),
					ACMConstants.ACCOUNTING_CANCEL,
					rechargeOrderDO.getOrderAmt(),
					null,
					ACMConstants.ITM_AC_TYP,
					null,
					ACMConstants.AC_D_FLG,
					CshConstants.AC_ITEM_CSH_BAL,
					null,
					null,
					null,
					null,
					null);

			//贷：应收账款-渠道充值-营业厅
			AccountingReqDTO cnlRechargeHallReqDTO=acmComponent.createAccountingReqDTO(
					rechargeOrderDO.getOrderNo(),
					rechargeOrderDO.getExtOrderNo(),
					rechargeOrderDO.getTxType(),
					ACMConstants.ACCOUNTING_CANCEL,
					rechargeOrderDO.getOrderAmt(),
					null,
					ACMConstants.ITM_AC_TYP,
					null,
					ACMConstants.AC_C_FLG,
					CshConstants.AC_ITEM_CNL_RECHARGE_HALL,
					null,
					null,
					null,
					null,
					null);

			acmComponent.requestAc(cshItemReqDTO,cnlRechargeHallReqDTO);
		}

		//更新收银订单状态
		HallRechargeOrderDTO hallRechargeOrderDTO = new HallRechargeOrderDTO();
		hallRechargeOrderDTO.setPayerId(busBody.getPayerId());
		if(JudgeUtils.isNotNull(busBody.getFee())){
			hallRechargeOrderDTO.setFee(BigDecimal.valueOf(busBody.getFee()));
		}
		hallRechargeOrderDTO.setOrderAmt(rechargeOrderDO.getOrderAmt());
		hallRechargeOrderDTO.setOrderNo(rechargeOrderDO.getOrderNo());
		hallRechargeOrderDTO.setOrderStatus(PwmConstants.RECHARGE_ORD_C);

		GenericDTO<HallRechargeOrderDTO> genericHallRechargeOrderDTO = new GenericDTO<>();
		genericHallRechargeOrderDTO.setBody(hallRechargeOrderDTO);
		GenericRspDTO<NoBody> cashierOrderUpdateResult = cshOrderClient.hallRevocationOrderUpdate(genericHallRechargeOrderDTO);
		if(!JudgeUtils.isSuccess(cashierOrderUpdateResult.getMsgCd())) {
			LemonException.throwBusinessException(cashierOrderUpdateResult.getMsgCd());
		}

		//更新订单状态
		RechargeOrderDO updOrderDO = new RechargeOrderDO();
		updOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		updOrderDO.setExtOrderNo(rechargeOrderDO.getExtOrderNo());
		updOrderDO.setOrderStatus(PwmConstants.RECHARGE_ORD_C);
		updOrderDO.setOrderSuccTm(DateTimeUtils.getCurrentLocalDateTime());
		updOrderDO.setOrderCcy(rechargeOrderDO.getOrderCcy());
		updOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		updOrderDO.setOrderNo(rechargeOrderDO.getOrderNo());
		updOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		service.updateOrder(updOrderDO);

		//返回处理结果
		HallRechargeResultDTO hallRechargeResultDTO = new HallRechargeResultDTO();
		hallRechargeResultDTO.setAmount(updOrderDO.getOrderAmt());
		hallRechargeResultDTO.setStatus(updOrderDO.getOrderStatus());
		hallRechargeResultDTO.setFee(BigDecimal.valueOf(busBody.getFee()));
		hallRechargeResultDTO.setHallOrderNo(updOrderDO.getExtOrderNo());
		hallRechargeResultDTO.setOrderNo(updOrderDO.getOrderNo());
		return hallRechargeResultDTO;

	}

	@Override
	public OfflineRechargeResultDTO offlineRechargeApplication(GenericDTO<OfflineRechargeApplyDTO> genericDTO) {
		OfflineRechargeApplyDTO offlineRechargeApplyDTO = genericDTO.getBody();
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = PwmConstants.BUS_TYPE_RECHARGE_OFL + ymd + IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 15);
		// 生成充值订单
		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_OFL);
		rechargeOrderDO.setExtOrderNo("");
		rechargeOrderDO.setOrderAmt(offlineRechargeApplyDTO.getAmount());
		rechargeOrderDO.setOrderCcy(offlineRechargeApplyDTO.getCcy());
		rechargeOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		rechargeOrderDO.setOrderNo(orderNo);
		rechargeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setIpAddress("");
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setOrderSuccTm(null);
		rechargeOrderDO.setCrdCorpOrg(offlineRechargeApplyDTO.getCrdCorpOrg());
		//设置汇款订单状态
		rechargeOrderDO.setOrderStatus(PwmConstants.OFFLINE_RECHARGE_ORD_W);
		rechargeOrderDO.setPsnFlag(offlineRechargeApplyDTO.getPsnFlag());
		rechargeOrderDO.setSysChannel(PwmConstants.ORD_SYSCHANNEL_APP);
		rechargeOrderDO.setTxType(PwmConstants.TX_TYPE_RECHANGE);
		rechargeOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setRemark("");

		this.service.initOrder(rechargeOrderDO);

		// 调用收银,生成收银订单
		InitCashierDTO initCashierDTO = new InitCashierDTO();
		initCashierDTO.setBusPaytype(PwmConstants.BUS_TYPE_RECHARGE_OFL);
		initCashierDTO.setBusType(rechargeOrderDO.getBusType());
		initCashierDTO.setExtOrderNo(rechargeOrderDO.getOrderNo());
		initCashierDTO.setPayeeId(offlineRechargeApplyDTO.getPayerId());
		initCashierDTO.setSysChannel(PwmConstants.ORD_SYSCHANNEL_APP);
		initCashierDTO.setPayerId(offlineRechargeApplyDTO.getPayerId());
		initCashierDTO.setTxType(rechargeOrderDO.getTxType());
		initCashierDTO.setOrderAmt(offlineRechargeApplyDTO.getAmount());
		initCashierDTO.setAppCnl("PWM");
		initCashierDTO.setFee(BigDecimal.valueOf(0));
		initCashierDTO.setTxType(PwmConstants.TX_TYPE_RECHANGE);
		initCashierDTO.setOrderCcy(PwmConstants.HALL_PAY_CCY);
		initCashierDTO.setPayerName("");
		initCashierDTO.setGoodsDesc("汇款充值$"+rechargeOrderDO.getOrderAmt());

		GenericDTO<InitCashierDTO> genericCashierDTO = new GenericDTO<>();
		genericCashierDTO.setBody(initCashierDTO);
		GenericRspDTO<CashierViewDTO> genericCashierViewDTO = cshOrderClient.initCashier(genericCashierDTO);
		CashierViewDTO cashierViewDTO = genericCashierViewDTO.getBody();
		if(JudgeUtils.isNotSuccess(genericCashierViewDTO.getMsgCd())) {
			//更新充值订单状态
			RechargeOrderDO updatOrderDO = new RechargeOrderDO();
			updatOrderDO.setOrderNo(orderNo);
			updatOrderDO.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
			updatOrderDO.setOrderStatus(PwmConstants.OFFLINE_RECHARGE_ORD_F);
			this.service.updateOrder(updatOrderDO);
			LemonException.throwBusinessException(genericCashierViewDTO.getMsgCd());
		}
		//更新充值订单的收银业务订单号
		rechargeOrderDO.setExtOrderNo(cashierViewDTO.getOrderNo());
		this.service.updateOrder(rechargeOrderDO);

		GenericRspDTO<RouteRspDTO> genericRounteListRsp = routeClient.queryEffOrgInfo(CorpBusTyp.REMITTANCE, CorpBusSubTyp.REMITTANCE);
		RouteRspDTO routeRspDTO = genericRounteListRsp.getBody();
		List<RouteRspDTO.RouteDTO> rounteList = routeRspDTO.getList();
		if(JudgeUtils.isNotSuccess(genericRounteListRsp.getMsgCd())) {
			LemonException.throwBusinessException(genericRounteListRsp.getMsgCd());
		}
		GenericRspDTO<UserBasicInfDTO> genericUserBasicInfDTO = userBasicInfClient.queryUser(offlineRechargeApplyDTO.getPayerId());
		UserBasicInfDTO userBasicInfDTO = genericUserBasicInfDTO.getBody();
		OfflineRechargeResultDTO offlineRechargeResultDTO = new OfflineRechargeResultDTO();
		offlineRechargeResultDTO.setAmount(rechargeOrderDO.getOrderAmt());
		offlineRechargeResultDTO.setCcy(rechargeOrderDO.getOrderCcy());
		offlineRechargeResultDTO.setPayerId(offlineRechargeApplyDTO.getPayerId());
		offlineRechargeResultDTO.setStatus(PwmConstants.OFFLINE_RECHARGE_ORD_W);
		offlineRechargeResultDTO.setOrderNo(rechargeOrderDO.getOrderNo());
		offlineRechargeResultDTO.setOrderTm(DateTimeUtils.getCurrentLocalTime());
		offlineRechargeResultDTO.setCashierOrderNo(cashierViewDTO.getOrderNo());
		if(JudgeUtils.isNotNull(userBasicInfDTO)){
			offlineRechargeResultDTO.setMblNo(userBasicInfDTO.getMblNo());
		}
		String crdCorpOrg = offlineRechargeApplyDTO.getCrdCorpOrg();
		for(RouteRspDTO.RouteDTO rd : rounteList) {
			if(JudgeUtils.equals(crdCorpOrg,rd.getCrdCorpOrg())) {
				//汇款银行账号
				offlineRechargeResultDTO.setCrdNo(rd.getCorpAccNo());
				//汇款银行账户
				offlineRechargeResultDTO.setCrdUsrNm("");
				break;
			}
		}
		return offlineRechargeResultDTO;
	}

	@Override
	public OfflineRechargeResultDTO offlineRemittanceUpload(GenericDTO<RemittanceUploadDTO> genericDTO) {
		RemittanceUploadDTO remittanceUploadDTO = genericDTO.getBody();
		//原订单校验
		String orderNo = remittanceUploadDTO.getOrderNo();
		RechargeOrderDO rechargeOrderDO = this.service.getRechangeOrderDao().get(orderNo);

		if(JudgeUtils.isNull(rechargeOrderDO)) {
			throw new LemonException("PWM20015");
		}
		//判断充值订单是否已提交审核
		if(JudgeUtils.equals(PwmConstants.OFFLINE_RECHARGE_ORD_W1,rechargeOrderDO.getOrderStatus())) {
			throw new LemonException("PWM20017");
		}

		//查询汇款充值个人信息
		GenericRspDTO<UserBasicInfDTO> genericUserBasicInfDTO = userBasicInfClient.queryUser(remittanceUploadDTO.getPayerId());
		UserBasicInfDTO userBasicInfDTO = genericUserBasicInfDTO.getBody();

		//根据资金机构查询汇款账户
		GenericRspDTO<RouteRspDTO> genericRounteListRsp = routeClient.queryEffOrgInfo(CorpBusTyp.REMITTANCE, CorpBusSubTyp.REMITTANCE);
		RouteRspDTO routeRspDTO = genericRounteListRsp.getBody();
		List<RouteRspDTO.RouteDTO> rounteList = routeRspDTO.getList();
		if(JudgeUtils.isNotSuccess(genericRounteListRsp.getMsgCd())) {
			LemonException.throwBusinessException(genericRounteListRsp.getMsgCd());
		}
		RouteRspDTO.RouteDTO routeDTO = rounteList.get(0);
		GenericDTO<OfflinePaymentDTO> genericOfflinePaymentDTO = new GenericDTO<>();
		OfflinePaymentDTO offlinePaymentDTO = new OfflinePaymentDTO();
		offlinePaymentDTO.setCashRemittUrl(remittanceUploadDTO.getRemittUrl());
		offlinePaymentDTO.setPayerId(remittanceUploadDTO.getPayerId());
		offlinePaymentDTO.setRemark(remittanceUploadDTO.getRemark());
		offlinePaymentDTO.setCrdCorpOrg(routeDTO.getCrdCorpOrg());
		offlinePaymentDTO.setCrdAcTyp(routeDTO.getCrdAcTyp());
		offlinePaymentDTO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_OFL);
		offlinePaymentDTO.setOrderNo(rechargeOrderDO.getOrderNo());
		genericOfflinePaymentDTO.setBody(offlinePaymentDTO);

		//收银台线下汇款处理
		GenericRspDTO<OfflinePaymentResultDTO> genericOfflinePaymentResultDTO = cshOrderClient.offlinePayment(genericOfflinePaymentDTO);
		OfflinePaymentResultDTO offlinePaymentResultDTO = genericOfflinePaymentResultDTO.getBody();
		if (JudgeUtils.isNotSuccess(genericOfflinePaymentResultDTO.getMsgCd())) {
			LemonException.throwBusinessException(genericOfflinePaymentResultDTO.getMsgCd());
		}
		//更新充值订单状态为已提交审核
		RechargeOrderDO updateRechargeOrderDo = new RechargeOrderDO();
		BeanUtils.copyProperties(updateRechargeOrderDo,rechargeOrderDO);
		updateRechargeOrderDo.setOrderStatus(PwmConstants.OFFLINE_RECHARGE_ORD_W1);
		updateRechargeOrderDo.setModifyTime(DateTimeUtils.getCurrentLocalDateTime());
		this.service.getRechangeOrderDao().update(updateRechargeOrderDo);

		OfflineRechargeResultDTO offlineRechargeResultDTO = new OfflineRechargeResultDTO();
		offlineRechargeResultDTO.setCashierOrderNo(offlinePaymentResultDTO.getCashierOrderNo());
		offlineRechargeResultDTO.setOrderTm(DateTimeUtils.getCurrentLocalTime());
		offlineRechargeResultDTO.setPayerId(offlinePaymentResultDTO.getPayerId());
		offlineRechargeResultDTO.setAmount(offlinePaymentResultDTO.getOrderAmt());
		offlineRechargeResultDTO.setCcy(offlinePaymentResultDTO.getOrderCcy());
		offlineRechargeResultDTO.setOrderNo(offlinePaymentResultDTO.getRemittOrderNo());
		offlineRechargeResultDTO.setStatus(offlinePaymentResultDTO.getOrderStatus());
		offlineRechargeResultDTO.setMblNo(userBasicInfDTO.getMblNo());
		offlineRechargeResultDTO.setRemark(offlinePaymentResultDTO.getRemark());
		return offlineRechargeResultDTO;
	}

	@Override
	public HallOrderQueryResultDTO queryOrderInfo(String hallOrderNo) {
		if (JudgeUtils.isBlank(hallOrderNo)) {
			return null;
		}
		//订单信息查询
		RechargeOrderDO rechargeOrderDO = this.service.getRechargeOrderByHallOrderNo(hallOrderNo);
		HallOrderQueryResultDTO hallOrderQueryResultDTO = new HallOrderQueryResultDTO();
		if (JudgeUtils.isNotNull(rechargeOrderDO)) {
			hallOrderQueryResultDTO.setHallOrderNo(rechargeOrderDO.getExtOrderNo());
			hallOrderQueryResultDTO.setOrderStatus(rechargeOrderDO.getOrderStatus());
			hallOrderQueryResultDTO.setOrderNo(rechargeOrderDO.getOrderNo());
			hallOrderQueryResultDTO.setOrderAmt(rechargeOrderDO.getOrderAmt());
			hallOrderQueryResultDTO.setOrderTm(rechargeOrderDO.getOrderTm());
			hallOrderQueryResultDTO.setFee(rechargeOrderDO.getFee());
			hallOrderQueryResultDTO.setTotalAmt(rechargeOrderDO.getOrderAmt().multiply(rechargeOrderDO.getFee()).setScale(2));
		}
		return hallOrderQueryResultDTO;

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

	private void signCheck(HallRechargeApplyDTO dto) {
		String applySign = dto.getSign();
		// 签名密钥
		String key = LemonUtils.getProperty("pwm.recharge.HALLKEY");
		HallRechargeApplyDTO.BussinessBody bussinessBody = dto.getBody();
		String md5 = md5Str(bussinessBody, key);
		// 签名校验
		if (JudgeUtils.isNull(md5) || !JudgeUtils.equals(applySign, md5.toUpperCase())) {
			throw new LemonException("PWM10036");
		}
	}
}
