package cn.lbcmmszdntnt.handler;

import cn.lbcmmszdntnt.common.SystemJsonResponse;
import cn.lbcmmszdntnt.common.enums.GlobalServiceStatusCode;
import cn.lbcmmszdntnt.exception.GlobalServiceException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static SystemJsonResponse<?> getGlobalServiceExceptionResult(GlobalServiceException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String message = e.getMessage();
        GlobalServiceStatusCode statusCode = e.getStatusCode();
        log.error("请求地址'{}', {}: {}", requestURI, statusCode, message);
        return SystemJsonResponse.CUSTOMIZE_MSG_ERROR(statusCode, message);
    }

    @ExceptionHandler(GlobalServiceException.class)
    public SystemJsonResponse<?> handleGlobalServiceException(GlobalServiceException e, HttpServletRequest request) {
        return getGlobalServiceExceptionResult(e, request);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public SystemJsonResponse<?> constraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        log.error("数据校验出现问题，异常类型:{}", e.getMessage());
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
        return getGlobalServiceExceptionResult(
                new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE),
                request
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public SystemJsonResponse<?> ValidationHandler(MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("数据校验出现问题，异常类型:{}", e.getMessage());
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .collect(Collectors.joining("\n"));
        return getGlobalServiceExceptionResult(
                new GlobalServiceException(message, GlobalServiceStatusCode.PARAM_FAILED_VALIDATE),
                request
        );
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public SystemJsonResponse<?> handleExpiredJwtException(ExpiredJwtException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String message = e.getMessage();
        log.error("请求地址'{}' {}", requestURI, message);
        e.printStackTrace();
        return SystemJsonResponse.CUSTOMIZE_MSG_ERROR(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID, GlobalServiceStatusCode.USER_TOKEN_NOT_VALID.getMessage());
    }

    @ExceptionHandler(SignatureException.class)
    public SystemJsonResponse<?> handleSignatureException(SignatureException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String message = e.getMessage();
        log.error("请求地址'{}' {}", requestURI, message);
        e.printStackTrace();
        return SystemJsonResponse.CUSTOMIZE_MSG_ERROR(GlobalServiceStatusCode.USER_TOKEN_NOT_VALID, GlobalServiceStatusCode.USER_TOKEN_NOT_VALID.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public SystemJsonResponse<?> handleException(Exception e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String message = e.getMessage();
        log.error("请求地址'{}' {}", requestURI, message);
        e.printStackTrace();
        return SystemJsonResponse.CUSTOMIZE_MSG_ERROR(GlobalServiceStatusCode.SYSTEM_SERVICE_FAIL, message);
    }

}
