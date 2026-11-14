class TwelveDays {
    private static final String[] ORDINALS = {
            "first", "second", "third", "fourth", "fifth", "sixth",
            "seventh", "eighth", "ninth", "tenth", "eleventh", "twelfth"
    };

    private static final String[] GIFTS = {
            "a Partridge in a Pear Tree",
            "two Turtle Doves",
            "three French Hens",
            "four Calling Birds",
            "five Gold Rings",
            "six Geese-a-Laying",
            "seven Swans-a-Swimming",
            "eight Maids-a-Milking",
            "nine Ladies Dancing",
            "ten Lords-a-Leaping",
            "eleven Pipers Piping",
            "twelve Drummers Drumming"
    };

    String verse(int verseNumber) {
        StringBuilder gifts = new StringBuilder();
        for (int i = verseNumber; i >= 1; i--) {
            if (i == 1 && verseNumber > 1) {
                gifts.append("and ");
            }
            gifts.append(GIFTS[i - 1]);
            if (i > 1) {
                gifts.append(", ");
            }
        }
        return "On the " + ORDINALS[verseNumber - 1] + " day of Christmas my true love gave to me: "
                + gifts + ".\n";
    }

    String verses(int startVerse, int endVerse) {
        StringBuilder result = new StringBuilder();
        for (int i = startVerse; i <= endVerse; i++) {
            result.append(verse(i));
            if (i < endVerse) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    String sing() {
        return verses(1, 12);
    }
}
