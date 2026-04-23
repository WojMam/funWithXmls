package pl.wojma.funwithxmls.services;

import java.io.InputStream;
import pl.wojma.funwithxmls.adapters.compare.SmartXmlComparator;
import pl.wojma.funwithxmls.adapters.json.JacksonJsonDocumentParser;
import pl.wojma.funwithxmls.adapters.report.HtmlReportRenderer;
import pl.wojma.funwithxmls.adapters.xml.DomXmlDocumentParser;
import pl.wojma.funwithxmls.core.usecase.CompareJsonUseCase;
import pl.wojma.funwithxmls.core.usecase.CompareXmlUseCase;
import pl.wojma.funwithxmls.core.usecase.RenderHtmlUseCase;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;

/**
 * Publiczna fasada biblioteczna do porównywania XML i JSON bez konieczności pracy na plikach.
 */
public class XmlComparisonFacade {
    private final CompareXmlUseCase compareXmlUseCase;
    private final CompareJsonUseCase compareJsonUseCase;
    private final RenderHtmlUseCase renderHtmlUseCase;

    /**
     * Tworzy fasadę biblioteczną z domyślnymi komponentami.
     */
    public XmlComparisonFacade() {
        this(
                new CompareXmlUseCase(new DomXmlDocumentParser(), new SmartXmlComparator()),
                new CompareJsonUseCase(new JacksonJsonDocumentParser(), new SmartXmlComparator()),
                new RenderHtmlUseCase(new HtmlReportRenderer())
        );
    }

    /**
     * Tworzy fasadę biblioteczną z wstrzykniętymi use-case’ami.
     *
     * @param compareXmlUseCase use-case porównania XML
     * @param compareJsonUseCase use-case porównania JSON
     * @param renderHtmlUseCase use-case renderowania raportu
     */
    public XmlComparisonFacade(
            CompareXmlUseCase compareXmlUseCase,
            CompareJsonUseCase compareJsonUseCase,
            RenderHtmlUseCase renderHtmlUseCase
    ) {
        this.compareXmlUseCase = compareXmlUseCase;
        this.compareJsonUseCase = compareJsonUseCase;
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
    public ComparisonResult compareXml(InputStream leftStream, InputStream rightStream, ComparisonMode mode) {
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
    public ComparisonResult compareXml(
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
    public String compareXmlAndRender(InputStream leftStream, InputStream rightStream, ComparisonMode mode) {
        ComparisonResult result = compareXml(leftStream, rightStream, mode);
        return renderHtml(result);
    }

    /**
     * Porównuje dwa dokumenty JSON dostarczone jako strumienie.
     *
     * @param leftStream strumień pierwszego JSON
     * @param rightStream strumień drugiego JSON
     * @param mode tryb porównania
     * @return wynik porównania
     */
    public ComparisonResult compareJson(InputStream leftStream, InputStream rightStream, ComparisonMode mode) {
        return compareJsonUseCase.execute(leftStream, rightStream, "left-stream", "right-stream", mode);
    }

    /**
     * Porównuje dwa dokumenty JSON i opisuje ich źródła w raporcie.
     *
     * @param leftStream strumień pierwszego JSON
     * @param rightStream strumień drugiego JSON
     * @param leftSource nazwa źródła pierwszego JSON
     * @param rightSource nazwa źródła drugiego JSON
     * @param mode tryb porównania
     * @return wynik porównania
     */
    public ComparisonResult compareJson(
            InputStream leftStream,
            InputStream rightStream,
            String leftSource,
            String rightSource,
            ComparisonMode mode
    ) {
        return compareJsonUseCase.execute(leftStream, rightStream, leftSource, rightSource, mode);
    }

    /**
     * Wykonuje porównanie JSON i zwraca gotowy raport HTML bez zapisu do pliku.
     *
     * @param leftStream strumień pierwszego JSON
     * @param rightStream strumień drugiego JSON
     * @param mode tryb porównania
     * @return raport HTML
     */
    public String compareJsonAndRender(InputStream leftStream, InputStream rightStream, ComparisonMode mode) {
        ComparisonResult result = compareJson(leftStream, rightStream, mode);
        return renderHtml(result);
    }
}
