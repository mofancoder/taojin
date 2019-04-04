package com.tj.util.A;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;

/**
 * Created by ldh on 2018-02-02.
 */
@Service
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpringContextAware implements ApplicationContextAware {
    private static ApplicationContext context = null;

    public static ApplicationContext getContext() {
        if (null == context) {
            context = ContextLoader.getCurrentWebApplicationContext();
        }
        return context;
    }

    public static void setContext(ApplicationContext context) {
        SpringContextAware.context = context;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (context == null) {
            context = applicationContext;
        }

    }

    public static <T> T getBean(Class<T> clazz) {
        return getContext().getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return getContext().getBean(name, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        ApplicationContext c = getContext();
        return (T) c.getBean(name);
    }

    public static boolean containsBean(String name) {
        ApplicationContext c = getContext();
        if (c.containsBean(name)) {
            return true;
        } else {
            return false;
        }
    }
}
