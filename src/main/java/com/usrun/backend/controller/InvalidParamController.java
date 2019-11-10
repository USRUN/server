package com.usrun.backend.controller;

import com.usrun.backend.config.ErrorCode;
import com.usrun.backend.payload.CodeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
public class InvalidParamController extends ResponseEntityExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<?> handleConstraintViolation(
            ConstraintViolationException ex,
            WebRequest request) {
        List<String> details = ex.getConstraintViolations()
                .parallelStream()
                .map(e -> e.getMessage())
                .collect(Collectors.toList());
        return new ResponseEntity<>(new CodeResponse(ErrorCode.PARAM_INVALID, details), HttpStatus.BAD_REQUEST);
    }
}
