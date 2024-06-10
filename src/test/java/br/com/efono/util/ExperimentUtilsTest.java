package br.com.efono.util;

import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.tree.BinaryTreePrinter;
import static br.com.efono.util.Defaults.SORTED_WORDS;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2024, Jun 10.
 */
public class ExperimentUtilsTest {

    /**
     * Executes before each method.
     */
    @Before
    public void beforeMethod() {
        Defaults.TREE.clear();
        Defaults.TARGET_PHONEMES.clear();
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)}.
     */
    @Test
    public void testSortList() {
        /**
         * Empty representation. It doesn't matter here.
         */
        KnownCase faca = new KnownCase("Faca", "", true);
        KnownCase dedo = new KnownCase("Dedo", "", true);
        KnownCase travesseiro = new KnownCase("Travesseiro", "", true);
        KnownCase terra = new KnownCase("Terra", "", true);
        KnownCase sapo = new KnownCase("Sapo", "", true);

        List<KnownCase> list = Arrays.asList(faca, dedo, travesseiro, terra, sapo);

        System.out.println("testSortList - EasyWordsFirst");
        ExperimentUtils.sortList(list, KnownCaseComparator.EasyWordsFirst);
        KnownCase[] expected = new KnownCase[]{dedo, terra, faca, sapo, travesseiro};
        for (int i = 0; i < list.size(); i++) {
            assertEquals(expected[i], list.get(i));
        }

        System.out.println("testSortList - HardWordsFirst");
        ExperimentUtils.sortList(list, KnownCaseComparator.HardWordsFirst);
        expected = new KnownCase[]{travesseiro, sapo, faca, terra, dedo};
        for (int i = 0; i < list.size(); i++) {
            assertEquals(expected[i], list.get(i));
        }

        System.out.println("testSortList - EasyHardWords");
        list = Arrays.asList(faca, dedo, travesseiro, terra, sapo);
        ExperimentUtils.sortList(list, KnownCaseComparator.EasyHardWords);
        expected = new KnownCase[]{dedo, travesseiro, terra, sapo, faca};
        for (int i = 0; i < list.size(); i++) {
            assertEquals("Failed in " + i, expected[i], list.get(i));
        }

        System.out.println("testSortList - EasyHardWords - with repeated words");
        KnownCase otherDedo = new KnownCase(dedo);
        list = Arrays.asList(faca, dedo, travesseiro, terra, otherDedo, sapo);
        ExperimentUtils.sortList(list, KnownCaseComparator.EasyHardWords);
        expected = new KnownCase[]{dedo, otherDedo, travesseiro, terra, sapo, faca};
        for (int i = 0; i < list.size(); i++) {
            assertEquals("Failed in " + i, expected[i], list.get(i));
        }
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)} with
     * {@link KnownCaseComparator#BinaryTreeComparator}.
     */
    @Test
    public void testSortListTree() {
        System.out.println("testSortListTree - all correct cases");
        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(String[]::new));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        // all correct
        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase terra = new KnownCase("Terra", "[’tɛχə]", true, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", true, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase bebe = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));

        final List<KnownCase> list = Arrays.asList(batom, terra, dado, tenis, dente, navio, dedo, cama, anel, bebe);
        list.sort(KnownCaseComparator.EasyWordsFirst.getComparator()); // this is already tested
        ExperimentUtils.sortList(list, KnownCaseComparator.BinaryTreeComparator);

        // the indexes // essa eh uma abordagem, vai ate o mais dificil e vai voltando
        int[] expectedSequence = new int[]{4, 7, 8, 9, 5, 6, 2, 3, 1, 0};
        for (int i = 0; i < expectedSequence.length; i++) {
            int index = expectedSequence[i];
            assertEquals(SORTED_WORDS[index], list.get(i).getWord());
        }
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)} with
     * {@link KnownCaseComparator#BinaryTreeComparator}.
     */
    @Test
    public void testSortListTree2() {
        System.out.println("testSortListTree2");

        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(String[]::new));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase terra = new KnownCase("Terra", "[’tɛχə]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", true, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase bebe = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));

        final List<KnownCase> list = Arrays.asList(batom, terra, dado, tenis, dente, navio, dedo, cama, anel, bebe);
        list.sort(KnownCaseComparator.EasyWordsFirst.getComparator()); // this is already tested
        ExperimentUtils.sortList(list, KnownCaseComparator.BinaryTreeComparator);

        // the indexes // essa eh uma abordagem, vai ate o mais dificil e vai voltando
        int[] expectedSequence = new int[]{4, 7, 5, 6, 8, 9, 2, 3, 1, 0};
        for (int i = 0; i < expectedSequence.length; i++) {
            int index = expectedSequence[i];
            assertEquals(SORTED_WORDS[index], list.get(i).getWord());
        }
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)} with
     * {@link KnownCaseComparator#BinaryTreeComparator}.
     */
    @Test
    public void testSortListTree3() {
        System.out.println("testSortListTree3");

        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(String[]::new));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase terra = new KnownCase("Terra", "[’tɛχə]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase bebe = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));

        final List<KnownCase> list = Arrays.asList(batom, terra, dado, tenis, dente, navio, dedo, cama, anel, bebe);
        list.sort(KnownCaseComparator.EasyWordsFirst.getComparator()); // this is already tested
        ExperimentUtils.sortList(list, KnownCaseComparator.BinaryTreeComparator);

        // the indexes // essa eh uma abordagem, vai ate o mais dificil e vai voltando
        int[] expectedSequence = new int[]{4, 7, 5, 6, 8, 9, 2, 3, 1, 0};
        for (int i = 0; i < expectedSequence.length; i++) {
            int index = expectedSequence[i];
            assertEquals(SORTED_WORDS[index], list.get(i).getWord());
        }
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)} with
     * {@link KnownCaseComparator#BinaryTreeComparator}.
     *
     * Same logic of {@link SimulationWordsSequenceTest#testSortListTree3()} but now with error on the first word
     * (rootNode).
     */
    @Test
    public void testSortListTree4() {
        System.out.println("testSortListTree4");

        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(String[]::new));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", false, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase terra = new KnownCase("Terra", "[’tɛχə]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase bebe = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));

        final List<KnownCase> list = Arrays.asList(batom, terra, dado, tenis, dente, navio, dedo, cama, anel, bebe);
        list.sort(KnownCaseComparator.EasyWordsFirst.getComparator()); // this is already tested
        ExperimentUtils.sortList(list, KnownCaseComparator.BinaryTreeComparator);

        // the indexes // essa eh uma abordagem, vai ate o mais dificil e vai voltando
        int[] expectedSequence = new int[]{4, 2, 3, 1, 0, 7, 5, 6, 8, 9};
        for (int i = 0; i < expectedSequence.length; i++) {
            int index = expectedSequence[i];
            assertEquals(SORTED_WORDS[index], list.get(i).getWord());
        }
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)} with
     * {@link KnownCaseComparator#BinaryTreeComparator}.
     *
     * Same logic of {@link SimulationWordsSequenceTest#testSortListTree3()} but now with error on the first word
     * (rootNode).
     */
    @Test
    public void testSortListTree5() {
        System.out.println("testSortListTree5");

        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(String[]::new));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", false, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase terra = new KnownCase("Terra", "[’tɛχə]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", false, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase bebe = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));

        final List<KnownCase> list = Arrays.asList(batom, terra, dado, tenis, dente, navio, dedo, cama, anel, bebe);
        list.sort(KnownCaseComparator.EasyWordsFirst.getComparator()); // this is already tested
        ExperimentUtils.sortList(list, KnownCaseComparator.BinaryTreeComparator);

        // the indexes // essa eh uma abordagem, vai ate o mais dificil e vai voltando
        int[] expectedSequence = new int[]{4, 2, 1, 0, 3, 7, 5, 6, 8, 9};
        for (int i = 0; i < expectedSequence.length; i++) {
            int index = expectedSequence[i];
            assertEquals(SORTED_WORDS[index], list.get(i).getWord());
        }
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)} with
     * {@link KnownCaseComparator#BinaryTreeComparator}.
     *
     * This test has more words in the tree than the others, and less words in given cases to sort. This simulates what
     * happens if we use an incomplete assessment which didn't use all the words from the original set.
     *
     */
    @Test
    public void testSortListTree6() {
        System.out.println("testSortListTree6 - incomplete evaluation");

        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(String[]::new));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        // missing "Terra (7)"
        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", false, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase bebe = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));

        // incomplete evaluation
        final List<KnownCase> list = Arrays.asList(batom, dado, tenis, dente, navio, dedo, cama, anel, bebe);
        assertEquals(9, list.size());
        list.sort(KnownCaseComparator.EasyWordsFirst.getComparator()); // this is already tested
        ExperimentUtils.sortList(list, KnownCaseComparator.BinaryTreeComparator);
        assertEquals(9, list.size()); // makes sure that the list size didn't change

        // the indexes // essa eh uma abordagem, vai ate o mais dificil e vai voltando
        int[] expectedSequence = new int[]{4, 8, 9, 5, 6, 2, 1, 0, 3};
        for (int i = 0; i < expectedSequence.length; i++) {
            int index = expectedSequence[i];
            assertEquals(SORTED_WORDS[index], list.get(i).getWord());
        }
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)} with
     * {@link KnownCaseComparator#BinaryTreeComparator}.
     *
     * This test has more words in the tree than the others, and less words in given cases to sort. This simulates what
     * happens if we use an incomplete assessment which didn't use all the words from the original set.
     *
     */
    @Test
    public void testSortListTree7() {
        System.out.println("testSortListTree7 - incomplete evaluation");

        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(String[]::new));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        // missing "Terra (7)" and "Navio (6)"
        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", false, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase bebe = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));

        // incomplete evaluation
        final List<KnownCase> list = Arrays.asList(batom, dado, tenis, dente, dedo, cama, anel, bebe);
        assertEquals(8, list.size());
        list.sort(KnownCaseComparator.EasyWordsFirst.getComparator()); // this is already tested
        ExperimentUtils.sortList(list, KnownCaseComparator.BinaryTreeComparator);
        assertEquals(8, list.size()); // makes sure that the list size didn't change

        // the indexes // essa eh uma abordagem, vai ate o mais dificil e vai voltando
        int[] expectedSequence = new int[]{4, 8, 9, 5, 2, 1, 0, 3};
        for (int i = 0; i < expectedSequence.length; i++) {
            int index = expectedSequence[i];
            assertEquals(SORTED_WORDS[index], list.get(i).getWord());
        }
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)} with
     * {@link KnownCaseComparator#BinaryTreeComparator}.
     *
     * This test has more words in the tree than the others, and less words in given cases to sort. This simulates what
     * happens if we use an incomplete assessment which didn't use all the words from the original set.
     *
     */
    @Test
    public void testSortListTree8() {
        System.out.println("testSortListTree8 - incomplete evaluation");

        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(String[]::new));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        // missing "Terra (7)" and "Navio (6)" and "Dedo (2)"
        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", false, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase bebe = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));

        // incomplete evaluation
        final List<KnownCase> list = Arrays.asList(batom, dado, tenis, dente, cama, anel, bebe);
        assertEquals(7, list.size());
        list.sort(KnownCaseComparator.EasyWordsFirst.getComparator()); // this is already tested
        ExperimentUtils.sortList(list, KnownCaseComparator.BinaryTreeComparator);
        assertEquals(7, list.size()); // makes sure that the list size didn't change

        // the indexes // essa eh uma abordagem, vai ate o mais dificil e vai voltando
        int[] expectedSequence = new int[]{4, 3, 1, 0, 8, 9, 5};
        for (int i = 0; i < expectedSequence.length; i++) {
            int index = expectedSequence[i];
            assertEquals(SORTED_WORDS[index], list.get(i).getWord());
        }
    }

}
