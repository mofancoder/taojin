package com.tj.util.A;

import com.tj.dto.CallbackReturnDto;
import com.tj.util.Results;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Results.Result<String> handler(MethodArgumentNotValidException e) {
        log.error("exception:", e);
        return new Results.Result<>(Results.Result.PARAMETER_INCORRENT, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Results.Result<String> handler(ConstraintViolationException e) {
        log.error("exception:", e);
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation constraintViolation : constraintViolations) {
            sb.append(constraintViolation.getMessage());
        }
        return new Results.Result<>(Results.Result.PARAMETER_INCORRENT, sb.toString(), null);
    }


    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Results.Result<String> handler(MissingServletRequestParameterException e) {
        log.error("exception:", e);
        return new Results.Result<>(Results.PARAMETER_INCORRENT, e.getMessage());
    }

    @ExceptionHandler(value = ShiroException.class)
    @ResponseBody
    public Results.Result<String> handler(ShiroException e) {
        return new Results.Result<>(Results.NO_AUTH, null);
    }

    @ExceptionHandler(value = AuthenticationException.class)
    @ResponseBody
    public Results.Result<String> handler(AuthenticationException e) {
        return new Results.Result<>(Results.NO_AUTH, null);
    }

    @ExceptionHandler(value = AuthorizationException.class)
    @ResponseBody
    public Results.Result<String> handler(AuthorizationException e) {
        return new Results.Result<>(Results.NO_AUTHOR, null);
    }


    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(SacException.class)
    @ResponseBody
    public Results.Result<String> handler(SacException e) {
        log.error("exception:", e);
        return new Results.Result<>(Results.Result.SYSTEM_BUSY, e.getLocalizedMessage(), null);
    }

    /**
     * 处理所有接口数据验证异常
     */
    @ExceptionHandler(FBDException.class)
    @ResponseBody
    public CallbackReturnDto handler(FBDException e) {
        log.error("exception:", e);
        return CallbackReturnDto.builder().code(Results.Result.TX_FAIL).msg(e.getLocalizedMessage()).build();
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object handler(Exception e, HttpServletRequest request) {
        log.error("exception:", e);
        Results.Result<String> response;
        ResponseStatus annotation = e.getClass().getAnnotation(ResponseStatus.class);
        if (annotation != null) {
            response = new Results.Result<>(annotation.code().value(), e.getMessage(), null);
        } else {
            if (request.getRequestURI().startsWith("/transaction/open/callback")) {
                return CallbackReturnDto.builder().code(Results.Result.TX_FAIL).msg(null).build();
            }
            response = new Results.Result<>(Results.SYSTEM_BUSY, null);
        }
        return response;
    }


}
