package test.shop.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import test.shop.exception.web.CustomRefreshTokenFailException;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResult> illegalExHandle(IllegalArgumentException e) {
        log.error("[exceptionHandle] ex ", e);
        ErrorResult errorResult = new ErrorResult("BAD_REQUEST", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomTokenException.class)
    public ResponseEntity<ErrorResult> httpExHandle(CustomTokenException e) {
        log.error("[httpExHandle] ex ", e);
        ErrorResult errorResult = new ErrorResult("UNAUTHORIZED", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(CustomRefreshTokenFailException.class)
    public ResponseEntity<ErrorResult> refreshTokenExHandle(CustomRefreshTokenFailException e) {
        log.error("[refreshTokenExHandle] ex ", e);
        ErrorResult errorResult = new ErrorResult("FORBIDDEN", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.FORBIDDEN);
    }



    @ExceptionHandler(ChangeSetPersister.NotFoundException.class)
    public ResponseEntity<ErrorResult> handleNotFoundException(ChangeSetPersister.NotFoundException e) {
        ErrorResult errorResult = new ErrorResult("NOT_FOUND", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.NOT_FOUND);
    }

    //return new ErrorResult("BAD_REQUEST", e.getMessage()); 했더니
    //exception인데도 200 ok response였음..
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResult> exHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);
        ErrorResult errorResult = new ErrorResult("EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
