# funWithXmls

Narzędzie Java do porównywania dwóch dokumentów XML/JSON i generowania nowoczesnego raportu HTML.

## Funkcje

- Dwa tryby porównania:
  - `STRUCTURE_ONLY` - porównanie tylko struktury i ścieżek pól.
  - `STRUCTURE_AND_VALUES` - porównanie struktury oraz wartości (strict) z best-effort dla dopasowania list wartości.
- Osobne metody API dla XML i JSON (bez parametru formatu).
- Rozróżnianie pól po pełnej ścieżce zagnieżdżenia (np. `testa/cd` i `testbb/cd`).
- Wykrywanie powtarzalnych obiektów i porównanie ich liczności między XML-ami.
- Raport single-file HTML z tabelą różnic i sekcją podsumowania.

## Uruchomienie

Przykładowe pliki wejściowe znajdują się w:

- XML: `src/main/resources/xml`
- JSON: `src/main/resources/json`

### CLI (aktualnie XML)

```bash
mvn test
mvn exec:java -Dexec.args="xml/sample-left.xml xml/sample-right.xml STRUCTURE_AND_VALUES comparison.html"
```

Wynikowy raport zostanie zapisany do:

`target/reports/comparison.html`

### Scenariusz plikowy JSON (z poziomu kodu)

```java
JsonComparisonService service = new JsonComparisonService();
ComparisonResult result = service.compare(
    "json/shop.json",
    "json/shop-extended.json",
    "shop-json-report.html",
    ComparisonMode.STRUCTURE_AND_VALUES
);
```

## Użycie jako biblioteka/helper

Możesz użyć fasady `XmlComparisonFacade`, bez pracy na `resources` i bez zapisu do pliku:

```java
XmlComparisonFacade facade = new XmlComparisonFacade();
ComparisonResult result = facade.compareXml(leftInputStream, rightInputStream, ComparisonMode.STRUCTURE_AND_VALUES);
String html = facade.renderHtml(result);
```

Dostępne są też metody łączone:

```java
String xmlHtml = facade.compareXmlAndRender(leftXmlStream, rightXmlStream, ComparisonMode.STRUCTURE_ONLY);
String jsonHtml = facade.compareJsonAndRender(leftJsonStream, rightJsonStream, ComparisonMode.STRUCTURE_ONLY);
```

Jeśli chcesz zapisać HTML do pliku, użyj adaptera infrastrukturalnego `FileHtmlReportWriter`.

## Pełna struktura plików

Poniżej aktualna struktura projektu z opisem roli plików:

