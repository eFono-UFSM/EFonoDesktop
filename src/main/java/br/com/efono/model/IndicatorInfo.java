/*
 * @copyright Copyright (c) 2014 Animati Sistemas de Informática Ltda. (http://www.animati.com.br)
 */
package br.com.efono.model;

import br.com.efono.tree.Node;
import br.com.efono.util.Defaults;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2024, May 30.
 */
public class IndicatorInfo {

    private final List<Node<String>> sequence;
    private final String currentWord;

    public IndicatorInfo(final List<Node<String>> sequence, final String currentWord) {
        this.sequence = sequence;
        this.currentWord = currentWord;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public List<Node<String>> getSequence() {
        return sequence;
    }

    public int getNumberOfWords() {
        return sequence.size();
    }

    public int getIndicator() {
        return Arrays.asList(Defaults.SORTED_WORDS).indexOf(currentWord);
    }

    public String getIndicatorAsString() {
        int indicator = getIndicator();
        if (indicator == 41) {
            return null;
        } else if (indicator >= 0 && indicator <= 20) {
            return "High";
        } else if (indicator >= 21 && indicator <= 40) {
            return "Moderate-High";
        } else if (indicator >= 42 && indicator <= 61) {
            return "Moderate-Low";
        }
        return "Low";
    }

    public List<String> getWordsSequence() {
        List<String> words = new LinkedList<>();
        sequence.forEach(node -> words.add(node.getValue()));
        return words;
    }
}
