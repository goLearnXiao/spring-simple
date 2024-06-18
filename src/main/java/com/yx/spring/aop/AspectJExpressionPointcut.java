package com.yx.spring.aop;

import com.yx.spring.iface.MethodMatcher;
import com.yx.spring.iface.Pointcut;

import java.lang.reflect.Method;

/**
 * 同时是Pointcut和MethodMatcher
 */
public class AspectJExpressionPointcut implements Pointcut, MethodMatcher {

    private String expression;

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        if (expression.startsWith("execution")) {
            return executionMatches(method, targetClass);
        } else if (expression.startsWith("@annotation")) {
            return annotationMatches(method, targetClass);
        } else {
            System.out.println("默认返回 true");
            return true;
        }
    }

    private boolean annotationMatches(Method method, Class<?> targetClass) {
        return true;
    }

    private boolean executionMatches(Method method, Class<?> targetClass) {
        String simpleName = targetClass.getSimpleName();
        return expression.contains(simpleName);
    }
}
