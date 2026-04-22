package pl.wojma.funwithxmls.infrastructure;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import pl.wojma.funwithxmls.application.XmlDocumentParser;
import pl.wojma.funwithxmls.domain.XmlNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementacja parsera XML oparta o DOM z bezpieczną konfiguracją parsera.
 */
public class DomXmlDocumentParser implements XmlDocumentParser {
    @Override
    public XmlNode parse(InputStream inputStream) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            factory.setExpandEntityReferences(false);

            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(inputStream);
            Element root = document.getDocumentElement();
            return mapElement(root);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Nie udało się sparsować dokumentu XML.", exception);
        }
    }

    private XmlNode mapElement(Element element) {
        Map<String, String> attributes = new LinkedHashMap<>();
        NamedNodeMap namedNodeMap = element.getAttributes();
        for (int index = 0; index < namedNodeMap.getLength(); index++) {
            Node attribute = namedNodeMap.item(index);
            attributes.put(attribute.getNodeName(), attribute.getNodeValue());
        }

        List<XmlNode> children = new ArrayList<>();
        StringBuilder ownText = new StringBuilder();
        NodeList nodeList = element.getChildNodes();
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                children.add(mapElement((Element) node));
            } else if (node.getNodeType() == Node.TEXT_NODE) {
                String textContent = node.getTextContent();
                if (textContent != null && !textContent.isBlank()) {
                    ownText.append(textContent.trim());
                }
            }
        }

        String value = ownText.isEmpty() ? null : ownText.toString();
        return new XmlNode(element.getTagName(), value, attributes, children);
    }
}
