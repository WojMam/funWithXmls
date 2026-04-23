package pl.wojma.funwithxmls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import pl.wojma.funwithxmls.adapters.report.FileHtmlReportWriter;

/**
 * Testy zapisu raportu HTML do pliku.
 */
class FileHtmlReportWriterTest {
    @Test
    void shouldSaveHtmlToRequestedPath() throws Exception {
        FileHtmlReportWriter writer = new FileHtmlReportWriter();
        Path tempDir = Files.createTempDirectory("xml-writer-test");
        Path outputPath = tempDir.resolve("nested").resolve("report.html");
        String html = "<html><body>ok</body></html>";

        writer.saveHtml(html, outputPath);

        assertTrue(Files.exists(outputPath));
        assertEquals(html, Files.readString(outputPath, StandardCharsets.UTF_8));
    }
}
