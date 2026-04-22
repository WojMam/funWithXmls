package pl.wojma.funwithxmls.entrypoint;

import pl.wojma.funwithxmls.application.CompareXmlRequest;
import pl.wojma.funwithxmls.application.CompareXmlUseCase;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.infrastructure.DefaultXmlResourceLoader;
import pl.wojma.funwithxmls.infrastructure.DomXmlDocumentParser;
import pl.wojma.funwithxmls.infrastructure.HtmlReportRenderer;
import pl.wojma.funwithxmls.infrastructure.SmartXmlComparator;

/**
 * Fasada aplikacyjna udostępniająca prostą metodę porównania XML po ścieżkach relatywnych.
 */
public class XmlComparisonService {
    private final CompareXmlUseCase useCase;

    /**
     * Tworzy serwis z domyślną konfiguracją wszystkich komponentów.
     */
    public XmlComparisonService() {
        this.useCase = new CompareXmlUseCase(
                new DefaultXmlResourceLoader(),
                new DomXmlDocumentParser(),
                new SmartXmlComparator(),
                new HtmlReportRenderer()
        );
    }

    /**
     * Porównuje dwa XML i generuje raport HTML.
     *
     * @param leftRelativePath ścieżka relatywna pierwszego XML w resources
     * @param rightRelativePath ścieżka relatywna drugiego XML w resources
     * @param outputRelativePath ścieżka relatywna raportu HTML
     * @param mode tryb porównania
     * @return wynik porównania
     */
    public ComparisonResult compare(String leftRelativePath, String rightRelativePath, String outputRelativePath, ComparisonMode mode) {
        CompareXmlRequest request = new CompareXmlRequest(leftRelativePath, rightRelativePath, outputRelativePath, mode);
        return useCase.execute(request);
    }
}
