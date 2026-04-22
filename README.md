# funWithXmls

Narzędzie Java do porównywania dwóch plików XML i generowania nowoczesnego raportu HTML.

## Funkcje

- Dwa tryby porównania:
  - `STRUCTURE_ONLY` - porównanie tylko struktury i ścieżek pól.
  - `STRUCTURE_AND_VALUES` - porównanie struktury oraz wartości (strict) z best-effort dla dopasowania list wartości.
- Rozróżnianie pól po pełnej ścieżce zagnieżdżenia (np. `testa/cd` i `testbb/cd`).
- Wykrywanie powtarzalnych obiektów i porównanie ich liczności między XML-ami.
- Raport single-file HTML z tabelą różnic i sekcją podsumowania.

## Uruchomienie

Przykładowe XML-e znajdują się w `src/main/resources/xml`.

```bash
mvn test
mvn exec:java -Dexec.args="xml/sample-left.xml xml/sample-right.xml STRUCTURE_AND_VALUES comparison.html"
```

Wynikowy raport zostanie zapisany do:

`target/reports/comparison.html`