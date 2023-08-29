package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationConsonantClustersInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Ago 28.
 */
public class SimulationConsonantClustersTest {

    /**
     * Executes before each method.
     */
    @Before
    public void beforeMethod() {
        Defaults.TARGET_PHONEMES.clear();
    }

    /**
     * Tests {@link SimulationConsonantClusters#run(Assessment, KnownCaseComparator)}.
     */
    @Test
    public void testRun() {
        System.out.println("testRun");

        Defaults.TARGET_PHONEMES.put("Brinco", Arrays.asList(
                new Phoneme("bɾ", Phoneme.POSITION.OCI),
                new Phoneme("n", Phoneme.POSITION.CM),
                new Phoneme("k", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Vaca", Arrays.asList(
                new Phoneme("vl", Phoneme.POSITION.OCI),
                new Phoneme("k", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Prato", Arrays.asList(
                new Phoneme("pɾ", Phoneme.POSITION.OCI),
                new Phoneme("t", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Placa", Arrays.asList(
                new Phoneme("pl", Phoneme.POSITION.OCI),
                new Phoneme("k", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Fruta", Arrays.asList(
                new Phoneme("fɾ", Phoneme.POSITION.OCI),
                new Phoneme("t", Phoneme.POSITION.OM)));

        Defaults.TARGET_PHONEMES.put("Flor", Arrays.asList(
                new Phoneme("fl", Phoneme.POSITION.OCI),
                new Phoneme("ɾ", Phoneme.POSITION.CM)));

        Defaults.TARGET_PHONEMES.put("Biblioteca", Arrays.asList(
                new Phoneme("b", Phoneme.POSITION.OI),
                new Phoneme("bl", Phoneme.POSITION.OCME),
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("k", Phoneme.POSITION.OM)));

        List<KnownCase> cases = new ArrayList<>();
        cases.add(new KnownCase("Vaca", "vlaka", false, Arrays.asList(
                new Phoneme("vl", Phoneme.POSITION.OCI),
                new Phoneme("k", Phoneme.POSITION.OM))));

        cases.add(new KnownCase("Brinco", "bɾĩko", false, Arrays.asList(
                new Phoneme("bɾ", Phoneme.POSITION.OCI),
                new Phoneme("k", Phoneme.POSITION.OM))));
        // aqui já consigo inferir o "bl(OCI)"

        cases.add(new KnownCase("Prato", "pɾato", true, Arrays.asList(
                new Phoneme("pɾ", Phoneme.POSITION.OCI),
                new Phoneme("t", Phoneme.POSITION.OM))));

        // aqui já consigo inferir o "pl(OCI)"
        cases.add(new KnownCase("Placa", "plakə", true, Arrays.asList(
                new Phoneme("pl", Phoneme.POSITION.OCI),
                new Phoneme("k", Phoneme.POSITION.OM))));

        /////////////////
        cases.add(new KnownCase("Flor", "floɾ", true, Arrays.asList(
                new Phoneme("fl", Phoneme.POSITION.OCI),
                new Phoneme("ɾ", Phoneme.POSITION.CF))));
        // até aqui eu consigo inferir que ele consegue produzir o fɾ

        cases.add(new KnownCase("Fruta", "fluta", false, Arrays.asList(
                new Phoneme("fl", Phoneme.POSITION.OCI),
                new Phoneme("t", Phoneme.POSITION.OM)))); // e aqui eu vejo que não conseguiu o "fɾ"
        /////////////////

        cases.add(new KnownCase("Biblioteca", "bliotɛka", false, Arrays.asList(
                new Phoneme("bl", Phoneme.POSITION.OCI), // is not in target words in this position but can reproduce
                new Phoneme("t", Phoneme.POSITION.OM),
                new Phoneme("k", Phoneme.POSITION.OM))));

        // fonemas inferidos
        List<Phoneme> expected = Arrays.asList(
                new Phoneme("bl", Phoneme.POSITION.OCI), // is not in target words, but it is in the assessment: can reproduce
                new Phoneme("vɾ", Phoneme.POSITION.OCI), // is not in target words and not even in the assessment: can't validate if she can reproduce or not
                new Phoneme("pl", Phoneme.POSITION.OCI),
                new Phoneme("fɾ", Phoneme.POSITION.OCI)); // keep this here

        Assessment assessment = new Assessment(cases);

        SimulationConsonantClustersInfo info = SimulationConsonantClusters.run(
                assessment, KnownCaseComparator.EasyWordsFirst);
        List<Phoneme> inferredPhonemes = info.getInferredPhonemes();

        assertEquals(expected.size(), inferredPhonemes.size());
        assertTrue(expected.containsAll(inferredPhonemes));

        System.out.println("testRun - inferred phonemes that are in target words");
        List<Phoneme> expectedInfInTargetWords = Arrays.asList(
                new Phoneme("pl", Phoneme.POSITION.OCI),
                new Phoneme("fɾ", Phoneme.POSITION.OCI));

        List<Phoneme> inferredPhonemesinTargetWords = info.getInferredPhonemesInTargetWords();
        assertEquals(expectedInfInTargetWords.size(), inferredPhonemesinTargetWords.size());
        assertTrue(expectedInfInTargetWords.containsAll(inferredPhonemesinTargetWords));

        System.out.println("testRun - consonant clusters in assessment");
        List<Phoneme> expectedClustersInAssessment = Arrays.asList(
                new Phoneme("vl", Phoneme.POSITION.OCI),
                new Phoneme("bɾ", Phoneme.POSITION.OCI),
                new Phoneme("pɾ", Phoneme.POSITION.OCI),
                new Phoneme("pl", Phoneme.POSITION.OCI),
                new Phoneme("fl", Phoneme.POSITION.OCI),
                new Phoneme("bl", Phoneme.POSITION.OCI));
        List<Phoneme> resultClustersInAssessment = info.getAllClustersInAssessment();
        assertEquals(expectedClustersInAssessment.size(), resultClustersInAssessment.size());
        assertTrue(expectedClustersInAssessment.containsAll(resultClustersInAssessment));

        // aqui eu testo a precisão do meu método
        System.out.println("testRun - valid inferred phonemes");
        List<Phoneme> expectedValidInferredPhonemes = Arrays.asList(
                new Phoneme("bl", Phoneme.POSITION.OCI), // is not in target words but the child could reproduce as well
                new Phoneme("pl", Phoneme.POSITION.OCI));
        // "fɾ" was inferred but the child couldn't reproduce

        List<Phoneme> resultValidInferred = info.getValidInferred();
        assertEquals(expectedValidInferredPhonemes.size(), resultValidInferred.size());
        assertTrue(expectedValidInferredPhonemes.containsAll(resultValidInferred));

        // dos X fonemas inferidos Y foram reproduzidos na avaliação. porém, dos (X-Y) fonemas Z estavam presentes nos fonemas
        // alvo das palavras. Ou seja, não temos como saber se a criança não os produziu por não ser capaz de produzí-los ou
        // simplesmente porque ela não foi estimulada a produzí-los via palavras-alvo.
        System.out.println("testRun - inferred not reproduced but are in target words");
        /**
         * We inferred that the child would be capable of reproduce this phoneme, and the assessment offers this
         * possibility with a target words that contains this consonant cluster. Besides that, she wasn't capable of
         * reproduce this phoneme, so the inference was invalid.
         */
        List<Phoneme> expectedInferredNotReproducedInTargetWords = Arrays.asList(
                new Phoneme("fɾ", Phoneme.POSITION.OCI));

        List<Phoneme> resultInferredNotReproducedInTargetWords = info.getInferredNotReproducedInTargetWords();
        assertEquals(expectedInferredNotReproducedInTargetWords.size(), resultInferredNotReproducedInTargetWords.size());
        assertTrue(expectedInferredNotReproducedInTargetWords.containsAll(resultInferredNotReproducedInTargetWords));

        System.out.println("testRun - inferred not reproduced but are not in target words");
        /**
         * We inferred that the child would be capable of reproduce this phoneme, but she wasn't. Besides that, we are
         * not capable of invalidated the inference, because we can't known if the wasn't capable of reproduce it
         * because she really can't or because the assessment doesn't offer this possibility, by not containing a target
         * words with the phoneme inferred.
         */
        List<Phoneme> expectedInferredNotReproducedNotInTargetWords = Arrays.asList(
                new Phoneme("vɾ", Phoneme.POSITION.OCI) // is not in target words and not even in the assessment: can't validate if she can reproduce or not
        );

        List<Phoneme> resultInferredNotReproducedNotInTargetWords = info.getInferredNotReproducedNotInTargetWords();
        assertEquals(expectedInferredNotReproducedNotInTargetWords.size(), resultInferredNotReproducedNotInTargetWords.size());
        assertTrue(expectedInferredNotReproducedNotInTargetWords.containsAll(resultInferredNotReproducedNotInTargetWords));
    }

}
