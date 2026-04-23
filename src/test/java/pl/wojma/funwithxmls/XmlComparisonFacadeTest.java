package pl.wojma.funwithxmls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.entrypoint.XmlComparisonFacade;

/**
 * Testy publicznej fasady bibliotecznej do porównywania XML.
 */
class XmlComparisonFacadeTest {
    private final XmlComparisonFacade facade = new XmlComparisonFacade();

    @Test
    void shouldCompareStreamsWithoutFilesystemDependency() {
        ByteArrayInputStream left = stream("<root><status>NEW</status></root>");
        ByteArrayInputStream right = stream("<root><status>NEW</status></root>");

        ComparisonResult result = facade.compare(left, right, ComparisonMode.STRUCTURE_AND_VALUES);

        assertEquals(ComparisonMode.STRUCTURE_AND_VALUES, result.mode());
        assertTrue(result.summary().matchingFields() >= 2);
    }

    @Test
    void shouldRenderHtmlFromComparisonResult() {
        ComparisonResult result = facade.compare(
                stream("<root><status>NEW</status></root>"),
                stream("<root><status>OLD</status></root>"),
                "left-memory",
                "right-memory",
                ComparisonMode.STRUCTURE_AND_VALUES
        );

        String html = facade.renderHtml(result);

        assertTrue(html.contains("<!DOCTYPE html>"));
        assertTrue(html.contains("left-memory"));
        assertTrue(html.contains("right-memory"));
    }

    @Test
    void shouldCompareAndRenderInSingleCall() {
        String html = facade.compareAndRender(
                stream("<root><item>1</item></root>"),
                stream("<root><item>2</item></root>"),
                ComparisonMode.STRUCTURE_AND_VALUES
        );

        assertTrue(html.contains("Raport porównania XML"));
        assertTrue(html.contains("Różna wartość"));
    }

    private ByteArrayInputStream stream(String xml) {
        return new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
    }
}
