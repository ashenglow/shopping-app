package test.shop.domain.exception;

public class InvalidAddressException extends RuntimeException{
    public InvalidAddressException(String message) {
        super(message);
    }
}
