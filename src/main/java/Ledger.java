import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Ledger {
    private static final int MAX_DESCRIPTION_LENGTH = 25;
    private static final int TRUNCATION_INDICATOR_LENGTH = 3;

    public LedgerEntry createLedgerEntry(String d, String desc, int c) {
        LedgerEntry le = new LedgerEntry();
        le.setChange(c);
        le.setDescription(desc);
        le.setLocalDate(LocalDate.parse(d));
        return le;
    }

    public String format(String currency, String locale, LedgerEntry[] entries) {
        validateInputs(currency, locale);
        LocaleConfig config = LocaleConfig.get(currency, locale);

        StringBuilder result = new StringBuilder(config.getHeader());

        if (entries.length > 0) {
            List<LedgerEntry> sortedEntries = sortEntries(entries);
            for (LedgerEntry entry : sortedEntries) {
                appendFormattedEntry(result, entry, config);
            }
        }

        return result.toString();
    }

    private void validateInputs(String currency, String locale) {
        if (!currency.equals("USD") && !currency.equals("EUR")) {
            throw new IllegalArgumentException("Invalid currency");
        }
        if (!locale.equals("en-US") && !locale.equals("nl-NL")) {
            throw new IllegalArgumentException("Invalid locale");
        }
    }

    private List<LedgerEntry> sortEntries(LedgerEntry[] entries) {
        List<LedgerEntry> negative = new ArrayList<>();
        List<LedgerEntry> positive = new ArrayList<>();

        for (LedgerEntry entry : entries) {
            if (entry.getChange() < 0) {
                negative.add(entry);
            } else {
                positive.add(entry);
            }
        }

        negative.sort((e1, e2) -> e1.getLocalDate().compareTo(e2.getLocalDate()));
        positive.sort((e1, e2) -> e1.getLocalDate().compareTo(e2.getLocalDate()));

        List<LedgerEntry> sorted = new ArrayList<>();
        sorted.addAll(negative);
        sorted.addAll(positive);
        return sorted;
    }

    private void appendFormattedEntry(StringBuilder result, LedgerEntry entry, LocaleConfig config) {
        result.append("\n");
        String formattedDate = formatDate(entry, config);
        String formattedDescription = truncateDescription(entry.getDescription());
        String formattedAmount = formatAmount(entry, config);

        result.append(String.format("%s | %-25s | %13s",
                formattedDate,
                formattedDescription,
                formattedAmount));
    }

    private String formatDate(LedgerEntry entry, LocaleConfig config) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(config.getDatePattern());
        return entry.getLocalDate().format(formatter);
    }

    private String truncateDescription(String description) {
        if (description.length() > MAX_DESCRIPTION_LENGTH) {
            return description.substring(0, MAX_DESCRIPTION_LENGTH - TRUNCATION_INDICATOR_LENGTH) + "...";
        }
        return description;
    }

    private String formatAmount(LedgerEntry entry, LocaleConfig config) {
        long amountInCents = (long) entry.getChange();
        boolean isNegative = amountInCents < 0;

        long absoluteAmount = Math.abs(amountInCents);
        long dollars = absoluteAmount / 100;
        long cents = absoluteAmount % 100;

        String integerPart = addThousandSeparators(dollars, config.getThousandSeparator());
        String formattedValue = integerPart + config.getDecimalSeparator() + String.format("%02d", cents);

        return config.formatCurrency(formattedValue, isNegative);
    }

    private String addThousandSeparators(long amount, String separator) {
        String str = String.valueOf(amount);
        StringBuilder result = new StringBuilder();
        int digits = str.length();

        for (int i = 0; i < digits; i++) {
            if (i > 0 && (digits - i) % 3 == 0) {
                result.append(separator);
            }
            result.append(str.charAt(i));
        }

        return result.toString();
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

    static class LocaleConfig {
        private final String header;
        private final String currencySymbol;
        private final String datePattern;
        private final String decimalSeparator;
        private final String thousandSeparator;
        private final boolean isDutch;

        private LocaleConfig(String header, String currencySymbol, String datePattern,
                           String decimalSeparator, String thousandSeparator, boolean isDutch) {
            this.header = header;
            this.currencySymbol = currencySymbol;
            this.datePattern = datePattern;
            this.decimalSeparator = decimalSeparator;
            this.thousandSeparator = thousandSeparator;
            this.isDutch = isDutch;
        }

        static LocaleConfig get(String currency, String locale) {
            boolean isEuro = currency.equals("EUR");
            boolean isDutch = locale.equals("nl-NL");

            if (isDutch) {
                String currencySymbol = isEuro ? "€" : "$";
                return new LocaleConfig(
                    "Datum      | Omschrijving              | Verandering  ",
                    currencySymbol,
                    "dd/MM/yyyy",
                    ",",
                    ".",
                    true
                );
            } else {
                String currencySymbol = isEuro ? "€" : "$";
                return new LocaleConfig(
                    "Date       | Description               | Change       ",
                    currencySymbol,
                    "MM/dd/yyyy",
                    ".",
                    ",",
                    false
                );
            }
        }

        String getHeader() {
            return header;
        }

        String getCurrencySymbol() {
            return currencySymbol;
        }

        String getDatePattern() {
            return datePattern;
        }

        String getDecimalSeparator() {
            return decimalSeparator;
        }

        String getThousandSeparator() {
            return thousandSeparator;
        }

        String formatCurrency(String value, boolean isNegative) {
            if (isDutch) {
                if (isNegative) {
                    return currencySymbol + " -" + value + " ";
                } else {
                    return " " + currencySymbol + " " + value + " ";
                }
            } else {
                if (isNegative) {
                    return "(" + currencySymbol + value + ")";
                } else {
                    return currencySymbol + value + " ";
                }
            }
        }
    }

}
