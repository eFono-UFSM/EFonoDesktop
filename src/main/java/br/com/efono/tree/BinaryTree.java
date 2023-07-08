package br.com.efono.tree;

import java.util.Comparator;
import java.util.Objects;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 06.
 * @param <E> Content type of the tree.
 */
public class BinaryTree<E> {

    private Node<E> root;
    private final Comparator<E> comparator;

    /**
     * Creates a binary tree.
     *
     * @param comparator Comparator to compare the nodes.
     */
    public BinaryTree(final Comparator<E> comparator) {
        this.comparator = comparator;
    }

    /**
     * Creates a binary tree.
     *
     * @param root The root node.
     * @param comparator Comparator to compare the nodes.
     */
    public BinaryTree(final Node<E> root, final Comparator<E> comparator) {
        this.root = root;
        this.comparator = comparator;
    }

    /**
     * @return The root node.
     */
    public Node<E> getRoot() {
        return root;
    }

    /**
     * Adds an element in the tree.
     *
     * @param value Element to add.
     */
    public void add(final E value) {
        root = addRecursive(root, value);
    }

    /**
     * Finds out if the tree contains the given value.
     *
     * @param value Value to find.
     * @return The node itself or null if not found.
     */
    public Node<E> getNode(final E value) {
        return containsNodeRecursive(root, value);
    }

    private Node<E> containsNodeRecursive(final Node<E> current, final E value) {
        if (current == null) {
            return null;
        }
        if (Objects.equals(value, current.getValue())) {
            return current;
        }

        return comparator.compare(value, current.getValue()) < 0
                ? containsNodeRecursive(current.getLeft(), value)
                : containsNodeRecursive(current.getRight(), value);
    }

    private Node<E> addRecursive(final Node<E> current, final E value) {
        // when the current node is null, we've reached a leaf node and we can insert the new node in that position
        if (current == null) {
            return new Node(value);
        }

        if (comparator.compare(current.getValue(), value) < 0) {
            current.setLeft(addRecursive(current.getLeft(), value));
        } else if (comparator.compare(current.getValue(), value) > 0) {
            current.setRight(addRecursive(current.getRight(), value));
        }

        // value already exists
        return current;
    }

}
