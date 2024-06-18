package com.yx.spring.iface;

/**
 * @author yangxiao
 * @date 2021/5/30 11:22
 */
public interface SmartInstantiationAwareBeanPostProcessor extends BeanPostProcessor {

    default Object getEarlyBeanReference(Object bean, String beanName) throws RuntimeException {
        return bean;
    }
}
