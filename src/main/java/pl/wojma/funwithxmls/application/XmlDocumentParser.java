package pl.wojma.funwithxmls.application;

import java.io.InputStream;
import pl.wojma.funwithxmls.domain.XmlNode;

/**
 * Port odpowiedzialny za parsowanie strumienia XML do modelu domenowego.
 */
public interface XmlDocumentParser {
    /**
     * Parsuje dokument XML ze strumienia wejściowego.
     *
     * @param inputStream strumień zawartości XML
     * @return korzeń drzewa XML
     */
    XmlNode parse(InputStream inputStream);
}
