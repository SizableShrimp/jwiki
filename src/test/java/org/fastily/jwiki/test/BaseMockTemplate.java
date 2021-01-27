package org.fastily.jwiki.test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.fastily.jwiki.core.Wiki;
import org.fastily.jwiki.core.WikiLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Template for mock tests.
 *
 * @author Fastily
 */
public class BaseMockTemplate {
    /**
     * The mock MediaWiki server
     */
    protected MockWebServer server;

    /**
     * The test Wiki object to use.
     */
    protected Wiki wiki;

    /**
     * Initializes mock objects
     *
     * @throws IOException If the MockWebServer failed to start.
     */
    @BeforeEach
    public void setUp() throws IOException {
        server = new MockWebServer();
        server.start();

        initWiki();

        WikiLogger.info(wiki, "MockServer is @ [{}]", server.url("/w/api.php"));
    }

    /**
     * Disposes of mock objects
     *
     * @throws IOException If the MockWebServer failed to exit.
     */
    @AfterEach
    public void tearDown() throws IOException {
        wiki = null;

        server.shutdown();
        server = null;
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