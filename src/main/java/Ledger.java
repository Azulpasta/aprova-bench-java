import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

/*
 * Refactoring log (tests kept green after each step, run via `mvn test`):
 * 1. Replaced the currency/locale if-else matrix with a `Currency` enum (USD, EUR; just holds
 *    the symbol) and a `LedgerLocale` enum (en-US, nl-NL; holds date pattern, header and
 *    amount-formatting rule). Invalid codes now throw via `forCode(...)` lookups instead of a
 *    nested if/else chain.
 * 2. Replaced the manual neg/pos split-then-concatenate "sort" with a single
 *    `Comparator.comparing(date).thenComparing(description).thenComparing(change)`, which is the
 *    actual intended ordering (the old split happened to agree with it on every existing test
 *    case but would have put all negative entries before all positive ones regardless of date).
 * 3. Replaced the hand-rolled thousands-separator loop and the ad-hoc string concatenation for
 *    sign/parentheses/symbol placement with `DecimalFormat` + `DecimalFormatSymbols` driven by
 *    each locale's `java.util.Locale`, with `RoundingMode.HALF_UP` to match the original
 *    `String.format("%.02f", ...)` behavior. Each `LedgerLocale` now owns its own
 *    `formatAmount(change, currency)` (parentheses for US negatives, "symbol -amount" for NL
 *    negatives).
 * 4. Extracted `truncate(description)` and `formatEntry(entry, currency, locale)` helper methods
 *    out of the single giant `format` method, and removed the now-unused imports
 *    (`ArrayList`/`List`) in favor of `Arrays`/`Comparator`.
 * 5. Renamed short/unclear local variables (`s`, `le`, `c`, `d`, `datPat`, `curSymb`, etc.) to
 *    descriptive names throughout.
 */
public class Ledger {

    private static final int COLUMN_WIDTH_DESCRIPTION = 25;

    public LedgerEntry createLedgerEntry(String date, String description, int change) {
        LedgerEntry entry = new LedgerEntry();
        entry.setChange(change);
        entry.setDescription(description);
        entry.setLocalDate(LocalDate.parse(date));
        return entry;
    }

    public String format(String currencyCode, String localeCode, LedgerEntry[] entries) {
        Currency currency = Currency.forCode(currencyCode);
        LedgerLocale locale = LedgerLocale.forCode(localeCode);

        StringBuilder result = new StringBuilder(locale.header());

        for (LedgerEntry entry : sorted(entries)) {
            result.append("\n").append(formatEntry(entry, currency, locale));
        }

        return result.toString();
    }

    private LedgerEntry[] sorted(LedgerEntry[] entries) {
        LedgerEntry[] copy = Arrays.copyOf(entries, entries.length);
        Arrays.sort(copy, Comparator.comparing(LedgerEntry::getLocalDate)
                .thenComparing(LedgerEntry::getDescription)
                .thenComparing(LedgerEntry::getChange));
        return copy;
    }

    private String formatEntry(LedgerEntry entry, Currency currency, LedgerLocale locale) {
        String date = entry.getLocalDate().format(locale.dateFormatter());
        String description = truncate(entry.getDescription());
        String amount = locale.formatAmount(entry.getChange(), currency);
        return String.format("%s | %-25s | %13s", date, description, amount);
    }

    private String truncate(String description) {
        if (description.length() <= COLUMN_WIDTH_DESCRIPTION) {
            return description;
        }
        return description.substring(0, COLUMN_WIDTH_DESCRIPTION - 3) + "...";
    }

    private enum Currency {
        USD("$"),
        EUR("€");

        private final String symbol;

        Currency(String symbol) {
            this.symbol = symbol;
        }

        static Currency forCode(String code) {
            try {
                return Currency.valueOf(code);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid currency");
            }
        }

        String symbol() {
            return symbol;
        }
    }

    private enum LedgerLocale {
        EN_US("en-US", "MM/dd/yyyy", Locale.US,
                "Date       | Description               | Change       ") {
            @Override
            String formatAmount(double change, Currency currency) {
                String formatted = currency.symbol() + decimalFormat().format(Math.abs(change) / 100);
                return change < 0 ? "(" + formatted + ")" : formatted + " ";
            }
        },
        NL_NL("nl-NL", "dd/MM/yyyy", new Locale("nl", "NL"),
                "Datum      | Omschrijving              | Verandering  ") {
            @Override
            String formatAmount(double change, Currency currency) {
                String sign = change < 0 ? "-" : "";
                return currency.symbol() + " " + sign + decimalFormat().format(Math.abs(change) / 100) + " ";
            }
        };

        private final String code;
        private final String datePattern;
        private final Locale locale;
        private final String header;

        LedgerLocale(String code, String datePattern, Locale locale, String header) {
            this.code = code;
            this.datePattern = datePattern;
            this.locale = locale;
            this.header = header;
        }

        static LedgerLocale forCode(String code) {
            for (LedgerLocale locale : values()) {
                if (locale.code.equals(code)) {
                    return locale;
                }
            }
            throw new IllegalArgumentException("Invalid locale");
        }

        String header() {
            return header;
        }

        DateTimeFormatter dateFormatter() {
            return DateTimeFormatter.ofPattern(datePattern);
        }

        DecimalFormat decimalFormat() {
            DecimalFormat format = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(locale));
            format.setRoundingMode(RoundingMode.HALF_UP);
            return format;
        }

        abstract String formatAmount(double change, Currency currency);
    }

    public static class LedgerEntry {
        LocalDate localDate;
        String description;
        double change;

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getChange() {
            return change;
        }

        public void setChange(double change) {
            this.change = change;
        }
    }

}
