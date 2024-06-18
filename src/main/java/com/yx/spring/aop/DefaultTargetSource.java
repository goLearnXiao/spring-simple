package com.yx.spring.aop;

import com.yx.spring.iface.TargetSource;

/**
 * @author yangxiao
 * @date 2021/6/5 16:45
 */
public class DefaultTargetSource implements TargetSource {
    private final Object target;

    public DefaultTargetSource(Object target) {
        this.target = target;
    }

    @Override
    public Object getTarget() throws Exception {
        return this.target;
    }

}
