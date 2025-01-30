package test.shop.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import test.shop.domain.exception.InvalidAddressException;
import test.shop.exception.web.CustomTokenException;
import test.shop.exception.web.CustomRefreshTokenFailException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(InvalidAddressException.class)
    public ResponseEntity<ErrorResult> handleInvalidAddressException(InvalidAddressException e) {
        log.error("[handleInvalidAddressException] ex ", e);
        ErrorResult errorResult = new ErrorResult("UNPROCESSABLE_ENTITY", "ADDRESS_REQUIRED");
                return new ResponseEntity<>(errorResult, HttpStatus.UNPROCESSABLE_ENTITY);
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
