package pl.wojma.funwithxmls.infrastructure;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import pl.wojma.funwithxmls.application.XmlResourceLoader;

/**
 * Loader plików XML korzystający z katalogu resources projektu.
 */
public class DefaultXmlResourceLoader implements XmlResourceLoader {
    private static final Path FILESYSTEM_RESOURCES = Paths.get("src", "main", "resources");
    private static final Path REPORTS_ROOT = Paths.get("target", "reports");

    @Override
    public InputStream openXml(String relativePath) {
        String normalized = normalize(relativePath);
        InputStream classpathStream = DefaultXmlResourceLoader.class.getClassLoader().getResourceAsStream(normalized);
        if (classpathStream != null) {
            return classpathStream;
        }

        Path xmlPath = FILESYSTEM_RESOURCES.resolve(normalized);
        if (Files.notExists(xmlPath)) {
            throw new IllegalArgumentException("Nie znaleziono pliku XML w resources: " + relativePath);
        }
        try {
            return Files.newInputStream(xmlPath);
        } catch (IOException exception) {
            throw new UncheckedIOException("Nie można otworzyć pliku XML: " + relativePath, exception);
        }
    }

    @Override
    public Path resolveOutputPath(String relativePath) {
        return REPORTS_ROOT.resolve(normalize(relativePath)).normalize();
    }

    private String normalize(String relativePath) {
        String normalized = relativePath.replace("\\", "/");
        if (normalized.startsWith("/")) {
            return normalized.substring(1);
        }
        return normalized;
    }
}
