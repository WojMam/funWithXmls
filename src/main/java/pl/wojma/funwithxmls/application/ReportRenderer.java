package pl.wojma.funwithxmls.application;

import pl.wojma.funwithxmls.domain.ComparisonResult;

/**
 * Port odpowiedzialny za renderowanie raportu końcowego.
 */
public interface ReportRenderer {
    /**
     * Renderuje wynik porównania do pojedynczego dokumentu HTML.
     *
     * @param result wynik porównania XML
     * @return gotowa treść dokumentu HTML
     */
    String render(ComparisonResult result);
}
