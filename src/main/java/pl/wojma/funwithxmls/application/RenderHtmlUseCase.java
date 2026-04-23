package pl.wojma.funwithxmls.application;

import pl.wojma.funwithxmls.domain.ComparisonResult;

/**
 * Przypadek użycia odpowiedzialny za renderowanie raportu HTML z wyniku porównania.
 */
public class RenderHtmlUseCase {
    private final ReportRenderer reportRenderer;

    /**
     * Tworzy przypadek użycia renderowania HTML.
     *
     * @param reportRenderer renderer raportu
     */
    public RenderHtmlUseCase(ReportRenderer reportRenderer) {
        this.reportRenderer = reportRenderer;
    }

    /**
     * Renderuje raport HTML dla wskazanego wyniku porównania.
     *
     * @param comparisonResult wynik porównania XML
     * @return gotowy raport HTML
     */
    public String execute(ComparisonResult comparisonResult) {
        return reportRenderer.render(comparisonResult);
    }
}
