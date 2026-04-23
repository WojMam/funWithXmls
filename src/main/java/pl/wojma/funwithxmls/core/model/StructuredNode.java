package pl.wojma.funwithxmls.core.model;

import java.util.List;
import java.util.Map;

/**
 * Reprezentuje neutralny węzeł dokumentu strukturalnego (XML/JSON).
 *
 * @param name nazwa logiczna pola
 * @param value wartość tekstowa pola, jeśli występuje
 * @param attributes atrybuty pola (używane głównie przez XML)
 * @param children dzieci węzła
 */
public record StructuredNode(
        String name,
        String value,
        Map<String, String> attributes,
        List<StructuredNode> children
) {
    /**
     * Tworzy bezpieczną instancję z niemodyfikowalnymi kolekcjami.
     */
    public StructuredNode {
        attributes = Map.copyOf(attributes);
        children = List.copyOf(children);
    }

    /**
     * Informuje, czy węzeł nie posiada dzieci.
     *
     * @return {@code true}, gdy węzeł jest liściem
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }
}
