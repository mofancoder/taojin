package com.tj.zuul.handler;

import com.tj.dto.CallbackReturnDto;
import com.tj.util.A.FBDException;
import com.tj.util.A.SacException;
import com.tj.util.Results;
import com.tj.util.log.Rlog;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequestMapping("/error")
public class MyExceptionHandlerResolver implements ErrorController {

    private final ErrorAttributes errorAttributes;
    @Autowired
    private Rlog rlog;
    @Autowired
    public MyExceptionHandlerResolver(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

    @RequestMapping
    public Object error(HttpServletRequest request, HttpServletResponse response) {

        ServletWebRequest servletWebRequest = new ServletWebRequest(request);

        Map<String, Object> result = this.errorAttributes.getErrorAttributes(servletWebRequest, false);
        Throwable error = this.errorAttributes.getError(servletWebRequest);
        System.out.println("-------");
        rlog.error("error", error);
        ResponseStatus annotation = AnnotationUtils.getAnnotation(error.getClass(), ResponseStatus.class);
        HttpStatus httpStatus = annotation != null ? annotation.value() : HttpStatus.INTERNAL_SERVER_ERROR;
        Results.Result<Void> objectResult = new Results.Result<>(Results.SYSTEM_BUSY, null);

        if (error.getCause() instanceof SacException || error.getCause() instanceof AuthorizationException || error.getCause() instanceof AuthenticationException) {
            objectResult = new Results.Result<>(Results.Result.SYSTEM_BUSY, error.getCause().getLocalizedMessage(), null);
        }
        if (error.getCause() instanceof FBDException) {
            return CallbackReturnDto.builder().code(Results.Result.TX_FAIL).msg(error.getLocalizedMessage()).build();
        }
        return objectResult;

    }
}
