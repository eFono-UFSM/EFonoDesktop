package br.com.efono.experiments;

import br.com.efono.model.Assessment;
import br.com.efono.util.FileUtils;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2024, Jun 10.
 */
public class SequencesExperiment extends Experiment {

    public SequencesExperiment(final Properties prop) {
        super(prop);
    }

    @Override
    protected void runExperimentResults(final File outputDirectory) {
        final List<Assessment> assessments = dbUtils.getCompleteAssessmentsFromDB();

        System.out.println("Running experiment with " + assessments.size() + " complete assessments");
        File parent = new File(outputDirectory, "sequences-experiment-results");
        parent.mkdir();
    }

    public static void main(final String[] args) {
        System.out.println("Arguments received: " + Arrays.toString(args));
        Properties prop = FileUtils.readProperties(args);

        new SequencesExperiment(prop).init();
    }



}
