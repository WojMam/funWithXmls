package pl.wojma.funwithxmls.core.usecase;

import java.io.InputStream;
import pl.wojma.funwithxmls.core.model.StructuredNode;
import pl.wojma.funwithxmls.core.ports.StructuredComparator;
import pl.wojma.funwithxmls.core.ports.StructuredDocumentParser;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;

/**
 * Przypadek użycia odpowiedzialny za porównanie dwóch strumieni XML.
 */
public class CompareXmlUseCase {
    private final StructuredDocumentParser documentParser;
    private final StructuredComparator comparator;

    /**
     * Tworzy przypadek użycia porównania XML.
     *
     * @param documentParser parser XML
     * @param xmlComparator silnik porównania
     */
    public CompareXmlUseCase(
            StructuredDocumentParser documentParser,
            StructuredComparator comparator
    ) {
        this.documentParser = documentParser;
        this.comparator = comparator;
    }

    /**
     * Wykonuje porównanie dwóch dokumentów XML.
     *
     * @param leftStream strumień pierwszego XML
     * @param rightStream strumień drugiego XML
     * @param leftSource opis źródła pierwszego XML
     * @param rightSource opis źródła drugiego XML
     * @param mode tryb porównania
     * @return wynik porównania
     */
    public ComparisonResult execute(
            InputStream leftStream,
            InputStream rightStream,
            String leftSource,
            String rightSource,
            ComparisonMode mode
    ) {
        StructuredNode leftNode = documentParser.parse(leftStream);
        StructuredNode rightNode = documentParser.parse(rightStream);
        return comparator.compare(leftNode, rightNode, leftSource, rightSource, mode);
    }
}
