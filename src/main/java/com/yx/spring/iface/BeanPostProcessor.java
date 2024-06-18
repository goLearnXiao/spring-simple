package com.yx.spring.iface;

/**
 *
 * @author yangxiao
 * @date 2021/5/30 16:53
 */
public interface BeanPostProcessor {

    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }

}
