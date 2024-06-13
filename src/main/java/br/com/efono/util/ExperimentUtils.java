package br.com.efono.util;

import br.com.efono.model.KnownCase;
import br.com.efono.model.KnownCaseComparator;
import static br.com.efono.model.KnownCaseComparator.BinaryTreeComparator;
import static br.com.efono.model.KnownCaseComparator.EasyHardWords;
import br.com.efono.tree.TreeUtils;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
     * @return The sorted list.
     */
    public static List<KnownCase> sortList(final List<KnownCase> list, final KnownCaseComparator comp) {
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
                    List<KnownCase> sortedCases = new LinkedList<>();
                    TreeUtils.buildSequenceOrder(Defaults.TREE.getRoot(), sortedCases, list);

                    /**
                     * Sorts the list as insertion order in the tree. This means that the first word will be the middle
                     * word in the tree (root node) and the next one will always be easier or harder than the previous
                     * one according with the result from the user. The numbers represents the words indexes at
                     * {@link Defaults#SORTED_WORDS}. Example: the case (41) was incorrect, so the next case to be
                     * analyzed will be 20 (easier than 41); the case (20) was correct, so the next will be 31 (harder
                     * than 20 and easier than 41); and so on. When the algorithm arrive in some leaf node, it starts to
                     * returning back to parents nodes and visit the ones in the other side of its node parent.
                     */
                    if (sortedCases.size() == list.size()) {
                        return sortedCases;
                    }
                    return null;
                }
                default ->
                    list.sort(comp.getComparator());
            }
        }
        return list;
    }

    /**
     * Get lines in CSV format from the given map.
     *
     * @param key Key header.
     * @param value Value header.
     * @param map The map.
     * @return All the lines in CSV format.
     */
    public static List<String> getLinesFromMap(final String key, final String value, final Map map) {
        List<String> lines = new LinkedList<>();
        lines.add(key + "," + value); // header

        Iterator<Map.Entry> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry next = it.next();
            lines.add(next.getKey() + "," + next.getValue());
        }

        return lines;
    }

    public static String concatListsToCSV(final List<List<String>> lists) {
        StringBuilder csv = new StringBuilder();

        // list -> numero de colunas
        Map<List<String>, Integer> mapData = new LinkedHashMap<>();

        int maxRows = 0;
        for (List<String> l : lists) {
            if (l.size() > maxRows) {
                maxRows = l.size();
            }
            mapData.put(l, l.get(0).split(",").length);
        }
        for (int i = 0; i < maxRows; i++) {
            StringBuilder line = new StringBuilder();

            for (Map.Entry<List<String>, Integer> next : mapData.entrySet()) {
                List<String> list = next.getKey();

                if (i < list.size()) {
                    if (!line.toString().replaceAll(",", "").isEmpty()) {
                        line.append(",");
                    }
                    line.append(list.get(i));
                } else {
                    Integer columns = next.getValue();
                    for (int j = 0; j < columns; j++) {
                        line.append(",");
                    }
                }
            }

            csv.append(line).append("\n");
        }
        return csv.toString();
    }

}
