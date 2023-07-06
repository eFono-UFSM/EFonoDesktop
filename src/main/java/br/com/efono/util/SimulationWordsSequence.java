package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
     * Default value for split consonants parameter.
     */
    public static final boolean SPLIT_CONSONANTS = true;

    // TODO: tests
    /**
     * Runs the simulation with the words sequence in the given assessment.
     *
     * @param assessment Assessment.
     * @param comp Comparator to sort KnownCases or null.
     * @param minimum Number of times that the same phoneme in the same position must be produced to be considered in
     * the phonetic inventory.
     * @return The information about the simulation.
     */
    public static SimulationInfo runSimulation(final Assessment assessment, final KnownCaseComparator comp,
            final int minimum) {
        return runSimulation(assessment, comp, minimum, SPLIT_CONSONANTS);
    }

    /**
     * Runs the simulation with the words sequence in the given assessment.
     *
     * @param assessment Assessment.
     * @param comp Comparator to sort KnownCases or null.
     * @param minimum Number of times that the same phoneme in the same position must be produced to be considered in
     * the phonetic inventory.
     * @param splitConsonantClusters True - this will count the consonant clusters as two phonemes:
     * <code>bɾ(OCME) -> b(OCME) + ɾ(OCME).</code>. The phoneme ɾ(OCME) can be counted more times in this way, and we
     * can evaluate more precisely the consonant clusters productions.
     * @return The information about the simulation.
     */
    public static SimulationInfo runSimulation(final Assessment assessment, final KnownCaseComparator comp,
            final int minimum, boolean splitConsonantClusters) {
        final Map<Phoneme, Integer> mapCounter = new HashMap<>();
        final List<String> wordsRequired = new LinkedList<>();
        if (assessment != null && minimum > 0) {
            List<KnownCase> cases = assessment.getCases();
            sortList(cases, comp);

            for (KnownCase c : cases) {
                // TODO: ao inves de "fonemas produzidos" depois vai ser preciso pegar de algum gabarito os "fonemas alvo", pois são esses que estão sendo testados.
                // os "fonemas produzidos" aqui precisam ser testados no mínimo 2 vezes para serem considerados "adquiridos" no inv. fonético. [esse é o trabalho da simulação]
                // ou seja, ver qual o impacto que a sequência da avaliação tem sobre o inv. fonético.
                // TODO: adicionar um c.getPhonemesRequired//target. Isso vai ser útil para fazer o PCC-R depois.
                for (Phoneme phoneme : c.getPhonemes()) {
                    final List<Phoneme> list = new ArrayList<>();

                    // bɾ(OCME) -> b(OCME) + ɾ(OCME)
                    if (phoneme.isConsonantCluster() && splitConsonantClusters) {
                        String[] split = phoneme.getPhoneme().split("");
                        for (String s : split) {
                            list.add(new Phoneme(s, phoneme.getPosition()));
                        }
                    } else {
                        list.add(phoneme);
                    }

                    for (Phoneme p : list) {
                        int count = 1;
                        if (mapCounter.containsKey(p)) {
                            count = mapCounter.get(p) + 1;
                        }
                        mapCounter.put(p, count);

                        if (count <= minimum) {
                            /**
                             * If this word contains at least one phoneme which wasn't be tested at minimum two times,
                             * then the word is important and will be "required".
                             *
                             * If all the phonemes tested by this word were already tested at minimum 2 times, so the
                             * word doesn't would need to be here.
                             */
                            if (!wordsRequired.contains(c.getWord())) {
                                wordsRequired.add(c.getWord());
                            }
                        }
                    }
                }
            }
            /**
             * TODO: todos os fonemas produzidos estão em "mapCounter". Os fonemas testados (alvos) deverão ser
             * calculados a partir dos gabaritos corretos. Aí sim, podemos calcular o PCC-R.
             */
        }
        return new SimulationInfo(mapCounter, wordsRequired, assessment, comp, splitConsonantClusters);
    }

    /**
     * Sorts the list with cases according with the given comparator.
     *
     * @param list List to sort.
     * @param comp {@link KnownCaseComparator} to use.
     */
    public static void sortList(final List<KnownCase> list, final KnownCaseComparator comp) {
        if (list != null && comp != null) {
            switch (comp) {
                case EasyHardWords:
                    // sorting just to have the right indexes inside the list: [easiest, ..., hardest]
                    list.sort(KnownCaseComparator.EasyWordsFirst.getComparator());
                    // all the words
                    final LinkedList<String> words = new LinkedList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (!words.contains(list.get(i).getWord())) {
                            words.add(list.get(i).getWord());
                        }
                    }
                    String[] easyHardWords = KnownCaseComparator.getEasyHardWords(
                            words.toArray(new String[words.size()]));
                    final List<String> wordsSorted = Arrays.asList(easyHardWords);
                    list.sort((KnownCase o1, KnownCase o2) -> {
                        // TODO: ignore case and acentuation
                        int indexOfo1 = wordsSorted.indexOf(o1.getWord());
                        int indexOfo2 = wordsSorted.indexOf(o2.getWord());
                        return indexOfo1 - indexOfo2;
                    });
                    break;
                default:
                    list.sort(comp.getComparator());
                    break;
            }
        }
    }

    // TODO: depois, simular a avaliação toda com o mesmo lance da busca binária, mas dessa vez, se o usuário acertou vai para uma mais difícil, se errou para mais fácil e assim por diante.
    // TODO: comparator com indices misturados (busca binaria).
}
