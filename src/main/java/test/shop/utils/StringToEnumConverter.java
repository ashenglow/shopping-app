package test.shop.utils;

import org.springframework.core.convert.converter.Converter;
import test.shop.domain.item.Category;

public class StringToEnumConverter implements Converter<String, Category> {
    @Override
    public Category convert(String source) {
        return Category.valueOf(source.toUpperCase());
    }
}
