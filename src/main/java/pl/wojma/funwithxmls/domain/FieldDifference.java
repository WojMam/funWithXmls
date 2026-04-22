package pl.wojma.funwithxmls.domain;

/**
 * Reprezentuje pojedynczą różnicę lub zgodność dla konkretnej ścieżki pola.
 *
 * @param path pełna ścieżka pola
 * @param type typ wpisu różnicy
 * @param leftValue wartość z pierwszego XML, jeśli istnieje
 * @param rightValue wartość z drugiego XML, jeśli istnieje
 * @param leftOccurrences liczba wystąpień pola w pierwszym XML
 * @param rightOccurrences liczba wystąpień pola w drugim XML
 * @param note dodatkowa adnotacja wyjaśniająca wynik
 */
public record FieldDifference(
        String path,
        DifferenceType type,
        String leftValue,
        String rightValue,
        int leftOccurrences,
        int rightOccurrences,
        String note
) {
}
