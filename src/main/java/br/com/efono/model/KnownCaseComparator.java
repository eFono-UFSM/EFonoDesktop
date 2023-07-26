package br.com.efono.model;

import br.com.efono.util.Defaults;
import br.com.efono.util.SimulationWordsSequence;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 28.
 */
public enum KnownCaseComparator {

    /**
     * Sort KnownCases with harder words first.
     */
    HardWordsFirst((final KnownCase o1, final KnownCase o2) -> {
        /**
         * The constructor of {@link KnownCase} already guarantee that will have only known words - which means: words
         * from our database (SORTED_WORDS).
         */
        int indexOfo1 = Arrays.asList(Defaults.SORTED_WORDS).indexOf(o1.getWord());
        int indexOfo2 = Arrays.asList(Defaults.SORTED_WORDS).indexOf(o2.getWord());
        return indexOfo2 - indexOfo1;
    }),
    /**
     * Sort KnownCases with easiest words first.
     */
    EasyWordsFirst((KnownCase o1, KnownCase o2) -> {
        int indexOfo1 = Arrays.asList(Defaults.SORTED_WORDS).indexOf(o1.getWord());
        int indexOfo2 = Arrays.asList(Defaults.SORTED_WORDS).indexOf(o2.getWord());
        return indexOfo1 - indexOfo2;
    }),
    /**
     * Sort KnownCases by switching between easy/hard words. This will generate a list with words like: <code>easy, hard, easy, hard,
     * ...</code>. This should be used with {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)}. It
     * considers all the words in {@link KnownCaseComparator.Defaults#SORTED_WORDS}, so if the given list to sort
     * doesn't contain all the words, it may presents weird results. To consider only the words in the list and to sort
     * them like <code>easy, hard, easy</code>, then you should use
     * {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)}.
     */
    EasyHardWords((KnownCase o1, KnownCase o2) -> {
        List<String> list = Arrays.asList(Defaults.EASY_HARD_WORDS);
        int indexOfo1 = list.indexOf(o1.getWord());
        int indexOfo2 = list.indexOf(o2.getWord());
        return indexOfo1 - indexOfo2;
    }),
    BinaryTreeComparator((KnownCase o1, KnownCase o2) -> {
        int indexOfo1 = Arrays.asList(Defaults.SORTED_WORDS).indexOf(o1.getWord());
        int indexOfo2 = Arrays.asList(Defaults.SORTED_WORDS).indexOf(o2.getWord());
        return indexOfo1 - indexOfo2;
    });

    private final Comparator<KnownCase> comp;

    private KnownCaseComparator(final Comparator<KnownCase> comp) {
        this.comp = comp;
    }

    /**
     * @return The comparator.
     */
    public Comparator<KnownCase> getComparator() {
        return comp;
    }

    @Override
    public String toString() {
        return name();
    }

}
