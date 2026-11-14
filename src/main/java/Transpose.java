public class Transpose {
    public String transpose(String toTranspose) {
        if (toTranspose.isEmpty()) {
            return "";
        }

        String[] lines = toTranspose.split("\n", -1);
        int maxLength = 0;
        for (String line : lines) {
            maxLength = Math.max(maxLength, line.length());
        }

        StringBuilder result = new StringBuilder();
        for (int col = 0; col < maxLength; col++) {
            if (col > 0) {
                result.append('\n');
            }
            for (int row = 0; row < lines.length; row++) {
                String line = lines[row];
                if (col < line.length()) {
                    result.append(line.charAt(col));
                } else if (hasLongerLineAfter(lines, row, col)) {
                    result.append(' ');
                } else {
                    break;
                }
            }
        }
        return result.toString();
    }

    private boolean hasLongerLineAfter(String[] lines, int row, int col) {
        for (int i = row + 1; i < lines.length; i++) {
            if (lines[i].length() > col) {
                return true;
            }
        }
        return false;
    }
}
