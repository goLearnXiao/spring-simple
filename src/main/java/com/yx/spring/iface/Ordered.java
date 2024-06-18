package com.yx.spring.iface;

/**
 * @author yangxiao
 * @date 2021/5/30 15:36
 */
public interface Ordered {

    /**
     * high precedence
     */
    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    /**
     * low precedence
     */
    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;


    /**
     * Get the order value of this object
     */
    int getOrder();
}
