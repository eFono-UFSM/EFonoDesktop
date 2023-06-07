package br.com.efono.model;

import br.com.efono.util.FileUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 03.
 */
public class KnownCase {

    @JsonProperty(value = "word")
    private final String word;

    @JsonProperty(value = "representation")
    private final String representation;

    @JsonProperty(value = "correct")
    private final boolean correct;

    @JsonProperty(value = "phonemes")
    private final List<Phoneme> phonemes;

    /**
     * Default constructor.
     */
    public KnownCase() {
        this("", "", false);
    }

    /**
     * Creates a know case.
     *
     * @param word Target word.
     * @param representation Phonetic transcription.
     * @param correct If the transcription represents a correct pronunciation or not.
     */
    public KnownCase(final String word, final String representation, boolean correct) {
        this(word, representation, correct, new ArrayList<>());
    }

    /**
     * Creates a know case.
     *
     * @param word Target word.
     * @param representation Phonetic transcription.
     * @param correct If the transcription represents a correct pronunciation or not.
     * @param phonemas Only consonant phonemes in the transcription.
     */
    public KnownCase(final String word, final String representation, boolean correct, final List<Phoneme> phonemas) {
        this.word = word;
        this.representation = representation;
        this.correct = correct;
        this.phonemes = phonemas;
    }

    /**
     * Gets the target word of this case.
     *
     * @return The word.
     */
    public String getWord() {
        return word;
    }

    /**
     * Gets the representation/transcription of the pronunciation.
     *
     * @return The representation/transcription of the pronunciation.
     */
    public String getRepresentation() {
        return representation;
    }

    /**
     * All the phonemes in this case.
     *
     * @return A list with the phonemes in this case.
     */
    public List<Phoneme> getPhonemes() {
        return phonemes;
    }

    /**
     * @return If the transcription represents a correct pronunciation or not.
     */
    public boolean isCorrect() {
        return correct;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + Objects.hashCode(this.word);
        hash = 89 * hash + Objects.hashCode(this.representation);
        hash = 89 * hash + Objects.hashCode(this.phonemes);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KnownCase other = (KnownCase) obj;
        if (!Objects.equals(this.word, other.word)) {
            return false;
        }
        if (!Objects.equals(this.representation, other.representation)) {
            return false;
        }
        return Objects.equals(this.phonemes, other.phonemes);
    }

    @Override
    public String toString() {
        return "representation: " + representation + " word: " + word;
    }

    /**
     * Reads file containing known cases.
     *
     * @param file File to read.
     * @return A list with known cases from the file.
     * @throws IOException
     */
    public static List<KnownCase> loadFile(final File file) throws IOException {
        if (file != null && file.getAbsolutePath().endsWith(".json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(file, new TypeReference<List<KnownCase>>() {
            });
        }
        return Collections.emptyList();
    }

    /**
     * Builds a list with known cases from a file. Accepts only .CSV format for now.
     *
     * @param file File to read
     * @return A list with known cases.
     */
    public static List<KnownCase> buildKnownCases(final File file) {
        final List<KnownCase> list = new ArrayList<>();

        if (file != null && file.getAbsolutePath().endsWith(".csv")) {
            List<String[]> csv = FileUtils.readCSV(file, ",");
            for (String[] line : csv) {
                // word,transcription,correct
                list.add(new KnownCase(line[0], line[1], "1".equals(line[2])));
            }
        }

        return list;
    }

}
