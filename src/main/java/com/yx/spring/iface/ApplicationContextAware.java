package com.yx.spring.iface;

import com.yx.spring.YxApplicationContext;

/**
 * @author yangxiao
 * @date 2021/5/30 11:24
 */
public interface ApplicationContextAware {

    void setApplicationContext(YxApplicationContext applicationContext);

}
