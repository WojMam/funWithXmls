package pl.wojma.funwithxmls.core.usecase;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import pl.wojma.funwithxmls.core.ports.HtmlReportWriter;
import pl.wojma.funwithxmls.core.ports.XmlResourceLoader;
import pl.wojma.funwithxmls.domain.ComparisonResult;

/**
 * Orkiestrator scenariusza plikowego: porównaj dokument, wyrenderuj HTML i zapisz raport.
 */
public class CompareAndSaveReportUseCase {
    private final XmlResourceLoader resourceLoader;
    private final CompareXmlUseCase compareXmlUseCase;
    private final RenderHtmlUseCase renderHtmlUseCase;
    private final HtmlReportWriter htmlReportWriter;

    /**
     * Tworzy orkiestrator scenariusza plikowego.
     *
     * @param resourceLoader loader plików wejściowych i ścieżek raportu
     * @param compareXmlUseCase use-case porównania
     * @param renderHtmlUseCase use-case renderowania HTML
     * @param htmlReportWriter writer zapisu raportu
     */
    public CompareAndSaveReportUseCase(
            XmlResourceLoader resourceLoader,
            CompareXmlUseCase compareXmlUseCase,
            RenderHtmlUseCase renderHtmlUseCase,
            HtmlReportWriter htmlReportWriter
    ) {
        this.resourceLoader = resourceLoader;
        this.compareXmlUseCase = compareXmlUseCase;
        this.renderHtmlUseCase = renderHtmlUseCase;
        this.htmlReportWriter = htmlReportWriter;
    }

    /**
     * Wykonuje pełny scenariusz porównania i zapisu raportu.
     *
     * @param request dane wejściowe porównania plikowego
     * @return wynik porównania dokumentów
     */
    public ComparisonResult execute(CompareXmlRequest request) {
        try (InputStream leftStream = resourceLoader.openXml(request.leftRelativePath());
             InputStream rightStream = resourceLoader.openXml(request.rightRelativePath())) {
            ComparisonResult result = compareXmlUseCase.execute(
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
            throw new UncheckedIOException("Nie udało się wykonać scenariusza porównania i zapisu raportu.", exception);
        }
    }
}
