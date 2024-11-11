package test.shop.infrastructure.persistence.jpa.repository;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;
import test.shop.utils.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpecificationBuilderV2<T> {

    public Specification<T> buildSpecification(Map<String, Object> searchParams) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            searchParams.forEach((key, value) -> {
                if (value != null) {
                    Path<?> path = getNestedPath(root, key);
                    Predicate predicate = createPredicate(builder, path, value);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            });

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Path<?> getNestedPath(Path<?> path, String attributeName) {
        String[] attributeParts = attributeName.split("\\.");
        Path<?> currentPath = path;
        for (String part : attributeParts) {
            currentPath = currentPath.get(part);
        }
        return currentPath;
    }
    @SuppressWarnings("unchecked")
    private Predicate createPredicate(CriteriaBuilder builder, Path<?> path, Object value) {
        if (value instanceof String) {
            return builder.like(path.as(String.class), "%" + value + "%");
        } else if (value instanceof Number) {
           if(((Number)value).intValue() == 0) {
               return null;// Return null for rating 0
           }
            return builder.equal(path, value);
        } else if (value instanceof Boolean) {
            return builder.equal(path, value);
        } else if (value instanceof Enum<?>) {
            return builder.equal(path, value);
        } else if (value instanceof Range<?>) {
            Range<?> range = (Range<?>) value; //fixed to Number
            if(path.getJavaType().equals(Double.class)){
                //handle ratings
                Double lowerBound = (Double) range.getLowerBound();
                if(lowerBound == 0.0){
                    return null; //show all ratings
                }
                return builder.greaterThanOrEqualTo(path.as(Double.class), lowerBound);
            }
            else if(path.getJavaType().equals(Number.class)){
                Integer lowerBound = ((Number) range.getLowerBound()).intValue();
                Integer upperBound = ((Number) range.getUpperBound()).intValue();

                if(lowerBound == 0 & upperBound == 0){
                    return null;
                }
                if(upperBound >= 999999){
                    return builder.greaterThanOrEqualTo(
                            path.as(Integer.class),
                            lowerBound
                    );

                }

                return builder.between(
                        path.as(Integer.class),
                        lowerBound,
                        upperBound
                );

            }

                return builder.between((Path<Comparable>) path, (Comparable) range.getLowerBound(), (Comparable) range.getUpperBound());


        }
        return null;
    }
}
