package br.com.efono.tree;

import java.util.Comparator;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 06.
 * @param <E> Content type of the tree.
 */
public class BinaryTree<E> {

    private Node root;
    private final Comparator comparator;

    /**
     * Creates a binary tree.
     *
     * @param comparator Comparator to compare the nodes.
     */
    public BinaryTree(final Comparator comparator) {
        this.comparator = comparator;
    }

    /**
     * Creates a binary tree.
     *
     * @param root The root node.
     * @param comparator Comparator to compare the nodes.
     */
    public BinaryTree(final Node root, final Comparator comparator) {
        this.root = root;
        this.comparator = comparator;
    }

    /**
     * @return The root node.
     */
    public Node getRoot() {
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

    private Node addRecursive(final Node current, final E value) {
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
