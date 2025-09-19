import java.awt.*;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public enum MJTextWrapper implements TextWrapper {
    INSTANCE;
    public List<String> wrap(String text, FontMetrics fm, int maxWidth) {
        List<String> lines = new ArrayList<String>();
        if (text == null || text.isEmpty()) {
            return lines;
        }

        BreakIterator boundary = BreakIterator.getLineInstance(Locale.ROOT);
        boundary.setText(text);

        StringBuilder currentLine = new StringBuilder();
        int currentWidth = 0;

        int start = boundary.first();
        int end = boundary.next();

        while (end != BreakIterator.DONE) {
            String word = text.substring(start, end);
            int wordWidth = fm.stringWidth(word);
            int candidateWidth = currentWidth + wordWidth;

            if (candidateWidth > maxWidth) {
                if (currentLine.length() > 0) {
                    // flush current line
                    lines.add(currentLine.toString().trim());
                    currentLine.setLength(0);
                    currentWidth = 0;
                }

                // If the single word does not fit on an empty line, force-split by width.
                if (wordWidth > maxWidth) {
                    forceSplitByWidth(lines, word, fm, maxWidth);
                } else {
                    currentLine.append(word);
                    currentWidth = wordWidth;
                }
            } else {
                currentLine.append(word);
                currentWidth = candidateWidth;
            }

            start = end;
            end = boundary.next();
        }

        if (currentLine.length() > 0) {
            lines.add(currentLine.toString().trim());
        }

        return lines;
    }

    /**
     * Splits a token into chunks that each fit within maxWidth, appending chunks to the output list.
     * This ensures progress even for very long tokens without natural break points.
     */
    private static void forceSplitByWidth(List<String> out, String token, FontMetrics fm, int maxWidth) {
        char[] chars = token.toCharArray();
        int n = chars.length;
        int pos = 0;

        while (pos < n) {
            int width = 0;
            int end = pos;

            // grow by one char at a time using charWidth (no substring allocations for measuring)
            while (end < n) {
                int cw = fm.charWidth(chars[end]);
                if (width + cw > maxWidth) {
                    break;
                }
                width += cw;
                end++;
            }

            // ensure at least 1-char progress
            int len = Math.max(1, end - pos);
            out.add(new String(chars, pos, len));
            pos += len;
        }
    }
}
