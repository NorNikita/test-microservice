package ru.pflb.boomq.test.controller.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ru.pflb.boomq.model.test.ExceptionMessage;
import ru.pflb.boomq.model.test.TestServiceExceptionDto;
import ru.pflb.boomq.model.test.exception.TestServiceException;

@ControllerAdvice(basePackages = {"ru.pflb.boomq.test.controller"})
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TestServiceException.class)
    public ResponseEntity<TestServiceExceptionDto> testNotFound(TestServiceException exception) {
        return ResponseEntity
                .status(exception.getExceptionMessage().getStatus())
                .body(TestServiceExceptionDto
                        .builder()
                        .exceptionMessage(exception.getExceptionMessage())
                        .description(exception.getMessage())
                        .build());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .reduce((u,v) -> u + " " + v).get();

        return ResponseEntity
                .status(status)
                .body(TestServiceExceptionDto
                        .builder()
                        .exceptionMessage(ExceptionMessage.EMPTY_FIELDS)
                        .description(errors)
                        .build());
    }
}
