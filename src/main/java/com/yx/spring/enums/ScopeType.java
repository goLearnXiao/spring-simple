package com.yx.spring.enums;

/**
 * @author yangxiao
 * @date 2021/5/30 11:04
 */
public enum ScopeType {
    SINGLETON("singleton"),
    PROTOTYPE("prototype");

    private String value;

    ScopeType(String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }

}
