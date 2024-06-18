package com.yx.spring.iface;

import java.util.List;

/**
 * 解析 @Aspect
 * @author yangxiao
 * @date 2021/6/5 14:44
 */
public interface AspectJAdvisorFactory {

    /**
     * 是否是切面
     * @param clazz
     * @return
     */
    boolean isAspect(Class<?> clazz);

    /**
     * 获取所有切面
     * @param clazz
     * @return
     */
    List<Advisor> getAdvisors(Class<?> clazz);
}
