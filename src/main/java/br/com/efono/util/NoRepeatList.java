package br.com.efono.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Ago 26.
 * @param <E>
 */
public class NoRepeatList<E> extends ArrayList<E> {

    @Override
    public boolean add(final E e) {
        if (e != null && !contains(e)) {
            return super.add(e);
        }
        return false;
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        boolean changed = false;
        if (c != null) {
            for (E element : c) {
                boolean add = add(element);
                if (add && !changed) {
                    changed = true;
                }
            }
        }
        return changed;
    }

}
