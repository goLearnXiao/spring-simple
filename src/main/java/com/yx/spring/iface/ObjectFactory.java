package com.yx.spring.iface;

/**
 * @author yangxiao
 * @date 2021/5/30 16:55
 */
@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws RuntimeException;
}
