public class Transpose {
    public String transpose(String toTranspose) {
        if (toTranspose.isEmpty()) {
            return "";
        }

        String[] rows = toTranspose.split("\n", -1);

        int maxLength = 0;
        for (String row : rows) {
            maxLength = Math.max(maxLength, row.length());
        }

        StringBuilder result = new StringBuilder();

        for (int col = 0; col < maxLength; col++) {
            StringBuilder column = new StringBuilder();
            boolean[] isPadding = new boolean[rows.length];

            for (int row = 0; row < rows.length; row++) {
                if (col < rows[row].length()) {
                    column.append(rows[row].charAt(col));
                    isPadding[row] = false;
                } else {
                    column.append(' ');
                    isPadding[row] = true;
                }
            }

            int trimLength = column.length();
            while (trimLength > 0 && column.charAt(trimLength - 1) == ' ' && isPadding[trimLength - 1]) {
                trimLength--;
            }

            if (col > 0) {
                result.append('\n');
            }
            result.append(column.substring(0, trimLength));
        }

        return result.toString();
    }
}
