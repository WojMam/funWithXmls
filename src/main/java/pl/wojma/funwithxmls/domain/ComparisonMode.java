package pl.wojma.funwithxmls.domain;

/**
 * Definiuje tryb porównania dwóch dokumentów XML.
 */
public enum ComparisonMode {
    /**
     * Porównuje wyłącznie strukturę dokumentu i ścieżki pól.
     */
    STRUCTURE_ONLY,

    /**
     * Porównuje strukturę dokumentu oraz wartości pól.
     */
    STRUCTURE_AND_VALUES
}
