package br.com.efono.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 06.
 */
public class FileUtils {

    /**
     * Reads the given CSV file with the known cases.
     *
     * @param file File to read.
     * @param separator Column separator.
     * @return A list with all lines read. The array indexes represent each column.
     */
    public static List<String[]> readCSV(final File file, final String separator) {
        final List<String[]> list = new ArrayList<>();
        if (file != null && separator != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        String[] row = line.split(separator);
                        list.add(row);
                    }
                }
            } catch (final FileNotFoundException ex) {
                // TODO: substituir por sistema de logs
                System.out.println("File " + file + " not found: " + ex);
            } catch (final IOException ex) {
                System.out.println("Couldn't read file " + file + ": " + ex);
            }
        }
        return list;
    }

    /**
     * Property name to store the output dir to be used to store generated files.
     */
    public static final String OUTPUT_DIR_PROP_NAME = "output.dir";

    /**
     * Reads the properties from the file path given in the first argument.
     *
     * @param args Arguments, the first position keeps the path to read properties from.
     * @return The loaded properties.
     */
    public static Properties readProperties(final String[] args) {
        final Properties prop = new Properties();

        if (args != null && args.length >= 1) {
            try {
                String configPath = args[0];

                System.out.println("Reading config file at " + configPath);
                prop.load(new FileInputStream(configPath));

                prop.put(OUTPUT_DIR_PROP_NAME, new File(configPath).getParent());
            } catch (final IOException e) {
                System.out.println("Couldn't read properties file: " + e);
            }
        }

        return prop;
    }

}
