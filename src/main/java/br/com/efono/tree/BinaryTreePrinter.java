package br.com.efono.tree;

import java.io.PrintStream;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jul 06.
 */
public class BinaryTreePrinter {

    private static String traversePreOrder(final Node root) {
        if (root == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(root.printValue());

        String pointerRight = "└──";
        String pointerLeft = (root.getRight() != null) ? "├──" : "└──";

        traverseNodes(sb, "", pointerLeft, root.getLeft(), root.getRight() != null);
        traverseNodes(sb, "", pointerRight, root.getRight(), false);

        return sb.toString();
    }

    private static void traverseNodes(final StringBuilder sb, final String padding, final String pointer,
            final Node node, final boolean hasRightSibling) {
        if (node != null) {
            sb.append("\n");
            sb.append(padding);
            sb.append(pointer);
            sb.append(node.printValue());

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
     * Prints the tree. The first value is the root. The second branch is left side and the other is the right side of
     * the binary tree.
     *
     * @param tree
     * @param os Output stream.
     */
    @Deprecated
    public static void print(final BinaryTree tree, final PrintStream os) {
        os.print(traversePreOrder(tree.getRoot()));
    }

    /**
     * Prints the given BST.
     *
     * @param tree Tree to print in UML (plantUML format).
     */
    public static void print(final BinaryTree tree) {
        System.out.println("-------------------");
        System.out.println(
            "@startuml\n"
            + "top to bottom direction");

        BinaryTreePrinter.printUML(tree.getRoot());

        System.out.println("@enduml");
        System.out.println("\n-------------------");
    }

    /**
     * Prints the content of a Plant UML diagram.
     *
     * @param root Root node.
     */
    public static void printUML(final Node root) {
        /*
        Start the file with:
        @startuml
top to bottom direction
         */
        if (root != null) {
            if (root.getLeft() != null) {
                System.out.println("(" + root.printValue() + ") --> (" + root.getLeft().printValue() + ") : L");
            }

            if (root.getRight() != null) {
                System.out.println("(" + root.printValue() + ") --> (" + root.getRight().printValue() + ") : R");
            }

            printUML(root.getLeft());
            printUML(root.getRight());
        }

        /*
        @enduml
         */
    }

}
