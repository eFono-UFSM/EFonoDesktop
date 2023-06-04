package br.com.efono.util;

import br.com.efono.model.Phoneme;
import static br.com.efono.model.Phoneme.CONSONANT_CLUSTERS;
import static br.com.efono.model.Phoneme.VOWELS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONObject;

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
        "\\ø"};

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
                    if (!line.isBlank()) {
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
        String phoneme = clean.substring(clean.length() - 1);
        if (Arrays.asList(VOWELS).contains(phoneme)) {
            return null;
        }
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
        List<Phoneme> list = new ArrayList<>();
        if (phoneme != null) {
            phoneme = StringEscapeUtils.unescapeJava(phoneme); // just in case...
            phoneme = phoneme.trim();
            if (!phoneme.isBlank()) {
                // TODO: ter uma lista de fonemas consonantais válidos? existe algum pacote com esses fonemas? ver Phon
                if (phoneme.length() == 1 || Arrays.asList(Phoneme.LABIALIZATION).contains(phoneme)) {
                    list.add(new Phoneme(phoneme, Phoneme.POSITION.OM));
                } else if (Arrays.asList(CONSONANT_CLUSTERS).contains(phoneme)) {
                    list.add(new Phoneme(phoneme, Phoneme.POSITION.OCME));
                } else {
                    // the array as an inconsistensy. Treating it here...

                    // always CM, because CF is at the end of the word.
                    list.add(new Phoneme(phoneme.substring(0, 1), Phoneme.POSITION.CM));

                    Phoneme next = new Phoneme(phoneme.substring(1));

                    /**
                     * The next phoneme can be only OM/OCME, because OI and OCME must be at the beginning of the word,
                     * and codas it's not the case (it was just read before).
                     */
                    if (next.getPhoneme().length() == 1
                            || Arrays.asList(Phoneme.LABIALIZATION).contains(next.getPhoneme())) {
                        next.setPosition(Phoneme.POSITION.OM);
                    } else {
                        // TODO: nesse caso, precisaria avaliar se o foneme é um encontro consonantal válido, se não for, precisaria rever o algoritmo
                        next.setPosition(Phoneme.POSITION.OCME);
                    }

                    list.add(next);
                }
            }
        }
        return list.toArray(new Phoneme[list.size()]);
    }

    /**
     * Replace labialization representations in the given string as their indexes at {@link Phoneme#LABIALIZATIO}.
     *
     * @param str The given string.
     * @return The treat string without labialization representations.
     */
    public static String replaceLabialization(final String str) {
        if (str != null) {
            String clean = str;

            int index = 0;
            for (String p : Phoneme.LABIALIZATION) {
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
        if (transcription != null && !transcription.isBlank()) {
            String clean = cleanTranscription(transcription);

            // remove special characters
            for (String vowel : VOWELS) {
                clean = clean.replaceAll(vowel, " ");
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
            clean = replaceLabialization(clean);

            String[] split = clean.split(" ");

            if (split.length >= 1) {
                for (String phoneme : split) {
                    // replaces all possible labialization phonemes. This array never must have an index greater than 9
                    for (int i = 0; i < Phoneme.LABIALIZATION.length; i++) {
                        phoneme = phoneme.replaceAll(i + "", Phoneme.LABIALIZATION[i]);
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

    public static File createJSON(String value1, String value2) {
        // {"key1":"[\u2019lu.vẽj̃]","key2":"olaMundinho"}
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("key1", value1);
        jsonObject.put("key2", value2);

        File file = new File("C:\\Users\\Joao\\Documents\\mestrado\\Java");
        System.out.println("is directory: " + file.isDirectory());
        try (FileWriter writer = new FileWriter("C:\\Users\\Joao\\Documents\\mestrado\\Java\\output.json")) {
            writer.write(jsonObject.toJSONString());
        } catch (final IOException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }

        return file;
    }
}
