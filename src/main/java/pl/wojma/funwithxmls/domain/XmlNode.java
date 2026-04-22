package pl.wojma.funwithxmls.domain;

import java.util.List;
import java.util.Map;

/**
 * Reprezentuje pojedynczy węzeł XML niezależny od modelu biznesowego.
 *
 * @param name nazwa elementu XML
 * @param value wartość tekstowa elementu, jeśli występuje
 * @param attributes atrybuty elementu XML
 * @param children dzieci elementu XML
 */
public record XmlNode(
        String name,
        String value,
        Map<String, String> attributes,
        List<XmlNode> children
) {
    /**
     * Tworzy bezpieczną instancję z niemodyfikowalnymi kolekcjami.
     */
    public XmlNode {
        attributes = Map.copyOf(attributes);
        children = List.copyOf(children);
    }

    /**
     * Informuje, czy element nie posiada dzieci.
     *
     * @return {@code true}, gdy element jest liściem
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }
}
