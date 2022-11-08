package dev.mccue.json;

import java.io.IOException;
import java.util.IdentityHashMap;

final class JsonWriter {
    private interface Writer {
        void write(Json v, Appendable out, OptionsWithIndentDepth options) throws IOException;
    }

    private static final IdentityHashMap<Class<? extends Json>, Writer> WRITERS;

    static {
        WRITERS = new IdentityHashMap<>();
        WRITERS.put(Null.class, (__, out, ___) -> out.append("null"));
        WRITERS.put(True.class, (__, out, ___) -> out.append("true"));
        WRITERS.put(False.class, (__, out, ___) -> out.append("false"));
        WRITERS.put(Long.class, (v, out, ___) -> out.append(v.toString()));
        WRITERS.put(Double.class, (v, out, ___) -> out.append(v.toString()));
        WRITERS.put(BigInteger.class, (v, out, ___) -> out.append(v.toString()));
        WRITERS.put(BigDecimal.class, (v, out, ___) -> out.append(v.toString()));
        WRITERS.put(String.class, (v, out, options) -> writeString((Json.String) v, out, options));
        WRITERS.put(Array.class, (v, out, options) -> writeArray((Json.Array) v, out, options));
        WRITERS.put(Object.class, (v, out, options) -> writeObject((Json.Object) v, out, options));
    }

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

    private static void emitHexString(Appendable out, char cp) throws IOException {
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

    private static void writeString(CharSequence s, Appendable out, OptionsWithIndentDepth options) throws IOException {
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

    private static void writeIndent(Appendable out, OptionsWithIndentDepth options) throws IOException {
        out.append('\n');
        int i = options.indentDepth;
        while (i > 0) {
            out.append(' ');
            i--;
        }
    }

    private static void writeObject(Json.Object m, Appendable out, OptionsWithIndentDepth options) throws IOException {
        var indent = options.indent();
        var opts = indent
                ? options.incrementIndentDepth()
                : options;

        out.append('{');
        if (indent && !m.isEmpty()) {
            writeIndent(out, opts);
        }

        var x = m.entrySet().iterator();
        var havePrintedKV = false;
        while (x.hasNext()) {
            var entry = x.next();
            var k = entry.getKey();
            var v = entry.getValue();
            if (havePrintedKV) {
                out.append(',');
                if (indent) {
                    writeIndent(out, opts);
                }
            }
            writeString(k, out, opts);
            out.append(':');
            if (indent) {
                out.append(' ');
            }
            write(v, out, opts);
            if (x.hasNext()) {
                havePrintedKV = true;
            }
        }
        if (indent && !m.isEmpty()) {
            writeIndent(out, options);
        }
        out.append('}');
    }

    private static void writeArray(Json.Array a, Appendable out, OptionsWithIndentDepth options) throws IOException {
        var indent = options.indent();
        var opts = indent
                ? options.incrementIndentDepth()
                : options;

        out.append('[');
        if (indent && !a.isEmpty()) {
            writeIndent(out, opts);
        }
        var x = a.iterator();
        while (x.hasNext()) {
            var first = x.next();
            write(first, out, opts);
            if (x.hasNext()) {
                out.append(',');
                if (indent) {
                    writeIndent(out, opts);
                }
            }
        }

        if (indent && !a.isEmpty()) {
            writeIndent(out, options);
        }

        out.append(']');
    }


    private static void write(Json v, Appendable out, OptionsWithIndentDepth options) throws IOException {
        WRITERS.get(v.getClass()).write(v, out, options);
    }

    private record OptionsWithIndentDepth(
            Json.WriteOptions options,
            int indentDepth
    ) {
        OptionsWithIndentDepth(Json.WriteOptions options) {
            this(options, 0);
        }

        boolean escapeUnicode() {
            return options.escapeUnicode();
        }


        boolean escapeJavascriptSeparators() {
            return options.escapeJavascriptSeparators();
        }

        boolean escapeSlash() {
            return options.escapeSlash();
        }

        boolean indent() {
            return options.indent();
        }

        OptionsWithIndentDepth incrementIndentDepth() {
            return new OptionsWithIndentDepth(options, indentDepth + 1);
        }
    }



    public void write(Json json, Appendable out, Json.WriteOptions options) throws IOException {
        write(json, out, new OptionsWithIndentDepth(options));
    }
}
