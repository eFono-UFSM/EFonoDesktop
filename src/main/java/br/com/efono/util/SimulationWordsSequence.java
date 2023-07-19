package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationInfo;
import br.com.efono.tree.TreeUtils;
import java.util.ArrayList;
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
        return runSimulation(assessment, comp, minimum, SPLIT_CONSONANTS, true);
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
     * @param phoneticInventory True - will compute words required for phonetic inventory. False - it'll compute words
     * required for phonemes testing (PCC-R).
     * @return The information about the simulation.
     */
    public static SimulationInfo runSimulation(final Assessment assessment, final KnownCaseComparator comp,
            final int minimum, boolean splitConsonantClusters, final boolean phoneticInventory) {
        if (assessment != null && minimum > 0) {
            final Map<Phoneme, Integer> mapCounter = new HashMap<>();

            List<KnownCase> cases = assessment.getCases();
            sortList(cases, comp);

            List<String> wordsRequired = getWordsRequired(cases, mapCounter, splitConsonantClusters, minimum,
                    phoneticInventory);
            /**
             * TODO: todos os fonemas produzidos ou testados (depende da flag phoneticInventory) estão em "mapCounter".
             * Os fonemas testados (alvos) deverão ser calculados a partir dos gabaritos corretos. Aí sim, podemos
             * calcular o PCC-R.
             */
            return new SimulationInfo(mapCounter, wordsRequired, assessment, comp, splitConsonantClusters);
        }
        return new SimulationInfo(assessment, comp, splitConsonantClusters);
    }

    /**
     * Gets the words required for phonetic inventory according with criteria: a word that contains at least one phoneme
     * which was not tested a <code>minimum</code> of times, then that word is important and will be in the required
     * list.The words are analyzed according with the order in the <code>cases</code> list, so changing the order of the
     * array can reproduce different results.The phonetic inventory contains all the phonemes that were spoken correctly
     * at minimum of times.
     *
     * In this method only will be considered phonemes spoken in the list of cases. So here, we can have incomplete
     * assessments, since probably the phonetic inventory will be incomplete as well.
     *
     * @param cases The cases to analyze.
     * @param mapCounter A map only to count how many times each phoneme was tested, according with the criteria bellow.
     * @param splitConsonantClusters True - the consonant clusters will be transformed into 2 consonants phonemes. Ex.:
     * bɾ(OCME) -> b(OCME) + ɾ(OCME). False - keep the consonant phonemes as they are. Letting this flag with false
     * possibly will return more words because it's more difficult to find the phoneme bɾ(OCME) in the cases, but
     * r(OCME) can appear in many others consonant clusters, an b(OCME) as well (br, bl).
     * @param minimum The minimum of times that each phoneme must be reproduced in order to be considered in the
     * phonetic inventory. Higher values can return more required words.
     * @param phoneticInventory True - will compute words required for phonetic inventory. False - it'll compute words
     * required for phonemes testing (PCC-R).
     * @return A list with the required words, according with the criteria above.
     */
    public static List<String> getWordsRequired(final List<KnownCase> cases, final Map<Phoneme, Integer> mapCounter,
            boolean splitConsonantClusters, final int minimum, final boolean phoneticInventory) {
        final List<String> wordsRequired = new LinkedList<>();

        // TODO: vai pegar somente as palavras que estão nos casos e não considerando todas as palavras do conjunto
        if (cases != null && mapCounter != null) {
            mapCounter.clear();
            for (KnownCase c : cases) {
                // TODO: ao inves de "fonemas produzidos" depois vai ser preciso pegar de algum gabarito os "fonemas alvo", pois são esses que estão sendo testados.
                // os "fonemas produzidos" aqui precisam ser testados no mínimo 2 vezes para serem considerados "adquiridos" no inv. fonético. [esse é o trabalho da simulação]
                // ou seja, ver qual o impacto que a sequência da avaliação tem sobre o inv. fonético.
                // TODO: adicionar um c.getPhonemesRequired//target. Isso vai ser útil para fazer o PCC-R depois.

                /**
                 * c.getPhonemes() contains all the produced phonemes, so it's useful for us to get the phonetic
                 * inventory information. For a phoneme to be considered in phonetic inventory it must be produced a
                 * minimum of times. For PCC-R, only matters the number of times that a phoneme was tested, because with
                 * that we can calculate later the percentage of correct productions in Assessment. For that, it's good
                 * that a phoneme can be tested a minimum of times as well, to avoid false positive and negative cases.
                 *
                 * TODO: e se um fonema tiver 2 produções: correta e incorreta. Deveria ter uma palavra a mais pra
                 * desempatar...
                 */
                List<Phoneme> phonemes = c.getPhonemes();
                if (!phoneticInventory) {
                    phonemes = Defaults.TARGET_PHONEMES.get(c.getWord());
                }

                final List<Phoneme> list = new ArrayList<>();
                phonemes.forEach(phoneme -> {
                    // bɾ(OCME) -> b(OCME) + ɾ(OCME)
                    if (phoneme.isConsonantCluster() && splitConsonantClusters) {
                        String[] split = phoneme.getPhoneme().split("");
                        for (String s : split) {
                            // repeated phonemes are allowed here, because we wanna count
                            list.add(new Phoneme(s, phoneme.getPosition()));
                        }
                    } else {
                        // repeated phonemes are allowed
                        list.add(phoneme);
                    }
                });

                for (Phoneme p : list) {
                    int count = 1;
                    if (mapCounter.containsKey(p)) {
                        count = mapCounter.get(p) + 1;
                    }
                    mapCounter.put(p, count);

                    if (count <= minimum) {
                        /**
                         * If this word contains at least one phoneme which was not tested at minimum of times, then the
                         * word is important and will be "required".
                         *
                         * If all the phonemes tested by this word were already tested at minimum 2 times, so the word
                         * doesn't would need to be here.
                         */
                        if (!wordsRequired.contains(c.getWord())) {
                            wordsRequired.add(c.getWord());
                        }
                    }
                }
            }
        }

        return wordsRequired;
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
                case EasyHardWords: {
                    // sorting just to have the right indexes inside the list: [easiest, ..., hardest]
                    list.sort(KnownCaseComparator.EasyWordsFirst.getComparator());
                    // all the words
                    final LinkedList<String> words = new LinkedList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (!words.contains(list.get(i).getWord())) {
                            words.add(list.get(i).getWord());
                        }
                    }
                    /**
                     * Sorts considering only the words that are in the list of cases, avoiding getting indexes from
                     * global {@link Defaults#SORTED_WORDS}.
                     */
                    String[] easyHardWords = Defaults.getEasyHardWords(words.toArray(new String[0]));
                    list.sort((KnownCase o1, KnownCase o2) -> {
                        int indexOfo1 = Defaults.findIndexOf(o1.getWord(), easyHardWords);
                        int indexOfo2 = Defaults.findIndexOf(o2.getWord(), easyHardWords);
                        return indexOfo1 - indexOfo2;
                    });
                    break;
                }
                case BinaryTreeComparator: {
                    final List<String> insertionOrder = new LinkedList<>();

                    TreeUtils.buildSequenceOrder(Defaults.TREE.getRoot(), insertionOrder, list);

                    /**
                     * Sorts the list as insertion order in the tree. This means that the first word will be the middle
                     * word in the tree (root node) and the next one will always be easier or harder than the previous
                     * one according with the result from the user. The numbers represents the words indexes at
                     * {@link Defaults#SORTED_WORDS}. Example: the case (41) was incorrect, so the next case to be
                     * analyzed will be 20 (easier than 41); the case (20) was correct, so the next will be 31 (harder
                     * than 20 and easier than 41); and so on. When the algorithm arrive in some leaf node, it starts to
                     * returning back to parents nodes and visit the ones in the other side of its node parent.
                     */
                    list.sort((KnownCase o1, KnownCase o2) -> {
                        int indexOfo1 = Defaults.findIndexOf(o1.getWord(), insertionOrder.toArray(new String[0]));
                        int indexOfo2 = Defaults.findIndexOf(o2.getWord(), insertionOrder.toArray(new String[0]));
                        return indexOfo1 - indexOfo2;
                    });
                    break;
                }
                default:
                    list.sort(comp.getComparator());
                    break;
            }
        }
    }

    // TODO: depois, simular a avaliação toda com o mesmo lance da busca binária, mas dessa vez, se o usuário acertou vai para uma mais difícil, se errou para mais fácil e assim por diante.
    // TODO: comparator com indices misturados (busca binaria).
}
