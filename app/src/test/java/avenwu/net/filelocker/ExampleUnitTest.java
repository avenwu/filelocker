package avenwu.net.filelocker;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void testSubString() throws Exception {
        String src = "xyz.abc";
        String sub = src.substring(0, src.indexOf(".abc"));
        assertTrue(sub.equals("xyz"));
    }
}