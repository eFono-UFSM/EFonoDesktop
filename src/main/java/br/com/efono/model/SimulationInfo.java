package br.com.efono.model;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Used to store information about the simulation running.
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 28.
 */
public class SimulationInfo {

    private final Map<Phoneme, Integer> mapCounter;
    private final List<String> wordsRequired;

    /**
     * Default constructor.
     *
     * @param mapCounter The map counter.
     * @param wordsRequired The words required.
     */
    public SimulationInfo(final Map<Phoneme, Integer> mapCounter, final List<String> wordsRequired) {
        this.mapCounter = Objects.requireNonNull(mapCounter);
        this.wordsRequired = Objects.requireNonNull(wordsRequired);
    }

    /**
     * @return The map with phonemes count.
     */
    public Map<Phoneme, Integer> getMapCounter() {
        return mapCounter;
    }

    /**
     * @return The list with words required.
     */
    public List<String> getWordsRequired() {
        return wordsRequired;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        int mapHashCode = 1;

        Iterator<Map.Entry<Phoneme, Integer>> it = mapCounter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Phoneme, Integer> next = it.next();
            mapHashCode *= 12 + (next.getKey().hashCode() + next.getValue().hashCode());
        }

        int listHashCode = 1;
        for (String s : wordsRequired) {
            listHashCode *= 26 + s.hashCode();
        }

        hash = 83 * hash + mapHashCode;
        hash = 83 * hash + listHashCode;
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
        final SimulationInfo other = (SimulationInfo) obj;

        if (mapCounter.size() != other.mapCounter.size()) {
            return false;
        }

        Iterator<Map.Entry<Phoneme, Integer>> it = mapCounter.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Phoneme, Integer> next = it.next();

            if (other.mapCounter.containsKey(next.getKey())) {
                if (!Objects.equals(other.mapCounter.get(next.getKey()), next.getValue())) {
                    return false;
                }
            } else {
                return false;
            }
        }

        if (wordsRequired.size() != other.wordsRequired.size()) {
            return false;
        }

        return wordsRequired.containsAll(other.wordsRequired) && other.wordsRequired.containsAll(wordsRequired);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder(getClass().getSimpleName());

        builder.append(": wordsRequired: ").append(wordsRequired.size()).
                append(": [").append(wordsRequired.toString()).append("]");

        return builder.toString();
    }

}
