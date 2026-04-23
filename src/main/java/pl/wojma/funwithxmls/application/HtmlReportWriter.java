package pl.wojma.funwithxmls.application;

import java.nio.file.Path;

/**
 * Port odpowiedzialny za zapis gotowego raportu HTML.
 */
public interface HtmlReportWriter {
    /**
     * Zapisuje raport HTML pod wskazaną ścieżką.
     *
     * @param html gotowa treść raportu
     * @param outputPath ścieżka docelowa zapisu
     */
    void saveHtml(String html, Path outputPath);
}
