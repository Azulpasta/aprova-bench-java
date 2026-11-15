import java.util.*;
import java.util.stream.Collectors;

class Poker {
    private List<String> hands;

    Poker(List<String> hands) {
        this.hands = hands;
    }

    List<String> getBestHands() {
        if (hands.isEmpty()) {
            return new ArrayList<>();
        }

        List<Hand> evaluatedHands = hands.stream()
            .map(Hand::new)
            .collect(Collectors.toList());

        Hand bestHand = evaluatedHands.get(0);
        for (Hand hand : evaluatedHands) {
            if (hand.compareTo(bestHand) > 0) {
                bestHand = hand;
            }
        }

        final Hand finalBest = bestHand;
        return evaluatedHands.stream()
            .filter(h -> h.compareTo(finalBest) == 0)
            .map(h -> h.originalHand)
            .collect(Collectors.toList());
    }

    static class Hand implements Comparable<Hand> {
        private String originalHand;
        private List<Card> cards;
        private HandRank rank;
        private List<Integer> tiebreakers;

        Hand(String handString) {
            this.originalHand = handString;
            this.cards = Arrays.stream(handString.split(" "))
                .map(Card::new)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());
            evaluateHand();
        }

        private void evaluateHand() {
            if (isStraightFlush()) {
                rank = HandRank.STRAIGHT_FLUSH;
            } else if (isFourOfAKind()) {
                rank = HandRank.FOUR_OF_A_KIND;
            } else if (isFullHouse()) {
                rank = HandRank.FULL_HOUSE;
            } else if (isFlush()) {
                rank = HandRank.FLUSH;
            } else if (isStraight()) {
                rank = HandRank.STRAIGHT;
            } else if (isThreeOfAKind()) {
                rank = HandRank.THREE_OF_A_KIND;
            } else if (isTwoPair()) {
                rank = HandRank.TWO_PAIR;
            } else if (isOnePair()) {
                rank = HandRank.ONE_PAIR;
            } else {
                rank = HandRank.HIGH_CARD;
                tiebreakers = cards.stream().map(c -> c.value).collect(Collectors.toList());
            }
        }

        private boolean isStraightFlush() {
            Set<String> suits = new HashSet<>();
            for (Card card : cards) {
                suits.add(card.suit);
            }
            if (suits.size() != 1) return false;

            if (checkStraight()) {
                tiebreakers = Arrays.asList(getStraightHighCard());
                return true;
            }
            return false;
        }

        private boolean isFourOfAKind() {
            Map<Integer, Integer> counts = getCounts();
            for (Integer value : counts.keySet()) {
                if (counts.get(value) == 4) {
                    tiebreakers = new ArrayList<>();
                    tiebreakers.add(value);
                    for (Card card : cards) {
                        if (card.value != value) {
                            tiebreakers.add(card.value);
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        private boolean isFullHouse() {
            Map<Integer, Integer> counts = getCounts();
            Integer threeRank = null;
            Integer twoRank = null;

            for (Integer value : counts.keySet()) {
                if (counts.get(value) == 3) {
                    threeRank = value;
                } else if (counts.get(value) == 2) {
                    twoRank = value;
                }
            }

            if (threeRank != null && twoRank != null) {
                tiebreakers = Arrays.asList(threeRank, twoRank);
                return true;
            }
            return false;
        }

        private boolean isFlush() {
            Set<String> suits = new HashSet<>();
            for (Card card : cards) {
                suits.add(card.suit);
            }
            if (suits.size() == 1) {
                tiebreakers = cards.stream().map(c -> c.value).collect(Collectors.toList());
                return true;
            }
            return false;
        }

        private boolean isStraight() {
            if (checkStraight()) {
                tiebreakers = Arrays.asList(getStraightHighCard());
                return true;
            }
            return false;
        }

        private boolean checkStraight() {
            boolean isRegularStraight = true;
            for (int i = 0; i < cards.size() - 1; i++) {
                if (cards.get(i).value - cards.get(i + 1).value != 1) {
                    isRegularStraight = false;
                    break;
                }
            }
            if (isRegularStraight) return true;

            if (cards.get(0).value == 14 && cards.get(1).value == 5 &&
                cards.get(2).value == 4 && cards.get(3).value == 3 &&
                cards.get(4).value == 2) {
                return true;
            }
            return false;
        }

        private int getStraightHighCard() {
            if (cards.get(0).value == 14 && cards.get(1).value == 5) {
                return 5;
            }
            return cards.get(0).value;
        }

        private boolean isThreeOfAKind() {
            Map<Integer, Integer> counts = getCounts();
            for (Integer value : counts.keySet()) {
                if (counts.get(value) == 3) {
                    tiebreakers = new ArrayList<>();
                    tiebreakers.add(value);
                    for (Card card : cards) {
                        if (card.value != value) {
                            tiebreakers.add(card.value);
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        private boolean isTwoPair() {
            Map<Integer, Integer> counts = getCounts();
            List<Integer> pairValues = new ArrayList<>();
            for (Integer value : counts.keySet()) {
                if (counts.get(value) == 2) {
                    pairValues.add(value);
                }
            }

            if (pairValues.size() == 2) {
                tiebreakers = new ArrayList<>(pairValues);
                Collections.sort(tiebreakers, Collections.reverseOrder());
                for (Card card : cards) {
                    if (!pairValues.contains(card.value)) {
                        tiebreakers.add(card.value);
                    }
                }
                return true;
            }
            return false;
        }

        private boolean isOnePair() {
            Map<Integer, Integer> counts = getCounts();
            for (Integer value : counts.keySet()) {
                if (counts.get(value) == 2) {
                    tiebreakers = new ArrayList<>();
                    tiebreakers.add(value);
                    for (Card card : cards) {
                        if (card.value != value) {
                            tiebreakers.add(card.value);
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        private Map<Integer, Integer> getCounts() {
            Map<Integer, Integer> counts = new HashMap<>();
            for (Card card : cards) {
                counts.put(card.value, counts.getOrDefault(card.value, 0) + 1);
            }
            return counts;
        }

        @Override
        public int compareTo(Hand other) {
            if (this.rank != other.rank) {
                return this.rank.compareTo(other.rank);
            }

            for (int i = 0; i < this.tiebreakers.size() && i < other.tiebreakers.size(); i++) {
                int cmp = this.tiebreakers.get(i).compareTo(other.tiebreakers.get(i));
                if (cmp != 0) {
                    return cmp;
                }
            }
            return 0;
        }
    }

    static class Card implements Comparable<Card> {
        private int value;
        private String suit;

        Card(String cardString) {
            this.suit = cardString.substring(cardString.length() - 1);
            String valueStr = cardString.substring(0, cardString.length() - 1);

            switch (valueStr) {
                case "A":
                    this.value = 14;
                    break;
                case "K":
                    this.value = 13;
                    break;
                case "Q":
                    this.value = 12;
                    break;
                case "J":
                    this.value = 11;
                    break;
                default:
                    this.value = Integer.parseInt(valueStr);
                    break;
            }
        }

        @Override
        public int compareTo(Card other) {
            return Integer.compare(this.value, other.value);
        }
    }

    enum HandRank {
        HIGH_CARD,
        ONE_PAIR,
        TWO_PAIR,
        THREE_OF_A_KIND,
        STRAIGHT,
        FLUSH,
        FULL_HOUSE,
        FOUR_OF_A_KIND,
        STRAIGHT_FLUSH
    }
}