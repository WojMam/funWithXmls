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

Case study: ewolucja podejścia do danych testowych w frameworku

1. Punkt wyjścia — dane wpisywane ręcznie w testach i builderach

Na początku budowaliśmy requesty bezpośrednio w testach, ręcznie ustawiając dużą liczbę pól w builderach modeli transakcji. Przy prostszych przypadkach to jeszcze działało, ale przy większych komunikatach — szczególnie bankowych, gdzie modeli i pól jest bardzo dużo — testy zaczynały się rozrastać do nieczytelnych rozmiarów.

Co było dobre
pełna jawność tego, co trafia do requestu,
brak dodatkowej warstwy pośredniej,
łatwo było zrozumieć mechanikę „tu i teraz”.
Co było słabe
bardzo długie testy,
duża duplikacja danych,
trudne przełączanie środowisk,
brak centralnego źródła prawdy dla danych typu konto, BIC, adres itp.,
testy stawały się bardziej edytorem danych niż opisem scenariusza. 2. Pomysł z resolverami i plikami YAML

Kolejny krok był logiczny: wynieść dane testowe do zewnętrznych plików, np. YAML, i wprowadzić resolver, który na podstawie klucza lub tagu pobiera właściwe dane.

Przykładowo zamiast wpisywać numer konta bezpośrednio, można było podać identyfikator typu NORMAL_CREDITOR_PLN, a resolver wyszukiwał odpowiedni obiekt w YAML.

Co było dobre
centralizacja danych,
łatwiejsza zmiana danych środowiskowych,
możliwość trzymania wielu obiektów testowych w jednym miejscu,
rozdzielenie danych od logiki testu.
Co było słabe
pojawiła się dodatkowa warstwa ładowania i mapowania plików,
sporo logiki zaczęło trafiać do resolverów i factory,
trudniej było śledzić przepływ danych,
brak natywnego wsparcia IDE dla samych danych,
pojawiło się ryzyko, że pliki staną się drugim XML-em — tylko w innym formacie. 3. Wariant z tagami typu @TAG w builderach

Potem pojawił się pomysł, żeby test nadal wyglądał krótko, a jednocześnie umożliwiał odwołanie do danych testowych przez specjalny string, np.:

.accountNumber("@COOWNER_ACCOUNT")
.creditorAccountIban("@STANDARD_EUR")

Factory albo enhanced builder miały rozpoznawać, czy dana wartość to literal, czy referencja do danych testowych.

Co było dobre
testy wyglądały na krótsze,
dało się mieszać literały i dane testowe,
użytkowanie pozornie było wygodne.
Co było słabe
semantyka metod została zepsuta: pole oczekujące wartości biznesowej zaczęło przyjmować ukryty klucz,
factory musiały zgadywać, co oznacza dany string,
pojawiło się bardzo dużo boilerplate’u,
dla jednego modelu powstawały setki linii kodu tylko po to, by rozstrzygać „literal czy tag”,
logika danych testowych zaczęła się rozlewać po builderach i factory,
trudniejsze debugowanie i większa magia.

To był ważny moment, bo wtedy stało się jasne, że problem nie leży w samych danych testowych, tylko w złym miejscu ich rozwiązywania.

4. Rozważania o bardziej „frameworkowych” rozwiązaniach

W tym momencie zaczęliśmy rozważać bardziej zaawansowane rozwiązania:

reflection wrapper,
path-based API,
dynamic proxy,
annotation processor,
semantic roles API,
generyczne val()/ref().

To była próba zbudowania mechanizmu, który:

nie wymagałby ręcznej pracy per model,
wspierałby wiele przyszłych typów transakcji,
działałby generycznie.
Co było dobre
te pomysły odpowiadały na realną potrzebę skalowania rozwiązania,
pozwalały myśleć o przyszłości i dziesiątkach modeli.
Co było słabe
bardzo szybko wchodziliśmy w overengineering,
rosła złożoność builda, frameworka i debugowania,
pojawiało się więcej „inżynierii mechanizmu” niż realnej wartości biznesowej,
rozwiązanie zaczynało żyć własnym życiem i mogło stać się trudniejsze niż sam pierwotny problem.

Najważniejszy wniosek z tego etapu był taki, że technicznie da się zbudować bardzo sprytny mechanizm, ale nie znaczy to, że warto.

5. Prostszе podejście: jawny odczyt danych z katalogów testowych

Następnie pojawił się prostszy i zdrowszy kierunek: zamiast ukrywać odczyt danych za tagami i resolverami, można po prostu używać jawnego API danych testowych, np. w stylu:

testData().accounts().get("LORO_PLN").iban()

albo jeszcze lepiej:

testData().accounts().loroPln().iban()

Czyli:

