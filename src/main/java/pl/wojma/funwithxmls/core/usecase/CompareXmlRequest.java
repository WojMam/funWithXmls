package pl.wojma.funwithxmls.core.usecase;

import pl.wojma.funwithxmls.domain.ComparisonMode;

/**
 * Parametry wejściowe dla przypadku użycia porównania dokumentów.
 *
 * @param leftRelativePath ścieżka relatywna do pierwszego dokumentu w katalogu resources
 * @param rightRelativePath ścieżka relatywna do drugiego dokumentu w katalogu resources
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
