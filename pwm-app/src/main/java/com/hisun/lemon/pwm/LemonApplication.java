package com.hisun.lemon.pwm;

import com.hisun.lemon.common.LemonFramework;
import com.hisun.lemon.framework.LemonBootApplication;

/**
 * @author yuzhou
 * @date 2017&#x5e74;11&#x6708;17&#x65e5;
 * @time &#x4e0a;&#x5348;10:56:44
 *
 */
//@SpringBootApplication
@LemonBootApplication({"com.hisun.lemon"})
public class LemonApplication {

    public static void main(String[] args) {
//        SpringApplication.run(LemonApplication.class, args);
        LemonFramework.run(LemonApplication.class, args);
    }
}
