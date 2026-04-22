package pl.wojma.funwithxmls.domain;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * Reprezentuje ścieżkę do węzła XML wraz z pełnym zagnieżdżeniem.
 */
public final class NodePath {
    private final List<String> segments;

    /**
     * Tworzy nową ścieżkę z listy segmentów.
     *
     * @param segments segmenty ścieżki od korzenia do liścia
     */
    public NodePath(List<String> segments) {
        this.segments = List.copyOf(segments);
    }

    /**
     * Zwraca nową ścieżkę rozszerzoną o kolejny segment.
     *
     * @param segment nazwa segmentu do dołączenia
     * @return nowa instancja ścieżki
     */
    public NodePath append(String segment) {
        List<String> next = new java.util.ArrayList<>(segments);
        next.add(segment);
        return new NodePath(next);
    }

    /**
     * Zwraca segmenty ścieżki.
     *
     * @return niemodyfikowalna lista segmentów
     */
    public List<String> segments() {
        return segments;
    }

    /**
     * Zwraca ścieżkę w postaci tekstowej oddzielonej znakiem slash.
     *
     * @return ścieżka tekstowa
     */
    public String asString() {
        StringJoiner joiner = new StringJoiner("/");
        for (String segment : segments) {
            joiner.add(segment);
        }
        return joiner.toString();
    }

    @Override
    public String toString() {
        return asString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NodePath nodePath)) {
            return false;
        }
        return Objects.equals(segments, nodePath.segments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(segments);
    }
}
