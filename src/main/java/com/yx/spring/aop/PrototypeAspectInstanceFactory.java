package com.yx.spring.aop;

import com.yx.spring.iface.AspectInstanceFactory;

/**
 * @author yangxiao
 * @date 2021/6/5 14:52
 */
public class PrototypeAspectInstanceFactory implements AspectInstanceFactory {

    private Class<?> clazz;

    public PrototypeAspectInstanceFactory(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public Object getAspectInstance() {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
