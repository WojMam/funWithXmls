package pl.wojma.funwithxmls.services;

import pl.wojma.funwithxmls.adapters.compare.SmartXmlComparator;
import pl.wojma.funwithxmls.adapters.io.DefaultXmlResourceLoader;
import pl.wojma.funwithxmls.adapters.json.JacksonJsonDocumentParser;
import pl.wojma.funwithxmls.adapters.report.FileHtmlReportWriter;
import pl.wojma.funwithxmls.adapters.report.HtmlReportRenderer;
import pl.wojma.funwithxmls.core.usecase.CompareAndSaveJsonReportUseCase;
import pl.wojma.funwithxmls.core.usecase.CompareJsonUseCase;
import pl.wojma.funwithxmls.core.usecase.CompareXmlRequest;
import pl.wojma.funwithxmls.core.usecase.RenderHtmlUseCase;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;

/**
 * Fasada aplikacyjna udostępniająca metodę porównania JSON po ścieżkach relatywnych.
 */
public class JsonComparisonService {
    private final CompareAndSaveJsonReportUseCase compareAndSaveJsonReportUseCase;

    /**
     * Tworzy serwis z domyślną konfiguracją wszystkich komponentów.
     */
    public JsonComparisonService() {
        this.compareAndSaveJsonReportUseCase = new CompareAndSaveJsonReportUseCase(
                new DefaultXmlResourceLoader(),
                new CompareJsonUseCase(new JacksonJsonDocumentParser(), new SmartXmlComparator()),
                new RenderHtmlUseCase(new HtmlReportRenderer()),
                new FileHtmlReportWriter()
        );
    }

    /**
     * Tworzy serwis z wstrzykniętym orkiestratorem scenariusza plikowego.
     *
     * @param compareAndSaveJsonReportUseCase use-case porównania i zapisu raportu JSON
     */
    public JsonComparisonService(CompareAndSaveJsonReportUseCase compareAndSaveJsonReportUseCase) {
        this.compareAndSaveJsonReportUseCase = compareAndSaveJsonReportUseCase;
    }

    /**
     * Porównuje dwa JSON i generuje raport HTML.
     *
     * @param leftRelativePath ścieżka relatywna pierwszego JSON w resources
     * @param rightRelativePath ścieżka relatywna drugiego JSON w resources
     * @param outputRelativePath ścieżka relatywna raportu HTML
     * @param mode tryb porównania
     * @return wynik porównania
     */
    public ComparisonResult compare(String leftRelativePath, String rightRelativePath, String outputRelativePath, ComparisonMode mode) {
        CompareXmlRequest request = new CompareXmlRequest(leftRelativePath, rightRelativePath, outputRelativePath, mode);
        return compareAndSaveJsonReportUseCase.execute(request);
    }
}
