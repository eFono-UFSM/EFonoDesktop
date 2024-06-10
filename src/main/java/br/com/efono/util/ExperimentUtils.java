package br.com.efono.util;

import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import static br.com.efono.model.KnownCaseComparator.BinaryTreeComparator;
import static br.com.efono.model.KnownCaseComparator.EasyHardWords;
import br.com.efono.tree.TreeUtils;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com.br)
 * @version 2024, Jun 10.
 */
public class ExperimentUtils {

    /**
     * Sorts the list with cases according with the given comparator.
     *
     * @param list List to sort.
     * @param comp {@link KnownCaseComparator} to use.
     */
    public static void sortList(final List<KnownCase> list, final KnownCaseComparator comp) {
        if (list != null && comp != null) {
            switch (comp) {
                case EasyHardWords -> {
                    // sorting just to have the right indexes inside the list: [easiest, ..., hardest]
                    list.sort(KnownCaseComparator.EasyWordsFirst.getComparator());
                    // all the words
                    final LinkedList<String> words = new LinkedList<>();
                    for (int i = 0; i < list.size(); i++) {
                        if (!words.contains(list.get(i).getWord())) {
                            words.add(list.get(i).getWord());
                        }
                    }
                    /**
                     * Sorts considering only the words that are in the list of cases, avoiding getting indexes from
                     * global {@link Defaults#SORTED_WORDS}.
                     */
                    String[] easyHardWords = Defaults.getEasyHardWords(words.toArray(String[]::new));
                    list.sort((KnownCase o1, KnownCase o2) -> {
                        int indexOfo1 = Defaults.findIndexOf(o1.getWord(), easyHardWords);
                        int indexOfo2 = Defaults.findIndexOf(o2.getWord(), easyHardWords);
                        return indexOfo1 - indexOfo2;
                    });
                }
                case BinaryTreeComparator -> {
                    final List<String> insertionOrder = new LinkedList<>();

                    TreeUtils.buildSequenceOrder(Defaults.TREE.getRoot(), insertionOrder, list);

                    /**
                     * Sorts the list as insertion order in the tree. This means that the first word will be the middle
                     * word in the tree (root node) and the next one will always be easier or harder than the previous
                     * one according with the result from the user. The numbers represents the words indexes at
                     * {@link Defaults#SORTED_WORDS}. Example: the case (41) was incorrect, so the next case to be
                     * analyzed will be 20 (easier than 41); the case (20) was correct, so the next will be 31 (harder
                     * than 20 and easier than 41); and so on. When the algorithm arrive in some leaf node, it starts to
                     * returning back to parents nodes and visit the ones in the other side of its node parent.
                     */
                    list.sort((KnownCase o1, KnownCase o2) -> {
                        int indexOfo1 = Defaults.findIndexOf(o1.getWord(), insertionOrder.toArray(String[]::new));
                        int indexOfo2 = Defaults.findIndexOf(o2.getWord(), insertionOrder.toArray(String[]::new));
                        return indexOfo1 - indexOfo2;
                    });
                }
                default ->
                    list.sort(comp.getComparator());
            }
        }
    }

}
