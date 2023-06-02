package br.com.efono.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    // TODO: deveria pegar essas constantes de algum pacote. Ver Phon.
    /**
     * Vowel phonemes.
     */
    public static final String[] VOWELS = new String[]{"a", "ɐ", "ə", "e", "ɛ", "Ɛ", "ẽ", "i", "ɪ", "ĩ", "o", "ɔ", "õ", "u",
        "ʊ", "ũ", "ø", "w", "j̃"};

    /**
     * Consonant clusters.
     */
    public static final String[] CONSONANT_CLUSTERS = new String[]{"pɾ", "pl", "bɾ", "bl", "tɾ", "dl", "dɾ", "kɾ", "kl",
        "gɾ", "gχ", "gl", "fɾ", "fl", "vɾ"};

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
     * Gets the consonant phoneme at initial onset from the given transcription.
     *
     * @param transcription The given transcription.
     * @return The consonant phoneme at Initial Onset or {@code null}.
     */
    public static String getInitialOnset(final String transcription) {
        String clean = cleanTranscription(transcription);
        if (!clean.isEmpty()) {
            // tests for Initial Onset
            if (!Arrays.asList(VOWELS).contains(clean.substring(0, 1))) {
                return clean.substring(0, 1);
            }
        }
        return null;
    }

    /**
     * Checks the consistency of the given array and return a new one with valid phonemes. It's used most to separate
     * invalid consonant clusters, like "nk": this usually represents a coda (n) followed by another phoneme from the
     * next syllable.
     *
     * @param phonemes The given phonemes to check.
     * @return An array containing only valid phonemes or empty if couldn't resolve inconsistencies.
     */
    public static String[] checkPhonemes(final String[] phonemes) {
        List<String> list = new ArrayList<>();
        if (phonemes != null) {
            for (String phoneme : phonemes) {
                if (phoneme != null) {
                    phoneme = StringEscapeUtils.unescapeJava(phoneme); // just in case...
                    phoneme = phoneme.trim();
                    if (!phoneme.isBlank()) {
                        if (phoneme.length() == 1 || Arrays.asList(CONSONANT_CLUSTERS).contains(phoneme)) {
                            // if the phoneme is represented by only one char or contains a consonant cluster, it's ok
                            list.add(phoneme);
                        } else {
                            // TODO: aqui deveria "marcar" o primeiro fonema como CODA e o outro como onset alguma coisa... (precisa de um objeto para essa lista de phonemas, nao vai ter como descobrir depois só usando o array)
                            // TODO: TESTAR CM seguido de OCME (vai falhar)
                            // the array as a inconsistensy: just split all the chars
                            list.addAll(Arrays.asList(phoneme.split("")));
                        }
                    }
                }
            }
        }

        /**
         * TODO: ter uma lista de fonemas consonantais válidos? existe algum pacote com esses fonemas? ver Phon aqui
         * poderia ter uma chamada de recursiva, e se tiver algum fonema inválido no array retorna lista vazia.
         */
        return list.toArray(new String[list.size()]);
    }

    /**
     * Gets consonant phonemes from the given transcription.
     *
     * @param transcription Given transcription.
     * @return An ordered list with the consonant phonemes or empty if inconsistencies were found.
     */
    public static List<String> getConsonantPhonemes(final String transcription) {
        String clean = cleanTranscription(transcription);

        // remove special characters
        for (String vowel : VOWELS) {
            clean = clean.replaceAll(vowel, " ");
        }

        // replace double spaces for just one: this will be used to separe the positions of the phonemes
        clean = clean.trim().replaceAll(" +", " ");
        String[] split = clean.split(" ");

        return Arrays.asList(checkPhonemes(split));
    }

    /**
     * Decompose a transcription into phonemes with their respective position in the transcription.
     *
     * @param transcription Given transcription to decompose.
     */
    public static void decomposeTranscription(final String transcription) {
        String clean = cleanTranscription(transcription);

        // TODO: 
        getConsonantPhonemes(clean);

        System.out.println("Initial Onset: " + getInitialOnset(clean));

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
