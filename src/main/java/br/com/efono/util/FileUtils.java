package br.com.efono.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
                    if (!line.isBlank()) {
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

}
