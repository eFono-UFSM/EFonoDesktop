package br.com.efono.tree;

import java.util.Comparator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 06.
 */
public class BinaryTreePrinterTest {

    /**
     * Just prints an example tree.
     */
    @Test
    public void test() {
        Node<String> root = new Node("root");

        Node<String> node1 = new Node("node1");
        Node<String> node2 = new Node("node2");
        root.setLeft(node1);
        root.setRight(node2);

        Node<String> node3 = new Node("node3");
        Node<String> node4 = new Node("node4");
        node1.setLeft(node3);
        node1.setRight(node4);

        node2.setLeft(new Node("node5"));
        node2.setRight(new Node("node6"));

        Node<String> node7 = new Node("node7");
        node3.setLeft(node7);
        node7.setLeft(new Node("node8"));
        node7.setRight(new Node("node9"));

        final Comparator<String> comp = (final String o1, final String o2) -> {
            int val1 = Integer.parseInt(o1.replaceFirst("node", ""));
            int val2 = Integer.parseInt(o2.replaceFirst("node", ""));

            return val1 - val2;
        };
        BinaryTree<String> tree = new BinaryTree(root, comp);

        BinaryTreePrinter.print(tree, System.out);
        fail();
    }

}
