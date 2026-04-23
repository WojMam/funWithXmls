package pl.wojma.funwithxmls;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.domain.DifferenceType;
import pl.wojma.funwithxmls.domain.FieldDifference;
import pl.wojma.funwithxmls.domain.OccurrenceStat;
import pl.wojma.funwithxmls.adapters.compare.SmartXmlComparator;
import pl.wojma.funwithxmls.core.model.StructuredNode;

/**
 * Testy jednostkowe silnika porównania XML.
 */
class SmartXmlComparatorTest {
    private final SmartXmlComparator comparator = new SmartXmlComparator();

    @Test
    void shouldTreatNestedFieldsWithSameLeafNameAsDifferentPaths() {
        StructuredNode left = node("root",
                node("testa", node("cd", "A")),
                node("testbb", node("cd", "B")));
        StructuredNode right = node("root",
                node("testa", node("cd", "A")),
                node("testbb", node("cd", "B")));

        ComparisonResult result = comparator.compare(left, right, "left", "right", ComparisonMode.STRUCTURE_ONLY);

        assertTrue(hasPath(result.fieldDifferences(), "root/testa/cd"));
        assertTrue(hasPath(result.fieldDifferences(), "root/testbb/cd"));
    }

    @Test
    void shouldDetectOrderDifferenceInValuesMode() {
        StructuredNode left = node("root", node("users",
                node("user", node("name", "Tomek")),
                node("user", node("name", "Krzysztof"))));
        StructuredNode right = node("root", node("users",
                node("user", node("name", "Krzysztof")),
                node("user", node("name", "Tomek"))));

        ComparisonResult result = comparator.compare(left, right, "left", "right", ComparisonMode.STRUCTURE_AND_VALUES);
        FieldDifference difference = byPath(result.fieldDifferences(), "root/users/user/name");

        assertEquals(DifferenceType.ORDER_DIFFERENCE, difference.type());
    }

    @Test
    void shouldTrackRepeatedObjectOccurrenceDifferenceSeparatelyFromStructure() {
        StructuredNode left = node("root", node("users",
                node("user", node("name", "Tomek")),
                node("user", node("name", "Krzysztof"))));
        StructuredNode right = node("root", node("users",
                node("user", node("name", "Tomek")),
                node("user", node("name", "Krzysztof")),
                node("user", node("name", "Ania"))));

        ComparisonResult result = comparator.compare(left, right, "left", "right", ComparisonMode.STRUCTURE_AND_VALUES);
        OccurrenceStat stat = result.occurrenceStats().stream()
                .filter(item -> item.objectPath().equals("root/users/user"))
                .findFirst()
                .orElseThrow();

        assertTrue(stat.isCountOnlyDifference());
        assertEquals(2, stat.leftCount());
        assertEquals(3, stat.rightCount());
    }

    @Test
    void shouldDetectStrictValueDifference() {
        StructuredNode left = node("root", node("status", "NEW"));
        StructuredNode right = node("root", node("status", "new"));

        ComparisonResult result = comparator.compare(left, right, "left", "right", ComparisonMode.STRUCTURE_AND_VALUES);
        FieldDifference difference = byPath(result.fieldDifferences(), "root/status");

        assertEquals(DifferenceType.VALUE_MISMATCH, difference.type());
    }

    @Test
    void shouldBuildConsistentSummaryMetrics() {
        StructuredNode left = node("root", node("a", "1"), node("b", "2"));
        StructuredNode right = node("root", node("a", "1"), node("c", "3"));

        ComparisonResult result = comparator.compare(left, right, "left", "right", ComparisonMode.STRUCTURE_AND_VALUES);

        assertTrue(result.summary().matchingFields() >= 2);
        assertEquals(1, result.summary().leftOnlyFields());
        assertEquals(1, result.summary().rightOnlyFields());
    }

    private boolean hasPath(List<FieldDifference> differences, String path) {
        return differences.stream().anyMatch(diff -> diff.path().equals(path));
    }

    private FieldDifference byPath(List<FieldDifference> differences, String path) {
        return differences.stream()
                .filter(diff -> diff.path().equals(path))
                .findFirst()
                .orElseThrow();
    }

    private StructuredNode node(String name, StructuredNode... children) {
        return new StructuredNode(name, null, Map.of(), List.of(children));
    }

    private StructuredNode node(String name, String value) {
        return new StructuredNode(name, value, Map.of(), List.of());
    }
}
