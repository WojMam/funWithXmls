package pl.wojma.funwithxmls.core.usecase;

import java.io.InputStream;
import pl.wojma.funwithxmls.core.model.StructuredNode;
import pl.wojma.funwithxmls.core.ports.StructuredComparator;
import pl.wojma.funwithxmls.core.ports.StructuredDocumentParser;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;

/**
 * Przypadek użycia odpowiedzialny za porównanie dwóch strumieni JSON.
 */
public class CompareJsonUseCase {
    private final StructuredDocumentParser documentParser;
    private final StructuredComparator comparator;

    /**
     * Tworzy przypadek użycia porównania JSON.
     *
     * @param documentParser parser JSON
     * @param comparator współdzielony silnik porównania
     */
    public CompareJsonUseCase(
            StructuredDocumentParser documentParser,
            StructuredComparator comparator
    ) {
        this.documentParser = documentParser;
        this.comparator = comparator;
    }

    /**
     * Wykonuje porównanie dwóch dokumentów JSON.
     *
     * @param leftStream strumień pierwszego JSON
     * @param rightStream strumień drugiego JSON
     * @param leftSource opis źródła pierwszego JSON
     * @param rightSource opis źródła drugiego JSON
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
