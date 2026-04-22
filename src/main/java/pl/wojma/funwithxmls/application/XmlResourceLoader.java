package pl.wojma.funwithxmls.application;

import java.io.InputStream;

/**
 * Port dostarczający pliki XML i ścieżki wyjściowe raportu.
 */
public interface XmlResourceLoader {
    /**
     * Otwiera plik XML z katalogu resources.
     *
     * @param relativePath ścieżka relatywna w resources
     * @return strumień danych XML
     */
    InputStream openXml(String relativePath);

    /**
     * Buduje ścieżkę docelową raportu HTML.
     *
     * @param relativePath ścieżka relatywna pliku raportu
     * @return pełna ścieżka systemowa
     */
    java.nio.file.Path resolveOutputPath(String relativePath);
}
