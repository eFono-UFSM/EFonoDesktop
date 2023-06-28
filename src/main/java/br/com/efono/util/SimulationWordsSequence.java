package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.Phoneme;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 26.
 */
public class SimulationWordsSequence {

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

    // TODO: tests
    /**
     * Runs the simulation with the words sequence in the given assessment.
     *
     * @param assessment Assessment.
     * @param comp Comparator to sort KnownCases or null.
     */
    public void runSimulation(final Assessment assessment, final Comparator<KnownCase> comp) {
        System.out.println("-----------------\n"
                + "Running simulation in assessment with " + assessment.getCases().size()
                + " cases with " + comp + " comparator");
        final Map<Phoneme, Integer> mapCounter = new HashMap<>();

        List<KnownCase> cases = assessment.getCases();
        if (comp != null) {
            cases.sort(comp);
        }

        final List<String> wordsRequired = new LinkedList<>();

        for (KnownCase c : cases) {
            // TODO: ao inves de "fonemas produzidos" depois vai ser preciso pegar de algum gabarito os "fonemas alvo", pois são esses que estão sendo testados.
            // os "fonemas produzidos" aqui precisam ser testados no mínimo 2 vezes para serem considerados "adquiridos" no inv. fonético. [esse é o trabalho da simulação]
            // ou seja, ver qual o impacto que a sequência da avaliação tem sobre o inv. fonético.
            // TODO: adicionar um c.getPhonemesRequired//target. Isso vai ser útil para fazer o PCC-R depois.
//            System.out.println("\tcase of " + c.getWord() + " phonemes produced: " + c.getPhonemes().size());
            for (Phoneme p : c.getPhonemes()) {
                int count = 1;
                if (mapCounter.containsKey(p)) {
                    count = mapCounter.get(p) + 1;
                }

                if (count <= 2) { // minumum of tests required
//                    System.out.println(p + " -> " + count);
                    mapCounter.put(p, count);

                    /**
                     * If this word contains at least one phoneme which wasn't be tested at minimum two times, then the
                     * word is important and will be "required".
                     *
                     * If all the phonemes tested by this word were already tested at minimum 2 times, so the word
                     * doesn't would need to be here.
                     */
//                    System.out.println("word required, " + p + " -> " + count);
                    if (!wordsRequired.contains(c.getWord())) {
                        wordsRequired.add(c.getWord());
                    }
                }
//                System.out.println("-----");
            }

            // check map counter
            Iterator<Map.Entry<Phoneme, Integer>> iterator = mapCounter.entrySet().iterator();

            int notOk = 0, ok = 0;
            while (iterator.hasNext()) {
                Map.Entry<Phoneme, Integer> next = iterator.next();

                if (next.getValue() < 2) {
                    notOk++;
                } else {
                    ok++;
                }
            }
//            System.out.println("phonemes test: not ok: " + notOk + " ok: " + ok);
        }

        System.out.println("Result");
        Iterator<Map.Entry<Phoneme, Integer>> iterator = mapCounter.entrySet().iterator();

        int notOk = 0, ok = 0;
        while (iterator.hasNext()) {
            Map.Entry<Phoneme, Integer> next = iterator.next();

            if (next.getValue() < 2) {
//                System.out.println("not ok: " + next.getKey() + " -> " + next.getValue());
                notOk++;
            } else {
                ok++;
            }
        }

        System.out.println("words required: " + wordsRequired.size() + ": " + wordsRequired);
        float notOkcent = notOk * 100 / mapCounter.size();
        float okcent = ok * 100 / mapCounter.size();

        System.out.println("less than the required two tests: " + notOkcent + "% at least two tests: " + okcent + "%");
//        System.out.println("-------------------------------------------------------------------------");
    }

    // TODO: depois, simular a avaliação toda com o mesmo lance da busca binária, mas dessa vez, se o usuário acertou vai para uma mais difícil, se errou para mais fácil e assim por diante.
    // TODO: comparator com indices misturados (busca binaria).
    /**
     * Sort KnownCases with harder words first.
     */
    public static class HarderWordsFirst implements Comparator<KnownCase> {

        @Override
        public int compare(final KnownCase o1, final KnownCase o2) {
            // TODO: ignore case and acentuation
            int indexOfo1 = Arrays.asList(SORTED_WORDS).indexOf(o1.getWord());
            int indexOfo2 = Arrays.asList(SORTED_WORDS).indexOf(o2.getWord());
            return indexOfo2 - indexOfo1;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

    }

    /**
     * Sort KnownCases with easiest words first.
     */
    public static class EasiestWordsFirst implements Comparator<KnownCase> {

        @Override
        public int compare(final KnownCase o1, final KnownCase o2) {
            // TODO: ignore case and acentuation
            int indexOfo1 = Arrays.asList(SORTED_WORDS).indexOf(o1.getWord());
            int indexOfo2 = Arrays.asList(SORTED_WORDS).indexOf(o2.getWord());
            return indexOfo1 - indexOfo2;
        }

        @Override
        public String toString() {
            return this.getClass().getSimpleName();
        }

    }

}
