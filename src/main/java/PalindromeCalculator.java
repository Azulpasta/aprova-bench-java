import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

class PalindromeCalculator {

    SortedMap<Long, List<List<Integer>>> getPalindromeProductsWithFactors(int minFactor, int maxFactor) {
        if (minFactor > maxFactor) {
            throw new IllegalArgumentException("invalid input: min must be <= max");
        }

        SortedMap<Long, List<List<Integer>>> palindromes = new TreeMap<>();

        for (int i = minFactor; i <= maxFactor; i++) {
            for (int j = i; j <= maxFactor; j++) {
                long product = (long) i * j;
                if (isPalindrome(product)) {
                    palindromes
                            .computeIfAbsent(product, key -> new ArrayList<>())
                            .add(Arrays.asList(i, j));
                }
            }
        }

        return palindromes;
    }

    private boolean isPalindrome(long number) {
        String str = Long.toString(number);
        String reversed = new StringBuilder(str).reverse().toString();
        return str.equals(reversed);
    }

}
