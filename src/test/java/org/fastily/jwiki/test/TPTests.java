package org.fastily.jwiki.test;

import org.fastily.jwiki.core.WParser;
import org.fastily.jwiki.core.WParser.WTemplate;
import org.fastily.jwiki.core.WParser.WikiText;
import org.fastily.jwiki.core.Wiki;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for jwiki's template parsing package.
 *
 * @author Fastily
 */
class TPTests {
    /**
     * The Wiki object to use for this test set.
     */
    private static final Wiki wiki = new Wiki.Builder().withDomain("test.wikipedia.org").build();

    /**
     * Test parsePage in WParse
     */
    @Test
    void testParsePage() {
        WikiText wt = WParser.parsePage(wiki, "User:Fastily/Sandbox/TPTest1");

        // Test getTemplates
        Set<String> l = wt.getTemplates().stream().map(t1 -> t1.title).collect(Collectors.toSet());
        assertTrue(l.contains("Tl"));
        assertTrue(l.contains("int:license-header"));
        assertTrue(l.contains("Ombox"));
        assertEquals(3, l.size());

        // Test recursive getTemplates
        l = wt.getTemplatesR().stream().map(t -> t.title).collect(Collectors.toSet());
        assertTrue(l.contains("Tlx"));
        assertTrue(l.contains("Ombox"));
        assertTrue(l.contains("Tl"));
        assertTrue(l.contains("int:license-header"));
        assertTrue(l.contains("="));
        assertEquals(5, l.size());
    }

    /**
     * Test parseText in WParse
     */
    @Test
    void testParseText() {
        WikiText wt = WParser.parseText(wiki, wiki.getPageText("User:Fastily/Sandbox/TPTest2"));
        List<WTemplate> wtl = wt.getTemplates();
        assertEquals(1, wtl.size());

        WTemplate t = wtl.get(0);

        // verify internals
        assertEquals("Tl", t.title);
        assertEquals("TEST1", t.get("1").toString());
        assertEquals("{{Tlx|1=FOOBAR|n=123456}}", t.get("another").toString());
        assertEquals("", t.get("empty").toString());
        assertEquals("test <!-- meh --> abc", t.get("asdf").toString());
        assertEquals("big space", t.get("s").toString());

        // test drop
        t.drop();
        assertTrue(wt.getTemplates().isEmpty());
    }

    /**
     * Tests parseText with comments in strange locations
     */
    @Test
    void testParseTextWithComments() {
        WikiText wt = WParser.parseText(wiki, wiki.getPageText("User:Fastily/Sandbox/TPTest3"));
        List<WTemplate> wtl = wt.getTemplates();

        WTemplate t = wtl.get(0);
        assertEquals("Tl", t.title);
        assertEquals("test <!-- meh --> abc", t.get("asdf").toString());
        assertEquals("<!-- ignore --> ok", t.get("bsdf").toString());
    }

    /**
     * Test for WikiText
     */
    @Test
    void testWikiText() {
        WikiText wt = new WikiText();
        assertTrue(wt.getTemplatesR().isEmpty());

        wt.append("foo");
        assertEquals("foo", wt.toString());

        WTemplate tp1 = new WTemplate();
        tp1.title = "Template:test";
        wt.append(tp1);

        assertEquals("foo{{Template:test}}", wt.toString());

        List<WTemplate> wtl = wt.getTemplates();
        assertEquals(1, wtl.size());
        assertEquals("Template:test", wtl.get(0).title);

        tp1.normalizeTitle(wiki);
        assertEquals("Test", tp1.title);

        assertEquals("foo{{Test}}", wt.toString());

        wt.append("bar");
        assertEquals("foo{{Test}}bar", wt.toString());

        tp1.put("baz", "nyah");
        assertEquals("foo{{Test|baz=nyah}}bar", wt.toString());

        tp1.drop();
        assertEquals("foobar", wt.toString());
    }
}