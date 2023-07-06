package br.com.efono.tree;

import java.io.PrintStream;
import java.util.Objects;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 06.
 */
public class BinaryTreePrinter {

    private final BinaryTree tree;

    /**
     * Creates a binary tree printer.
     *
     * @param tree The tree to print.
     */
    public BinaryTreePrinter(final BinaryTree tree) {
        this.tree = Objects.requireNonNull(tree);
    }

    private String traversePreOrder(final Node root) {
        if (root == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(root.getValue());

        String pointerRight = "└──";
        String pointerLeft = (root.getRight() != null) ? "├──" : "└──";

        traverseNodes(sb, "", pointerLeft, root.getLeft(), root.getRight() != null);
        traverseNodes(sb, "", pointerRight, root.getRight(), false);

        return sb.toString();
    }

    private void traverseNodes(final StringBuilder sb, final String padding, final String pointer, final Node node,
            final boolean hasRightSibling) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.getValue());

            StringBuilder paddingBuilder = new StringBuilder(padding);
            if (hasRightSibling) {
                paddingBuilder.append("│  ");
            } else {
                paddingBuilder.append("   ");
            }

            String paddingForBoth = paddingBuilder.toString();
            String pointerRight = "└──";
            String pointerLeft = (node.getRight() != null) ? "├──" : "└──";

            traverseNodes(sb, paddingForBoth, pointerLeft, node.getLeft(), node.getRight() != null);
            traverseNodes(sb, paddingForBoth, pointerRight, node.getRight(), false);
        }
    }

    /**
     * Prints the tree.
     *
     * @param os Output stream.
     */
    public void print(final PrintStream os) {
        os.print(traversePreOrder(tree.getRoot()));
    }

}
