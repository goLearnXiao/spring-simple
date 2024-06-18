package com.yx.spring.core;

import com.yx.spring.enums.ScopeType;

/**
 * bean定义
 * @author yangxiao
 * @date 2021/5/30 15:33
 */
public class BeanDefinition {

    private Class type;
    private String scope;

    public boolean isSingleton() {
        return ScopeType.SINGLETON.getValue().equals(scope);
    }

    public Class getType() {
        return type;
    }

    public void setType(Class type) {
        this.type = type;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "BeanDefinition{" +
                "type=" + type +
                ", scope='" + scope + '\'' +
                '}';
    }
}
