package pl.wojma.funwithxmls.adapters.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import pl.wojma.funwithxmls.core.model.StructuredNode;
import pl.wojma.funwithxmls.core.ports.StructuredDocumentParser;

/**
 * Implementacja parsera JSON oparta o Jackson i mapująca wynik do modelu neutralnego.
 */
public class JacksonJsonDocumentParser implements StructuredDocumentParser {
    private static final String ROOT_NAME = "root";
    private static final String ARRAY_SUFFIX = "[]";
    private static final String ARRAY_ITEM_NAME = "item";

    private final ObjectMapper objectMapper;

    /**
     * Tworzy parser JSON z domyślną konfiguracją ObjectMapper.
     */
    public JacksonJsonDocumentParser() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public StructuredNode parse(InputStream inputStream) {
        try {
            JsonNode rootNode = objectMapper.readTree(inputStream);
            return mapRoot(rootNode);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Nie udało się sparsować dokumentu JSON.", exception);
        }
    }

    private StructuredNode mapRoot(JsonNode rootNode) {
        if (rootNode == null || rootNode.isNull()) {
            return new StructuredNode(ROOT_NAME, "null", Map.of(), List.of());
        }

        if (rootNode.isObject()) {
            List<StructuredNode> children = new ArrayList<>();
            rootNode.fields().forEachRemaining(entry -> children.addAll(mapNamedNode(entry.getKey(), entry.getValue())));
            return new StructuredNode(ROOT_NAME, null, Map.of(), children);
        }

        if (rootNode.isArray()) {
            List<StructuredNode> children = new ArrayList<>();
            for (JsonNode element : rootNode) {
                children.add(mapArrayElement(ARRAY_ITEM_NAME + ARRAY_SUFFIX, element));
            }
            return new StructuredNode(ROOT_NAME, null, Map.of(), children);
        }

        return new StructuredNode(ROOT_NAME, scalarValue(rootNode), Map.of(), List.of());
    }

    private List<StructuredNode> mapNamedNode(String name, JsonNode node) {
        if (node == null || node.isNull()) {
            return List.of(new StructuredNode(name, "null", Map.of(), List.of()));
        }

        if (node.isArray()) {
            List<StructuredNode> arrayItems = new ArrayList<>();
            for (JsonNode element : node) {
                arrayItems.add(mapArrayElement(name + ARRAY_SUFFIX, element));
            }
            if (arrayItems.isEmpty()) {
                arrayItems.add(new StructuredNode(name + ARRAY_SUFFIX, null, Map.of(), List.of()));
            }
            return arrayItems;
        }

        if (node.isObject()) {
            List<StructuredNode> children = new ArrayList<>();
            node.fields().forEachRemaining(entry -> children.addAll(mapNamedNode(entry.getKey(), entry.getValue())));
            return List.of(new StructuredNode(name, null, Map.of(), children));
        }

        return List.of(new StructuredNode(name, scalarValue(node), Map.of(), List.of()));
    }

    private StructuredNode mapArrayElement(String arrayNodeName, JsonNode element) {
        if (element == null || element.isNull()) {
            return new StructuredNode(arrayNodeName, "null", Map.of(), List.of());
        }

        if (element.isObject()) {
            List<StructuredNode> children = new ArrayList<>();
            for (Entry<String, JsonNode> entry : iterable(element.fields())) {
                children.addAll(mapNamedNode(entry.getKey(), entry.getValue()));
            }
            return new StructuredNode(arrayNodeName, null, Map.of(), children);
        }

        if (element.isArray()) {
            List<StructuredNode> children = new ArrayList<>();
            for (JsonNode nestedElement : element) {
                children.add(mapArrayElement(ARRAY_ITEM_NAME + ARRAY_SUFFIX, nestedElement));
            }
            return new StructuredNode(arrayNodeName, null, Map.of(), children);
        }

        return new StructuredNode(arrayNodeName, scalarValue(element), Map.of(), List.of());
    }

    private String scalarValue(JsonNode node) {
        if (node.isTextual()) {
            return node.textValue();
        }
        if (node.isNumber() || node.isBoolean()) {
            return node.asText();
        }
        if (node.isNull()) {
            return "null";
        }
        return node.toString();
    }

    private <T> Iterable<T> iterable(java.util.Iterator<T> iterator) {
        return () -> iterator;
    }
}
