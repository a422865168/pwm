package com.hisun.lemon.pwm.service.impl;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hisun.lemon.common.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.csh.client.CshOrderClient;
import com.hisun.lemon.csh.order.dto.InitCashierDTO;
import com.hisun.lemon.framework.data.GenericDTO;
import com.hisun.lemon.framework.utils.IdGenUtils;
import com.hisun.lemon.framework.utils.LemonUtils;
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

@Service
public class RechargeOrderServiceImpl implements IRechargeOrderService {

	private static final Logger logger = LoggerFactory.getLogger(RechargeOrderServiceImpl.class);
	@Resource
	RechargeOrderTransactionalService service;
	@Resource
	CshOrderClient cshOrderClient;

	/**
	 * 海币充值下单
	 */
	@Override
	public GenericDTO createHCouponOrder(GenericDTO<RechargeHCouponDTO> rechargeHCouponDTO) {
		RechargeHCouponDTO rechargeDTO = rechargeHCouponDTO.getBody();
		if (!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_HCOUPON)) {
			throw new LemonException("PWM20001");
		}

		RechargeHCouponDO rechargeDO = new RechargeHCouponDO();

		if (!JudgeUtils.isNull(rechargeDTO.getOrderCcy())) {

			rechargeDO.setOrderCcy(rechargeDTO.getOrderCcy());
		}
		rechargeDO.setOrderCcy("USD");
		rechargeDO.setAcTm(rechargeHCouponDTO.getAccDate());
		rechargeDO.setOrderStatus(PwmConstants.RECHARGE_ORD_W);
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = IdGenUtils.generateId(PwmConstants.R_SEA_GEN_PRE + ymd, 15);
		// 1:100的充值比例
		BigDecimal hCouponAmt = rechargeDTO.getOrderAmt().multiply(BigDecimal.valueOf(100));
		rechargeDO.sethCouponAmt(hCouponAmt);
		rechargeDO.setOrderNo(ymd + orderNo);
		rechargeDO.setOrderAmt(rechargeDTO.getOrderAmt());
		// 会计日期
		rechargeDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
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

		// 更新订单
		//计算海币数量  1:100multiply
		BigDecimal hCouponAmt=rechargSeaDTO.getOrderAmt().multiply(BigDecimal.valueOf(100));
		RechargeHCouponDO update=new RechargeHCouponDO();
	    update.setAcTm(rechargeHCouponDTO.getAccDate());
	    update.sethCouponAmt(hCouponAmt);
	    update.setOrderAmt(rechargSeaDTO.getOrderAmt());
		update.setOrderStatus(PwmConstants.RECHARGE_ORD_S);
		update.setOrderCcy(rechargSeaDTO.getOrderCcy());
		update.setOrderNo(rechargSeaDTO.getOrderNo());
		service.updateSeaOrder(update);
	}

	@Override
	public GenericDTO createOrder(RechargeDTO rechargeDTO, String ipAddress) {
		if (!rechargeDTO.getBusType().startsWith(PwmConstants.TX_TYPE_RECHANGE)) {
			throw new LemonException("PWM20001");
		}

		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 15);
		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(rechargeDTO.getBusType());
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setIpAddress(ipAddress);
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
		if (StringUtils.equals(rechargeOrderDO.getOrderStatus(), PwmConstants.RECHARGE_ORD_S)) {
			return;
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

		// 账务处理

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

		return null;
	}

	@Override
	public HallRechargeResultDTO hallRecharge(HallRechargeApplyDTO dto) {

		String merchantId = dto.getMerchantId();
		String applySign = dto.getSign();
		String key = "";
		HallRechargeApplyDTO.BussinessBody bussinessBody = dto.getBody();
		String md5 = md5Str(bussinessBody,key);
		//签名校验
		if(JudgeUtils.isNull(md5) || !JudgeUtils.equals(applySign, md5.toUpperCase())) {
			throw new LemonException("PWM10036");
		}
		//解析校验,状态
		if(!JudgeUtils.equals(bussinessBody.getStatus(), PwmConstants.RECHARGE_OPR_O)) {
			throw new LemonException("PWM10037");
		}
		String payerId = bussinessBody.getPayerId();
		String ymd = DateTimeUtils.getCurrentDateStr();
		String orderNo = IdGenUtils.generateId(PwmConstants.R_ORD_GEN_PRE + ymd, 15);
		//生成订单
		RechargeOrderDO rechargeOrderDO = new RechargeOrderDO();
		rechargeOrderDO.setAcTm(DateTimeUtils.getCurrentLocalDate());
		rechargeOrderDO.setBusType(PwmConstants.BUS_TYPE_RECHARGE_HALL);
		rechargeOrderDO.setExtOrderNo(bussinessBody.getHallOrderNo());
		rechargeOrderDO.setOrderAmt(bussinessBody.getAmount());
		rechargeOrderDO.setOrderCcy(bussinessBody.getCcy());
		rechargeOrderDO.setOrderExpTm(DateTimeUtils.parseLocalDateTime("99991231235959"));
		rechargeOrderDO.setOrderNo(ymd + orderNo);
		rechargeOrderDO.setOrderTm(DateTimeUtils.getCurrentLocalDateTime());
		rechargeOrderDO.setOrderStatus(bussinessBody.getStatus());
		rechargeOrderDO.setIpAddress("");
		rechargeOrderDO.setModifyOpr("");
		rechargeOrderDO.setOrderSuccTm(null);
		rechargeOrderDO.setPsnFlag(bussinessBody.getPsnFlag());
		rechargeOrderDO.setSysChannel("WEB");
		rechargeOrderDO.setTxType(PwmConstants.TX_TYPE_RECHANGE);
		rechargeOrderDO.setRemark("");

		this.service.initOrder(rechargeOrderDO);

		//调用收银

		//返回
		HallRechargeResultDTO hallRechargeResultDTO = new HallRechargeResultDTO();
		hallRechargeResultDTO.setAmount(bussinessBody.getAmount());
		hallRechargeResultDTO.setStatus(PwmConstants.RECHARGE_ORD_W);
		hallRechargeResultDTO.setFee(BigDecimal.valueOf(bussinessBody.getFee()));
		hallRechargeResultDTO.setHallOrderNo(bussinessBody.getHallOrderNo());
		hallRechargeResultDTO.setOrderNo(orderNo);
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
		if(!JudgeUtils.equals(status, PwmConstants.RECHARGE_OPR_C)) {
			throw new LemonException("PWM10037");
		}
		if(JudgeUtils.equals(amount, rechargeOrderDO.getOrderAmt())) {
			throw new LemonException("PWM20003");
		}
		//调用收银(更新账户余额)

		//更新订单
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
			retStr = getMd5(retStr+key);

		} catch (JsonProcessingException e) {
//			logger.error("PWM:营业厅充值请求参数转换成Json格式错误!");
		}
		return retStr;
	}

	private static String getMd5(String plainText) {
		try {
			MessageDigest md = null;
			md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			//32位加密
			return buf.toString();
			// 16位的加密
			//return buf.toString().substring(8, 24);
		} catch (NoSuchAlgorithmException e) {
			return "";
		}

	}

}