```text
funWithXmls/
├─ pom.xml                                  # konfiguracja Maven (build, testy, exec main)
├─ README.md                                # główna dokumentacja projektu
├─ docs/
│  └─ AI_INTEGRATION_GUIDE.md               # quickstart integracyjny dla innych AI
├─ src/
│  ├─ main/
│  │  ├─ java/pl/wojma/funwithxmls/
│  │  │  ├─ cli/
│  │  │  │  └─ Main.java                    # entrypoint CLI (aktualnie scenariusz XML)
│  │  │  ├─ domain/
│  │  │  │  ├─ ComparisonMode.java          # tryby porównania
│  │  │  │  ├─ DifferenceType.java          # typy różnic
│  │  │  │  ├─ FieldDifference.java         # pojedynczy wpis różnicy pola
│  │  │  │  ├─ OccurrenceStat.java          # statystyki liczności obiektów
│  │  │  │  ├─ SummaryMetrics.java          # metryki podsumowania
│  │  │  │  ├─ ComparisonResult.java        # pełny wynik porównania
│  │  │  │  ├─ NodePath.java                # pomocniczy model ścieżki
│  │  │  │  └─ XmlNode.java                 # legacy model XML (pozostawiony kompatybilnościowo)
│  │  │  ├─ core/
│  │  │  │  ├─ model/
│  │  │  │  │  └─ StructuredNode.java       # neutralny model węzła XML/JSON
│  │  │  │  ├─ ports/
│  │  │  │  │  ├─ StructuredDocumentParser.java # parser dokumentu do modelu neutralnego
│  │  │  │  │  ├─ StructuredComparator.java # wspólny kontrakt comparatora
│  │  │  │  │  ├─ XmlDocumentParser.java    # alias XML parsera (compat)
│  │  │  │  │  ├─ XmlComparator.java        # alias XML comparatora (compat)
│  │  │  │  │  ├─ ReportRenderer.java       # kontrakt renderera raportu
│  │  │  │  │  ├─ HtmlReportWriter.java     # kontrakt zapisu HTML
│  │  │  │  │  └─ XmlResourceLoader.java    # kontrakt loadera zasobów/ścieżek
│  │  │  │  └─ usecase/
│  │  │  │     ├─ CompareXmlRequest.java    # request scenariusza plikowego
│  │  │  │     ├─ CompareXmlUseCase.java    # porównanie XML (stream -> wynik)
│  │  │  │     ├─ CompareJsonUseCase.java   # porównanie JSON (stream -> wynik)
│  │  │  │     ├─ RenderHtmlUseCase.java    # renderowanie wyniku do HTML
│  │  │  │     ├─ CompareAndSaveReportUseCase.java     # XML: compare + render + save
│  │  │  │     └─ CompareAndSaveJsonReportUseCase.java # JSON: compare + render + save
│  │  │  ├─ adapters/
│  │  │  │  ├─ compare/
│  │  │  │  │  └─ SmartXmlComparator.java   # wspólny silnik porównań XML/JSON
│  │  │  │  ├─ xml/
│  │  │  │  │  └─ DomXmlDocumentParser.java # parser XML (DOM)
│  │  │  │  ├─ json/
│  │  │  │  │  └─ JacksonJsonDocumentParser.java # parser JSON (Jackson)
│  │  │  │  ├─ report/
│  │  │  │  │  ├─ HtmlReportRenderer.java   # generator single-file HTML
│  │  │  │  │  └─ FileHtmlReportWriter.java # zapis raportu HTML do pliku
│  │  │  │  └─ io/
│  │  │  │     └─ DefaultXmlResourceLoader.java # loader plików wejściowych z resources
│  │  │  └─ services/
│  │  │     ├─ XmlComparisonFacade.java     # publiczne API pamięciowe (XML + JSON)
│  │  │     ├─ XmlComparisonService.java    # scenariusz plikowy XML
│  │  │     └─ JsonComparisonService.java   # scenariusz plikowy JSON
│  │  └─ resources/
│  │     ├─ xml/
│  │     │  ├─ sample-left.xml              # mały sample do szybkich testów
│  │     │  ├─ sample-right.xml             # mały sample do szybkich testów
│  │     │  ├─ shop.xml                     # scenariusz biznesowy XML (base)
│  │     │  ├─ shop-extended.xml            # scenariusz biznesowy XML (extended)
│  │     │  └─ shop-aggresive.xml           # scenariusz biznesowy XML (aggressive)
│  │     └─ json/
│  │        ├─ shop.json                    # scenariusz biznesowy JSON (base)
│  │        ├─ shop-extended.json           # scenariusz biznesowy JSON (extended)
│  │        └─ shop-aggresive.json          # scenariusz biznesowy JSON (aggressive)
│  └─ test/
│     └─ java/pl/wojma/funwithxmls/
│        ├─ SmartXmlComparatorTest.java     # testy logiki porównania
│        ├─ XmlComparisonFacadeTest.java    # testy API pamięciowego XML/JSON
│        ├─ JsonComparisonFeatureTest.java  # testy JSON + parity XML/JSON
│        ├─ XmlComparisonServiceTest.java   # test scenariusza plikowego XML
│        └─ FileHtmlReportWriterTest.java   # test zapisu raportu HTML
└─ target/                                  # artefakty builda i raporty wygenerowane
```
