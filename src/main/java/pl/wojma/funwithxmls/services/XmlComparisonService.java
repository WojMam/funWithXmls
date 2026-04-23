package pl.wojma.funwithxmls.services;

import pl.wojma.funwithxmls.adapters.compare.SmartXmlComparator;
import pl.wojma.funwithxmls.adapters.io.DefaultXmlResourceLoader;
import pl.wojma.funwithxmls.adapters.report.FileHtmlReportWriter;
import pl.wojma.funwithxmls.adapters.report.HtmlReportRenderer;
import pl.wojma.funwithxmls.adapters.xml.DomXmlDocumentParser;
import pl.wojma.funwithxmls.core.usecase.CompareAndSaveReportUseCase;
import pl.wojma.funwithxmls.core.usecase.CompareXmlRequest;
import pl.wojma.funwithxmls.core.usecase.CompareXmlUseCase;
import pl.wojma.funwithxmls.core.usecase.RenderHtmlUseCase;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;

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
