package com.hisun.lemon.pwm.schedule;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.hisun.lemon.framework.schedule.batch.BatchScheduled;
import com.hisun.lemon.pwm.service.chk.AbstractChkFileService;

/**
 * 定时任务调度器
 */
@Component
public class Scheduler {
	ExecutorService executorService = Executors.newFixedThreadPool(2);

	@Resource
	private List<AbstractChkFileService> scheduleService;


	//@BatchScheduled(cron="0 0/1 * * * ?")
	@BatchScheduled(cron="0 0/30 9-12 * * ?")
	public void createChkFile(){
		System.out.println("生成对账文件");
		for(AbstractChkFileService item:scheduleService){
			executorService.submit(item);
		}
	}
}
