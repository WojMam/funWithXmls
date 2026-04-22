package pl.wojma.funwithxmls.entrypoint;

import pl.wojma.funwithxmls.domain.ComparisonMode;
import pl.wojma.funwithxmls.domain.ComparisonResult;

/**
 * Punkt wejścia CLI dla narzędzia porównywania XML.
 */
public final class Main {
    private Main() {
    }

    /**
     * Uruchamia porównanie XML na podstawie argumentów wejściowych.
     *
     * @param args argumenty: leftPath rightPath mode outputPath
     */
    public static void main(String[] args) {
        if (args.length < 4) {
            printUsage();
            return;
        }

        String leftPath = args[0];
        String rightPath = args[1];
        ComparisonMode mode = ComparisonMode.valueOf(args[2]);
        String outputPath = args[3];

        XmlComparisonService service = new XmlComparisonService();
        ComparisonResult result = service.compare(leftPath, rightPath, outputPath, mode);

        System.out.println("Porównanie zakończone.");
        System.out.println("Tryb: " + result.mode());
        System.out.println("Pola zgodne: " + result.summary().matchingFields());
        System.out.println("Różnice wartości: " + result.summary().valueDifferences());
        System.out.println("Różnice kolejności: " + result.summary().orderDifferences());
        System.out.println("Różnice liczności: " + result.summary().occurrenceDifferences());
        System.out.println("Raport wygenerowany w target/reports/" + outputPath);
    }

    private static void printUsage() {
        System.out.println("Użycie:");
        System.out.println("  mvn exec:java -Dexec.args=\"xml/left.xml xml/right.xml STRUCTURE_AND_VALUES comparison.html\"");
        System.out.println("Dostępne tryby: STRUCTURE_ONLY, STRUCTURE_AND_VALUES");
    }
}
