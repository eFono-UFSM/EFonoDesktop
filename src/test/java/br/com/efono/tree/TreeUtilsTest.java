package br.com.efono.tree;

import br.com.efono.model.KnownCase;
import br.com.efono.model.Phoneme;
import br.com.efono.util.Defaults;
import static br.com.efono.util.Defaults.SORTED_WORDS;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 16.
 */
public class TreeUtilsTest {

    /**
     * Tests {@link TreeUtils#buildSequenceOrder(Node, List, List)}.
     */
    @Test
    public void testBuildSequenceOrder() {
        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(new String[0]));

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

        String[] expected = new String[]{"Batom", "Terra", "Dado", "Navio", "Tênis", "Dente", "Dedo", "Cama", "Bebê", "Anel"};
        final List<String> result = new LinkedList<>();
        TreeUtils.buildSequenceOrder(Defaults.TREE.getRoot(), result, list);

        assertEquals(expected.length, result.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result.get(i));
        }
    }
    
    /**
     * Tests {@link TreeUtils#buildSequenceOrder(Node, List, List)} with missing words in the cases.
     */
    @Test
    public void testBuildSequenceOrder2() {
        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(new String[0]));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        // missing Dado(5) and Bebê(1)
        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase terra = new KnownCase("Terra", "[’tɛχə]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", true, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));

        final List<KnownCase> list = Arrays.asList(batom, terra, tenis, dente, navio, dedo, cama, anel);

        String[] expected = new String[]{"Batom", "Terra", "Navio", "Tênis", "Dente", "Dedo", "Cama", "Anel"};
        final List<String> result = new LinkedList<>();
        TreeUtils.buildSequenceOrder(Defaults.TREE.getRoot(), result, list);

        assertEquals(expected.length, result.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], result.get(i));
        }
    }

    /**
     * Tests {@link TreeUtils#getFirstWords(Node, LinkedList, List)}.
     */
    @Test
    public void testGetFirstWords() {
        System.out.println("testGetBestFirstWords - complete evaluation");

        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(new String[0]));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase terra = new KnownCase("Terra", "[’tɛχə]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", false, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase bebe = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));

        final List<KnownCase> listCases = Arrays.asList(batom, terra, navio, dedo, dado, tenis, dente, cama, anel, bebe);
        final LinkedList<String> result = new LinkedList<>();
        TreeUtils.getFirstWords(Defaults.TREE.getRoot(), result, listCases);

        // the indexes
        int[] expectedSequence = new int[]{4, 7, 5, 6};
        assertEquals(expectedSequence.length, result.size());
        for (int i = 0; i < expectedSequence.length; i++) {
            int index = expectedSequence[i];
            assertEquals(SORTED_WORDS[index], result.get(i));
        }
    }

    /**
     * Tests {@link TreeUtils#getFirstWords(Node, LinkedList, List)}.
     */
    @Test
    public void testGetFirstWords2() {
        System.out.println("testGetBestFirstWords - incomplete evaluation");

        final List<String> words = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            words.add(Defaults.SORTED_WORDS[i]);
        }
        Defaults.TREE.init(words.toArray(new String[0]));

        System.out.println("-------------------");
        BinaryTreePrinter.print(Defaults.TREE, System.out);
        System.out.println("\n-------------------");

        // missing Terra(7)
        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase tenis = new KnownCase("Tênis", "[’tenis]", false, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", false, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase bebe = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));

        final List<KnownCase> listCases = Arrays.asList(batom, navio, dedo, dado, tenis, dente, cama, anel, bebe);
        final LinkedList<String> result = new LinkedList<>();
        TreeUtils.getFirstWords(Defaults.TREE.getRoot(), result, listCases);

        // the indexes
        int[] expectedSequence = new int[]{4, 8, 9};
        assertEquals(expectedSequence.length, result.size());
        for (int i = 0; i < expectedSequence.length; i++) {
            int index = expectedSequence[i];
            assertEquals(SORTED_WORDS[index], result.get(i));
        }
    }

}
