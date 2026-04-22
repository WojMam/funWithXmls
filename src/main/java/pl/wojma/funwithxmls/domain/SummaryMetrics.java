package pl.wojma.funwithxmls.domain;

/**
 * Zawiera zagregowane metryki porównania dwóch XML.
 *
 * @param matchingFields liczba zgodnych pól
 * @param leftOnlyFields liczba pól obecnych tylko w pierwszym XML
 * @param rightOnlyFields liczba pól obecnych tylko w drugim XML
 * @param valueDifferences liczba różnic wartości
 * @param orderDifferences liczba różnic kolejności
 * @param occurrenceDifferences liczba różnic liczności obiektów
 */
public record SummaryMetrics(
        int matchingFields,
        int leftOnlyFields,
        int rightOnlyFields,
        int valueDifferences,
        int orderDifferences,
        int occurrenceDifferences
) {
}
