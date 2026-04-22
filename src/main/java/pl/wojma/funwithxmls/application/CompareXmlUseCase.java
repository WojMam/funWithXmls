package pl.wojma.funwithxmls.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.domain.XmlNode;

/**
 * Przypadek użycia odpowiedzialny za pełny proces porównania XML i zapis raportu.
 */
public class CompareXmlUseCase {
    private final XmlResourceLoader resourceLoader;
    private final XmlDocumentParser documentParser;
    private final XmlComparator xmlComparator;
    private final ReportRenderer reportRenderer;

    /**
     * Tworzy przypadek użycia porównania XML.
     *
     * @param resourceLoader loader zasobów XML i ścieżek raportu
     * @param documentParser parser XML
     * @param xmlComparator silnik porównania
     * @param reportRenderer renderer raportu HTML
     */
    public CompareXmlUseCase(
            XmlResourceLoader resourceLoader,
            XmlDocumentParser documentParser,
            XmlComparator xmlComparator,
            ReportRenderer reportRenderer
    ) {
        this.resourceLoader = resourceLoader;
        this.documentParser = documentParser;
        this.xmlComparator = xmlComparator;
        this.reportRenderer = reportRenderer;
    }

    /**
     * Wykonuje porównanie i zapisuje raport HTML pod wskazaną ścieżką.
     *
     * @param request dane wejściowe porównania
     * @return wynik porównania
     */
    public ComparisonResult execute(CompareXmlRequest request) {
        try (InputStream leftStream = resourceLoader.openXml(request.leftRelativePath());
             InputStream rightStream = resourceLoader.openXml(request.rightRelativePath())) {
            XmlNode leftNode = documentParser.parse(leftStream);
            XmlNode rightNode = documentParser.parse(rightStream);
            ComparisonResult comparisonResult = xmlComparator.compare(
                    leftNode,
                    rightNode,
                    request.leftRelativePath(),
                    request.rightRelativePath(),
                    request.mode()
            );
            String html = reportRenderer.render(comparisonResult);
            Path outputPath = resourceLoader.resolveOutputPath(request.outputRelativePath());
            Files.createDirectories(outputPath.getParent());
            Files.writeString(outputPath, html, StandardCharsets.UTF_8);
            return comparisonResult;
        } catch (IOException exception) {
            throw new UncheckedIOException("Nie udało się wykonać porównania XML.", exception);
        }
    }
}
