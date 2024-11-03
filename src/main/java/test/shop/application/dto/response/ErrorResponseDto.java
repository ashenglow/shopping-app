package test.shop.application.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponseDto {
    private int code;
    private String message;
private LocalDateTime timestamp;
    public ErrorResponseDto(int code, String message, LocalDateTime timestamp) {
        this.code = code;
        this.message = message;
        this.timestamp = timestamp;
    }

}
