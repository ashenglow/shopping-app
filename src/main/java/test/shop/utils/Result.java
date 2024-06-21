package test.shop.utils;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
public class Result<T> {

    private T data;
    private int count;

    public Result(T data) {
        this.data = data;
    }

    public Result(T data, int count) {
        this.data = data;
        this.count = count;
    }
}
