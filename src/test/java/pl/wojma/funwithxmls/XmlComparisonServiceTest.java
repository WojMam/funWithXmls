package pl.wojma.funwithxmls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.services.XmlComparisonService;

/**
 * Testy kompatybilności scenariusza plikowego używanego przez CLI.
 */
class XmlComparisonServiceTest {
    @Test
    void shouldKeepFileBasedFlowForCliCompatibility() {
        XmlComparisonService service = new XmlComparisonService();
        String reportName = "cli-compatibility-report.html";

        ComparisonResult result = service.compare(
                "xml/sample-left.xml",
                "xml/sample-right.xml",
                reportName,
                ComparisonMode.STRUCTURE_ONLY
        );

        Path outputPath = Path.of("target", "reports", reportName);
        assertTrue(Files.exists(outputPath));
        assertEquals(ComparisonMode.STRUCTURE_ONLY, result.mode());
    }
}
