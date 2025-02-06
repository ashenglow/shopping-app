package test.shop.infrastructure.persistence.jpa.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import test.shop.common.utils.Range;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class SpecificationBuilder<T> {

    private final Map<Class<?>, TriFunction<Path<?>, CriteriaBuilder, Object, Predicate>> handlers = new HashMap<>();

    public SpecificationBuilder() {
        handlers.put(String.class, this::handleString);
        handlers.put(Number.class, this::handleNumber);
        handlers.put(Boolean.class, this::handleBoolean);
        handlers.put(Enum.class, this::handleEnum);
        handlers.put(Range.class, this::handleRange);
    }
    public Specification<T> buildSpecification(Map<String, Object> searchParams) {
        return (Root<T> root, jakarta.persistence.criteria.CriteriaQuery<?> query, jakarta.persistence.criteria.CriteriaBuilder builder) -> {
            Predicate predicate = builder.conjunction();
            for (Map.Entry<String, Object> entry : searchParams.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value != null) {
                    Path<?> path = getNestedPath(root, key);
                    TriFunction<Path<?>, CriteriaBuilder,Object, Predicate> handler = getHandler(value);
                    if (handler != null) {
                       predicate =  builder.and(predicate, handler.apply(path, builder, value));
                    }
                }
            }
            return predicate;


        };


    }

    private TriFunction<Path<?>, CriteriaBuilder, Object, Predicate> getHandler(Object value) {
        if (value instanceof String) {
            return handlers.get(String.class);
        } else if(value instanceof Number) {
            return handlers.get(Number.class);
        }else if(value instanceof Boolean) {
            return handlers.get(Boolean.class);
        }else if(value instanceof Enum<?>) {
            return handlers.get(Enum.class);
        }else if(value instanceof Range<?>) {
            return handlers.get(Range.class);
        }
            return null;

    }

    private Predicate handleString(Path<?> path, CriteriaBuilder builder, Object value) {
        return builder.like(path.as(String.class), "%" + value + "%");
    }

    private Predicate handleNumber(Path<?> path, CriteriaBuilder builder, Object value) {
        if (value == null || value.toString().isEmpty() || value.equals(0)) {
            return builder.and(builder.isTrue(builder.literal(true)));
        }
        return builder.equal(path, value);
    }

    private Predicate handleEnum(Path<?> path, CriteriaBuilder builder, Object value) {
        if(value == null) {
            return builder.and(builder.isTrue(builder.literal(true)));
        }
        return builder.equal(path, value);
    }

    private Predicate handleBoolean(Path<?> path, CriteriaBuilder builder, Object value) {
        return builder.equal(path, value);
    }

    @SuppressWarnings("unchecked")
    private Predicate handleRange(Path<?> path, //fixed to Number
                              CriteriaBuilder builder,
                              Object value) {
    Range range = (Range) value; //fixed to Number
        Predicate result = builder.between((Path<Comparable>) path, range.getLowerBound(), range.getUpperBound());
        log.info("lowerBound: {}, upperBound: {}", range.getLowerBound(), range.getUpperBound());
        return result;

    }
    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }
    private Path<?> getNestedPath(Path<?> path, String attributeName) {
    String[] attributeParts = attributeName.split("\\.");
    Path<?> currentPath = path;
    for (String part : attributeParts) {
        currentPath = currentPath.get(part);
    }
    return currentPath;
}
}
