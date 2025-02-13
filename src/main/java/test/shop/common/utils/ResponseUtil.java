package test.shop.common.utils;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.TimeUnit;

public class ResponseUtil {
    public static <T> ResponseEntity.BodyBuilder getCacheableResponse(T data) {
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(300, TimeUnit.SECONDS))
                .eTag(String.valueOf(data.hashCode()))
                .header("Vary", "Origin, Accept-Encoding");
    }
}
