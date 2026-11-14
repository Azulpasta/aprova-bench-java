import java.util.List;

class ResistorColorTrio {
    private static final List<String> COLORS = List.of(
        "black", "brown", "red", "orange", "yellow",
        "green", "blue", "violet", "grey", "white"
    );

    private static final String[] PREFIXES = {"", "kilo", "mega", "giga"};

    String label(String[] colors) {
        int tens = COLORS.indexOf(colors[0]);
        int ones = COLORS.indexOf(colors[1]);
        int zeros = COLORS.indexOf(colors[2]);

        long value = (tens * 10L + ones) * (long) Math.pow(10, zeros);

        int prefixIndex = 0;
        while (value != 0 && value % 1000 == 0 && prefixIndex < PREFIXES.length - 1) {
            value /= 1000;
            prefixIndex++;
        }

        return value + " " + PREFIXES[prefixIndex] + "ohms";
    }
}
