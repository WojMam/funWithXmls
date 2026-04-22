package pl.wojma.funwithxmls.infrastructure;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import pl.wojma.funwithxmls.application.ReportRenderer;
import pl.wojma.funwithxmls.domain.ComparisonResult;
import pl.wojma.funwithxmls.domain.DifferenceType;
import pl.wojma.funwithxmls.domain.FieldDifference;
import pl.wojma.funwithxmls.domain.OccurrenceStat;
import pl.wojma.funwithxmls.domain.SummaryMetrics;

/**
 * Renderer tworzący nowoczesny, jednoplikowy raport HTML z porównania XML.
 */
public class HtmlReportRenderer implements ReportRenderer {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    @Override
    public String render(ComparisonResult result) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html lang=\"pl\"><head><meta charset=\"UTF-8\">")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                .append("<title>Raport porównania XML</title>")
                .append("<style>")
                .append("body{font-family:Inter,Segoe UI,Arial,sans-serif;margin:0;background:#0f172a;color:#e2e8f0;}")
                .append(".container{max-width:1200px;margin:0 auto;padding:32px 24px 48px;}")
                .append(".card{background:#111827;border:1px solid #334155;border-radius:14px;padding:20px;margin-bottom:20px;}")
                .append("h1,h2{margin:0 0 12px 0;}h1{font-size:28px;}h2{font-size:20px;color:#cbd5e1;}")
                .append(".meta{display:grid;grid-template-columns:repeat(auto-fit,minmax(220px,1fr));gap:12px;}")
                .append(".pill{display:inline-block;padding:4px 8px;border-radius:999px;background:#1e293b;color:#93c5fd;font-size:12px;}")
                .append(".table-wrap{overflow-x:auto;}")
                .append("table{width:100%;border-collapse:collapse;font-size:14px;table-layout:fixed;}")
                .append("th,td{padding:10px;border-bottom:1px solid #334155;text-align:left;vertical-align:top;overflow-wrap:anywhere;word-break:break-word;}")
                .append("th{position:sticky;top:0;background:#0b1220;color:#cbd5e1;}")
                .append("th.path-col,td.path-col{width:32%;}")
                .append("th.status-col,td.status-col{width:10%;}")
                .append("th.count-col,td.count-col{width:6%;text-align:center;}")
                .append("th.value-col,td.value-col{width:15%;}")
                .append("th.note-col,td.note-col{width:16%;}")
                .append(".type-MATCH{color:#34d399;}.type-LEFT_ONLY,.type-RIGHT_ONLY{color:#fbbf24;}")
                .append(".type-VALUE_MISMATCH{color:#f87171;}.type-ORDER_DIFFERENCE{color:#93c5fd;}")
                .append(".summary{display:grid;grid-template-columns:repeat(auto-fit,minmax(180px,1fr));gap:10px;}")
                .append(".metric{background:#0b1220;border:1px solid #334155;border-radius:12px;padding:12px;}")
                .append(".metric strong{display:block;font-size:24px;color:#f8fafc;}")
                .append("</style></head><body><div class=\"container\">");

        appendHeader(html, result);
        appendSummary(html, result.summary());
        appendFieldDifferences(html, result);
        appendOccurrenceStats(html, result);
        appendFooter(html, result.summary());

        html.append("</div></body></html>");
        return html.toString();
    }

    private void appendHeader(StringBuilder html, ComparisonResult result) {
        html.append("<section class=\"card\">")
                .append("<h1>Raport porównania XML</h1>")
                .append("<div class=\"meta\">")
                .append("<div><span class=\"pill\">Tryb</span><p>").append(escapeHtml(result.mode().name())).append("</p></div>")
                .append("<div><span class=\"pill\">Plik A</span><p>").append(escapeHtml(result.leftSource())).append("</p></div>")
                .append("<div><span class=\"pill\">Plik B</span><p>").append(escapeHtml(result.rightSource())).append("</p></div>")
                .append("<div><span class=\"pill\">Wygenerowano</span><p>")
                .append(escapeHtml(DATE_TIME_FORMATTER.format(result.generatedAt()))).append("</p></div>")
                .append("</div></section>");
    }

    private void appendSummary(StringBuilder html, SummaryMetrics summary) {
        html.append("<section class=\"card\"><h2>Podsumowanie metryk</h2><div class=\"summary\">");
        appendMetric(html, "Pola zgodne", summary.matchingFields());
        appendMetric(html, "Tylko w XML A", summary.leftOnlyFields());
        appendMetric(html, "Tylko w XML B", summary.rightOnlyFields());
        appendMetric(html, "Różnice wartości", summary.valueDifferences());
        appendMetric(html, "Różnice kolejności", summary.orderDifferences());
        appendMetric(html, "Różnice liczności obiektów", summary.occurrenceDifferences());
        html.append("</div></section>");
    }

    private void appendMetric(StringBuilder html, String label, int value) {
        html.append("<div class=\"metric\"><span>").append(escapeHtml(label)).append("</span><strong>")
                .append(value).append("</strong></div>");
    }

    private void appendFieldDifferences(StringBuilder html, ComparisonResult result) {
        html.append("<section class=\"card\"><h2>Porównanie pól</h2><div class=\"table-wrap\"><table><thead><tr>")
                .append("<th class=\"path-col\">Ścieżka</th><th class=\"status-col\">Status</th>")
                .append("<th class=\"count-col\">Liczność A</th><th class=\"count-col\">Liczność B</th>")
                .append("<th class=\"value-col\">Wartość A</th><th class=\"value-col\">Wartość B</th>")
                .append("<th class=\"note-col\">Adnotacja</th>")
                .append("</tr></thead><tbody>");

        for (FieldDifference difference : result.fieldDifferences()) {
            html.append("<tr>")
                    .append("<td class=\"path-col\">").append(escapeHtml(difference.path())).append("</td>")
                    .append("<td class=\"status-col type-").append(difference.type().name()).append("\">")
                    .append(escapeHtml(toPolishStatus(difference.type()))).append("</td>")
                    .append("<td class=\"count-col\">").append(difference.leftOccurrences()).append("</td>")
                    .append("<td class=\"count-col\">").append(difference.rightOccurrences()).append("</td>")
                    .append("<td class=\"value-col\">").append(escapeHtml(orDash(difference.leftValue()))).append("</td>")
                    .append("<td class=\"value-col\">").append(escapeHtml(orDash(difference.rightValue()))).append("</td>")
                    .append("<td class=\"note-col\">").append(escapeHtml(orDash(difference.note()))).append("</td>")
                    .append("</tr>");
        }

        html.append("</tbody></table></div></section>");
    }

    private void appendOccurrenceStats(StringBuilder html, ComparisonResult result) {
        html.append("<section class=\"card\"><h2>Powtarzalne obiekty</h2>")
                .append("<table><thead><tr><th>Obiekt</th><th>Liczność A</th><th>Liczność B</th><th>Interpretacja</th></tr></thead><tbody>");

        if (result.occurrenceStats().isEmpty()) {
            html.append("<tr><td colspan=\"4\">Brak powtarzalnych obiektów do porównania.</td></tr>");
        } else {
            for (OccurrenceStat stat : result.occurrenceStats()) {
                html.append("<tr><td>").append(escapeHtml(stat.objectPath())).append("</td>")
                        .append("<td>").append(stat.leftCount()).append("</td>")
                        .append("<td>").append(stat.rightCount()).append("</td>")
                        .append("<td>").append(escapeHtml(interpretOccurrence(stat))).append("</td></tr>");
            }
        }

        html.append("</tbody></table></section>");
    }

    private void appendFooter(StringBuilder html, SummaryMetrics summary) {
        int totalDiff = summary.leftOnlyFields() + summary.rightOnlyFields()
                + summary.valueDifferences() + summary.orderDifferences();
        html.append("<section class=\"card\"><h2>Wniosek</h2><p>")
                .append("Łączna liczba wykrytych różnic pól: <strong>").append(totalDiff).append("</strong>. ")
                .append("Różnice liczności powtarzalnych obiektów: <strong>")
                .append(summary.occurrenceDifferences()).append("</strong>.</p></section>");
    }

    private String interpretOccurrence(OccurrenceStat stat) {
        if (stat.leftCount() == stat.rightCount() && !stat.structureDifference()) {
            return "Zgodna struktura i liczność.";
        }
        if (stat.isCountOnlyDifference()) {
            return "Różna liczność wystąpień, bez różnicy struktury obiektu.";
        }
        if (stat.leftCount() != stat.rightCount()) {
            return "Różna liczność oraz różnica strukturalna obiektu.";
        }
        return "Liczność zgodna, ale struktura obiektu różna.";
    }

    private String toPolishStatus(DifferenceType type) {
        return switch (type) {
            case MATCH -> "Zgodne";
            case LEFT_ONLY -> "Tylko w XML A";
            case RIGHT_ONLY -> "Tylko w XML B";
            case VALUE_MISMATCH -> "Różna wartość";
            case ORDER_DIFFERENCE -> "Różna kolejność";
        };
    }

    private String orDash(String value) {
        return value == null || value.isBlank() ? "-" : value;
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
