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

import org.jetbrains.annotations.NotNull;

public class Range implements Comparable<Range> {
    private int start;
    private int end;

    public Range(int start, int end) {
        if (start > end) {
            throw new IllegalArgumentException("Start must be less than end");
        }
        this.start = start;
        this.end = end;
    }

    public void expandStart(int newStart) {
        if (newStart < start) {
            changeStart(newStart);
        }
    }

    public void expandEnd(int newEnd) {
        if (newEnd > end) {
            changeEnd(newEnd);
        }
    }

    public void shrinkStart(int newStart) {
        if (newStart > start) {
            changeStart(newStart);
        }
    }

    public void shrinkEnd(int newEnd) {
        if (newEnd < end) {
            changeEnd(newEnd);
        }
    }

    public void include(int number) {
        if (number < start) {
            changeStart(number);
        } else if (number > end) {
            changeEnd(number);
        }
    }

    public boolean contains(int number) {
        return number >= start && number <= end;
    }

    private void changeStart(int newStart) {
        changeRange(newStart, end);
    }

    private void changeEnd(int newEnd) {
        changeRange(start, newEnd);
    }

    private void changeRange(int newStart, int newEnd) {
        if (newStart > newEnd) {
            throw new IllegalArgumentException("Start must be less than end");
        }
        start = newStart;
        end = newEnd;
    }

    @Override
    public String toString() {
        if (end == start) {
            return start + "";
        } else {
            return start + "-" + end;
        }
    }

    @Override
    public int compareTo(@NotNull Range other) {
        if (this.start != other.start) {
            return Integer.compare(this.start, other.start);
        } else {
            return Integer.compare(this.end, other.end);
        }
    }
}