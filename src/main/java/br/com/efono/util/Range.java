package br.com.efono.util;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2024, May 30.
 */
public class Range {

    private final int min, max;

    /**
     * Creates an object that represents a range.
     *
     * @param min Min value in the range (inclusive).
     * @param max Max value in the range (inclusive).
     */
    public Range(int min, int max) {
        // min <= value <= max
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }


}
