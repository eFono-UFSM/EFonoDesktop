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

    /**
     * Vowel phonemes.
     */
    public static final String[] VOWELS = new String[]{"a", "ɐ", "ə", "e", "ɛ", "ẽ", "i", "ɪ", "ĩ", "o", "ɔ", "õ", "u", "ʊ", "ũ", "ø", "w", "j̃"};

    /**
     * Special characters found on transcriptions.
     */
    public static final String[] SPECIAL = new String[]{"\\[", "\\'", "\"", "\\]", "\\ʷ", "\\.", "\\'", "\\‘", "\\’", "\\ʼ", "\\ø"};

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
     * Decompose a transcription into phonemes with their respective position in the transcription.
     *
     * @param transcription Given transcription to decompose.
     */
    public static void decomposeTranscription(final String transcription) {
        String clean = cleanTranscription(transcription);

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
