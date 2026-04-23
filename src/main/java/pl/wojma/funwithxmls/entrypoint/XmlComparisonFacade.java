package pl.wojma.funwithxmls.entrypoint;

import java.io.InputStream;
import pl.wojma.funwithxmls.application.CompareXmlUseCase;
import pl.wojma.funwithxmls.application.RenderHtmlUseCase;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.infrastructure.DomXmlDocumentParser;
import pl.wojma.funwithxmls.infrastructure.HtmlReportRenderer;
import pl.wojma.funwithxmls.infrastructure.SmartXmlComparator;

/**
 * Publiczna fasada biblioteczna do porównywania XML bez konieczności pracy na plikach.
 */
public class XmlComparisonFacade {
    private final CompareXmlUseCase compareXmlUseCase;
    private final RenderHtmlUseCase renderHtmlUseCase;

    /**
     * Tworzy fasadę biblioteczną z domyślnymi komponentami.
     */
    public XmlComparisonFacade() {
        this(
                new CompareXmlUseCase(new DomXmlDocumentParser(), new SmartXmlComparator()),
                new RenderHtmlUseCase(new HtmlReportRenderer())
        );
    }

    /**
     * Tworzy fasadę biblioteczną z wstrzykniętymi use-case’ami.
     *
     * @param compareXmlUseCase use-case porównania XML
     * @param renderHtmlUseCase use-case renderowania raportu
     */
    public XmlComparisonFacade(
            CompareXmlUseCase compareXmlUseCase,
            RenderHtmlUseCase renderHtmlUseCase
    ) {
        this.compareXmlUseCase = compareXmlUseCase;
        this.renderHtmlUseCase = renderHtmlUseCase;
    }

    /**
     * Porównuje dwa dokumenty XML dostarczone jako strumienie.
     *
     * @param leftStream strumień pierwszego XML
     * @param rightStream strumień drugiego XML
     * @param mode tryb porównania
     * @return wynik porównania
     */
    public ComparisonResult compare(InputStream leftStream, InputStream rightStream, ComparisonMode mode) {
        return compareXmlUseCase.execute(leftStream, rightStream, "left-stream", "right-stream", mode);
    }

    /**
     * Porównuje dwa dokumenty XML i opisuje ich źródła w raporcie.
     *
     * @param leftStream strumień pierwszego XML
     * @param rightStream strumień drugiego XML
     * @param leftSource nazwa źródła pierwszego XML
     * @param rightSource nazwa źródła drugiego XML
     * @param mode tryb porównania
     * @return wynik porównania
     */
    public ComparisonResult compare(
            InputStream leftStream,
            InputStream rightStream,
            String leftSource,
            String rightSource,
            ComparisonMode mode
    ) {
        return compareXmlUseCase.execute(leftStream, rightStream, leftSource, rightSource, mode);
    }

    /**
     * Renderuje raport HTML na podstawie gotowego wyniku porównania.
     *
     * @param comparisonResult wynik porównania XML
     * @return raport HTML jako tekst
     */
    public String renderHtml(ComparisonResult comparisonResult) {
        return renderHtmlUseCase.execute(comparisonResult);
    }

    /**
     * Wykonuje porównanie XML i zwraca gotowy raport HTML bez zapisu do pliku.
     *
     * @param leftStream strumień pierwszego XML
     * @param rightStream strumień drugiego XML
     * @param mode tryb porównania
     * @return raport HTML
     */
    public String compareAndRender(InputStream leftStream, InputStream rightStream, ComparisonMode mode) {
        ComparisonResult result = compare(leftStream, rightStream, mode);
        return renderHtml(result);
    }
}
