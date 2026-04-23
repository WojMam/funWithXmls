package pl.wojma.funwithxmls.infrastructure;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import pl.wojma.funwithxmls.application.HtmlReportWriter;

/**
 * Zapisuje raport HTML do pliku w systemie plików.
 */
public class FileHtmlReportWriter implements HtmlReportWriter {
    @Override
    public void saveHtml(String html, Path outputPath) {
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(outputPath, html, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new UncheckedIOException("Nie udało się zapisać raportu HTML do pliku: " + outputPath, exception);
        }
    }
}
