package pl.wojma.funwithxmls.core.ports;

import java.io.InputStream;
import pl.wojma.funwithxmls.core.model.StructuredNode;

/**
 * Port odpowiedzialny za parsowanie dokumentu strukturalnego do modelu neutralnego.
 */
public interface StructuredDocumentParser {
    /**
     * Parsuje dokument ze strumienia wejściowego.
     *
     * @param inputStream strumień dokumentu wejściowego
     * @return korzeń drzewa strukturalnego
     */
    StructuredNode parse(InputStream inputStream);
}
