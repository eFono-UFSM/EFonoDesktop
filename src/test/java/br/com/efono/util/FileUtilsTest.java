package br.com.efono.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
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
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    @Test(expected = FileNotFoundException.class)
    public void testExceptionsReadCSV() throws URISyntaxException, IOException {
        System.out.println("testReadTranscriptions - invalid path - FileNotFoundException");

        File resDir = new File(UtilTest.class.getResource("/data").toURI());
        File invalid = new File(resDir, "invalid.csv");
        FileUtils.readCSV(invalid, ",");
    }
    
    /**
     * Tests {@link FileUtils#readCSV(File, String)}.
     * @throws java.net.URISyntaxException
     * @throws java.io.IOException
     */
    @Test
    public void testReadCSV() throws URISyntaxException, IOException {
        System.out.println("testReadCSV - reading file");
        File file = new File(UtilTest.class.getResource("/data/allKnownCases.csv").toURI());
        FileUtils.readCSV(file, ",");
        fail();
    }
    
}
