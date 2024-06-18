package com.yx.test;

import com.yx.spring.YxApplicationContext;

/**
 * @author yangxiao
 * @date 2021/5/30 10:13
 */
public class MyTest {

    public static void main(String[] args) {
        YxApplicationContext applicationContext = new YxApplicationContext(AppConfig.class);
        UserService userService = (UserService) applicationContext.getBean("userService");
        userService.printName("testName");

    }
}
