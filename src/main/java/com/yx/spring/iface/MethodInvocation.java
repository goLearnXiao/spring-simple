package com.yx.spring.iface;

import java.lang.reflect.Method;

public interface MethodInvocation extends Joinpoint {

    Object[] getArguments();

    void setArguments(Object[] args);

    Method getMethod();

}
