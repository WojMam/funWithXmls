package pl.wojma.funwithxmls.infrastructure;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import pl.wojma.funwithxmls.application.XmlComparator;
import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.domain.DifferenceType;
import pl.wojma.funwithxmls.domain.FieldDifference;
import pl.wojma.funwithxmls.domain.OccurrenceStat;
import pl.wojma.funwithxmls.domain.SummaryMetrics;
import pl.wojma.funwithxmls.domain.XmlNode;

/**
 * Silnik porównywania XML z obsługą trybu strukturalnego i trybu wartości.
 */
public class SmartXmlComparator implements XmlComparator {
    @Override
    public ComparisonResult compare(XmlNode left, XmlNode right, String leftSource, String rightSource, ComparisonMode mode) {
        XmlSnapshot leftSnapshot = XmlSnapshot.from(left);
        XmlSnapshot rightSnapshot = XmlSnapshot.from(right);

        List<FieldDifference> differences = compareFieldPaths(leftSnapshot, rightSnapshot, mode);
        List<OccurrenceStat> occurrenceStats = buildOccurrenceStats(leftSnapshot, rightSnapshot);
        SummaryMetrics summary = summarize(differences, occurrenceStats);

        return new ComparisonResult(
                leftSource,
                rightSource,
                mode,
                Instant.now(),
                differences,
                occurrenceStats,
                summary
        );
    }

    private List<FieldDifference> compareFieldPaths(XmlSnapshot leftSnapshot, XmlSnapshot rightSnapshot, ComparisonMode mode) {
        Set<String> allPaths = new HashSet<>();
        allPaths.addAll(leftSnapshot.countByPath.keySet());
        allPaths.addAll(rightSnapshot.countByPath.keySet());
        allPaths.addAll(leftSnapshot.valuesByPath.keySet());
        allPaths.addAll(rightSnapshot.valuesByPath.keySet());

        List<String> sortedPaths = new ArrayList<>(allPaths);
        sortedPaths.sort(Comparator.naturalOrder());

        List<FieldDifference> result = new ArrayList<>();
        for (String path : sortedPaths) {
            int leftCount = leftSnapshot.countByPath.getOrDefault(path, 0);
            int rightCount = rightSnapshot.countByPath.getOrDefault(path, 0);
            List<String> leftValues = leftSnapshot.valuesByPath.getOrDefault(path, Collections.emptyList());
            List<String> rightValues = rightSnapshot.valuesByPath.getOrDefault(path, Collections.emptyList());

            if (leftCount == 0) {
                result.add(new FieldDifference(path, DifferenceType.RIGHT_ONLY, null, summarizeValues(rightValues), 0, rightCount,
                        "Pole występuje wyłącznie w drugim XML."));
                continue;
            }
            if (rightCount == 0) {
                result.add(new FieldDifference(path, DifferenceType.LEFT_ONLY, summarizeValues(leftValues), null, leftCount, 0,
                        "Pole występuje wyłącznie w pierwszym XML."));
                continue;
            }

            if (mode == ComparisonMode.STRUCTURE_ONLY) {
                String note = leftCount == rightCount
                        ? "Zgodna struktura ścieżki."
                        : "Zgodna ścieżka, różna liczność wystąpień.";
                result.add(new FieldDifference(path, DifferenceType.MATCH, summarizeValues(leftValues), summarizeValues(rightValues),
                        leftCount, rightCount, note));
                continue;
            }

            DifferenceType type = DifferenceType.MATCH;
            String note = leftCount == rightCount
                    ? "Pole zgodne."
                    : "Pole zgodne, ale z inną licznością wystąpień.";

            if (!leftValues.equals(rightValues)) {
                if (leftValues.size() == rightValues.size() && hasSameMultiset(leftValues, rightValues)) {
                    type = DifferenceType.ORDER_DIFFERENCE;
                    note = "Wartości są te same, ale występują w innej kolejności.";
                } else {
                    type = DifferenceType.VALUE_MISMATCH;
                    double score = bestEffortScore(leftValues, rightValues);
                    note = "Różnica wartości. Dopasowanie best-effort: " + Math.round(score * 100) + "%.";
                }
            }

            result.add(new FieldDifference(
                    path,
                    type,
                    summarizeValues(leftValues),
                    summarizeValues(rightValues),
                    leftCount,
                    rightCount,
                    note
            ));
        }
        return result;
    }

    private List<OccurrenceStat> buildOccurrenceStats(XmlSnapshot leftSnapshot, XmlSnapshot rightSnapshot) {
        Set<String> allObjectPaths = new HashSet<>();
        allObjectPaths.addAll(leftSnapshot.objectNodesByPath.keySet());
        allObjectPaths.addAll(rightSnapshot.objectNodesByPath.keySet());

        List<OccurrenceStat> result = new ArrayList<>();
        for (String objectPath : allObjectPaths.stream().sorted().toList()) {
            List<XmlNode> leftNodes = leftSnapshot.objectNodesByPath.getOrDefault(objectPath, Collections.emptyList());
            List<XmlNode> rightNodes = rightSnapshot.objectNodesByPath.getOrDefault(objectPath, Collections.emptyList());

            if (leftNodes.size() <= 1 && rightNodes.size() <= 1) {
                continue;
            }

            Set<String> leftSignatures = leftNodes.stream().map(this::canonicalStructure).collect(Collectors.toSet());
            Set<String> rightSignatures = rightNodes.stream().map(this::canonicalStructure).collect(Collectors.toSet());
            boolean structureDifference = !Objects.equals(leftSignatures, rightSignatures);
            result.add(new OccurrenceStat(objectPath, leftNodes.size(), rightNodes.size(), structureDifference));
        }
        return result;
    }

