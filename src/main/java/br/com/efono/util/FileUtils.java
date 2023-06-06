package br.com.efono.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

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
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void readCSV(final File file, final String separator) throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int i = 1;
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(separator);
                System.out.println("Row " + (i++) + ": "+ Arrays.toString(row));
            }
        }
    }

}
