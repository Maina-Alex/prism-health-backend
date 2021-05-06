package com.prismhealth.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class PrismHealthExceptionHandler extends ResponseEntityExceptionHandler {

        @ExceptionHandler({ PrismHealthExceptions.UserNotFoundException.class })
        protected ResponseEntity<Object> handleNotFound(
                Exception ex, WebRequest request) {
            return handleExceptionInternal(ex, "User not found",
                    new HttpHeaders(), HttpStatus.NOT_FOUND, request);
        }

        @ExceptionHandler({ PrismHealthExceptions.SignUpFailedException.class,DataIntegrityViolationException.class})
        public ResponseEntity<Object> handleSignUpFailedRequest(
                Exception ex, WebRequest request) {
            return handleExceptionInternal(ex,"SignUp Failed",
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        }

}
