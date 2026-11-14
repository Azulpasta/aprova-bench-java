import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class Poker {

    private final List<String> hands;

    Poker(List<String> hand) {
        this.hands = hand;
    }

    List<String> getBestHands() {
        List<HandScore> scores = new ArrayList<>();
        for (String hand : hands) {
            scores.add(new HandScore(hand));
        }

        HandScore best = Collections.max(scores, Comparator.naturalOrder());

        List<String> result = new ArrayList<>();
        for (HandScore score : scores) {
            if (score.compareTo(best) == 0) {
                result.add(score.hand);
            }
        }
        return result;
    }

    private static class HandScore implements Comparable<HandScore> {
        final String hand;
        final List<Integer> ranks = new ArrayList<>();
        final boolean flush;
        final List<Integer> category;

        HandScore(String hand) {
            this.hand = hand;
            String[] cards = hand.split(" ");

            Map<Character, Integer> suitCounts = new TreeMap<>();
            List<Integer> values = new ArrayList<>();

            for (String card : cards) {
                char suit = card.charAt(card.length() - 1);
                String rankStr = card.substring(0, card.length() - 1);
                int value = parseRank(rankStr);
                values.add(value);
                suitCounts.merge(suit, 1, Integer::sum);
            }

            this.flush = suitCounts.size() == 1;

            Collections.sort(values);
            boolean isStraight = isStraight(values);
            if (isStraight && values.equals(Arrays.asList(2, 3, 4, 5, 14))) {
                values = Arrays.asList(1, 2, 3, 4, 5);
            }

            Map<Integer, Integer> rankCounts = new TreeMap<>();
            for (int v : values) {
                rankCounts.merge(v, 1, Integer::sum);
            }

            List<Map.Entry<Integer, Integer>> entries = new ArrayList<>(rankCounts.entrySet());
            entries.sort((a, b) -> {
                int cmp = b.getValue().compareTo(a.getValue());
                if (cmp != 0) {
                    return cmp;
                }
                return b.getKey().compareTo(a.getKey());
            });

            for (Map.Entry<Integer, Integer> entry : entries) {
                this.ranks.add(entry.getKey());
            }

            List<Integer> countsSorted = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : entries) {
                countsSorted.add(entry.getValue());
            }

            int rankType;
            if (isStraight && flush) {
                rankType = 8;
            } else if (countsSorted.equals(Arrays.asList(4, 1))) {
                rankType = 7;
            } else if (countsSorted.equals(Arrays.asList(3, 2))) {
                rankType = 6;
            } else if (flush) {
                rankType = 5;
            } else if (isStraight) {
                rankType = 4;
            } else if (countsSorted.equals(Arrays.asList(3, 1, 1))) {
                rankType = 3;
            } else if (countsSorted.equals(Arrays.asList(2, 2, 1))) {
                rankType = 2;
            } else if (countsSorted.equals(Arrays.asList(2, 1, 1, 1))) {
                rankType = 1;
            } else {
                rankType = 0;
            }

            this.category = new ArrayList<>();
            this.category.add(rankType);
            this.category.addAll(this.ranks);
        }

        private static boolean isStraight(List<Integer> sortedValues) {
            if (sortedValues.equals(Arrays.asList(2, 3, 4, 5, 14))) {
                return true;
            }
            for (int i = 1; i < sortedValues.size(); i++) {
                if (sortedValues.get(i) != sortedValues.get(i - 1) + 1) {
                    return false;
                }
            }
            return true;
        }

        private static int parseRank(String rankStr) {
            switch (rankStr) {
                case "J":
                    return 11;
                case "Q":
                    return 12;
                case "K":
                    return 13;
                case "A":
                    return 14;
                default:
                    return Integer.parseInt(rankStr);
            }
        }

        @Override
        public int compareTo(HandScore other) {
            int size = Math.max(this.category.size(), other.category.size());
            for (int i = 0; i < size; i++) {
                int a = i < this.category.size() ? this.category.get(i) : 0;
                int b = i < other.category.size() ? other.category.get(i) : 0;
                int cmp = Integer.compare(a, b);
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }
    }

}