    private SummaryMetrics summarize(List<FieldDifference> differences, List<OccurrenceStat> occurrenceStats) {
        int matching = 0;
        int leftOnly = 0;
        int rightOnly = 0;
        int valueDiff = 0;
        int orderDiff = 0;

        for (FieldDifference difference : differences) {
            switch (difference.type()) {
                case MATCH -> matching++;
                case LEFT_ONLY -> leftOnly++;
                case RIGHT_ONLY -> rightOnly++;
                case VALUE_MISMATCH -> valueDiff++;
                case ORDER_DIFFERENCE -> orderDiff++;
            }
        }

        int occurrenceDiff = (int) occurrenceStats.stream()
                .filter(stat -> stat.leftCount() != stat.rightCount())
                .count();

        return new SummaryMetrics(matching, leftOnly, rightOnly, valueDiff, orderDiff, occurrenceDiff);
    }

    private String summarizeValues(List<String> values) {
        if (values.isEmpty()) {
            return null;
        }
        if (values.size() == 1) {
            return values.getFirst();
        }
        return values.stream().limit(3).collect(Collectors.joining(", ")) + " (x" + values.size() + ")";
    }

    private boolean hasSameMultiset(List<String> leftValues, List<String> rightValues) {
        if (leftValues.size() != rightValues.size()) {
            return false;
        }
        Map<String, Integer> leftCounter = new HashMap<>();
        Map<String, Integer> rightCounter = new HashMap<>();
        for (String value : leftValues) {
            leftCounter.merge(value, 1, Integer::sum);
        }
        for (String value : rightValues) {
            rightCounter.merge(value, 1, Integer::sum);
        }
        return leftCounter.equals(rightCounter);
    }

    private double bestEffortScore(List<String> leftValues, List<String> rightValues) {
        if (leftValues.isEmpty() && rightValues.isEmpty()) {
            return 1.0;
        }
        if (leftValues.isEmpty() || rightValues.isEmpty()) {
            return 0.0;
        }

        List<String> unmatchedRight = new ArrayList<>(rightValues);
        double totalScore = 0.0;
        for (String leftValue : leftValues) {
            int bestIndex = -1;
            double bestScore = -1.0;
            for (int rightIndex = 0; rightIndex < unmatchedRight.size(); rightIndex++) {
                double score = stringSimilarity(leftValue, unmatchedRight.get(rightIndex));
                if (score > bestScore) {
                    bestScore = score;
                    bestIndex = rightIndex;
                }
            }
            if (bestIndex >= 0) {
                unmatchedRight.remove(bestIndex);
                totalScore += bestScore;
            }
        }

        return totalScore / Math.max(leftValues.size(), rightValues.size());
    }

    private double stringSimilarity(String left, String right) {
        if (Objects.equals(left, right)) {
            return 1.0;
        }
        if (left == null || right == null) {
            return 0.0;
        }
        int maxLength = Math.max(left.length(), right.length());
        if (maxLength == 0) {
            return 1.0;
        }
        int lcs = longestCommonSubsequence(left, right);
        return (double) lcs / maxLength;
    }

    private int longestCommonSubsequence(String left, String right) {
        int[][] table = new int[left.length() + 1][right.length() + 1];
        for (int leftIndex = 1; leftIndex <= left.length(); leftIndex++) {
            for (int rightIndex = 1; rightIndex <= right.length(); rightIndex++) {
                if (left.charAt(leftIndex - 1) == right.charAt(rightIndex - 1)) {
                    table[leftIndex][rightIndex] = table[leftIndex - 1][rightIndex - 1] + 1;
                } else {
                    table[leftIndex][rightIndex] = Math.max(table[leftIndex - 1][rightIndex], table[leftIndex][rightIndex - 1]);
                }
            }
        }
        return table[left.length()][right.length()];
    }

    private String canonicalStructure(XmlNode node) {
        if (node.children().isEmpty()) {
            return node.name() + "{}";
        }

        Map<String, Integer> childSignatureCounter = new LinkedHashMap<>();
        for (XmlNode child : node.children()) {
            String childSignature = canonicalStructure(child);
            childSignatureCounter.merge(childSignature, 1, Integer::sum);
        }

        return node.name() + childSignatureCounter.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "#" + entry.getValue())
                .collect(Collectors.joining(",", "{", "}"));
    }

    private static final class XmlSnapshot {
        private final Map<String, Integer> countByPath = new LinkedHashMap<>();
        private final Map<String, List<String>> valuesByPath = new LinkedHashMap<>();
        private final Map<String, List<XmlNode>> objectNodesByPath = new LinkedHashMap<>();

        private static XmlSnapshot from(XmlNode root) {
            XmlSnapshot snapshot = new XmlSnapshot();
            snapshot.visit(root, "");
            return snapshot;
        }

        private void visit(XmlNode node, String parentPath) {
            String currentPath = parentPath.isEmpty() ? node.name() : parentPath + "/" + node.name();
            countByPath.merge(currentPath, 1, Integer::sum);
            if (!node.children().isEmpty()) {
                objectNodesByPath.computeIfAbsent(currentPath, ignored -> new ArrayList<>()).add(node);
            }
            if (node.value() != null) {
                valuesByPath.computeIfAbsent(currentPath, ignored -> new ArrayList<>()).add(node.value());
            }
            for (Map.Entry<String, String> attributeEntry : node.attributes().entrySet()) {
                String attributePath = currentPath + "/@" + attributeEntry.getKey();
                countByPath.merge(attributePath, 1, Integer::sum);
                valuesByPath.computeIfAbsent(attributePath, ignored -> new ArrayList<>()).add(attributeEntry.getValue());
            }
            for (XmlNode child : node.children()) {
                visit(child, currentPath);
            }
        }
    }
}
