package com.hisun.lemon.pwm.service.chk;

import com.hisun.lemon.framework.data.BaseDO;
import com.hisun.lemon.framework.lock.DistributedLocker;
import com.hisun.lemon.framework.service.BaseService;
import com.hisun.lemon.pwm.component.ChkFileComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;


/**
 * @author tone
 * @date 2017年8月5日
 * @time 下午2:54:28
 *
 */
public abstract class AbstractChkFileService extends BaseService implements Callable {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractChkFileService.class);

    @Resource
    private DistributedLocker locker;

    @Resource
    protected ChkFileComponent chkFileComponent;

    protected String lockName;

    protected String[] chkOrderStatus;

    protected String appCnl;

    protected String appCnl_F1;

    public AbstractChkFileService() {

    }


    @Transactional(readOnly = true)
    protected void execute() {
        //获取对账日期
        LocalDate chkDate=chkFileComponent.getChkDate();
        //对账文件名
        String  chkFileName=chkFileComponent.getChkFileName(appCnl,chkDate);
        //标志文件名
        String flagName=chkFileName+".flag";
        if(chkFileComponent.isStart(appCnl,flagName)){
            logger.info("对账文件标志文件" +flagName+"已经存在,不重复生成对账文件");
            return;
        }
        logger.info("开始生成对账文件：" +flagName);
        //生成标志文件
        chkFileComponent.createFlagFile(appCnl,flagName);
        List<BaseDO> orders=chkFileComponent.queryDatas(chkDate,chkOrderStatus);
        //生成文件
        chkFileComponent.writeToFile(appCnl,orders,chkFileName);

        logger.info("生成对账文件"+flagName+"完成，开始上传至SFTP");


        //上传服务器
        chkFileComponent.upload(appCnl,chkFileName,flagName);
        logger.info("对账文件"+flagName+"上传至SFTP完成");
    }

    @Override
    public Object call() throws Exception {
        logger.info("唤起生成"+appCnl+"数据对账文件任务");
        locker.lock(lockName, 18, 22, () -> {
            execute();
            return null;
        });
        return null;
    }

}
