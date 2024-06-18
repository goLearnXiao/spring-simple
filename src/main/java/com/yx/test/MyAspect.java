package com.yx.test;

import com.yx.spring.annotation.After;
import com.yx.spring.annotation.Aspect;
import com.yx.spring.annotation.Before;
import com.yx.spring.annotation.Component;

/**
 * @author yangxiao
 * @date 2021/6/5 17:08
 */
@Aspect
@Component
public class MyAspect {

    @Before("execution(UserServiceImpl.*())")
    public void testBeforeMethod() {
        System.out.println("testMethod before 通知....");
    }

    @After("execution(UserServiceImpl.*())")
    public void testAfterMethod() {
        System.out.println("testMethod after 通知....");
    }
}
