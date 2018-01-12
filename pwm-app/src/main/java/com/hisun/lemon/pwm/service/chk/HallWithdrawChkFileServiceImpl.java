package com.hisun.lemon.pwm.service.chk;

import com.hisun.lemon.common.exception.LemonException;
import com.hisun.lemon.common.utils.DateTimeUtils;
import com.hisun.lemon.common.utils.JudgeUtils;
import com.hisun.lemon.framework.utils.LemonUtils;
import com.hisun.lemon.jcommon.file.FileUtils;
import com.hisun.lemon.pwm.constants.PwmConstants;
import com.hisun.lemon.pwm.entity.RechargeOrderDO;
import com.hisun.lemon.pwm.entity.WithdrawOrderDO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 生成营业厅个人提现对账文件，上传至服务器
 */
@Transactional
@Service
public class HallWithdrawChkFileServiceImpl extends AbstractChkFileService {


    public HallWithdrawChkFileServiceImpl() {
        super();
        this.chkOrderStatus=new String[]{
                PwmConstants.WITHDRAW_ORD_S1
        };
        appCnl="HALL";
        this.lockName="PWM_HALL_CHK_FILE_LOCK";
    }

    @Transactional(readOnly = true)
    protected void execute() {
        //获取对账日期
        LocalDate chkDate=chkFileComponent.getChkDate();
       // LocalDate chkDate=DateTimeUtils.getCurrentLocalDate().minusDays(2);
        //对账文件名
        String  chkFileName=getChkFileName(appCnl,chkDate);
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
        List<WithdrawOrderDO> orders=chkFileComponent.queryHallWithdraw(chkDate,chkOrderStatus);

        //读取对账文件汇总信息项:
        Map<String,Object> headItemMap = new HashMap<>();
        BigDecimal totalFee = BigDecimal.valueOf(0);
        BigDecimal totalAmt = BigDecimal.valueOf(0);
        for(WithdrawOrderDO ro : orders) {
            BigDecimal tmpFee = ro.getFeeAmt();
            if(JudgeUtils.isNotNull(tmpFee)){
                totalFee = totalFee.add(tmpFee);
            }
            //申请提现金额，订单金额
            totalAmt = totalAmt.add(ro.getWcApplyAmt());
        }
        headItemMap.put("count",orders.size());
        headItemMap.put("totalAmt",totalAmt);
        headItemMap.put("totalFee",totalFee);

        //生成文件
        writeToFile(appCnl, orders, chkFileName,headItemMap);
        logger.info("生成对账文件"+flagName+"完成，开始上传至SFTP");

        //上传服务器
        chkFileComponent.upload(appCnl, chkFileName, flagName);
        logger.info("对账文件"+flagName+"上传至SFTP完成");
    }

    /**
     * 定义写入对账文件的内容
     * @param appCnl
     * @param datas
     * @param fileName
     */
    private void writeToFile(String appCnl, List<WithdrawOrderDO> datas, String fileName, Map<String, Object> headItemMap) {
        final String itemSeperator = "|";
        final String lineSeparator = System.getProperty("line.separator", "\n");
        StringBuilder contextBuilder = new StringBuilder();
        //添加对账文件首行总明细(总笔数|总金额|总服务费)
        contextBuilder.append(headItemMap.get("count")).append(itemSeperator)
                .append(headItemMap.get("totalAmt")).append(itemSeperator)
                .append(headItemMap.get("totalFee")).append(lineSeparator);

        //营业厅订单号|平台提现订单号|提现用户id|订单金额|手续费|订单状态|订单日期|
        for (WithdrawOrderDO rdo : datas) {
            contextBuilder.append(rdo.getRspOrderNo()).append(itemSeperator)
                    .append(rdo.getOrderNo()).append(itemSeperator)
                    .append(rdo.getUserId()).append(itemSeperator)
                    .append(rdo.getWcApplyAmt()).append(itemSeperator)
                    .append(rdo.getFeeAmt()).append(itemSeperator)
                    .append(rdo.getOrderStatus()).append(itemSeperator)
                    .append(rdo.getOrderSuccTm()).append(lineSeparator);
        }
        //写入文件
        try {
            String localPath = chkFileComponent.getLocalPath(appCnl);
            FileUtils.write(contextBuilder.toString(), localPath + fileName);
        } catch (Exception e) {
            LemonException.throwBusinessException("PWM40701");
        }

    }
    //自定义平台生成的营业厅对账的文件名
    private String getChkFileName(String appCnl,LocalDate chkDate){
        return LemonUtils.getApplicationName()+"_"+appCnl+"_WITHDRAW"+"_"+DateTimeUtils.formatLocalDate(chkDate,"yyyyMMdd")+".ck";
    }
}
