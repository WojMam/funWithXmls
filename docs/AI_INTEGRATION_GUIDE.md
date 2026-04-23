# AI Integration Quickstart (XML Comparator)

Ten dokument jest krótką instrukcją dla innego AI (np. Copilot), jak szybko i poprawnie przenieść komparator XML do innego projektu Java.

## 1) Cel integracji

Po integracji masz 3 scenariusze:

1. Porównanie dwóch XML z `InputStream` i otrzymanie `ComparisonResult`.
2. Wygenerowanie HTML jako `String` (bez zapisu do pliku).
3. Opcjonalny zapis HTML do pliku.

## 2) Wymagania

- Java 21
- Maven
- Kodowanie UTF-8

## 3) Minimalny zestaw klas do przeniesienia

Skopiuj te pakiety/klasy:

- `pl.wojma.funwithxmls.domain` (cały pakiet)
- `pl.wojma.funwithxmls.application`
  - `CompareXmlUseCase`
  - `RenderHtmlUseCase`
  - `XmlComparator`
  - `XmlDocumentParser`
  - `ReportRenderer`
  - `HtmlReportWriter` (jeśli chcesz zapis plikowy)
- `pl.wojma.funwithxmls.infrastructure`
  - `SmartXmlComparator`
  - `DomXmlDocumentParser`
  - `HtmlReportRenderer`
  - `FileHtmlReportWriter` (opcjonalnie)
- `pl.wojma.funwithxmls.entrypoint`
  - `XmlComparisonFacade` (główne API biblioteczne)

Do integracji bibliotecznej nie są wymagane:

- `Main`
- `XmlComparisonService`
- `DefaultXmlResourceLoader`

Te klasy są potrzebne głównie do scenariusza CLI/plikowego.

## 4) Szybkie użycie API (bez zapisu do pliku)

```java
XmlComparisonFacade facade = new XmlComparisonFacade();

ComparisonResult result = facade.compare(
    leftInputStream,
    rightInputStream,
    ComparisonMode.STRUCTURE_AND_VALUES
);

String html = facade.renderHtml(result);
```

Lub skrót:

```java
String html = facade.compareAndRender(
    leftInputStream,
    rightInputStream,
    ComparisonMode.STRUCTURE_ONLY
);
```

## 5) Opcjonalny zapis HTML do pliku

```java
HtmlReportWriter writer = new FileHtmlReportWriter();
writer.saveHtml(html, Path.of("reports", "xml-report.html"));
```

## 6) Checklista po integracji

1. Uruchom porównanie prostych XML:
   - `<root><a>1</a></root>` vs `<root><a>1</a></root>`
   - oczekuj zgodności.
2. Sprawdź różnicę wartości:
   - `<a>1</a>` vs `<a>2</a>`
   - oczekuj `VALUE_MISMATCH` w trybie `STRUCTURE_AND_VALUES`.
3. Sprawdź raport HTML:
   - czy `html` zawiera `<!DOCTYPE html>` i `Raport porównania XML`.
4. Jeśli zapisujesz plik:
   - upewnij się, że katalog docelowy powstaje i plik jest zapisany w UTF-8.

## 7) Typowe pułapki

- Użycie `STRUCTURE_ONLY`, gdy oczekujesz porównania wartości.
- Przekazanie zużytych/nieprzewiniętych `InputStream`.
- Pominięcie klas z `domain` (to są modele kontraktowe całego rozwiązania).
- Mieszanie scenariusza bibliotecznego ze scenariuszem opartym o `resources`.

## 8) Gotowy prompt dla innego AI

Użyj tego promptu w narzędziu AI:

```text
Zintegruj komparator XML jako helper w moim projekcie Java 21.
Skopiuj klasy wskazane w docs/AI_INTEGRATION_GUIDE.md (sekcja "Minimalny zestaw klas do przeniesienia").
Wykorzystaj XmlComparisonFacade jako publiczne API.
Dodaj klasę serwisową IntegrationXmlComparatorService z metodami:
- compare(InputStream left, InputStream right, ComparisonMode mode)
- compareAndRender(InputStream left, InputStream right, ComparisonMode mode)
Dodaj też opcjonalny zapis HTML przez FileHtmlReportWriter.
Na końcu utwórz test integracyjny potwierdzający:
- zgodność dla identycznych XML,
- VALUE_MISMATCH dla różnych wartości,
- obecność "<!DOCTYPE html>" w wygenerowanym HTML.
Nie modyfikuj logiki algorytmu porównywania.
```
