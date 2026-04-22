package pl.wojma.funwithxmls.domain;

/**
 * Typ różnicy wykrytej pomiędzy dwoma dokumentami XML.
 */
public enum DifferenceType {
    /**
     * Pole istnieje w obu dokumentach i jest zgodne.
     */
    MATCH,

    /**
     * Pole występuje tylko w pierwszym dokumencie.
     */
    LEFT_ONLY,

    /**
     * Pole występuje tylko w drugim dokumencie.
     */
    RIGHT_ONLY,

    /**
     * Pole występuje w obu dokumentach, ale ma różne wartości.
     */
    VALUE_MISMATCH,

    /**
     * Pole występuje w obu dokumentach, ale kolejność elementów została zmieniona.
     */
    ORDER_DIFFERENCE
}
