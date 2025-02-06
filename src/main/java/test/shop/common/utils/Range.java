package test.shop.common.utils;

public class Range<T extends Comparable<T>> {

    private final T lowerBound;

    private final T upperBound;


    public Range(T lower, T upper) {
        this.lowerBound = lower;
        this.upperBound = upper;
    }


    public T getLowerBound() {
        return lowerBound;
    }

    public T getUpperBound() {
        return upperBound;
    }
}
