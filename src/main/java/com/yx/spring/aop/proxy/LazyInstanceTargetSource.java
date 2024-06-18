package com.yx.spring.aop.proxy;

import com.yx.spring.YxApplicationContext;
import com.yx.spring.iface.TargetSource;

/**
 * @author yangxiao
 * @date 2021/6/5 9:36
 */
public class LazyInstanceTargetSource implements TargetSource {

    private final YxApplicationContext applicationContext;

    private final String beanName;

    public LazyInstanceTargetSource(YxApplicationContext applicationContext, String beanName) {
        this.applicationContext = applicationContext;
        this.beanName = beanName;
    }

    @Override
    public Object getTarget() throws Exception {
        return applicationContext.getBean(beanName);
    }
}
