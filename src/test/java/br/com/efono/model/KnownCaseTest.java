package br.com.efono.model;

import br.com.efono.util.UtilTest;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author João Bolsson (joaovictorbolsson@gmail.com)
 * @version 2023, Jun 03.
 */
public class KnownCaseTest {

    /**
     * Tests {@link KnownCase#loadFile(File)} when invalid path is given.
     *
     * @throws URISyntaxException
     * @throws IOException
     */
    @Test(expected = IOException.class)
    public void test() throws URISyntaxException, IOException {
        System.out.println("testReadTranscriptions - invalid path - FileNotFoundException");

        File resDir = new File(UtilTest.class.getResource("/data").toURI());
        File invalid = new File(resDir, "invalid.json");
        List<KnownCase> cases = KnownCase.loadFile(invalid);
        assertTrue(cases.isEmpty());
    }

    /**
     * Tests {@link KnownCase#loadFile(java.io.File)}.
     *
     * @throws java.net.URISyntaxException Exception.
     * @throws java.io.IOException Exception reading file.
     */
    @Test
    public void testLoadFile() throws URISyntaxException, IOException {
        System.out.println("testLoadFile - tests for valid path but invalid file");
        File empty = new File(UtilTest.class.getResource("/data/empty.txt").toURI());
        List<KnownCase> emptyCases = KnownCase.loadFile(empty);
        assertTrue(emptyCases.isEmpty());

        System.out.println("testLoadFile - test if all cases were read");
        File allCorrect = new File(UtilTest.class.getResource("/data/allCorrect.json").toURI());
        List<KnownCase> cases = KnownCase.loadFile(allCorrect);        
        assertEquals(163, cases.size());
    }

}
