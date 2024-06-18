package com.yx.spring.iface;

/**
 * @author yangxiao
 * @date 2021/6/5 14:52
 */
public interface AspectInstanceFactory {

    /**
     * Create an instance of this factory's aspect.
     * @return the aspect instance (never {@code null})
     */
    Object getAspectInstance();

}
