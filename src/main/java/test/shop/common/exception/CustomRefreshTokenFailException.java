package test.shop.common.exception;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class CustomRefreshTokenFailException extends RuntimeException {

    public CustomRefreshTokenFailException(String message) {
        super(message);
    }

    public CustomRefreshTokenFailException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomRefreshTokenFailException(Throwable cause) {
        super(cause);
    }

    protected CustomRefreshTokenFailException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
