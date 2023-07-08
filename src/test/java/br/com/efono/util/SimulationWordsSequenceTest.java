package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import br.com.efono.model.Phoneme;
import br.com.efono.model.SimulationInfo;
import static br.com.efono.util.Defaults.SORTED_WORDS;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 28.
 */
public class SimulationWordsSequenceTest {

    /**
     * Number of times that the same phoneme in the same position must be produced to be considered in the phonetic
     * inventory. It's 1 only in tests.
     */
    private final int minimum = 1;

    /**
     * Tests parameters of {@link SimulationWordsSequence#runSimulation(Assessment, KnownCaseComparator, int)}.
     */
    @Test
    public void testRunSimulationParameters() {
        System.out.println("testRunSimulationParameters - all invalid");
        SimulationInfo info = SimulationWordsSequence.runSimulation(null, null, 0);

        assertTrue(info.getMapCounter().isEmpty());
        assertTrue(info.getWordsRequired().isEmpty());

        System.out.println("testRunSimulationParameters - valid minimum");
        info = SimulationWordsSequence.runSimulation(null, null, 1);

        assertTrue(info.getMapCounter().isEmpty());
        assertTrue(info.getWordsRequired().isEmpty());

        // only the comparator can be null, then it'll get the assessment as it is on the database
        System.out.println("testRunSimulationParameters - null comparator");
        KnownCase anel = new KnownCase(SORTED_WORDS[0], "anɛw", true, Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM)));
        info = SimulationWordsSequence.runSimulation(new Assessment(Arrays.asList(anel)), null, 1);

        assertTrue(info.getMapCounter().containsKey(new Phoneme("n", Phoneme.POSITION.OM)));
        assertTrue(info.getWordsRequired().contains(SORTED_WORDS[0]));

        System.out.println("testRunSimulationParameters - empty assessment");
        info = SimulationWordsSequence.runSimulation(new Assessment(), null, 1);

        assertTrue(info.getMapCounter().isEmpty());
        assertTrue(info.getWordsRequired().isEmpty());

        System.out.println("testRunSimulationParameters - null assessment");
        info = SimulationWordsSequence.runSimulation(null, KnownCaseComparator.EasyWordsFirst, 1);

        assertTrue(info.getMapCounter().isEmpty());
        assertTrue(info.getWordsRequired().isEmpty());

        System.out.println("testRunSimulationParameters - invalid minimum");
        info = SimulationWordsSequence.runSimulation(new Assessment(Arrays.asList(anel)),
                KnownCaseComparator.EasyWordsFirst, 0);

        assertTrue(info.getMapCounter().isEmpty());
        assertTrue(info.getWordsRequired().isEmpty());
    }

    /**
     * Tests {@link SimulationWordsSequence#runSimulation(Assessment, KnownCaseComparator, int)}.
     */
    @Test
    public void testRunSimulationEasyFirst() {
        /**
         * The transcription doesn't matter. Only the phonemes.
         */
        KnownCase anel = new KnownCase(SORTED_WORDS[0], "anɛw", true, Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM)));

        KnownCase cabelo = new KnownCase(SORTED_WORDS[16], "kabelu", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM)));

        KnownCase gato = new KnownCase(SORTED_WORDS[21], "gato", true, Arrays.asList(
                new Phoneme("g", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM)));

        KnownCase chinelo = new KnownCase(SORTED_WORDS[33], "ʃinɛlo", true, Arrays.asList(
                new Phoneme("ʃ", Phoneme.POSITION.OI),
                new Phoneme("n", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM)));

        /**
         * 'fakeCase' is harder than 'Cabelo'. But 'fakeCase' has more phonemes than 'Cabelo', so depending on the
         * sequence of the evaluation 'Cabelo' won't be necessary, because 'fakeCase' can test all its phonemes and +1.
         */
        KnownCase fakeCase = new KnownCase(SORTED_WORDS[35], "kajʃə", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM),
                new Phoneme("ɾ", Phoneme.POSITION.OM)));

        Assessment assessment = new Assessment(Arrays.asList(anel, cabelo, gato, chinelo, fakeCase));

        /**
         * The insertion order doesn't matter, it's only to have a visual interpretation.
         */
        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        mapCounterExpected.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        // cabelo
        mapCounterExpected.put(new Phoneme("k", Phoneme.POSITION.OI), 2);
        mapCounterExpected.put(new Phoneme("b", Phoneme.POSITION.OM), 2);
        mapCounterExpected.put(new Phoneme("l", Phoneme.POSITION.OM), 3);
        // gato
        mapCounterExpected.put(new Phoneme("g", Phoneme.POSITION.OI), 1);
        mapCounterExpected.put(new Phoneme("t", Phoneme.POSITION.OM), 1);
        // chinelo
        mapCounterExpected.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 1);
        // fakeCase
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OM), 1);

        /**
         * The insertion order doesn't matter, it's only to have a visual interpretation.
         */
        final List<String> wordsRequiredExpected = Arrays.asList("Anel", "Cabelo", "Gato", "Chinelo", SORTED_WORDS[35]);

        KnownCaseComparator comp = KnownCaseComparator.EasyWordsFirst;
        SimulationInfo expected = new SimulationInfo(mapCounterExpected, wordsRequiredExpected, assessment, comp,
                SimulationWordsSequence.SPLIT_CONSONANTS);
        SimulationInfo result = SimulationWordsSequence.runSimulation(assessment, comp,
                minimum);

        assertEquals(expected, result);
    }

    /**
     * Tests {@link SimulationWordsSequence#runSimulation(Assessment, KnownCaseComparator, int)}.
     */
    @Test
    public void testRunSimulationHardFirst() {
        /**
         * The transcription doesn't matter. Only the phonemes.
         */
        KnownCase anel = new KnownCase(SORTED_WORDS[0], "anɛw", true, Arrays.asList(
                new Phoneme("n", Phoneme.POSITION.OM)));

        KnownCase cabelo = new KnownCase(SORTED_WORDS[16], "kabelu", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM)));

        KnownCase gato = new KnownCase(SORTED_WORDS[21], "gato", true, Arrays.asList(
                new Phoneme("g", Phoneme.POSITION.OI),
                new Phoneme("t", Phoneme.POSITION.OM)));

        KnownCase chinelo = new KnownCase(SORTED_WORDS[33], "ʃinɛlo", true, Arrays.asList(
                new Phoneme("ʃ", Phoneme.POSITION.OI),
                new Phoneme("n", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM)));

        /**
         * 'fakeCase' is harder than 'Cabelo'. But 'fakeCase' has more phonemes than 'Cabelo', so depending on the
         * sequence of the evaluation 'Cabelo' won't be necessary, because 'fakeCase' can test all its phonemes and +1.
         */
        KnownCase fakeCase = new KnownCase(SORTED_WORDS[35], "kajʃə", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("b", Phoneme.POSITION.OM),
                new Phoneme("l", Phoneme.POSITION.OM),
                new Phoneme("ɾ", Phoneme.POSITION.OM)));

        Assessment assessment = new Assessment(Arrays.asList(anel, cabelo, gato, chinelo, fakeCase));

        /**
         * The insertion order doesn't matter, it's only to have a visual interpretation.
         */
        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        // fakeCase
        mapCounterExpected.put(new Phoneme("k", Phoneme.POSITION.OI), 2);
        mapCounterExpected.put(new Phoneme("b", Phoneme.POSITION.OM), 2);
        mapCounterExpected.put(new Phoneme("l", Phoneme.POSITION.OM), 3);
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OM), 1);
        // chinelo
        mapCounterExpected.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 1);
        mapCounterExpected.put(new Phoneme("n", Phoneme.POSITION.OM), 2);
        // gato
        mapCounterExpected.put(new Phoneme("g", Phoneme.POSITION.OI), 1);
        mapCounterExpected.put(new Phoneme("t", Phoneme.POSITION.OM), 1);
        // cabelo: no new phonemes to be tested
        // anel: no new phonemes to be tested

        /**
         * The insertion order doesn't matter, it's only to have a visual interpretation.
         */
        final List<String> wordsRequiredExpected = Arrays.asList(SORTED_WORDS[35], "Chinelo", "Gato");

        KnownCaseComparator comp = KnownCaseComparator.HardWordsFirst;
        SimulationInfo expected = new SimulationInfo(mapCounterExpected, wordsRequiredExpected, assessment, comp,
                SimulationWordsSequence.SPLIT_CONSONANTS);
        SimulationInfo result = SimulationWordsSequence.runSimulation(assessment, comp,
                minimum, true);

        assertEquals(expected, result);
    }

    /**
     * Tests {@link SimulationWordsSequence#runSimulation(Assessment, KnownCaseComparator, int)} for consonant clusters.
     */
    @Test
    public void testRunSimulationConsonantClusters() {
        System.out.println("testRunSimulationConsonantClusters - with consonant clusters");
        KnownCase cobra = new KnownCase(SORTED_WORDS[52], "kɔbɾə", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("bɾ", Phoneme.POSITION.OCME)));

        KnownCase fakeCase = new KnownCase(SORTED_WORDS[55], "kɔbɾə", true, Arrays.asList(
                new Phoneme("k", Phoneme.POSITION.OI),
                new Phoneme("bɾ", Phoneme.POSITION.OCME)));

        KnownCase chifre = new KnownCase(SORTED_WORDS[65], "ʃifɾis", true, Arrays.asList(
                new Phoneme("ʃ", Phoneme.POSITION.OI),
                new Phoneme("fɾ", Phoneme.POSITION.OCME),
                new Phoneme("s", Phoneme.POSITION.CF)));
        final Assessment assessment = new Assessment(Arrays.asList(cobra, fakeCase, chifre));

        final Map<Phoneme, Integer> mapCounterExpected = new HashMap();
        // cobra: bɾ(OCME) -> b(OCME) + ɾ(OCME)
        mapCounterExpected.put(new Phoneme("k", Phoneme.POSITION.OI), 2);
        mapCounterExpected.put(new Phoneme("b", Phoneme.POSITION.OCME), 2);
        mapCounterExpected.put(new Phoneme("ɾ", Phoneme.POSITION.OCME), 3);
        // fakeCase: not needed
        // chifre: fɾ(OCME) -> f(OCME) + ɾ(OCME)
        mapCounterExpected.put(new Phoneme("ʃ", Phoneme.POSITION.OI), 1);
        mapCounterExpected.put(new Phoneme("f", Phoneme.POSITION.OCME), 1);
        mapCounterExpected.put(new Phoneme("s", Phoneme.POSITION.CF), 1);

        /**
         * The insertion order doesn't matter, it's only to have a visual interpretation.
         */
        final List<String> wordsRequiredExpected = Arrays.asList(SORTED_WORDS[52], SORTED_WORDS[65]);

        KnownCaseComparator comp = null;
        SimulationInfo expected = new SimulationInfo(mapCounterExpected, wordsRequiredExpected, assessment, comp,
                SimulationWordsSequence.SPLIT_CONSONANTS);
        SimulationInfo result = SimulationWordsSequence.runSimulation(assessment, comp, minimum);

        assertEquals(expected, result);
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)}.
     */
    @Test
    public void testSortList() {
        /**
         * Empty representation. It doesn't matter here.
         */
        KnownCase faca = new KnownCase("Faca", "", true);
        KnownCase dedo = new KnownCase("Dedo", "", true);
        KnownCase travesseiro = new KnownCase("Travesseiro", "", true);
        KnownCase terra = new KnownCase("Terra", "", true);
        KnownCase sapo = new KnownCase("Sapo", "", true);

        List<KnownCase> list = Arrays.asList(faca, dedo, travesseiro, terra, sapo);

        System.out.println("testSortList - EasyWordsFirst");
        SimulationWordsSequence.sortList(list, KnownCaseComparator.EasyWordsFirst);
        KnownCase[] expected = new KnownCase[]{dedo, terra, faca, sapo, travesseiro};
        for (int i = 0; i < list.size(); i++) {
            assertEquals(expected[i], list.get(i));
        }

        System.out.println("testSortList - HardWordsFirst");
        SimulationWordsSequence.sortList(list, KnownCaseComparator.HardWordsFirst);
        expected = new KnownCase[]{travesseiro, sapo, faca, terra, dedo};
        for (int i = 0; i < list.size(); i++) {
            assertEquals(expected[i], list.get(i));
        }

        System.out.println("testSortList - EasyHardWords");
        list = Arrays.asList(faca, dedo, travesseiro, terra, sapo);
        SimulationWordsSequence.sortList(list, KnownCaseComparator.EasyHardWords);
        expected = new KnownCase[]{dedo, travesseiro, terra, sapo, faca};
        for (int i = 0; i < list.size(); i++) {
            assertEquals("Failed in " + i, expected[i], list.get(i));
        }

        System.out.println("testSortList - EasyHardWords - with repeated words");
        KnownCase otherDedo = new KnownCase(dedo);
        list = Arrays.asList(faca, dedo, travesseiro, terra, otherDedo, sapo);
        SimulationWordsSequence.sortList(list, KnownCaseComparator.EasyHardWords);
        expected = new KnownCase[]{dedo, otherDedo, travesseiro, terra, sapo, faca};
        for (int i = 0; i < list.size(); i++) {
            assertEquals("Failed in " + i, expected[i], list.get(i));
        }
    }

    /**
     * Tests {@link SimulationWordsSequence#sortList(List, KnownCaseComparator)} with
     * {@link KnownCaseComparator#DecisionTre}.
     */
    @Test
    public void testSortListDecisionTree() {
        System.out.println("testSortListDecisionTree");
        
        final Assessment assessment = getTestAssessment();
        
        
        
    }

    /**
     * @return An assessment with real cases and all correct.
     */
    private Assessment getTestAssessment() {
        KnownCase anel = new KnownCase("Anel", "[a’nɛw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase barriga = new KnownCase("Barriga", "[ba’χigə]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM), new Phoneme("g", Phoneme.POSITION.OM)));
        KnownCase batom = new KnownCase("Batom", "[ba’tõw]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase bebê = new KnownCase("Bebê", "[be’be]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));
        KnownCase beijo = new KnownCase("Beijo", "[’beʒo]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("ʒ", Phoneme.POSITION.OM)));
        KnownCase biblioteca = new KnownCase("Biblioteca", "[biblio’tɛkə]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("bl", Phoneme.POSITION.OCME), new Phoneme("t", Phoneme.POSITION.OM), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase bicicleta = new KnownCase("Bicicleta", "[bisi’klɛtə]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("s", Phoneme.POSITION.OM), new Phoneme("kl", Phoneme.POSITION.OCME), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase bolsa = new KnownCase("Bolsa", "[’bowsə]", true, Arrays.asList(new Phoneme("b", Phoneme.POSITION.OI), new Phoneme("s", Phoneme.POSITION.OM)));
        KnownCase brinco = new KnownCase("Brinco", "[’bɾĩnko]", true, Arrays.asList(new Phoneme("bɾ", Phoneme.POSITION.OCI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase bruxa = new KnownCase("Bruxa", "[’bɾuʃə]", true, Arrays.asList(new Phoneme("bɾ", Phoneme.POSITION.OCI), new Phoneme("ʃ", Phoneme.POSITION.OM)));
        KnownCase cabelo = new KnownCase("Cabelo", "[ka’belu]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM), new Phoneme("l", Phoneme.POSITION.OM)));
        KnownCase cachorro = new KnownCase("Cachorro", "[ka’ʃoχo]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("ʃ", Phoneme.POSITION.OM), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase caixa = new KnownCase("Caixa", "[’kaʃə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("ʃ", Phoneme.POSITION.OM)));
        KnownCase calça = new KnownCase("Calça", "[’kawsə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("s", Phoneme.POSITION.OM)));
        KnownCase cama = new KnownCase("Cama", "[’kəmə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase caminhão = new KnownCase("Caminhão", "[kami’ɲəw]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("m", Phoneme.POSITION.OM), new Phoneme("ɲ", Phoneme.POSITION.OM)));
        KnownCase casa = new KnownCase("Casa", "[‘kazə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("z", Phoneme.POSITION.OM)));
        KnownCase cavalo = new KnownCase("Cavalo", "[ka’valu]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM), new Phoneme("l", Phoneme.POSITION.OM)));
        KnownCase chapéu = new KnownCase("Chapéu", "[ʃa’pɛw]", true, Arrays.asList(new Phoneme("ʃ", Phoneme.POSITION.OI), new Phoneme("p", Phoneme.POSITION.OM)));
        KnownCase chiclete = new KnownCase("Chiclete", "[ʃi’klƐte]", true, Arrays.asList(new Phoneme("ʃ", Phoneme.POSITION.OI), new Phoneme("kl", Phoneme.POSITION.OCME), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase chifre = new KnownCase("Chifre", "[’ʃifɾi]", true, Arrays.asList(new Phoneme("ʃ", Phoneme.POSITION.OI), new Phoneme("fɾ", Phoneme.POSITION.OCME)));
        KnownCase chinelo = new KnownCase("Chinelo", "[ʃi’nɛlu]", true, Arrays.asList(new Phoneme("ʃ", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("l", Phoneme.POSITION.OM)));
        KnownCase cobra = new KnownCase("Cobra", "[’kɔbɾə]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("bɾ", Phoneme.POSITION.OCME)));
        KnownCase coelho = new KnownCase("Coelho", "[ko’eʎo]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("ʎ", Phoneme.POSITION.OM)));
        KnownCase colher = new KnownCase("Colher", "[ko’ʎɛɾ]", true, Arrays.asList(new Phoneme("k", Phoneme.POSITION.OI), new Phoneme("ʎ", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.CF)));
        KnownCase cruz = new KnownCase("Cruz", "[’kɾus]", true, Arrays.asList(new Phoneme("kɾ", Phoneme.POSITION.OCI), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase dado = new KnownCase("Dado", "[’dadu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dedo = new KnownCase("Dedo", "[’dedu]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase dente = new KnownCase("Dente", "[’dẽnʧi]", true, Arrays.asList(new Phoneme("d", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase dragão = new KnownCase("Dragão", "[dɾa’gəw]", true, Arrays.asList(new Phoneme("dɾ", Phoneme.POSITION.OCI), new Phoneme("g", Phoneme.POSITION.OM)));
        KnownCase escrever = new KnownCase("Escrever", "[eskɾe’ve]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("kɾ", Phoneme.POSITION.OCME), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase espelho = new KnownCase("Espelho", "[is’peʎo]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("p", Phoneme.POSITION.OM), new Phoneme("ʎ", Phoneme.POSITION.OM)));
        KnownCase estrela = new KnownCase("Estrela", "[is’tɾelə]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("tɾ", Phoneme.POSITION.OCME), new Phoneme("l", Phoneme.POSITION.OM)));
        KnownCase faca = new KnownCase("Faca", "[’fakə]", true, Arrays.asList(new Phoneme("f", Phoneme.POSITION.OI), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase flor = new KnownCase("Flor", "['floɾ]", true, Arrays.asList(new Phoneme("fl", Phoneme.POSITION.OCI), new Phoneme("ɾ", Phoneme.POSITION.CF)));
        KnownCase floresta = new KnownCase("Floresta", "[flo’ɾɛstə]", true, Arrays.asList(new Phoneme("fl", Phoneme.POSITION.OCI), new Phoneme("ɾ", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase fogo = new KnownCase("Fogo", "[’fogo]", true, Arrays.asList(new Phoneme("f", Phoneme.POSITION.OI), new Phoneme("g", Phoneme.POSITION.OM)));
        KnownCase folha = new KnownCase("Folha", "[‘foʎə]", true, Arrays.asList(new Phoneme("f", Phoneme.POSITION.OI), new Phoneme("ʎ", Phoneme.POSITION.OM)));
        KnownCase fralda = new KnownCase("Fralda", "[’fɾawdə]", true, Arrays.asList(new Phoneme("fɾ", Phoneme.POSITION.OCI), new Phoneme("d", Phoneme.POSITION.OM)));
        KnownCase fruta = new KnownCase("Fruta", "[’fɾutəs]", true, Arrays.asList(new Phoneme("fɾ", Phoneme.POSITION.OCI), new Phoneme("t", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase galinha = new KnownCase("Galinha", "[ga’liɲə]", true, Arrays.asList(new Phoneme("g", Phoneme.POSITION.OI), new Phoneme("l", Phoneme.POSITION.OM), new Phoneme("ɲ", Phoneme.POSITION.OM)));
        KnownCase garfo = new KnownCase("Garfo", "[’gaɾfu]", true, Arrays.asList(new Phoneme("g", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.CM), new Phoneme("f", Phoneme.POSITION.OM)));
        KnownCase gato = new KnownCase("Gato", "[’gatu]", true, Arrays.asList(new Phoneme("g", Phoneme.POSITION.OI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase girafa = new KnownCase("Girafa", "[ʒi’ɾafə]", true, Arrays.asList(new Phoneme("ʒ", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.OM), new Phoneme("f", Phoneme.POSITION.OM)));
        KnownCase grama = new KnownCase("Grama", "[’gɾəmə]", true, Arrays.asList(new Phoneme("gɾ", Phoneme.POSITION.OCI), new Phoneme("m", Phoneme.POSITION.OM)));
        KnownCase gritar = new KnownCase("Gritar", "[gɾi’ta]", true, Arrays.asList(new Phoneme("gɾ", Phoneme.POSITION.OCI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase igreja = new KnownCase("Igreja", "[i’gɾeʒə]", true, Arrays.asList(new Phoneme("gɾ", Phoneme.POSITION.OCME), new Phoneme("ʒ", Phoneme.POSITION.OM)));
        KnownCase jacaré = new KnownCase("Jacaré", "[ʒaka’ɾɛ]", true, Arrays.asList(new Phoneme("ʒ", Phoneme.POSITION.OI), new Phoneme("k", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.OM)));
        KnownCase jornal = new KnownCase("Jornal", "[ʒoɾ’naw]", true, Arrays.asList(new Phoneme("ʒ", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.CM), new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase letra = new KnownCase("Letra", "[’letɾəs]", true, Arrays.asList(new Phoneme("l", Phoneme.POSITION.OI), new Phoneme("tɾ", Phoneme.POSITION.OCME), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase livro = new KnownCase("Livro", "[’livɾo]", true, Arrays.asList(new Phoneme("l", Phoneme.POSITION.OI), new Phoneme("vɾ", Phoneme.POSITION.OCME)));
        KnownCase lápis = new KnownCase("Lápis", "[’lapis]", true, Arrays.asList(new Phoneme("l", Phoneme.POSITION.OI), new Phoneme("p", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase língua = new KnownCase("Língua", "[’lĩngʷa]", true, Arrays.asList(new Phoneme("l", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("gʷ", Phoneme.POSITION.OM)));
        KnownCase magro = new KnownCase("Magro", "[’magɾu]", true, Arrays.asList(new Phoneme("m", Phoneme.POSITION.OI), new Phoneme("gɾ", Phoneme.POSITION.OCME)));
        KnownCase mesa = new KnownCase("Mesa", "[’mezə]", true, Arrays.asList(new Phoneme("m", Phoneme.POSITION.OI), new Phoneme("z", Phoneme.POSITION.OM)));
        KnownCase microfone = new KnownCase("Microfone", "[mikɾo’foni]", true, Arrays.asList(new Phoneme("m", Phoneme.POSITION.OI), new Phoneme("kɾ", Phoneme.POSITION.OCME), new Phoneme("f", Phoneme.POSITION.OM), new Phoneme("n", Phoneme.POSITION.OM)));
        KnownCase nariz = new KnownCase("Nariz", "[na’ɾis]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase navio = new KnownCase("Navio", "[na’viw]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM)));
        KnownCase nuvem = new KnownCase("Nuvem", "[’nuvẽj̃s]", true, Arrays.asList(new Phoneme("n", Phoneme.POSITION.OI), new Phoneme("v", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase passarinho = new KnownCase("Passarinho", "[pasa’ɾiɲo]", true, Arrays.asList(new Phoneme("p", Phoneme.POSITION.OI), new Phoneme("s", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.OM), new Phoneme("ɲ", Phoneme.POSITION.OM)));
        KnownCase pastel = new KnownCase("Pastel", "[pas’tɛw]", true, Arrays.asList(new Phoneme("p", Phoneme.POSITION.OI), new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase pedra = new KnownCase("Pedra", "[’pɛdɾə]", true, Arrays.asList(new Phoneme("p", Phoneme.POSITION.OI), new Phoneme("dɾ", Phoneme.POSITION.OCME)));
        KnownCase placa = new KnownCase("Placa", "[’plakə]", true, Arrays.asList(new Phoneme("pl", Phoneme.POSITION.OCI), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase plástico = new KnownCase("Plástico", "[’plasʧiko]", true, Arrays.asList(new Phoneme("pl", Phoneme.POSITION.OCI), new Phoneme("s", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase porta = new KnownCase("Porta", "[’pɔɾtə]", true, Arrays.asList(new Phoneme("p", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.CM), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase prato = new KnownCase("Prato", "[’pɾato]", true, Arrays.asList(new Phoneme("pɾ", Phoneme.POSITION.OCI), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase presente = new KnownCase("Presente", "[pɾe’zẽnʧi]", true, Arrays.asList(new Phoneme("pɾ", Phoneme.POSITION.OCI), new Phoneme("z", Phoneme.POSITION.OM), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM)));
        KnownCase rabo = new KnownCase("Rabo", "[’χabu]", true, Arrays.asList(new Phoneme("χ", Phoneme.POSITION.OI), new Phoneme("b", Phoneme.POSITION.OM)));
        KnownCase refri = new KnownCase("Refri", "[χe’fɾi]", true, Arrays.asList(new Phoneme("χ", Phoneme.POSITION.OI), new Phoneme("fɾ", Phoneme.POSITION.OCME)));
        KnownCase relógio = new KnownCase("Relógio", "[χe’lɔʒu]", true, Arrays.asList(new Phoneme("χ", Phoneme.POSITION.OI), new Phoneme("l", Phoneme.POSITION.OM), new Phoneme("ʒ", Phoneme.POSITION.OM)));
        KnownCase sapato = new KnownCase("Sapato", "[sa’pato]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.OI), new Phoneme("p", Phoneme.POSITION.OM), new Phoneme("t", Phoneme.POSITION.OM)));
        KnownCase sapo = new KnownCase("Sapo", "[’sapu]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.OI), new Phoneme("p", Phoneme.POSITION.OM)));
        KnownCase sofá = new KnownCase("Sofá", "[so’fa]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.OI), new Phoneme("f", Phoneme.POSITION.OM)));
        KnownCase soprar = new KnownCase("Soprar", "[so’pɾaɾ]", true, Arrays.asList(new Phoneme("s", Phoneme.POSITION.OI), new Phoneme("pɾ", Phoneme.POSITION.OCME), new Phoneme("ɾ", Phoneme.POSITION.CF)));
        KnownCase terra = new KnownCase("Terra", "[’tɛχə]", true, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("χ", Phoneme.POSITION.OM)));
        KnownCase tesoura = new KnownCase("Tesoura", "[ʧi’zoɾə]", true, Arrays.asList(new Phoneme("ʧ", Phoneme.POSITION.OI), new Phoneme("z", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.OM)));
        KnownCase travesseiro = new KnownCase("Travesseiro", "[tɾave’seɾo]", true, Arrays.asList(new Phoneme("tɾ", Phoneme.POSITION.OCI), new Phoneme("v", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.OM)));
        KnownCase trem = new KnownCase("Trem", "[’tɾẽj̃]", true, Arrays.asList(new Phoneme("tɾ", Phoneme.POSITION.OCI)));
        KnownCase tênis = new KnownCase("Tênis", "[’tenis]", true, Arrays.asList(new Phoneme("t", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.OM), new Phoneme("s", Phoneme.POSITION.CF)));
        KnownCase vaca = new KnownCase("Vaca", "[’vakə]", true, Arrays.asList(new Phoneme("v", Phoneme.POSITION.OI), new Phoneme("k", Phoneme.POSITION.OM)));
        KnownCase ventilador = new KnownCase("Ventilador", "[vẽnʧila’doɾ]", true, Arrays.asList(new Phoneme("v", Phoneme.POSITION.OI), new Phoneme("n", Phoneme.POSITION.CM), new Phoneme("ʧ", Phoneme.POSITION.OM), new Phoneme("l", Phoneme.POSITION.OM), new Phoneme("d", Phoneme.POSITION.OM), new Phoneme("ɾ", Phoneme.POSITION.CF)));
        KnownCase vidro = new KnownCase("Vidro", "[vi’dɾu]", true, Arrays.asList(new Phoneme("v", Phoneme.POSITION.OI), new Phoneme("dɾ", Phoneme.POSITION.OCME)));
        KnownCase zebra = new KnownCase("Zebra", "[’zebɾə]", true, Arrays.asList(new Phoneme("z", Phoneme.POSITION.OI), new Phoneme("bɾ", Phoneme.POSITION.OCME)));
        KnownCase zero = new KnownCase("Zero", "[’zɛɾu]", true, Arrays.asList(new Phoneme("z", Phoneme.POSITION.OI), new Phoneme("ɾ", Phoneme.POSITION.OM)));
        Assessment test = new Assessment(Arrays.asList(anel, barriga, batom, bebê, beijo, biblioteca, bicicleta, bolsa, brinco, bruxa, cabelo, cachorro, caixa, calça, cama, caminhão, casa, cavalo, chapéu, chiclete, chifre, chinelo, cobra, coelho, colher, cruz, dado, dedo, dente, dragão, escrever, espelho, estrela, faca, flor, floresta, fogo, folha, fralda, fruta, galinha, garfo, gato, girafa, grama, gritar, igreja, jacaré, jornal, letra, livro, lápis, língua, magro, mesa, microfone, nariz, navio, nuvem, passarinho, pastel, pedra, placa, plástico, porta, prato, presente, rabo, refri, relógio, sapato, sapo, sofá, soprar, terra, tesoura, travesseiro, trem, tênis, vaca, ventilador, vidro, zebra, zero));

        return test;
    }

}
