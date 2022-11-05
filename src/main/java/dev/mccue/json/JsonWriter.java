package dev.mccue.json;

import java.io.IOException;
import java.io.Writer;

public final class JsonWriter {
    private static final short[] CODEPOINT_DECODER;

    static {
        CODEPOINT_DECODER = new short[128];
        for (int i = 0; i < 128; i++) {
            CODEPOINT_DECODER[i] = switch (i) {
                case '"', '\\' -> 1;
                case '/' -> 2;
                case '\b' -> 3;
                case '\f' -> 4;
                case '\n' -> 5;
                case '\r' -> 6;
                case '\t' -> 7;
                default -> {
                    if (i < 32) {
                        yield 8;
                    }
                    else {
                        yield 0;
                    }
                }
            };
        }
    }

    private void emitHexString(Appendable out, char cp) throws IOException {
        out.append("\\u");
        if (cp < 16) {
            out.append("000");
        }
        else if (cp < 256) {
            out.append("00");
        }
        else if (cp < 4096) {
            out.append("0");
        }

        out.append(Integer.toHexString(cp));
    }

    private void writeString(CharSequence s, Appendable out, Options options) throws IOException {
        out.append('"');
        for (int i = 0; i < s.length(); i++) {
            char cp = s.charAt(i);
            if (cp < 128) {
                switch (CODEPOINT_DECODER[cp]) {
                    case 0 -> out.append(cp);
                    case 1 -> {
                        out.append('\\');
                        out.append(cp);
                    }
                    case 2 -> {
                        if (options.escapeSlash()) {
                            out.append("\\/");
                        }
                        else {
                            out.append("/");
                        }
                    }
                    case 3 ->
                            out.append("\\b");

                    case 4 ->
                            out.append("\\f");

                    case 5 ->
                            out.append("\\n");

                    case 6 ->
                            out.append("\\r");

                    case 7 ->
                            out.append("\\t");

                    case 8 ->
                            emitHexString(out, cp);
                }
            }
            else {
                if (cp == '\u2028' || cp == '\u2029') {
                    if (options.escapeJavascriptSeparators()) {
                        emitHexString(out, cp);
                    }
                    else {
                        out.append(cp);
                    }
                }
                else {
                    if (options.escapeUnicode()) {
                        emitHexString(out, cp);
                    }
                    else {
                        out.append(cp);
                    }
                }
            }
        }
        out.append('"');
    }

    private void writeIndent(Appendable out, int indentDepth) throws IOException {
        out.append('\n');
        int i = indentDepth;
        while (i > 0) {
            out.append(' ');
            i--;
        }
    }


    /**
     * @param escapeUnicode If true, non-ascii characters are escaped as \\uXXXX
     * @param escapeJavascriptSeparators If true (default) the Unicode characters U+2028 and U+2029 will
     *                                   be escaped as \\u2028 and \\u2029 even if :escape-unicode is
     *                                   false. (These two characters are valid in pure JSON but are not
     *                                   valid in JavaScript strings.).
     * @param escapeSlash If true (default) the slash / is escaped as \\/
     */
    public record Options(
            boolean escapeUnicode,
            boolean escapeJavascriptSeparators,
            boolean escapeSlash
    ) {
        public Options() {
            this(true, true, true);
        }
    }

    public void write(Writer writer) {

    }
}
