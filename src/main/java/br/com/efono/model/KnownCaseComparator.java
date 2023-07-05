package br.com.efono.model;

import br.com.efono.util.SimulationWordsSequence;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
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
        // TODO: ignore case and acentuation
        int indexOfo1 = Arrays.asList(Defaults.SORTED_WORDS).indexOf(o1.getWord());
        int indexOfo2 = Arrays.asList(Defaults.SORTED_WORDS).indexOf(o2.getWord());
        return indexOfo2 - indexOfo1;
    }),
    /**
     * Sort KnownCases with easiest words first.
     */
    EasyWordsFirst((KnownCase o1, KnownCase o2) -> {
        // TODO: ignore case and acentuation
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
        // TODO: ignore case and acentuation
        List<String> list = Arrays.asList(Defaults.EASY_HARD_WORDS);
        int indexOfo1 = list.indexOf(o1.getWord());
        int indexOfo2 = list.indexOf(o2.getWord());
        return indexOfo1 - indexOfo2;
    }),
    BinaryTree((KnownCase o1, KnownCase o2) -> {
        return 0;
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

    /**
     * Stores the sorted words by difficult level.
     */
    public static class Defaults {

        /**
         * The first words in the array are easiest ones, according with the statistics in our database. The number of
         * wrong transcriptions of a word indicates the level of difficult.
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

        private static final String[] EASY_HARD_WORDS = getEasyHardWords(SORTED_WORDS);
    }

}
