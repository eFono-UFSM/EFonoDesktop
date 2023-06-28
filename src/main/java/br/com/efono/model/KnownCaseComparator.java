package br.com.efono.model;

import java.util.Arrays;
import java.util.Comparator;

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
    }

}
