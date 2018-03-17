package com.hisun.lemon.pwm.service.chk;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;


@Transactional
@Service
public class CshRechargeChkFileServiceImpl extends AbstractChkFileService {


    public CshRechargeChkFileServiceImpl() {
        super();
        this.chkOrderStatus=new String[]{
                PwmConstants.RECHARGE_ORD_S
        };
        appCnl="CSH";
        this.lockName="PWM_CSH_CHK_FILE_LOCK";
    }
    
    @Transactional(readOnly = true)
    protected void execute() {
        //获取对账日期
        LocalDate chkDate=chkFileComponent.getChkDate();
        //对账文件名
        String chkFileName=chkFileComponent.getChkFileName(appCnl,chkDate);
        //标志文件名
        String flagName=chkFileName+".flag";
        if(chkFileComponent.isStart(appCnl,flagName)){
            logger.info("对账文件标志文件" +flagName+"已经存在,不重复生成对账文件");
            return;
        }
        logger.info("开始生成对账文件：" +flagName);
        //生成标志文件
        chkFileComponent.createFlagFile(appCnl,flagName);
        //读取数据
        List<RechargeOrderDO> orders=chkFileComponent.queryRecharge(chkDate,chkOrderStatus);

        //生成文件
        chkFileComponent.writeToFile(appCnl,orders,chkFileName);
        logger.info("生成对账文件"+flagName+"完成，开始上传至SFTP");

        //上传服务器
        chkFileComponent.upload(appCnl, chkFileName, flagName);
        logger.info("对账文件"+flagName+"上传至SFTP完成");

      
    }
}
