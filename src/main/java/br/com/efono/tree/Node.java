package br.com.efono.tree;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 05.
 * @param <E> Content type of the node.
 */
public class Node<E> {

    private final E value;
    private Node left;
    private Node right;

    /**
     * Creates a node with the given value.
     *
     * @param value Node value.
     */
    public Node(final E value) {
        this.value = value;
        right = null;
        left = null;
    }

    /**
     * @return The node value.
     */
    public E getValue() {
        return value;
    }

    /**
     * Sets the left child node.
     *
     * @param left The child node.
     */
    public void setLeft(final Node left) {
        this.left = left;
    }

    /**
     * @return The left child.
     */
    public Node getLeft() {
        return left;
    }

    /**
     * Sets the right child node.
     *
     * @param right The child node.
     */
    public void setRight(final Node right) {
        this.right = right;
    }

    /**
     * @return The right child.
     */
    public Node getRight() {
        return right;
    }

}
