class ResistorColorTrio {
    private static final java.util.Map<String, Integer> COLOR_VALUES = java.util.Map.ofEntries(
        java.util.Map.entry("black", 0),
        java.util.Map.entry("brown", 1),
        java.util.Map.entry("red", 2),
        java.util.Map.entry("orange", 3),
        java.util.Map.entry("yellow", 4),
        java.util.Map.entry("green", 5),
        java.util.Map.entry("blue", 6),
        java.util.Map.entry("violet", 7),
        java.util.Map.entry("grey", 8),
        java.util.Map.entry("white", 9)
    );

    String label(String[] colors) {
        int firstDigit = COLOR_VALUES.get(colors[0]);
        int secondDigit = COLOR_VALUES.get(colors[1]);
        int exponent = COLOR_VALUES.get(colors[2]);

        long baseValue = firstDigit * 10 + secondDigit;
        long value = baseValue * (long) Math.pow(10, exponent);

        if (value >= 1_000_000_000) {
            long giga = value / 1_000_000_000;
            return giga + " gigaohms";
        } else if (value >= 1_000_000) {
            long mega = value / 1_000_000;
            return mega + " megaohms";
        } else if (value >= 1_000) {
            long kilo = value / 1_000;
            return kilo + " kiloohms";
        } else {
            return value + " ohms";
        }
    }
}
