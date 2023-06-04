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
import org.apache.commons.lang.StringEscapeUtils;
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
    public static final String[] SPECIAL = new String[]{"\\[", "\\'", "\"", "\\]", "\\ʷ", "\\.", "\\'", "\\‘", "\\’",
        "\\ʼ", "\\ø"};

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
     * @return The consonant phoneme at Initial Onset or {@code null}.
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
                if (phoneme.length() == 1) {
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
                    if (next.getPhoneme().length() == 1) {
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

            String[] split = clean.split(" ");

            if (split.length >= 1) {
                for (String phoneme : split) {
                    list.addAll(Arrays.asList(checkPhoneme(phoneme)));
                }
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
