package br.com.efono.util;

import br.com.efono.tree.BinaryTree;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;

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
     * Sorts the given array with words like: <code>easy, hard, easy, hard words...</code>
     *
     * @param sortedWords Given array to sort.
     * @return The sorted array.
     */
    public static String[] getEasyHardWords(final String[] sortedWords) {
        final LinkedList<String> easyHardWords = new LinkedList<>();

        int easyIndex = 0;
        int hardIndex = sortedWords.length - 1;

        // TODO: testes com arrays com tamanho par/impar.
        /**
         * This is sorting the sortedWords by <code>easy, hard, easy, hard words...</code>
         */
        while (easyHardWords.size() < sortedWords.length && easyIndex < sortedWords.length && hardIndex > 0) {
            String easy = sortedWords[easyIndex++];
            String hard = sortedWords[hardIndex--];

            if (!easyHardWords.contains(easy)) {
                easyHardWords.add(easy);
            }
            if (!easyHardWords.contains(hard)) {
                easyHardWords.add(hard);
            }
        }

        return easyHardWords.toArray(new String[easyHardWords.size()]);
    }

}
