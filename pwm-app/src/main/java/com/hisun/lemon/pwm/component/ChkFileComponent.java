package com.hisun.lemon.pwm.component;


import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.common.utils.StringUtils;
import com.hisun.lemon.framework.data.BaseDO;
import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.jcommon.file.FileSftpUtils;
import com.hisun.lemon.jcommon.file.FileUtils;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.dao.IRechargeHCouponDao;
import com.hisun.lemon.pwm.dao.IRechargeOrderDao;
import com.hisun.lemon.pwm.dao.IWithdrawOrderDao;
import com.hisun.lemon.pwm.entity.RechargeHCouponDO;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对账文件组件
 * 
 * @author tome
 *
 */
@Component
public class ChkFileComponent {
	private static final Logger logger = LoggerFactory.getLogger(ChkFileComponent.class);
	final String yyyyMMdd="yyyyMMdd";

	final int defaultSftpTimeout=2000;

	@Resource
	private IRechargeOrderDao rechargeOrderDao;

	@Resource
	private IRechargeHCouponDao rechargeHCouponDao;

	@Resource
	private IWithdrawOrderDao withdrawOrderDao;

	public List<BaseDO> queryDatas(LocalDate date,String[] chkOrderStatus){
		Map queryDo=new HashMap<>();
		queryDo.put("acTm",date);
		queryDo.put("statusList",chkOrderStatus);

		List<BaseDO> datas=new ArrayList<>();
		datas.addAll(rechargeOrderDao.queryList(queryDo));

		datas.addAll(rechargeHCouponDao.queryList(queryDo));
		return datas;
	}

	public List<WithdrawOrderDO> queryWithdraws(LocalDate date,String[] chkOrderStatus){
		Map queryDo=new HashMap<>();
		queryDo.put("acTm",date);
		queryDo.put("statusList",chkOrderStatus);
		return withdrawOrderDao.queryList(queryDo);
	}

	public List<RechargeOrderDO> queryHallRecharges(LocalDate date,String[] chkOrderStatus){
		Map queryDo=new HashMap<>();
		queryDo.put("acTm",date);
		queryDo.put("statusList",chkOrderStatus);
		queryDo.put("busType", PwmConstants.BUS_TYPE_RECHARGE_HALL);
		return rechargeOrderDao.queryListOfHall(queryDo);
	}

	public List<WithdrawOrderDO> queryHallWithdraw(LocalDate date,String[] chkOrderStatus){
		Map queryDo=new HashMap<>();
		queryDo.put("acTm",date);
		queryDo.put("statusList",chkOrderStatus);
		queryDo.put("busType", PwmConstants.BUS_TYPE_WITHDRAW_HALL);
		return withdrawOrderDao.queryListOfHall(queryDo);
	}

	/**
	 * 获取对账数据日期
	 * @return
	 */
	public LocalDate getChkDate(){
		LocalDate today= DateTimeUtils.getCurrentLocalDate();
		return today.minusDays(1);
	}

	/**
	 * 获取对账文件名
	 * @param appCnl
	 * @param chkDate
	 * @return
	 */
	public String getChkFileName(String appCnl,LocalDate chkDate){
		return LemonUtils.getApplicationName()+"_"+appCnl+"_"+DateTimeUtils.formatLocalDate(chkDate,yyyyMMdd)+".ck";
	}

	/**
	 * 数据写入对账文件
	 * @param datas
	 * @param fileName
	 */
	public void writeToFile(String appCnl,List datas,String fileName){
		StringBuilder contextBuilder=new StringBuilder();
		for(int i=0;i<datas.size();i++){
			BaseDO rec=(BaseDO)datas.get(i);
			if(rec instanceof RechargeOrderDO){
				contextBuilder.append(recToLine((RechargeOrderDO)rec));
			}else if(rec instanceof WithdrawOrderDO){
                contextBuilder.append(recToWithdrawLine((WithdrawOrderDO)rec));
            }else{
				if(rec instanceof RechargeHCouponDO){
					contextBuilder.append(recToHLine((RechargeHCouponDO)rec));
				}
			}
		}


		//写入文件
		try {
			String localPath=getLocalPath(appCnl);
			FileUtils.write(contextBuilder.toString(), localPath + fileName);
		} catch (Exception e) {
			LemonException.throwBusinessException("PWM40701");
		}

}

