package pl.wojma.funwithxmls.core.usecase;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import pl.wojma.funwithxmls.core.ports.HtmlReportWriter;
import pl.wojma.funwithxmls.core.ports.XmlResourceLoader;
import pl.wojma.funwithxmls.domain.ComparisonResult;

/**
 * Orkiestrator scenariusza plikowego: porównaj JSON, wyrenderuj HTML i zapisz raport.
 */
public class CompareAndSaveJsonReportUseCase {
    private final XmlResourceLoader resourceLoader;
    private final CompareJsonUseCase compareJsonUseCase;
    private final RenderHtmlUseCase renderHtmlUseCase;
    private final HtmlReportWriter htmlReportWriter;

    /**
     * Tworzy orkiestrator scenariusza plikowego dla JSON.
     *
     * @param resourceLoader loader plików JSON i ścieżek raportu
     * @param compareJsonUseCase use-case porównania JSON
     * @param renderHtmlUseCase use-case renderowania HTML
     * @param htmlReportWriter writer zapisu raportu
     */
    public CompareAndSaveJsonReportUseCase(
            XmlResourceLoader resourceLoader,
            CompareJsonUseCase compareJsonUseCase,
            RenderHtmlUseCase renderHtmlUseCase,
            HtmlReportWriter htmlReportWriter
    ) {
        this.resourceLoader = resourceLoader;
        this.compareJsonUseCase = compareJsonUseCase;
        this.renderHtmlUseCase = renderHtmlUseCase;
        this.htmlReportWriter = htmlReportWriter;
    }

    /**
     * Wykonuje pełny scenariusz porównania JSON i zapisu raportu.
     *
     * @param request dane wejściowe porównania plikowego
     * @return wynik porównania dokumentów
     */
    public ComparisonResult execute(CompareXmlRequest request) {
        try (InputStream leftStream = resourceLoader.openXml(request.leftRelativePath());
             InputStream rightStream = resourceLoader.openXml(request.rightRelativePath())) {
            ComparisonResult result = compareJsonUseCase.execute(
                    leftStream,
                    rightStream,
                    request.leftRelativePath(),
                    request.rightRelativePath(),
                    request.mode()
            );
            String html = renderHtmlUseCase.execute(result);
            Path outputPath = resourceLoader.resolveOutputPath(request.outputRelativePath());
            htmlReportWriter.saveHtml(html, outputPath);
            return result;
        } catch (IOException exception) {
            throw new UncheckedIOException("Nie udało się wykonać scenariusza porównania JSON i zapisu raportu.", exception);
        }
    }
}
