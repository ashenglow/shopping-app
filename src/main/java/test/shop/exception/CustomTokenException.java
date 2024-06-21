package test.shop.exception;

public class CustomTokenException extends RuntimeException {
    public CustomTokenException() {
        super();
    }

    public CustomTokenException(String message) {
        super(message);
    }

    public CustomTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomTokenException(Throwable cause) {
        super(cause);
    }
}
