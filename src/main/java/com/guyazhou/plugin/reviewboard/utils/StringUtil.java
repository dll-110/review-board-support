package com.guyazhou.plugin.reviewboard.utils;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringUtil {
    public static @NotNull
    String trimEnd(@NotNull String s, @NotNull String suffix) {
        return trimEnd(s, suffix);
    }
    public static String trimEnd(@NotNull String s, @NotNull String suffix, boolean ignoreCase) {
        if (s == null) {
            return null;
        }

        if (suffix == null) {
            return null;
        }

        boolean endsWith = ignoreCase ? endsWithIgnoreCase(s, suffix) : s.endsWith(suffix);
        if (endsWith) {
            String var10000 = s.substring(0, s.length() - suffix.length());
            return var10000;
        } else {
            return s;
        }
    }
    public static boolean endsWithIgnoreCase(@NotNull CharSequence str, @NotNull String suffix) {

        return endsWithIgnoreCase(str, suffix);
    }
    public static boolean endsWithIgnoreCase(@NotNull CharSequence text, @NotNull CharSequence suffix) {

        int l1 = text.length();
        int l2 = suffix.length();
        if (l1 < l2) {
            return false;
        } else {
            for(int i = l1 - 1; i >= l1 - l2; --i) {
                if (!charsEqualIgnoreCase(text.charAt(i), suffix.charAt(i + l2 - l1))) {
                    return false;
                }
            }

            return true;
        }
    }
    public static boolean charsEqualIgnoreCase(char a, char b) {
        return a == b || toUpperCase(a) == toUpperCase(b) || toLowerCase(a) == toLowerCase(b);
    }
    public static char toUpperCase(char a) {
        if (a < 'a') {
            return a;
        } else {
            return a <= 'z' ? (char)(a + -32) : Character.toUpperCase(a);
        }
    }

    @Contract(
            pure = true
    )
    public static char toLowerCase(char a) {
        if (a > 'z') {
            return Character.toLowerCase(a);
        } else {
            return a >= 'A' && a <= 'Z' ? (char)(a + 32) : a;
        }
    }

    public static @NotNull String escapeStringCharacters(@NotNull String s) {
        StringBuilder buffer = new StringBuilder(s.length());
        escapeStringCharacters(s.length(), s, "\"", buffer);
        return buffer.toString();
    }

    public static String toUpperCase(String s) {
        return toUpperCase(s);
    }

    public static @NotNull StringBuilder escapeStringCharacters(int length,
                                                                @NotNull String str,
                                                                @Nullable String additionalChars,
                                                                @NotNull StringBuilder buffer) {
        return escapeStringCharacters(length, str, additionalChars, true, buffer);
    }
    public static @NotNull StringBuilder escapeStringCharacters(int length,
                                                                @NotNull String str,
                                                                @Nullable String additionalChars,
                                                                boolean escapeSlash,
                                                                @NotNull StringBuilder buffer) {
        return escapeStringCharacters(length, str, additionalChars, escapeSlash, true, buffer);
    }
    public static @NotNull StringBuilder escapeStringCharacters(int length,
                                                                @NotNull String str,
                                                                @Nullable String additionalChars,
                                                                boolean escapeSlash,
                                                                boolean escapeUnicode,
                                                                @NotNull StringBuilder buffer) {
        char prev = 0;
        for (int idx = 0; idx < length; idx++) {
            char ch = str.charAt(idx);
            switch (ch) {
                case '\b':
                    buffer.append("\\b");
                    break;

                case '\t':
                    buffer.append("\\t");
                    break;

                case '\n':
                    buffer.append("\\n");
                    break;

                case '\f':
                    buffer.append("\\f");
                    break;

                case '\r':
                    buffer.append("\\r");
                    break;

                default:
                    if (escapeSlash && ch == '\\') {
                        buffer.append("\\\\");
                    }
                    else if (additionalChars != null && additionalChars.indexOf(ch) > -1 && (escapeSlash || prev != '\\')) {
                        buffer.append("\\").append(ch);
                    }
                    else if (escapeUnicode && !isPrintableUnicode(ch)) {
                        CharSequence hexCode = toUpperCase(Integer.toHexString(ch));
                        buffer.append("\\u");
                        int paddingCount = 4 - hexCode.length();
                        while (paddingCount-- > 0) {
                            buffer.append(0);
                        }
                        buffer.append(hexCode);
                    }
                    else {
                        buffer.append(ch);
                    }
            }
            prev = ch;
        }
        return buffer;
    }
    @Contract(pure = true)
    public static boolean isPrintableUnicode(char c) {
        int t = Character.getType(c);
        return t != Character.UNASSIGNED && t != Character.LINE_SEPARATOR && t != Character.PARAGRAPH_SEPARATOR &&
                t != Character.CONTROL && t != Character.FORMAT && t != Character.PRIVATE_USE && t != Character.SURROGATE;
    }
    public static boolean equal(@Nullable CharSequence s1, @Nullable CharSequence s2, boolean caseSensitive) {
        if (s1 == s2) {
            return true;
        } else if (s1 != null && s2 != null) {
            if (s1.length() != s2.length()) {
                return false;
            } else {
                int i;
                if (caseSensitive) {
                    for(i = 0; i < s1.length(); ++i) {
                        if (s1.charAt(i) != s2.charAt(i)) {
                            return false;
                        }
                    }
                } else {
                    for(i = 0; i < s1.length(); ++i) {
                        if (!charsEqualIgnoreCase(s1.charAt(i), s2.charAt(i))) {
                            return false;
                        }
                    }
                }

                return true;
            }
        } else {
            return false;
        }
    }
    public static @NotNull List<String> split(@NotNull String s, @NotNull String separator) {
        return split(s, separator, true);
    }
    public static @NotNull List<String> split(@NotNull String s, @NotNull String separator, boolean excludeSeparator) {
        return split(s, separator, excludeSeparator, true);
    }
    public static @NotNull List<String> split(@NotNull String s, @NotNull String separator, boolean excludeSeparator, boolean excludeEmptyStrings) {
        //noinspection unchecked
        return (List)split((CharSequence)s, separator, excludeSeparator, excludeEmptyStrings);
    }
    public static @NotNull List<CharSequence> split(@NotNull CharSequence s, @NotNull CharSequence separator, boolean excludeSeparator, boolean excludeEmptyStrings) {
        if (separator.length() == 0) {
            return Collections.singletonList(s);
        }
        List<CharSequence> result = new ArrayList<>();
        int pos = 0;
        while (true) {
            int index = indexOf(s, separator, pos);
            if (index == -1) {
                break;
            }
            final int nextPos = index + separator.length();
            CharSequence token = s.subSequence(pos, excludeSeparator ? index : nextPos);
            if (token.length() != 0 || !excludeEmptyStrings) {
                result.add(token);
            }
            pos = nextPos;
        }
        if (pos < s.length() || !excludeEmptyStrings && pos == s.length()) {
            result.add(s.subSequence(pos, s.length()));
        }
        return result;
    }
//    public static int indexOf(@NotNull CharSequence sequence, @NotNull CharSequence infix, int start) {
//        return indexOf(sequence, infix, start);
//    }

    public static int indexOf(@NotNull CharSequence sequence, @NotNull CharSequence infix, int start) {
        return indexOf(sequence, infix, start, sequence.length());
    }

    public static int indexOf(@NotNull CharSequence sequence, @NotNull CharSequence infix, int start, int end) {

        for(int i = start; i <= end - infix.length(); ++i) {
            if (startsWith(sequence, i, infix)) {
                return i;
            }
        }

        return -1;
    }

    public static boolean startsWith(@NotNull CharSequence text, int startIndex, @NotNull CharSequence prefix) {

        int tl = text.length();
        if (startIndex >= 0 && startIndex <= tl) {
            int l1 = tl - startIndex;
            int l2 = prefix.length();
            if (l1 < l2) {
                return false;
            } else {
                for(int i = 0; i < l2; ++i) {
                    if (text.charAt(i + startIndex) != prefix.charAt(i)) {
                        return false;
                    }
                }

                return true;
            }
        } else {
            throw new IllegalArgumentException("Index is out of bounds: " + startIndex + ", length: " + tl);
        }
    }
}
