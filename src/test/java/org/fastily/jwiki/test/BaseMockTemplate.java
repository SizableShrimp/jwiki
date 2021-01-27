package org.fastily.jwiki.test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.fastily.jwiki.core.Wiki;
import org.fastily.jwiki.core.WikiLogger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Template for mock tests.
 *
 * @author Fastily
 */
public class BaseMockTemplate {
    /**
     * The mock MediaWiki server
     */
    protected static MockWebServer server;

    /**
     * The test Wiki object to use.
     */
    protected Wiki wiki;

    /**
     * Initializes mock server
     *
     * @throws IOException If the MockWebServer failed to start.
     */
    @BeforeAll
    static void setUpServer() throws IOException {
        server = new MockWebServer();
        server.start();

        WikiLogger.info(null, "MockServer is @ [{}]", server.url("/w/api.php"));
    }

    /**
     * Disposes of mock server
     *
     * @throws IOException If the MockWebServer failed to exit.
     */
    @AfterAll
    static void tearDownServer() throws IOException {
        server.shutdown();
        server = null;
    }

    /**
     * Initializes mock wiki
     */
    @BeforeEach
    void setUp() {
        initWiki();
    }

    /**
     * Disposes of mock wiki
     */
    @AfterEach
    void tearDown() {
        wiki = null;
    }

    /**
     * Loads a MockResponse into the {@code server}'s queue.
     *
     * @param fn The text file, without a {@code .txt} extension, to load a response from.
     */
    protected void addResponse(String fn) {
        try {
            server.enqueue(new MockResponse()
                    .setBody(String.join("\n", Files.readAllLines(Paths.get(getClass().getResource(fn + ".json").toURI())))));
        } catch (URISyntaxException | IOException e) {
            WikiLogger.error(wiki, "Error during mock generation response", e);
            fail(e);
        }
    }

    /**
     * Initializes the mock Wiki object. Runs with {@code setUp()}; override this to customize {@code wiki}'s
     * initialization behavior.
     */
    protected void initWiki() {
        addResponse("mockNSInfo");
        wiki = new Wiki.Builder().withApiEndpoint(server.url("/w/api.php")).build();
    }
}