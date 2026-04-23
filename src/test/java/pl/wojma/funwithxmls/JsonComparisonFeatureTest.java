package pl.wojma.funwithxmls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import pl.wojma.funwithxmls.core.ports.XmlResourceLoader;
import pl.wojma.funwithxmls.core.usecase.CompareAndSaveJsonReportUseCase;
import pl.wojma.funwithxmls.core.usecase.CompareJsonUseCase;
import pl.wojma.funwithxmls.core.usecase.CompareXmlRequest;
import pl.wojma.funwithxmls.core.usecase.CompareXmlUseCase;
import pl.wojma.funwithxmls.core.usecase.RenderHtmlUseCase;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.domain.DifferenceType;
import pl.wojma.funwithxmls.adapters.compare.SmartXmlComparator;
import pl.wojma.funwithxmls.adapters.json.JacksonJsonDocumentParser;
import pl.wojma.funwithxmls.adapters.report.FileHtmlReportWriter;
import pl.wojma.funwithxmls.adapters.report.HtmlReportRenderer;
import pl.wojma.funwithxmls.adapters.xml.DomXmlDocumentParser;
import pl.wojma.funwithxmls.services.JsonComparisonService;

/**
 * Testy nowego feature porównywania JSON oraz spójności względem XML.
 */
class JsonComparisonFeatureTest {
    @Test
    void shouldMapJsonArrayPathsWithoutIndexes() {
        CompareJsonUseCase jsonUseCase = new CompareJsonUseCase(new JacksonJsonDocumentParser(), new SmartXmlComparator());

        ComparisonResult result = jsonUseCase.execute(
                stream("{\"orders\":[{\"id\":\"A\"},{\"id\":\"B\"}]}"),
                stream("{\"orders\":[{\"id\":\"A\"},{\"id\":\"B\"}]}"),
                "left.json",
                "right.json",
                ComparisonMode.STRUCTURE_AND_VALUES
        );

        assertTrue(result.fieldDifferences().stream().anyMatch(item -> item.path().equals("root/orders[]/id")));
    }

    @Test
    void shouldDetectOrderDifferenceForJsonArraysInValuesMode() {
        CompareJsonUseCase jsonUseCase = new CompareJsonUseCase(new JacksonJsonDocumentParser(), new SmartXmlComparator());

        ComparisonResult result = jsonUseCase.execute(
                stream("{\"tags\":[\"a\",\"b\"]}"),
                stream("{\"tags\":[\"b\",\"a\"]}"),
                "left.json",
                "right.json",
                ComparisonMode.STRUCTURE_AND_VALUES
        );

        DifferenceType type = result.fieldDifferences().stream()
                .filter(item -> item.path().equals("root/tags[]"))
                .findFirst()
                .orElseThrow()
                .type();

        assertEquals(DifferenceType.ORDER_DIFFERENCE, type);
    }

    @Test
    void shouldKeepComparableMetricsBetweenXmlAndJsonForEquivalentData() {
        CompareXmlUseCase xmlUseCase = new CompareXmlUseCase(new DomXmlDocumentParser(), new SmartXmlComparator());
        CompareJsonUseCase jsonUseCase = new CompareJsonUseCase(new JacksonJsonDocumentParser(), new SmartXmlComparator());

        ComparisonResult xmlResult = xmlUseCase.execute(
                stream("<root><item>1</item><item>2</item></root>"),
                stream("<root><item>2</item><item>1</item></root>"),
                "left.xml",
                "right.xml",
                ComparisonMode.STRUCTURE_AND_VALUES
        );
        ComparisonResult jsonResult = jsonUseCase.execute(
                stream("{\"item\":[\"1\",\"2\"]}"),
                stream("{\"item\":[\"2\",\"1\"]}"),
                "left.json",
                "right.json",
                ComparisonMode.STRUCTURE_AND_VALUES
        );

        assertEquals(xmlResult.summary().orderDifferences(), jsonResult.summary().orderDifferences());
        assertEquals(xmlResult.summary().valueDifferences(), jsonResult.summary().valueDifferences());
    }

    @Test
    void shouldSupportJsonFileServiceWithDedicatedEntryPoint() {
        Path tempOutput;
        try {
            tempOutput = Files.createTempDirectory("json-service-test").resolve("report.html");
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        XmlResourceLoader inMemoryLoader = new XmlResourceLoader() {
            @Override
            public InputStream openXml(String relativePath) {
                return switch (relativePath) {
                    case "left.json" -> stream("{\"status\":\"new\"}");
                    case "right.json" -> stream("{\"status\":\"done\"}");
                    default -> throw new IllegalArgumentException("Nieznana ścieżka testowa: " + relativePath);
                };
            }

            @Override
            public Path resolveOutputPath(String relativePath) {
                return tempOutput;
            }
        };

        CompareAndSaveJsonReportUseCase useCase = new CompareAndSaveJsonReportUseCase(
                inMemoryLoader,
                new CompareJsonUseCase(new JacksonJsonDocumentParser(), new SmartXmlComparator()),
                new RenderHtmlUseCase(new HtmlReportRenderer()),
                new FileHtmlReportWriter()
        );
        JsonComparisonService service = new JsonComparisonService(useCase);

        ComparisonResult result = service.compare("left.json", "right.json", "ignored.html", ComparisonMode.STRUCTURE_AND_VALUES);

        assertEquals(ComparisonMode.STRUCTURE_AND_VALUES, result.mode());
        assertTrue(Files.exists(tempOutput));
    }

    private ByteArrayInputStream stream(String content) {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }
}
