package br.com.efono.tree;

import br.com.efono.model.KnownCase;
import br.com.efono.util.Util;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 16.
 */
public class TreeUtils {
    
    /**
     * Builds the optimal sequence of the words in an assessment according with the user cases. The first word will
     * always be in the root node. The next words will be according with users results: if he/she spells correctly, than
     * a harder word will be in the sequence (right node), otherwise an easier one will follow (left node). If the
     * function reaches some leaf node, it starts to returning back to parent node and going to the other side
     * recursively.
     *
     * The algorithm keep going to right side (harder words) until there is an error from the user. In this moment, it
     * starts to return and goes to the other side. The recursion makes sure that even if there was an incorrect
     * production and after a correct one, the algorithm will increase again the level of difficult.
     * 
     * If the list with cases (assessment) has less words than the tree, we always go to the right side (harder words).
     * 
     * Only words that are in the <code>cases</code> list will be in the <code>words</code> list.
     *
     * @param node The root node.
     * @param words A list with words in sequence.
     * @param cases The user cases.
     */
    public static void buildSequenceOrder(final Node<String> node, final List<String> words, 
            final List<KnownCase> cases) {
        // TODO: esse metodo serve apenas para saber a ordem de inserção na árvore das palavras que estão na lista de casos.
        // todos os casos poderiam estar organizados em árvore já, ficaria tudo numa instância só.
        if (node == null) {
            return;
        }
        String val = node.getValue();
        /**
         * In case of the list has less words than tree (incomplete assessment) we don't need to add this word at words
         * list.
         */
        KnownCase c = Util.getCaseFromWord(cases, val);
        if (c != null) {
            words.add(val);
        }

        if (c == null || c.isCorrect()) {
            buildSequenceOrder(node.getRight(), words, cases);
            buildSequenceOrder(node.getLeft(), words, cases);
        } else {
            buildSequenceOrder(node.getLeft(), words, cases);
            buildSequenceOrder(node.getRight(), words, cases);
        }
    }

    /**
     * Gets the best first words sequence from the given cases.
     *
     * @param node The root node of the tree of words in the complete set.
     * @param words The list to keep the words sequence.
     * @param cases The list with cases to analyze. This usually comes from assessments.
     */
    public static void getFirstWords(final Node<String> node, final LinkedList<String> words, 
            final List<KnownCase> cases) {
        if (node == null || node.isVisited()) {
            return;
        }
        node.setVisited(true);
        String val = node.getValue();

        /**
         * In case of the list has less words than the tree (incomplete assessment) we don't need to add this word at
         * words list.
         */
        KnownCase c = Util.getCaseFromWord(cases, val);
        if (c != null) {
            words.add(val);
        }

        if (c == null || c.isCorrect()) {
            getFirstWords(node.getRight(), words, cases);
            // makes sure that it goes until the last leaf
            if (node.getRight() == null) {
                getFirstWords(node.getLeft(), words, cases);
            }
        } else {
            getFirstWords(node.getLeft(), words, cases);
            // makes sure that it goes until the last leaf
            if (node.getLeft() == null) {
                getFirstWords(node.getRight(), words, cases);
            }
        }
    }
    
}
