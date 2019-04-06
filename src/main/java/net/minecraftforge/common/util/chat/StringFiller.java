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

public abstract class StringFiller {
    public static final StringFiller CHAT = new ChatStringFiller();
    public static final StringFiller FIXED_WIDTH = new StringFiller() {
        @Override
        public double getLength(String s) {
            return s.length();
        }

        @Override
        public String fill(String s, double requiredLength) {
            StringBuilder stringBuilder = new StringBuilder();
            double length = getLength(s);
            while (length < requiredLength) {
                stringBuilder.append(' ');
                length++;
            }
            return stringBuilder.append(s).toString();
        }
    };

    public abstract double getLength(String s);

    public abstract String fill(String s, double requiredLength);
}
