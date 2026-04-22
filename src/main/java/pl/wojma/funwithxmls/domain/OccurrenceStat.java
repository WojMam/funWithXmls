package pl.wojma.funwithxmls.domain;

/**
 * Statystyka liczności wystąpień powtarzalnego obiektu XML.
 *
 * @param objectPath ścieżka obiektu
 * @param leftCount liczba wystąpień w pierwszym XML
 * @param rightCount liczba wystąpień w drugim XML
 * @param structureDifference czy występuje różnica strukturalna obiektu
 */
public record OccurrenceStat(
        String objectPath,
        int leftCount,
        int rightCount,
        boolean structureDifference
) {
    /**
     * Określa, czy występuje tylko różnica liczności.
     *
     * @return {@code true}, jeśli struktura jest zgodna, ale liczność różna
     */
    public boolean isCountOnlyDifference() {
        return leftCount != rightCount && !structureDifference;
    }
}
