/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

/**
 * This is borrowed from the TickProfiler mod.
 *
 * Author: nallar
 * https://github.com/MinimallyCorrect/TickProfiler
 */
package net.minecraftforge.common.util.chat;

import java.util.*;

/**
 * Derived from https://github.com/andfRa/Saga/blob/master/src/org/saga/utility/chat/ChatFiller.java
 */
class ChatStringFiller extends StringFiller {
    private static final double DEFAULT_LENGTH = 3.0 / 2.0;
    private static final double MAX_GAP = 1.25;
    private static final HashMap<Character, Double> SIZE_MAP = new HashMap<Character, Double>() {
        {
            put('i', 0.5);
            put('k', 5.0 / 4.0);
            put('t', 1.0);
            put('f', 5.0 / 4.0);
            put('(', 5.0 / 4.0);
            put(')', 5.0 / 4.0);
            put('<', 5.0 / 4.0);
            put('>', 5.0 / 4.0);
            put('{', 5.0 / 4.0);
            put('}', 5.0 / 4.0);
            put(',', 1.0 / 2.0);
            put('.', 1.0 / 2.0);
            put('[', 1.0);
            put(']', 1.0);
            put('I', 1.0);
            put('|', 1.0 / 2.0);
            put('*', 5.0 / 4.0);
            put('"', 5.0 / 4.0);
            put('|', 0.5);
            put('!', 0.5);
            put(':', 0.5);
            put('l', 3.0 / 4.0);
            put('.', 1.0 / 2.0);
            put('\'', 3.0 / 4.0);
            put(' ', 1.0 / 1.0);
            put('\"', 5.0 / 4.0);
            put('`', 0.5);
            put('\0', 0.0);

            put('\u278A', 0.5);
            put('\u278B', 3.0 / 4.0);
            put(' ', 1.0);
            put('\u278C', 5.0 / 4.0);

            put('\u2500', 5.0 / 4.0);
            put('\u2502', 1.0 / 4.0);
            put('\u250C', 3.0 / 4.0);
            put('\u2510', 3.0 / 4.0);
            put('\u2514', 3.0 / 4.0);
            put('\u2518', 3.0 / 4.0);

            put('\u2550', 5.0 / 4.0);
            put('\u2551', 1.0 / 2.0);

            put('\u2554', 3.0 / 4.0);
            put('\u2560', 3.0 / 4.0);
            put('\u255A', 3.0 / 4.0);

            put('\u2557', 4.0 / 4.0);
            put('\u2563', 4.0 / 4.0);
            put('\u255D', 4.0 / 4.0);

            put('\u2591', 2.0);
        }
    };
    private static final HashSet<Character> FILL_CHARS = new HashSet<Character>() {
        private static final long serialVersionUID = 1L;

        {
            add('\u278A');
            add('\u278B');
            add(' ');
            add('\u278C');
        }
    };

    private static Character findCustom(double gapLen) {

        Set<Character> gapStrs = new HashSet<>(FILL_CHARS);
        double bestFitLen = -1.0;
        Character bestFitStr = null;

        for (Character gapStr : gapStrs) {

            double gapStrLen = SIZE_MAP.get(gapStr);

            if (gapLen - gapStrLen >= 0 && gapStrLen > bestFitLen) {
                bestFitLen = gapStrLen;
                bestFitStr = gapStr;
            }
        }

        return bestFitStr;
    }

    @Override
    public String fill(String str, double reqLength) {

        char[] chars = str.toCharArray();

        StringBuilder result = new StringBuilder();
        double length = 0.0;

        for (int i = 0; i < chars.length; i++) {

            Double charLength = SIZE_MAP.get(chars[i]);
            if (charLength == null) {
                charLength = DEFAULT_LENGTH;
            }

            if (length + charLength > reqLength) {
                break;
            }

            result.append(chars[i]);

            if (!(chars[i] == ChatFormat.FORMAT_CHAR || (i > 0 && chars[i - 1] == ChatFormat.FORMAT_CHAR))) {
                length += charLength;
            }
        }

        Character fillChar = ' ';
        double fillLength = 1.0;
        while (true) {

            double gapLength = reqLength - length;

            if (gapLength <= 0) {
                break;
            }

            if (gapLength <= MAX_GAP) {

                fillChar = findCustom(gapLength);
                if (fillChar != null) {
                    result.append(fillChar);
                }

                break;
            }

            result.append(fillChar);
            length += fillLength;
        }

        return result.toString()
                .replace("\u278A", ChatFormat.DARK_GRAY + "`" + ChatFormat.RESET)
                .replace("\u278B", ChatFormat.DARK_GRAY + String.valueOf(ChatFormat.BOLD) + '`' + ChatFormat.RESET)
                .replace("\u278C", ChatFormat.DARK_GRAY + String.valueOf(ChatFormat.BOLD) + ' ' + ChatFormat.RESET);
    }

    @Override
    public double getLength(String str) {
        char[] chars = str.toCharArray();

        double length = 0.0;

        for (int i = 0; i < chars.length; i++) {

            Double charLength = SIZE_MAP.get(chars[i]);
            if (charLength == null) {
                charLength = DEFAULT_LENGTH;
            }

            if (!(chars[i] == ChatFormat.FORMAT_CHAR || (i > 0 && chars[i - 1] == ChatFormat.FORMAT_CHAR))) {
                length += charLength;
            }
        }

        return length;
    }
}