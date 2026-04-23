package pl.wojma.funwithxmls.entrypoint;

import pl.wojma.funwithxmls.application.CompareXmlRequest;
import pl.wojma.funwithxmls.application.CompareAndSaveReportUseCase;
import pl.wojma.funwithxmls.application.CompareXmlUseCase;
import pl.wojma.funwithxmls.application.RenderHtmlUseCase;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.infrastructure.DefaultXmlResourceLoader;
import pl.wojma.funwithxmls.infrastructure.DomXmlDocumentParser;
import pl.wojma.funwithxmls.infrastructure.FileHtmlReportWriter;
import pl.wojma.funwithxmls.infrastructure.HtmlReportRenderer;
import pl.wojma.funwithxmls.infrastructure.SmartXmlComparator;

/**
 * Fasada aplikacyjna udostępniająca prostą metodę porównania XML po ścieżkach relatywnych.
 */
public class XmlComparisonService {
    private final CompareAndSaveReportUseCase compareAndSaveReportUseCase;

    /**
     * Tworzy serwis z domyślną konfiguracją wszystkich komponentów.
     */
    public XmlComparisonService() {
        this.compareAndSaveReportUseCase = new CompareAndSaveReportUseCase(
                new DefaultXmlResourceLoader(),
                new CompareXmlUseCase(new DomXmlDocumentParser(), new SmartXmlComparator()),
                new RenderHtmlUseCase(new HtmlReportRenderer()),
                new FileHtmlReportWriter()
        );
    }

    /**
     * Tworzy serwis z wstrzykniętym orkiestratorem scenariusza plikowego.
     *
     * @param compareAndSaveReportUseCase use-case porównania i zapisu raportu
     */
    public XmlComparisonService(CompareAndSaveReportUseCase compareAndSaveReportUseCase) {
        this.compareAndSaveReportUseCase = compareAndSaveReportUseCase;
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
        return compareAndSaveReportUseCase.execute(request);
    }
}
