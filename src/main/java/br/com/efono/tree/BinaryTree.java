package br.com.efono.tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 06.
 * @param <E> Content type of the tree.
 */
public class BinaryTree<E> {

    private final List<E> values = new ArrayList<>();
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
     * @return A copy of the values.
     */
    public List<E> getValues() {
        return new ArrayList<>(values);
    }

    /**
     * Init this tree with an array of values. This will created a balanced tree: the value in the middle of the array
     * will be the root node. Values in left of this one will be at its left as well; the same is true for values in the
     * right side.
     *
     * @param values Values to add.
     */
    public void init(final E[] values) {
        System.out.println("Init binary tree with values: " + Arrays.toString(values));

        if (values != null) {
            this.values.clear();
            root = addNode(values, 0, values.length - 1);
        }
        System.out.println("Tree initialized!");
    }

    public void resetVisited(final Node node) {
        if (node != null) {
            node.setVisited(false);
            resetVisited(node.getLeft());
            resetVisited(node.getRight());
        }
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
     * Clears this tree.
     */
    public void clear() {
        root = null;
    }

    /**
     * @return The root node.
     */
    public Node<E> getRoot() {
        return root;
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

    /**
     * Find the parent node of the node that has the searchData.
     *
     * @param searchData Search data.
     * @return The parent node.
     */
    public Node<E> findParent(final E searchData) {
        return findParent(root, searchData);
    }

    private Node<E> findParent(final Node<E> root, final E searchData) {
        if (root == null || root.getValue().equals(searchData)) {
            return null; // Não há pai ou o próprio nó é a raiz
        }

        if ((root.getLeft() != null && root.getLeft().getValue().equals(searchData))
            || (root.getRight() != null && root.getRight().getValue().equals(searchData))) {
            return root; // O nó atual é o pai do nó procurado
        }

        if (comparator.compare(root.getValue(), searchData) < 0) {
            return findParent(root.getLeft(), searchData);
        }
        return findParent(root.getRight(), searchData);
    }

    public Comparator<E> getComparator() {
        return comparator;
    }

    private Node<E> containsNodeRecursive(final Node<E> current, final E value) {
        if (current == null) {
            return null;
        }
        if (Objects.equals(value, current.getValue())) {
            return current;
        }

        return comparator.compare(current.getValue(), value) < 0
            ? containsNodeRecursive(current.getLeft(), value)
            : containsNodeRecursive(current.getRight(), value);
    }

    private void addValue(final E value) {
        if (!this.values.contains(value)) {
            this.values.add(value);
        }
    }

    private Node addNode(E[] values, int firstIndex, int lastIndex) {
        if (firstIndex == lastIndex) {
            addValue(values[firstIndex]);
            // leaf node
            return new Node(values[firstIndex]);
        } else if (firstIndex > lastIndex) {
            return null;
        }
        int middleIndex = (firstIndex + lastIndex) / 2;

        Node left = addNode(values, firstIndex, middleIndex - 1);
        Node right = addNode(values, middleIndex + 1, lastIndex);

        addValue(values[middleIndex]);
        return new Node(values[middleIndex], left, right);
    }

}
