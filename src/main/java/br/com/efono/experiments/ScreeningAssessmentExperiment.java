package br.com.efono.experiments;

import br.com.efono.tree.BinaryTreePrinter;
import br.com.efono.util.Defaults;
import br.com.efono.util.FileUtils;
import java.io.File;
import java.util.Arrays;
import java.util.Properties;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2024, May 11.
 */
public class ScreeningAssessmentExperiment {

    private void init(final Properties prop) {
        // output to store generated files
        File output = null;
        String outputDir = prop.getProperty(FileUtils.OUTPUT_DIR_PROP_NAME);
        if (outputDir != null) {
            output = new File(outputDir);
        }

        // just print the UML tree
        Defaults.TREE.init(Defaults.SORTED_WORDS);
        BinaryTreePrinter.print(Defaults.TREE);
    }

    /**
     * Main method.
     *
     * @param args Command line arguments.
     */
    public static void main(final String[] args) {
        System.out.println("Arguments received: " + Arrays.toString(args));
        Properties prop = FileUtils.readProperties(args);

        new ScreeningAssessmentExperiment().init(prop);
    }


}
