package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationInfo;
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
        final Map<Phoneme, Integer> mapCounter = new HashMap<>();
        final List<String> wordsRequired = new LinkedList<>();
        if (assessment != null && minimum > 0) {
            System.out.println("-----------------\n"
                    + "Running simulation in assessment with " + assessment.getCases().size()+ " cases with " + comp);

            List<KnownCase> cases = assessment.getCases();
            if (comp != null) {
                cases.sort(comp.getComparator());
            }

            for (KnownCase c : cases) {
                // TODO: ao inves de "fonemas produzidos" depois vai ser preciso pegar de algum gabarito os "fonemas alvo", pois são esses que estão sendo testados.
                // os "fonemas produzidos" aqui precisam ser testados no mínimo 2 vezes para serem considerados "adquiridos" no inv. fonético. [esse é o trabalho da simulação]
                // ou seja, ver qual o impacto que a sequência da avaliação tem sobre o inv. fonético.
                // TODO: adicionar um c.getPhonemesRequired//target. Isso vai ser útil para fazer o PCC-R depois.
                for (Phoneme p : c.getPhonemes()) {
                    // TODO: separar encontros consonantais: bɾ(OCME) -> b(OCME) + ɾ(OCME)
                    int count = 1;
                    if (mapCounter.containsKey(p)) {
                        count = mapCounter.get(p) + 1;
                    }
                    mapCounter.put(p, count);

                    if (count <= minimum) {
                        /**
                         * If this word contains at least one phoneme which wasn't be tested at minimum two times, then
                         * the word is important and will be "required".
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
            /**
             * TODO: todos os fonemas produzidos estão em "mapCounter". Os fonemas testados (alvos) deverão ser
             * calculados a partir dos gabaritos corretos. Aí sim, podemos calcular o PCC-R.
             */
        }
        return new SimulationInfo(mapCounter, wordsRequired);
    }

    // TODO: depois, simular a avaliação toda com o mesmo lance da busca binária, mas dessa vez, se o usuário acertou vai para uma mais difícil, se errou para mais fácil e assim por diante.
    // TODO: comparator com indices misturados (busca binaria).
}
