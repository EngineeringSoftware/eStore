package org.estore.planner.util;

public class PathRange {
    int low;
    int high;

    public PathRange(int low) {
        this(low, Integer.MAX_VALUE);
    }

    public PathRange(int low, int high) {
        this.low = low;
        this.high = high;
    }

    public int getLowerBound() {
        return low;
    }

    public int getUpperBound() {
        return high;
    }

    public boolean isBounded() {
        return !(high == Integer.MAX_VALUE);
    }
}
