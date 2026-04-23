package pl.wojma.funwithxmls.core.ports;

import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.core.model.StructuredNode;

/**
 * Port silnika porównującego dwa dokumenty strukturalne.
 */
public interface StructuredComparator {
    /**
     * Wykonuje porównanie dwóch dokumentów.
     *
     * @param left pierwszy dokument
     * @param right drugi dokument
     * @param leftSource źródło pierwszego dokumentu
     * @param rightSource źródło drugiego dokumentu
     * @param mode tryb porównania
     * @return wynik porównania
     */
    ComparisonResult compare(StructuredNode left, StructuredNode right, String leftSource, String rightSource, ComparisonMode mode);
}
