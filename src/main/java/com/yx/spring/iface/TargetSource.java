package com.yx.spring.iface;

/**
 * @author yangxiao
 * @date 2021/6/5 9:30
 */
@FunctionalInterface
public interface TargetSource {

    /**
     * 获取目标对象
     * @return
     * @throws Exception
     */
    Object getTarget() throws Exception;
}
