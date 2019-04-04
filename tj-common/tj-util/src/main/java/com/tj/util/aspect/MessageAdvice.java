package com.tj.util.aspect;

import com.tj.util.Results;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

import java.util.Locale;

/**
 * @program: parent
 * @description: ${description}
 * @author: songliang
 * @create: 2018-09-28 20:03
 **/
@ControllerAdvice
public class MessageAdvice extends AbstractMappingJacksonResponseBodyAdvice {
    @Autowired
    private MessageSource messageSource;

    @Override
    protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType, MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {
        Object value = bodyContainer.getValue();
        if (value instanceof Results.Result) {
            Results.Result result = (Results.Result) value;
            Integer code = result.getCode();
            String msg = result.getMsg();
            Locale locale = LocaleContextHolder.getLocale();
            String message = messageSource.getMessage(msg, null, msg, locale);
            result.setMsg(message);
        }
    }
}
