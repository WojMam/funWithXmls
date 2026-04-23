package pl.wojma.funwithxmls.application;

import java.io.InputStream;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.domain.XmlNode;

/**
 * Przypadek użycia odpowiedzialny za porównanie dwóch strumieni XML.
 */
public class CompareXmlUseCase {
    private final XmlDocumentParser documentParser;
    private final XmlComparator xmlComparator;

    /**
     * Tworzy przypadek użycia porównania XML.
     *
     * @param documentParser parser XML
     * @param xmlComparator silnik porównania
     */
    public CompareXmlUseCase(
            XmlDocumentParser documentParser,
            XmlComparator xmlComparator
    ) {
        this.documentParser = documentParser;
        this.xmlComparator = xmlComparator;
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
        XmlNode leftNode = documentParser.parse(leftStream);
        XmlNode rightNode = documentParser.parse(rightStream);
        return xmlComparator.compare(leftNode, rightNode, leftSource, rightSource, mode);
    }
}
