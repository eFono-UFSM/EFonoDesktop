package br.com.efono.util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 06.
 */
public class FileUtilsTest {

    /**
     * Tests {@link FileUtils#readCSV(File, String)} for FileNotFoundException.
     *
     * @throws java.net.URISyntaxException
     */
    @Test
    public void testExceptionsReadCSV() throws URISyntaxException {
        System.out.println("testExceptionsReadCSV - invalid path - FileNotFoundException");

        File resDir = new File(UtilTest.class.getResource("/data").toURI());
        File invalid = new File(resDir, "invalid.csv");
        List<String[]> csv = FileUtils.readCSV(invalid, ",");
        assertTrue(csv.isEmpty());

        System.out.println("testExceptionsReadCSV - empty csv");
        File empty = new File(UtilTest.class.getResource("/data/empty.csv").toURI());
        csv = FileUtils.readCSV(empty, ",");
        assertTrue(csv.isEmpty());

        System.out.println("testExceptionsReadCSV - null separator");
        File file = new File(UtilTest.class.getResource("/data/allKnownCases.csv").toURI());
        csv = FileUtils.readCSV(file, null);
        assertTrue(csv.isEmpty());
    }

    /**
     * Tests {@link FileUtils#readCSV(File, String)}.
     *
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    @Test
    public void testReadCSV() throws URISyntaxException, IOException {
        System.out.println("testReadCSV - reading file");
        File file = new File(UtilTest.class.getResource("/data/allKnownCases.csv").toURI());
        List<String[]> csv = FileUtils.readCSV(file, ",");
        assertEquals(4643, csv.size()); // lines read
    }

}
