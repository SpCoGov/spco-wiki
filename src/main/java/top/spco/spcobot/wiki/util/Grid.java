/*
 * Copyright 2024 SpCo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.spco.spcobot.wiki.util;

import java.util.Arrays;

/**
 * 该类用于定义一个二维字符网格（Grid），支持设置和获取网格中元素，并提供网格的字符串表示。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class Grid {
    private final char[][] grid;
    private final int row;
    private final int col;

    /**
     * 构造一个指定行数和列数的空网格。行数和列数必须为正数。
     *
     * @param rows 网格的行数，必须大于0
     * @param cols 网格的列数，必须大于0
     * @throws IllegalArgumentException 如果行数或列数小于1
     */
    public Grid(int rows, int cols) {
        if (rows < 1 || cols < 1) {
            throw new IllegalArgumentException("Rows and cols must be greater than 0.");
        }
        this.row = rows;
        this.col = cols;
        grid = new char[rows][cols];
    }

    /**
     * 设置网格中指定位置的字符值。
     *
     * @param row   要设置的行位置（从1开始计数）
     * @param col   要设置的列位置（从1开始计数）
     * @param value 要设置的字符值
     * @throws IllegalArgumentException 如果行或列超出范围，或者行数或列数小于1，则抛出该异常
     * @since 0.1.0
     */
    public void set(int row, int col, char value) {
        if (row > this.row || col > this.col) {
            throw new IllegalArgumentException("Row or col out of bounds.");
        }
        if (row < 1 || col < 1) {
            throw new IllegalArgumentException("Row and col must be greater than 0.");
        }
        grid[row - 1][col - 1] = value;
    }

    /**
     * 返回网格的字符串表示，每行以换行符分隔。
     * 如果网格中的元素为默认字符（{@code '\u0000'}），则显示为空格。
     *
     * @return 网格的字符串表示
     * @since 0.1.0
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (char[] cols : grid) {
            StringBuilder aCol = new StringBuilder();
            for (char c : cols) {
                aCol.append(c == '\u0000' ? ' ' : c);
            }
            sb.append(aCol).append("\n");
        }
        return sb.toString().trim();
    }

    /**
     * 比较两个 Grid 对象是否相等。
     *
     * @param obj 要比较的对象
     * @return 如果两个对象相等，则返回 {@code true} ；否则返回 {@code false}
     * @since 0.1.0
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Grid other) {
            return Arrays.deepEquals(this.grid, other.grid);
        }
        return false;
    }

    /**
     * 生成网格对象的哈希值。
     *
     * @return 该网格的哈希码
     * @since 0.1.0
     */
    @Override
    public int hashCode() {
        return Arrays.deepHashCode(grid);
    }
}