# AI Integration Quickstart (XML/JSON Comparator)

Ten dokument jest krótką instrukcją dla innego AI (np. Copilot), jak szybko i poprawnie przenieść komparator XML/JSON do innego projektu Java.

## 1) Cel integracji

Po integracji masz 4 scenariusze:

1. Porównanie dwóch XML z `InputStream` i otrzymanie `ComparisonResult`.
2. Porównanie dwóch JSON z `InputStream` i otrzymanie `ComparisonResult`.
3. Wygenerowanie HTML jako `String` (bez zapisu do pliku).
4. Opcjonalny zapis HTML do pliku.

## 2) Wymagania

- Java 21
- Maven
- Kodowanie UTF-8
- JSON parser: Jackson (`jackson-databind`)

## 3) Minimalny zestaw klas do przeniesienia

Skopiuj te pakiety/klasy:

- `pl.wojma.funwithxmls.domain` (cały pakiet)
- `pl.wojma.funwithxmls.core.model`
  - `StructuredNode`
- `pl.wojma.funwithxmls.core.usecase`
  - `CompareXmlUseCase`
  - `CompareJsonUseCase`
  - `RenderHtmlUseCase`
- `pl.wojma.funwithxmls.core.ports`
  - `StructuredComparator` / `XmlComparator`
  - `StructuredDocumentParser` / `XmlDocumentParser`
  - `ReportRenderer`
  - `HtmlReportWriter` (jeśli chcesz zapis plikowy)
- `pl.wojma.funwithxmls.adapters.compare`
  - `SmartXmlComparator`
- `pl.wojma.funwithxmls.adapters.xml`
  - `DomXmlDocumentParser`
- `pl.wojma.funwithxmls.adapters.json`
  - `JacksonJsonDocumentParser`
- `pl.wojma.funwithxmls.adapters.report`
  - `HtmlReportRenderer`
  - `FileHtmlReportWriter` (opcjonalnie)
- `pl.wojma.funwithxmls.services`
  - `XmlComparisonFacade` (główne API biblioteczne)

Do integracji bibliotecznej nie są wymagane:

- `pl.wojma.funwithxmls.cli.Main`
- `XmlComparisonService`
- `JsonComparisonService`
- `DefaultXmlResourceLoader`

Te klasy są potrzebne głównie do scenariusza CLI/plikowego.

## 4) Szybkie użycie API (bez zapisu do pliku)

```java
XmlComparisonFacade facade = new XmlComparisonFacade();

ComparisonResult xmlResult = facade.compareXml(
    leftInputStream,
    rightInputStream,
    ComparisonMode.STRUCTURE_AND_VALUES
);

ComparisonResult jsonResult = facade.compareJson(
    leftJsonInputStream,
    rightJsonInputStream,
    ComparisonMode.STRUCTURE_AND_VALUES
);

String html = facade.renderHtml(xmlResult);
```

Lub skrót:

```java
String html = facade.compareXmlAndRender(
    leftXmlInputStream,
    rightXmlInputStream,
    ComparisonMode.STRUCTURE_ONLY
);

String jsonHtml = facade.compareJsonAndRender(
    leftJsonInputStream,
    rightJsonInputStream,
    ComparisonMode.STRUCTURE_ONLY
);
```

## 5) Opcjonalny zapis HTML do pliku

```java
HtmlReportWriter writer = new FileHtmlReportWriter();
writer.saveHtml(html, Path.of("reports", "document-report.html"));
```

## 6) Checklista po integracji

1. Uruchom porównanie prostych XML:
   - `<root><a>1</a></root>` vs `<root><a>1</a></root>`
   - oczekuj zgodności.
2. Sprawdź różnicę wartości:
   - `<a>1</a>` vs `<a>2</a>`
   - oczekuj `VALUE_MISMATCH` w trybie `STRUCTURE_AND_VALUES`.
3. Uruchom porównanie prostych JSON:
   - `{\"a\":1}` vs `{\"a\":1}`
   - oczekuj zgodności.
4. Sprawdź raport HTML:
   - czy `html` zawiera `<!DOCTYPE html>` i `Raport porównania dokumentów`.
5. Jeśli zapisujesz plik:
   - upewnij się, że katalog docelowy powstaje i plik jest zapisany w UTF-8.

## 7) Typowe pułapki

- Użycie `STRUCTURE_ONLY`, gdy oczekujesz porównania wartości.
- Przekazanie zużytych/nieprzewiniętych `InputStream`.
- Pominięcie klas z `domain` (to są modele kontraktowe całego rozwiązania).
- Mieszanie scenariusza bibliotecznego ze scenariuszem opartym o `resources`.

## 8) Gotowy prompt dla innego AI

Użyj tego promptu w narzędziu AI:

```text
Zintegruj komparator XML/JSON jako helper w moim projekcie Java 21.
Skopiuj klasy wskazane w docs/AI_INTEGRATION_GUIDE.md (sekcja "Minimalny zestaw klas do przeniesienia").
Wykorzystaj XmlComparisonFacade jako publiczne API.
Dodaj klasę serwisową IntegrationXmlComparatorService z metodami:
- compareXml(InputStream left, InputStream right, ComparisonMode mode)
- compareJson(InputStream left, InputStream right, ComparisonMode mode)
- compareXmlAndRender(InputStream left, InputStream right, ComparisonMode mode)
- compareJsonAndRender(InputStream left, InputStream right, ComparisonMode mode)
Dodaj też opcjonalny zapis HTML przez FileHtmlReportWriter.
Na końcu utwórz test integracyjny potwierdzający:
- zgodność dla identycznych XML,
- zgodność dla identycznych JSON,
- VALUE_MISMATCH dla różnych wartości,
- obecność "<!DOCTYPE html>" w wygenerowanym HTML.
Nie modyfikuj logiki algorytmu porównywania.
```

## 9) Notatka o strukturze projektu

Aktualna struktura jest celowo warstwowa:

- `core/*` - rdzeń (kontrakty i use-case’y)
- `adapters/*` - implementacje techniczne (XML, JSON, report, IO, comparator)
- `services/*` - publiczne fasady i serwisy scenariuszy plikowych
- `cli/*` - wejście uruchomieniowe

Jeśli AI ma „przenieść cały feature”, najlepiej kopiować modułami zgodnie z tym podziałem, nie pojedynczymi losowymi klasami.
