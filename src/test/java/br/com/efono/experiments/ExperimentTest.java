package br.com.efono.experiments;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2024, Jul 01.
 */
public class ExperimentTest {

    /**
     * Tests {@link Experiment#sortByFrequency(java.util.List)}.
     */
    @Test
    public void testSortByFrequency() {
        List<Integer> numbers = Arrays.asList(1, 3, 2, 3, 4, 4, 3, 2, 3, 1, 3, 2, 4, 6, 4, 1, 3, 1, 1);
        // 1 -> 5
        // 2 -> 3
        // 3 -> 6
        // 4 -> 4
        // 6 -> 1
        List<Integer> actual = Experiment.sortByFrequency(numbers);

        List<Integer> expected = Arrays.asList(3, 1, 4, 2, 6);
        assertEquals(expected, actual);
    }

}
