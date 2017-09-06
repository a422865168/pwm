package com.hisun.lemon.pwm.schedule;

import com.hisun.lemon.pwm.service.chk.AbstractChkFileService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 定时任务调度器
 */
@Component
public class Scheduler {
	ExecutorService executorService = Executors.newFixedThreadPool(2);

	@Resource
	private List<AbstractChkFileService> scheduleService;

//    @Scheduled(cron="0 *  * * * *")
	public void createChkFile(){
		for(AbstractChkFileService item:scheduleService){
			executorService.submit(item);
		}
	}
}
