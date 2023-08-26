package br.com.efono.util;

import java.util.ArrayList;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Ago 26.
 * @param <E>
 */
public class NoRepeatList<E> extends ArrayList<E> {

    @Override
    public boolean add(final E e) {
        if (!contains(e)) {
            return super.add(e);
        }
        return false;
    }

}
