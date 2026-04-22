package pl.wojma.funwithxmls.domain;

import java.time.Instant;
import java.util.List;

/**
 * Pełny wynik porównania dwóch dokumentów XML.
 *
 * @param leftSource nazwa lub ścieżka pierwszego XML
 * @param rightSource nazwa lub ścieżka drugiego XML
 * @param mode zastosowany tryb porównania
 * @param generatedAt czas wygenerowania wyniku
 * @param fieldDifferences lista różnic i zgodności pól
 * @param occurrenceStats statystyki powtarzalnych obiektów
 * @param summary zagregowane metryki podsumowania
 */
public record ComparisonResult(
        String leftSource,
        String rightSource,
        ComparisonMode mode,
        Instant generatedAt,
        List<FieldDifference> fieldDifferences,
        List<OccurrenceStat> occurrenceStats,
        SummaryMetrics summary
) {
    /**
     * Tworzy bezpieczną instancję z niemodyfikowalnymi listami.
     */
    public ComparisonResult {
        fieldDifferences = List.copyOf(fieldDifferences);
        occurrenceStats = List.copyOf(occurrenceStats);
    }
}
