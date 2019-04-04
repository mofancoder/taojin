package com.tj.util.log;

import com.tj.util.aspect.CommonLogAspect;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@Getter
public class Rlog {
    private CommonLogAspect aspect;

    public Rlog(CommonLogAspect aspect) {
        this.aspect = aspect;
    }

    public void debug(String message) {
        if (aspect.getRLog().isDebugEnabled()) {
            aspect.getRLog().debug(builder(message));
        }
    }

    public void debug(String message, Object... params) {
        if (aspect.getRLog().isDebugEnabled()) {
            aspect.getRLog().debug(builder(message), params);
        }
    }

    public void info(String message) {
        if (aspect.getRLog().isInfoEnabled()) {
            aspect.getRLog().info(builder(message));
        }
    }

    public void info(String message, Object... params) {
        if (aspect.getRLog().isInfoEnabled()) {
            aspect.getRLog().info(builder(message), params);
        }
    }

    ;

    public void warn(String message) {
        if (aspect.getRLog().isWarnEnabled()) {
            aspect.getRLog().warn(builder(message));
        }
    }

    ;

    public void warn(String message, Object... params) {
        if (aspect.getRLog().isWarnEnabled()) {
            aspect.getRLog().warn(builder(message), params);
        }
    }

    public void error(String message) {
        if (aspect.getRLog().isErrorEnabled()) {
            aspect.getRLog().error(builder(message));
        }
    }

    ;

    public void error(String message, Object... params) {
        if (aspect.getRLog().isErrorEnabled()) {
            aspect.getRLog().error(builder(message), params);
        }
    }

    public void error(String message, Throwable throwable) {
        if (aspect.getRLog().isErrorEnabled()) {
            aspect.getRLog().error(builder(message), throwable);
        }
    }

    public void append(String prefix, String message) {
        Map<String, Object> map = aspect.getThreadLocal().get();
        StringBuilder msg = (StringBuilder) map.getOrDefault("message", new StringBuilder());
        msg.append("|").append(prefix).append(":").append(message);
        aspect.getThreadLocal().get().put("message", msg);
    }

    ;

    public void sqlLog(String message) {
        if (aspect.getRLog().isInfoEnabled()) {
            aspect.getSqlLog().info(message);
        }
    }

    private String builder(String message) {
        ThreadLocal<Map<String, Object>> threadLocal = aspect.getThreadLocal();
        Map<String, Object> map = threadLocal.get();
        Object msg = map.getOrDefault("message", new StringBuilder());
        if (msg != null) {
            StringBuilder older = (StringBuilder) msg;
            message = older.toString() + "|" + message;
        }
        return message;
    }
}
