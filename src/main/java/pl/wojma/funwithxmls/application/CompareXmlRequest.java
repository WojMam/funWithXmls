package pl.wojma.funwithxmls.application;

import pl.wojma.funwithxmls.domain.ComparisonMode;

/**
 * Parametry wejściowe dla przypadku użycia porównania XML.
 *
 * @param leftRelativePath ścieżka relatywna do pierwszego XML w katalogu resources
 * @param rightRelativePath ścieżka relatywna do drugiego XML w katalogu resources
 * @param outputRelativePath ścieżka pliku wyjściowego raportu HTML
 * @param mode tryb porównania
 */
public record CompareXmlRequest(
        String leftRelativePath,
        String rightRelativePath,
        String outputRelativePath,
        ComparisonMode mode
) {
}
