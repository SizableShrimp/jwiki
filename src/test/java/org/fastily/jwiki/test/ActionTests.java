package org.fastily.jwiki.test;

import okhttp3.HttpUrl;
import org.fastily.jwiki.core.NS;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for WAction. Mocks cases where user is anonymous.
 *
 * @author Fastily
 */
class ActionTests extends BaseMockTemplate {
    /**
     * Sanity check to make sure the mock Wiki object is properly initialized.
     */
    @Test
    void testInitializationForSanity() {
        assertEquals("File:Test.jpg", wiki.convertIfNotInNS("Test.jpg", NS.FILE));

        try {
            assertTrue(server.takeRequest(2, TimeUnit.SECONDS).getHeader("User-Agent").contains("jwiki"));
        } catch (InterruptedException e) {
            fail(e);
        }

        assertEquals(NS.FILE.v, wiki.whichNS("File:Test.jpg").v);
        assertEquals(NS.MAIN.v, wiki.whichNS("hello").v);
    }

    /**
     * Test move
     */
    @Test
    void testMove() {
        addResponse("mockMovePage");
        assertTrue(wiki.move("User:Example/12345678", "User:Example/1", true, true, true, "This is a test").isSuccess());
    }

    /**
     * Test editing
     */
    @Test
    void testEdit() {
        addResponse("mockSuccessEdit");
        assertTrue(wiki.edit("Wikipedia:Sandbox", "Hello, World!", "This is a test").isSuccess());
    }

    /**
     * Tests prepending and appending text via edit.
     */
    @Test
    void testAddText() {
        addResponse("mockSuccessEdit");
        addResponse("mockSuccessEdit");
        assertTrue(wiki.addText("Wikipedia:Sandbox", "Appending text!", "test", true).isSuccess());
        assertTrue(wiki.addText("Wikipedia:Sandbox", "Appending text!", "test", false).isSuccess());
    }

    /**
     * Tests uploading of files
     */
    @Test
    void testUpload() {
        addResponse("mockChunkedUpload");
        addResponse("mockFileUnstash");

        try {
            assertTrue(wiki.upload(Paths.get(getClass().getResource("uploadTestFile.svg").toURI()), "TestSVG.svg", "desc", "summary").isSuccess());
        } catch (Exception e) {
            fail("Should never reach here - is the classpath messed up or a test resource missing?", e);
        }
    }

    /**
     * Tests upload by url functionality.
     */
    @Test
    void testUploadByUrl() {
        addResponse("mockUploadByUrl");

        try {
            assertTrue(wiki.uploadByUrl(HttpUrl.parse("https://upload.wikimedia.org/wikipedia/en/a/a1/Example.jpg"), "TestFile.jpg", "desc", "summary").isSuccess());
        } catch (Exception e) {
            fail("Should never reach here - is the classpath messed up or a test resource missing?", e);
        }
    }

    /**
     * Tests purging of pages
     */
    @Test
    void testPurge() {
        addResponse("mockPagePurge");
        wiki.purge("Foo", "Test", "Wikipedia:Sandbox");
    }
}