	/**
	 * 文件上传SFTP服务器
	 * @param chkFileName
	 * @param flagName
	 */
	public void upload(String appCnl,String chkFileName, String flagName){
		String localPath=getLocalPath(appCnl);
		String[] uploadFileNames=new String[]{localPath+chkFileName,localPath+flagName};

		String remoteIp=LemonUtils.getProperty("pwm.sftp.ip");
		int remotePort=Integer.valueOf(LemonUtils.getProperty("pwm.sftp.port"));
		String timeoutStr=LemonUtils.getProperty("pwm.sftp.connectTimeout");
		int connectTimeout=defaultSftpTimeout;
		if(StringUtils.isNotEmpty(timeoutStr)){
			connectTimeout=Integer.valueOf(timeoutStr);
		}

		String remotePath=LemonUtils.getProperty("pwm.chk.remotePath");

		String name=LemonUtils.getProperty("pwm.sftp.name");
		String pwd=LemonUtils.getProperty("pwm.sftp.password");

		try {
			FileSftpUtils.upload(uploadFileNames,remoteIp,remotePort,connectTimeout,remotePath,name,pwd);
		} catch (Exception e) {
			logger.error(chkFileName+"上传SFTP文件服务器失败",e);
			LemonException.throwBusinessException("PWM40702");
		}
	}


	public boolean isStart(String appCnl,String flagName){
		return new File(getLocalPath(appCnl)+flagName).exists();
	}

	public void createFlagFile(String appCnl,String flagName){
		try {
			String localPath=getLocalPath(appCnl);
			FileUtils.write("flag", localPath+flagName);
		} catch (Exception e) {
			LemonException.throwBusinessException("PWM40703");
		}
	}

	/**
	 * 获取本地存放对账文件的目录，没有则创建
	 * @param appCnl
	 * @return
	 */
	public String getLocalPath(String appCnl){
		String localPath=LemonUtils.getProperty("pwm.chk.localPath")+appCnl+"/";
		File localPathFile=new File(localPath);
		if(localPathFile.exists()){
			if(localPathFile.isDirectory()){
				return localPath;
			}else{
				logger.error(localPath+"已经存在，但不是目录，任务退出");
				LemonException.throwBusinessException("CSH20038");
			}
		}
		boolean success=localPathFile.mkdirs();
		if(!success){
			logger.error(localPath+"目录创建失败，任务退出");
			LemonException.throwBusinessException("PWM40704");

		}
		return localPath;
	}


	private StringBuilder recToHLine(RechargeHCouponDO rec){
		StringBuilder lineBuilder=new StringBuilder();
		lineBuilder.append(rec.getOrderNo());
		lineBuilder.append("|");
		lineBuilder.append(rec.getOrderAmt());
		lineBuilder.append("|");
		lineBuilder.append(rec.getOrderStatus());
		lineBuilder.append("|");
		lineBuilder.append(rec.getAcTm());
		lineBuilder.append("\n");
		return lineBuilder;
	}

	private StringBuilder recToLine(RechargeOrderDO rec){
		StringBuilder lineBuilder=new StringBuilder();
		lineBuilder.append(rec.getOrderNo());
		lineBuilder.append("|");
		lineBuilder.append(rec.getOrderAmt());
		lineBuilder.append("|");
		lineBuilder.append(rec.getOrderStatus());
		lineBuilder.append("|");
		lineBuilder.append(rec.getAcTm());
		lineBuilder.append("\n");
		return lineBuilder;
	}

	private StringBuilder recToWithdrawLine(WithdrawOrderDO rec){
		StringBuilder lineBuilder=new StringBuilder();
		lineBuilder.append(rec.getOrderNo());
		lineBuilder.append("|");
		lineBuilder.append(rec.getWcApplyAmt());
		lineBuilder.append("|");
		lineBuilder.append(rec.getOrderStatus());
		lineBuilder.append("|");
        lineBuilder.append(rec.getRspOrderNo());
        lineBuilder.append("|");
		lineBuilder.append(rec.getAcTm());
		lineBuilder.append("\n");
		return lineBuilder;
	}
}
