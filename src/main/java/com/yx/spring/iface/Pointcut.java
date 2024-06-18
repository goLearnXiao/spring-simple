package com.yx.spring.iface;

/**
 * 切点，判断是否作用于切面
 * @author yangxiao
 * @date 2021/6/5 14:29
 */
public interface Pointcut {

    /**
     * Return the MethodMatcher for this pointcut.
     * @return the MethodMatcher (never {@code null})
     */
    MethodMatcher getMethodMatcher();

}
