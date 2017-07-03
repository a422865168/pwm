package com.hisun.lemon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;

/**
 * spring boot 启动
 * @author yuzhou
 * @date 2017年6月6日
 * @time 上午9:47:21
 *
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class Application {
    
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
