package br.com.efono.util;

import br.com.efono.model.Phoneme;
import br.com.efono.tree.BinaryTree;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 08.
 */
public class Defaults {

    /**
     * The first words in the array are easiest ones, according with the statistics in our database. The number of wrong
     * transcriptions of a word indicates the level of difficult.
     */
    public static final String[] SORTED_WORDS = new String[]{"Anel", "Bebê", "Dedo", "Cama", "Batom", "Dado", "Navio",
        "Terra", "Tênis", "Dente", "Rabo", "Faca", "Caminhão", "Fogo", "Sapo", "Sapato", "Cabelo", "Vaca", "Lápis",
        "Galinha", "Mesa", "Gato", "Cavalo", "Bolsa", "Casa", "Sofá", "Barriga", "Calça", "Folha", "Chapéu", "Pastel",
        "Coelho", "Língua", "Chinelo", "Beijo", "Caixa", "Cachorro", "Espelho", "Nuvem", "Relógio", "Porta",
        "Ventilador", "Zero", "Passarinho", "Tesoura", "Nariz", "Jornal", "Garfo", "Jacaré", "Girafa", "Brinco",
        "Prato", "Cobra", "Zebra", "Grama", "Cruz", "Trem", "Soprar", "Placa", "Fruta", "Fralda", "Refri", "Presente",
        "Livro", "Chiclete", "Chifre", "Bicicleta", "Gritar", "Bruxa", "Letra", "Plástico", "Igreja", "Flor",
        "Escrever", "Dragão", "Magro", "Estrela", "Pedra", "Vidro", "Microfone", "Colher", "Floresta", "Biblioteca",
        "Travesseiro"};

    /**
     * An array with words in the given words order: <code>easiest, hardest, 2º easiest, 2º hardest, ...</code>.
     */
    public static final String[] EASY_HARD_WORDS = getEasyHardWords(SORTED_WORDS);

    /**
     * Default words comparator. The easier words will be at the beginning, and the harder ones will be at the end.
     */
    public static final Comparator<String> DEFAULT_WORDS_COMPARATOR = (final String o1, final String o2) -> {
        int index1 = Arrays.asList(SORTED_WORDS).indexOf(o1);
        int index2 = Arrays.asList(SORTED_WORDS).indexOf(o2);
        if (index1 < 0 || index2 < 0) {
            // this should not happen, because all cases must use some of known words. Just in case...
            System.out.println("Something is wrong with tree");
            return 0;
        }

        return index2 - index1;
    };

    /**
     * The default binary tree used in simulations.
     */
    public static final BinaryTree<String> TREE = new BinaryTree<>(DEFAULT_WORDS_COMPARATOR);

    /**
     * The key is the word and the value is the target phonemes for this word. This should be initialized with data from
     * our base.
     */
    public static final Map<String, List<Phoneme>> mapTargetPhonemes = new HashMap<>();

    /**
     * Sorts the given array with words like: <code>easy, hard, easy, hard words...</code>
     *
     * @param sortedWords Given array to sort.
     * @return The sorted array.
     */
    public static String[] getEasyHardWords(final String[] sortedWords) {
        final LinkedList<String> easyHardWords = new LinkedList<>();

        if (sortedWords != null) {
            int easyIndex = 0;
            int hardIndex = sortedWords.length - 1;

            /**
             * This is sorting the sortedWords by <code>easy, hard, easy, hard words...</code>
             */
            while (easyHardWords.size() < sortedWords.length && easyIndex < sortedWords.length && hardIndex >= easyIndex) {
                String easy = sortedWords[easyIndex++];
                String hard = sortedWords[hardIndex--];

                if (!easyHardWords.contains(easy)) {
                    easyHardWords.add(easy);
                }
                if (!easyHardWords.contains(hard)) {
                    easyHardWords.add(hard);
                }
            }
        }

        return easyHardWords.toArray(new String[0]);
    }

    /**
     * Finds the index of the given word in the array {@link Defaults#SORTED_WORDS}. If <code>(word == null)</code> then
     * <code>-1</code> will be returned even if there is a null element in the array.
     *
     * @param word Word to find the index.
     * @return The index of the given word or -1 if not found.
     */
    public static int findIndexOf(final String word) {
        return findIndexOf(word, SORTED_WORDS);
    }

    /**
     * Finds the index of the given word in the given array. If <code>(word == null)</code> then <code>-1</code> will be
     * returned even if there is a null element in the array.
     *
     * @param word Word to find the index.
     * @param words The source array.
     * @return The index of the given word or -1 if not found.
     */
    public static int findIndexOf(final String word, final String[] words) {
        if (word != null && words != null) {
            for (int i = 0; i < words.length; i++) {
                String w = removeAccents(words[i]);

                if (w != null && w.equalsIgnoreCase(removeAccents(word))) {
                    return i;
                }
            }
        }
        System.out.println("The word " + word + " is not in our database. Someone is looking for it.");
        return -1;
    }

    private static String removeAccents(final String input) {
        if (input != null) {
            return Normalizer.normalize(input, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
        }
        return null;
    }

}