test dalej przekazuje finalną wartość do buildera,
ale źródło tej wartości pochodzi z jawnego katalogu danych testowych.
Co było dobre
zero magii,
buildery i factory nie muszą nic rozstrzygać,
semantyka metod pozostaje poprawna,
test pokazuje wprost, skąd bierze dane,
dużo łatwiejsze debugowanie,
brak potrzeby dopisywania logiki „czy to literal, czy tag”.
Co było słabe
jeśli backendem nadal był YAML, to i tak trzeba było utrzymywać loader i mapowanie,
publiczne API testów było javowe, ale źródło danych nadal siedziało w plikach,
pojawiło się pytanie, czy YAML w ogóle jeszcze daje realną wartość.

I to był punkt zwrotny.

6. Kluczowe pytanie: skoro i tak mamy javowe API, to po co nam pliki?

Na tym etapie okazało się, że niezależnie od tego, czy dane są w YAML, czy nie, testy i tak używają ich przez API typu:

testData().accounts().loroEurNoFunds()

To znaczy, że:

test nie pracuje na YAML-u bezpośrednio,
i tak musimy mieć warstwę Java,
i tak chcemy mieć wsparcie IDE,
i tak chcemy mieć jedno źródło prawdy.

W tym momencie pojawił się bardzo ważny wniosek:

wartość daje nam nie sam YAML, tylko centralizacja danych i wygodne API dostępu do nich.

Jeżeli tę samą wartość możemy osiągnąć prościej, bez warstwy plików, to YAML przestaje być konieczny.

7. Wersja końcowa — DTO i katalog danych testowych w Javie

Finalnie doszliśmy do rozwiązania, w którym:

modele requestów pozostają modelami,
factory pozostają po staremu,
testy pobierają gotowe dane przez jawne API,
dane testowe są trzymane w Javie jako DTO i katalogi danych,
nie używamy XML/YAML/JSON do przechowywania tych danych.

Przykładowy styl użycia:

testData().accounts().loroEurNoFunds().iban()
testData().banks().senderBank().bic()
testData().postal().addressPl1().full()
Co to daje
brak dodatkowego parsera i loadera,
brak resolverów per pole,
brak @TAG,
brak zmian w factory i modelach,
pełne wsparcie IDE,
ctrl+click, rename, find usages,
lepsze typowanie,
bardziej przewidywalny kod,
prostszy onboarding dla zespołu.
Dlaczego to rozwiązanie jest dla nas najlepsze teraz

Bo rozwiązuje nasz realny problem bez budowania drugiego frameworka obok frameworka testowego.

Nasze potrzeby
dużo modeli i typów transakcji,
część pól to zwykłe literały, część to dane testowe,
chcemy zachować istniejące modele i factory,
chcemy skrócić testy,
chcemy jedno źródło prawdy,
nie chcemy pisać per-model resolverów, wrapperów i enhanced builderów,
chcemy maksymalnej prostoty operacyjnej.
To rozwiązanie spełnia te potrzeby
nie rozsadza architektury,
nie wymaga parserów ani code generation,
nie miesza znaczenia pól,
dobrze skaluje się na kolejne modele,
nie zmusza nas do dotykania factory przy dodawaniu nowych danych testowych.
Potencjalne zagrożenia

To nie jest rozwiązanie idealne w każdym świecie, więc warto uczciwie powiedzieć, na co trzeba uważać.

1. Rozrost katalogu danych testowych

Jeśli nie będziemy pilnować nazewnictwa i struktury, testData() może urosnąć do dużego, chaotycznego katalogu.

Jak ograniczyć ryzyko
grupować dane per domena: accounts, banks, addresses, parties,
używać sensownych nazw,
nie robić płaskiego worka statyk. 2. Dane nadal są kodem

Zmiana danych wymaga zmiany kodu i commita, a nie tylko edycji pliku.

Dlaczego to akceptujemy

Bo u nas dane i tak utrzymują developerzy/test automation engineerowie, a korzyści z IDE i typowania są większe niż korzyści z edycji plików.

3. Rozwój modelu danych testowych

Jeśli za kilka miesięcy do TestAccount dojdzie nowe pole, np. accountType, trzeba dobrze zaprojektować DTO, najlepiej z builderem i opcjonalnymi polami.

Dlaczego to nie jest blocker

Bo publiczne API testów może zostać stabilne, np.:

testData().accounts().loroEurNoFunds()

a zmiany zachodzą tylko w definicji danych testowych pod spodem.

Końcowe podsumowanie

Przeszliśmy drogę od:

ręcznego wpisywania danych w testach,
przez YAML i resolvery,
przez pomysły z tagami, annotacjami, wrapperami i mechanizmami generycznego resolution,
aż do prostego, jawnego i javowego katalogu danych testowych.

Najważniejsze wnioski są takie:

1.

Nie potrzebowaliśmy bardziej sprytnego mechanizmu rozwiązywania stringów.

2.

Potrzebowaliśmy prostego i jawnego sposobu korzystania z centralnych danych testowych.

3.

Największą wartość dało nam nie „źródło danych w pliku”, tylko:

jedno źródło prawdy,
stabilne API dostępu,
brak zmian w modelach i factory,
prostota dla testów.
4.Na dziś najlepszym kierunkiem dla nas jest:
DTO i katalog danych testowych w Javie, bez YAML/XML/JSON, bez resolverów w builderach i bez overengineeringu.
