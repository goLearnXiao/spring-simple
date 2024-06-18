package com.yx.spring.aop;

import com.yx.spring.annotation.After;
import com.yx.spring.annotation.Aspect;
import com.yx.spring.annotation.Before;
import com.yx.spring.iface.Advisor;
import com.yx.spring.iface.AspectJAdvisorFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yangxiao
 * @date 2021/6/5 14:54
 */
public class DefaultAspectJAdvisorFactory implements AspectJAdvisorFactory {
    @Override
    public boolean isAspect(Class<?> clazz) {
        return clazz.isAnnotationPresent(Aspect.class);
    }

    @Override
    public List<Advisor> getAdvisors(Class<?> clazz) {
        PrototypeAspectInstanceFactory aspectInstanceFactory = new PrototypeAspectInstanceFactory(clazz);
        List<Advisor> advisors = new ArrayList<>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method : declaredMethods){
            // 这里直接解析注解，不再使用适配器
            if (method.isAnnotationPresent(Before.class)) {
                // pointcut
                String expression = method.getAnnotation(Before.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                BeforeAdvice advice = new BeforeAdvice(method, aspectInstanceFactory);
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisors.add(advisor);
            } else if (method.isAnnotationPresent(After.class)) {
                String expression = method.getAnnotation(After.class).value();
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(expression);
                AfterAdvice advice = new AfterAdvice(method, aspectInstanceFactory);
                Advisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisors.add(advisor);
            }

        }
        return advisors;
    }
}
