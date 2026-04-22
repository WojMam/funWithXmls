package pl.wojma.funwithxmls.application;

import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.domain.XmlNode;

/**
 * Port silnika porównującego dwa drzewa XML.
 */
public interface XmlComparator {
    /**
     * Wykonuje porównanie dwóch dokumentów XML.
     *
     * @param left pierwszy dokument
     * @param right drugi dokument
     * @param leftSource źródło pierwszego dokumentu
     * @param rightSource źródło drugiego dokumentu
     * @param mode tryb porównania
     * @return wynik porównania
     */
    ComparisonResult compare(XmlNode left, XmlNode right, String leftSource, String rightSource, ComparisonMode mode);
}
