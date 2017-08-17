package com.hisun.lemon.pwm.service.chk;

import com.hisun.lemon.pwm.constants.PwmConstants;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Transactional
@Service
public class CshRechargeChkFileServiceImpl extends AbstractChkFileService {


    public CshRechargeChkFileServiceImpl() {
        super();
        this.chkOrderStatus=new String[]{
                PwmConstants.RECHARGE_ORD_S
        };
        appCnl="PWM";
        this.lockName="PWM_CSH_CHK_FILE_LOCK";
    }
}
