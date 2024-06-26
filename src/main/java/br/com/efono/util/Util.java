package br.com.efono.util;

import br.com.efono.model.Assessment;
import br.com.efono.model.KnownCase;
import br.com.efono.model.Phoneme;
import static br.com.efono.model.Phoneme.CONSONANT_CLUSTERS;
import static br.com.efono.model.Phoneme.SEMI_VOWELS;
import static br.com.efono.model.Phoneme.VOWELS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, May 28.
 */
public class Util {

    /**
     * Special characters found on transcriptions.
     */
    public static final String[] SPECIAL = new String[]{"\\[", "\\'", "\"", "\\]", "\\.", "\\'", "\\‘", "\\’", "\\ʼ",
        "\\´", "\\ø"};

    /**
     * Map of equivalent phonemes. The key phoneme should be replaced by the value when found in a transcription.
     * Usually, this happens when there is a mistake from the user at the time of doing the transcription, and they can
     * insert the 'key' phoneme instead of the 'value'.
     */
    public static final Map<String, String> EQUIVALENT_PHONEMES = new HashMap<>();

    static {
        EQUIVALENT_PHONEMES.put("nh", "ɲ");
    }

    /**
     * Read all the transcriptions from file. Each line must contains a single transcription.
     *
     * @param file Given file.
     * @return All the read lines.
     */
    public static List<String> readTranscriptions(final File file) {
        System.out.println("Reading transcriptions from file: " + file);
        final List<String> list = new ArrayList<>();
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                // each line has a transcription from database
                String line = reader.readLine();

                while (line != null) {
                    if (!line.trim().isEmpty()) {
                        list.add(StringEscapeUtils.unescapeJava(line));
                    }

                    line = reader.readLine();
                }
                System.out.println("Finish. Lines read: " + list.size());
            } catch (final FileNotFoundException ex) {
                // TODO: substituir por sistema de logs
                System.out.println("File " + file + " not found: " + ex);
            } catch (IOException ex) {
                System.out.println("Couldn't read file " + file + ": " + ex);
            }
        }

        return list;
    }

    /**
     * Clean transcription and removes anything that doesn't represent a phoneme.
     *
     * @param transcription The given transcription.
     * @return The transcription containing only characters representing a phoneme.
     */
    public static String cleanTranscription(final String transcription) {
        String clean = StringEscapeUtils.unescapeJava(transcription);
        if (clean != null) {
            // remove double spaces
            clean = clean.trim().replaceAll(" +", "");

            // remove special characters
            for (String special : SPECIAL) {
                clean = clean.replaceAll(special, "");
            }

            Iterator<Map.Entry<String, String>> it = EQUIVALENT_PHONEMES.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> next = it.next();
                clean = clean.replaceAll(next.getKey(), next.getValue());
            }

            return clean;
        }
        return "";
    }

    /**
     * Gets the consonant phoneme at initial onset from the given string.
     *
     * @param transcription The given transcription.
     * @return The consonant phoneme at Initial Onset or {@code null} if it doesn't exist.
     */
    public static Phoneme getInitialOnset(final String transcription) {
        String clean = cleanTranscription(transcription);
        if (!clean.isEmpty()) {
            // if the first letter is a vowel, then Initial Onset doesn't exists here.
            if (!Arrays.asList(VOWELS).contains(clean.substring(0, 1))) {
                if (Arrays.asList(CONSONANT_CLUSTERS).contains(clean.substring(0, 2))) {
                    return new Phoneme(clean.substring(0, 2), Phoneme.POSITION.OCI);
                }
                return new Phoneme(clean.substring(0, 1), Phoneme.POSITION.OI);
            }
        }
        return null;
    }

    /**
     * Gets the consonant phoneme at Final Coda from the given string.
     *
     * @param transcription The given transcription.
     * @return The consonant phoneme at Final Coda or {@code null} if it doesn't exist.
     */
    public static Phoneme getFinalCoda(final String transcription) {
        String clean = cleanTranscription(transcription);

        List<String> vowelsAndSV = new ArrayList<>();
        vowelsAndSV.addAll(Arrays.asList(SEMI_VOWELS));
        vowelsAndSV.addAll(Arrays.asList(VOWELS));

        for (String v : vowelsAndSV) {
            if (clean.endsWith(v)) {
                return null;
            }
        }

        for (String c : Phoneme.SPECIAL_CONSONANTS) {
            if (clean.endsWith(c)) {
                return new Phoneme(c, Phoneme.POSITION.CF);
            }
        }

        String phoneme = clean.substring(clean.length() - 1);
        // if the final phoneme of the transcription is a consonant, then it's a Final Coda
        // TODO: testar somente codas finais válidas? (s) e (r)? quais são?
        return new Phoneme(phoneme, Phoneme.POSITION.CF);
    }

    /**
     * Replace the last occurrence of the regex at the given text. Method from here:
     * https://stackoverflow.com/questions/2282728/java-replacelast.
     *
     * @param text Text to change.
     * @param regex Text to be replaced.
     * @param replacement Replacement of the text.
     * @return The text without the last occurence of the regex.
     */
    public static String replaceLast(final String text, final String regex, final String replacement) {
        if (text != null && regex != null && replacement != null) {
            return text.replaceFirst("(?s)" + regex + "(?!.*?" + regex + ")", replacement);
        }
        return null;
    }

    /**
     * Checks if the given phoneme is valid or not and return a valid array containing all the valid phonemes in the
     * given target.
     *
     * @param phonemeTarget Target phoneme to check.
     * @return An array with the valid phonemes found in the target.
     */
    public static Phoneme[] checkPhoneme(final String phonemeTarget) {
        String phoneme = phonemeTarget;
        final List<Phoneme> list = new ArrayList<>();
        if (phoneme != null) {
            phoneme = StringEscapeUtils.unescapeJava(phoneme); // just in case...
            phoneme = phoneme.trim();
            if (!phoneme.trim().isEmpty()) {
                // TODO: ter uma lista de fonemas consonantais válidos? existe algum pacote com esses fonemas? ver Phon
                if (phoneme.length() == 1 || Arrays.asList(Phoneme.LABIALIZATION).contains(phoneme)
                        || Arrays.asList(Phoneme.SPECIAL_CONSONANTS).contains(phoneme)) {
                    list.add(new Phoneme(phoneme, Phoneme.POSITION.OM));
                } else if (Arrays.asList(CONSONANT_CLUSTERS).contains(phoneme)) {
                    list.add(new Phoneme(phoneme, Phoneme.POSITION.OCME));
                } else {
                    // the array has an inconsistensy. Treating it here...

                    /**
                     * some possibilities: ngʷ skɾ sʃk kɾʧ.
                     */
                    if (phoneme.length() == 3) {
                        // treating kɾʧ: consonant cluster at the beggining: si’kɾʧi.
                        for (String cluster : CONSONANT_CLUSTERS) {
                            if (phoneme.startsWith(cluster)) {
                                list.add(new Phoneme(cluster, Phoneme.POSITION.OCME));
                                list.add(new Phoneme(phoneme.substring(cluster.length()), Phoneme.POSITION.OM));
                                phoneme = "";
                            }
                        }
                    }

                    if (phoneme.length() > 0) {
                        // always CM, because CF is at the end of the word.
                        list.add(new Phoneme(phoneme.substring(0, 1), Phoneme.POSITION.CM));

                        Phoneme next = new Phoneme(phoneme.substring(1));

                        /**
                         * The next phoneme can be only OM/OCME, because OI and OCME must be at the beginning of the
                         * word, and codas it's not the case (it was just read before).
                         */
                        if (next.getPhoneme().length() == 1
                                || Arrays.asList(Phoneme.LABIALIZATION).contains(next.getPhoneme())
                                || Arrays.asList(Phoneme.SPECIAL_CONSONANTS).contains(next.getPhoneme())) {
                            next.setPosition(Phoneme.POSITION.OM);
                        } else {
                            /**
                             * Checks if the phoneme is a valid consonant cluster. If it's not, then split in two (and
                             * only two) Medial Onsets.
                             *
                             * Case of bisʃkɛtə, which /ʃ/ is a Medial Onset and not a consonant cluster.
                             */
                            if (next.getPhoneme().length() == 2
                                    && !Arrays.asList(Phoneme.CONSONANT_CLUSTERS).contains(next.getPhoneme())) {
                                Arrays.asList(next.getPhoneme().split("")).forEach(phon -> {
                                    list.add(new Phoneme(phon, Phoneme.POSITION.OM));
                                });
                                return list.toArray(new Phoneme[list.size()]);
                            }

                            next.setPosition(Phoneme.POSITION.OCME);
                        }

                        list.add(next);
                    }
                }
            }
        }
        return list.toArray(new Phoneme[list.size()]);
    }

    /**
     * Replace phonemes that are represented of more than 1 byte in the given string as indexes in a list with no more
     * than 10 elements.
     *
     * See {@link Phoneme#LABIALIZATION} and {@link Phoneme#SPECIAL_CONSONANTS}.
     *
     * @param str The given string.
     * @return The treat string without special representations.
     */
    public static String replaceSpecialPhonemes(final String str) {
        if (str != null) {
            String clean = str;

            List<String> list = new LinkedList<>();
            list.addAll(Arrays.asList(Phoneme.LABIALIZATION));
            list.addAll(Arrays.asList(Phoneme.SPECIAL_CONSONANTS));

            if (list.size() > 9) {
                /**
                 * If this happens, then it's time to increase the algorithm. More than 9 digits we won't be able to
                 * replace correctly again when translate the indexes with their representations.
                 */
                throw new UnsupportedOperationException("The total size of special phonemes must not be greater than 9.");
            }

            int index = 0;
            for (String p : list) {
                clean = clean.replaceAll(p, Integer.toString(index++));
            }
            return clean;
        }
        return null;
    }

    /**
     * Gets consonant phonemes from the given transcription with their respective position at the word.
     *
     * @param transcription Given transcription.
     * @return An ordered list with the consonant phonemes or empty if inconsistencies were found.
     */
    public static List<Phoneme> getConsonantPhonemes(final String transcription) {
        if (transcription != null && !transcription.trim().isEmpty()) {
            String clean = cleanTranscription(transcription);

            for (String vowel : VOWELS) {
                clean = clean.replaceAll(vowel, " ");
            }

            for (String sv : SEMI_VOWELS) {
                clean = clean.replaceAll(sv, " ");
            }

            // replace double spaces for just one: this will be used to separe the positions of the phonemes
            clean = clean.trim().replaceAll(" +", " ");

            List<Phoneme> list = new ArrayList<>();
            Phoneme initialOnset = getInitialOnset(transcription);
            if (initialOnset != null) {
                list.add(initialOnset);

                // remove the phoneme, because we already treat the inicial onset
                clean = clean.replaceFirst(initialOnset.getPhoneme(), "");
            }

            Phoneme finalCoda = getFinalCoda(transcription);
            if (finalCoda != null) {
                // remove the phoneme, because we already treat the final coda
                clean = replaceLast(clean, finalCoda.getPhoneme(), "");
            }

            // maps each labialization phoneme (if exists) to an index at Phoneme.LABIALIZATION
            clean = replaceSpecialPhonemes(clean);

            String[] split = clean.split(" ");

            if (split.length >= 1) {
                List<String> special = new LinkedList<>();
                special.addAll(Arrays.asList(Phoneme.LABIALIZATION));
                special.addAll(Arrays.asList(Phoneme.SPECIAL_CONSONANTS));

                for (String phoneme : split) {
                    // replaces all possible labialization phonemes. This array never must have an index greater than 9
                    for (int i = 0; i < special.size(); i++) {
                        phoneme = phoneme.replaceAll(i + "", special.get(i));
                    }
                    list.addAll(Arrays.asList(checkPhoneme(phoneme)));
                }
            }

            // Final Coda must be added only at the end, because it keep the same order of the phonemes in transcription
            if (finalCoda != null) {
                list.add(finalCoda);
            }

            return list;
        }
        return Collections.emptyList();
    }

    /**
     * Gets the case in the list from the given word. Ignore case is used here.
     *
     * @param list The list to look in.
     * @param word The word.
     * @return The case with the given word or null if the word is not in any case of the list.
     */
    public static KnownCase getCaseFromWord(final List<KnownCase> list, final String word) {
        if (list != null && word != null) {
            for (KnownCase c : list) {
                if (c.getWord().equalsIgnoreCase(word)) {
                    return c;
                }
            }
        }
        return null;
    }

    /**
     * Gets the target phonemes from the cases. The target phonemes will be the ones which are in all the given cases.
     *
     * @param cases Cases to look for target phonemes.
     * @return The target phonemes.
     */
    public static List<Phoneme> getTargetPhonemes(final List<KnownCase> cases) {
        final List<Phoneme> target = new ArrayList<>();
        if (cases != null && !cases.isEmpty()) {
            /**
             * The target phonemes will be the ones which are in all the given cases, that is the reason we only need
             * the first case here.
             */
            KnownCase firstCase = cases.get(0);
            firstCase.getPhonemes().forEach(p -> {
                if (!target.contains(p)) {
                    // count in how many cases this phoneme is 
                    int count = 0;
                    for (KnownCase k : cases) {
                        if (k.getPhonemes().contains(p)) {
                            count++;
                        }
                    }

                    if (count == cases.size()) {
                        target.add(p);
                    }
                }
            });
        }
        return target;
    }

    /**
     * Gets a list with inferred phonemes by splitting other consonant clusters.
     *
     * For example: if the user could reproduce words with <br>
     * Medial Complex Onset(bl)<br>
     * Medial Complex Onset(kɾ)<br>
     *
     * We can infer that he is capable of reproduce two other consonant clusters:
     *
     * Medial Complex Onset(bɾ)<br>
     * Medial Complex Onset(kl)<br>
     *
     * In this case, this method will return {Medial Complex Onset(bɾ), Medial Complex Onset(kl)}.
     *
     * @param map A map with words spoken by the subject and its phonemes reproduced.
     * @param clustersParts A list with all the clusters parts.
     * @return A non-null list with inferred phonemes.
     */
    @Deprecated
    public static List<Phoneme> getInferredPhonemes(final Map<String, List<Phoneme>> map,
            final List<Phoneme> clustersParts) {
        List<Phoneme> inferredPhonemes = new NoRepeatList<>();
        if (clustersParts != null) {
            clustersParts.clear();

            map.values().forEach(l -> {
                l.stream().filter(p -> p.isConsonantCluster()).forEach(p -> clustersParts.addAll(p.splitPhonemes()));
            });

            long countOCI = clustersParts.stream().filter(p -> p.getPosition().equals(Phoneme.POSITION.OCI)).count();
            long countOCM = clustersParts.stream().filter(p -> p.getPosition().equals(Phoneme.POSITION.OCME)).count();
            Arrays.asList(Phoneme.CONSONANT_CLUSTERS).forEach(s -> {
                Phoneme pOCI = new Phoneme(s, Phoneme.POSITION.OCI);
                /**
                 * If we have just 2 splitted phonemes, that means that the child only spoke "bl", for example, and then
                 * we can't infer nothing from that information.
                 */
                if (countOCI > 2 && clustersParts.containsAll(pOCI.splitPhonemes())) {
                    inferredPhonemes.add(pOCI);
                }

                Phoneme pOCM = new Phoneme(s, Phoneme.POSITION.OCME);
                if (countOCM > 2 && clustersParts.containsAll(pOCM.splitPhonemes())) {
                    inferredPhonemes.add(pOCM);
                }
            });
        }

        return inferredPhonemes;
    }

    /**
     * Print consonant clusters separately according with its positions.
     *
     * @param clusters Clusters to print.
     * @return The string to print.
     */
    public static String printClusters(final List<Phoneme> clusters) {
        final StringBuilder builder = new StringBuilder("total: " + clusters.size());

        builder.append(Phoneme.POSITION.OCI).append(": ");
        clusters.stream().filter(p -> p.getPosition().equals(Phoneme.POSITION.OCI)).forEach(
                p -> builder.append(p.getPhoneme()).append(","));
        builder.append("\n");

        builder.append(Phoneme.POSITION.OCME).append(": ");
        clusters.stream().filter(p -> p.getPosition().equals(Phoneme.POSITION.OCME)).forEach(
                p -> builder.append(p.getPhoneme()).append(","));
        builder.append("\n");

        return builder.toString();
    }

    /**
     * Gets a list of consonant clusters by combination of all phonemes parts given by the list. In a list with:
     *
     * <ul>
     * <li>b(OCI)</li>
     * <li>l(OCI)</li>
     * <li>f(OCI)</li>
     * <li>ɾ(OCI)</li>
     * </ul>
     *
     * The returned list will be:
     *
     * <ul>
     * <li>bl(OCI)</li>
     * <li>bɾ(OCI)</li>
     * <li>fl(OCI)</li>
     * <li>fɾ(OCI)</li>
     * </ul>
     *
     * A combination will always be with 2 parts, so if there is only one phoneme in a specific position, then no
     * combination can be done.
     *
     * @param list A list with all the clusters parts.
     * @return A non-null list with possible consonant clusters.
     */
    public static List<Phoneme> getPossibleClusters(final List<Phoneme> list) {
        final List<Phoneme> possibleClusters = new NoRepeatList<>();
        final List<Phoneme> clustersParts = new NoRepeatList<>();
        clustersParts.addAll(list); // avoid elements repeated
        long countOCI = clustersParts.stream().filter(p -> p.getPosition().equals(Phoneme.POSITION.OCI)).count();
        long countOCM = clustersParts.stream().filter(p -> p.getPosition().equals(Phoneme.POSITION.OCME)).count();
        Arrays.asList(Phoneme.CONSONANT_CLUSTERS).forEach(s -> {
            Phoneme pOCI = new Phoneme(s, Phoneme.POSITION.OCI);
            /**
             * If we have just 2 splitted phonemes, that means that the child only spoke "bl", for example, and then we
             * can't infer nothing from that information.
             */
            if (countOCI > 2 && clustersParts.containsAll(pOCI.splitPhonemes())) {
                possibleClusters.add(pOCI);
            }

            Phoneme pOCM = new Phoneme(s, Phoneme.POSITION.OCME);
            if (countOCM > 2 && clustersParts.containsAll(pOCM.splitPhonemes())) {
                possibleClusters.add(pOCM);
            }
        });

        return possibleClusters;
    }

    /**
     * Exports info in CSV format as input for ML algorithms.
     *
     * @param assessments Assessment to be analyzed.
     * @param targetPhonemes Target phonemes expected for each word.
     * @return The info in CSV format.
     */
    public static String exportClustersInfo(final List<Assessment> assessments,
            final Map<String, List<Phoneme>> targetPhonemes) {
        StringBuilder builder = new StringBuilder("assessmentID,targetWord,transcription,correct,targetClusters,producedClusters1,producedClusters2\n");

        assessments.forEach(a -> {
            a.getCases().forEach(c -> {
                List<Phoneme> targetClusters = targetPhonemes.get(c.getWord()).stream().filter(p -> p.isConsonantCluster()).collect(Collectors.toList());
                List<Phoneme> producedClusters = c.getPhonemes().stream().filter(p -> p.isConsonantCluster()).collect(Collectors.toList());

                List<Phoneme> subList1;
                List<Phoneme> subList2 = new ArrayList<>();

                if (producedClusters.size() > 1) {
                    int middle = producedClusters.size() / 2;

                    subList1 = producedClusters.subList(0, middle);
                    subList2 = producedClusters.subList(middle, producedClusters.size());
                } else {
                    subList1 = producedClusters;
                }

                if (subList1.size() + subList2.size() != producedClusters.size()) {
                    System.out.println("ERRO!");
                }

                List<String> cols = new LinkedList<>();
                cols.add(Integer.toString(a.getId()));
                cols.add(c.getWord());
                cols.add(c.getRepresentation());
                cols.add(Boolean.toString(c.isCorrect()));
                cols.add(getCSVFormat(targetClusters, " "));
                cols.add(getCSVFormat(subList1, " "));
                cols.add(getCSVFormat(subList2, " "));

                builder.append(getCSVFormat(cols, ",")).append("\n");
            });
        });

        return builder.toString();
    }

    /**
     * Exports info in CSV format as input for ML algorithms.
     *
     * @param assessments Assessment to be analyzed.
     * @param targetPhonemes Target phonemes expected for each word.
     * @return The info in CSV format.
     */
    public static String exportClustersInfosGeneral(final List<Assessment> assessments,
            final Map<String, List<Phoneme>> targetPhonemes) {
        StringBuilder builder = new StringBuilder("assessmentID,targetClusters,producedClusters1,producedClusters2\n");
        
        List<Phoneme> targetClusters = new NoRepeatList<>();
        targetPhonemes.values().forEach(list -> list.stream().filter(p -> p.isConsonantCluster()).forEach(p -> targetClusters.add(p)));

        assessments.forEach(a -> {
            List<Phoneme> producedClusters = new NoRepeatList<>();
            a.getCases().forEach(c -> {
                c.getPhonemes().stream().filter(p -> p.isConsonantCluster()).forEach(p -> producedClusters.add(p));
            });

            List<Phoneme> subList1;
            List<Phoneme> subList2 = new ArrayList<>();

            if (producedClusters.size() > 1) {
                int middle = producedClusters.size() / 2;

                subList1 = producedClusters.subList(0, middle);
                subList2 = producedClusters.subList(middle, producedClusters.size());
            } else {
                subList1 = producedClusters;
            }

            if (subList1.size() + subList2.size() != producedClusters.size()) {
                System.out.println("ERRO!");
            }

            List<String> cols = new LinkedList<>();
            cols.add(Integer.toString(a.getId()));
            cols.add(getCSVFormat(targetClusters, " "));
            cols.add(getCSVFormat(subList1, " "));
            cols.add(getCSVFormat(subList2, " "));

            builder.append(getCSVFormat(cols, ",")).append("\n");

        });

        return builder.toString();
    }

    public static String getCSVFormat(final List cols, final String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < cols.size(); i++) {
            builder.append(cols.get(i));
            if (i < cols.size() - 1) {
                builder.append(separator);
            }
        }

        return builder.toString();
    }

}
