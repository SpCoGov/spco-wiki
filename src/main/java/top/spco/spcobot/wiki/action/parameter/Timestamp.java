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
package top.spco.spcobot.wiki.action.parameter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;

/**
 * 时间戳。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public class Timestamp {
    private final static String PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN).withZone(ZoneId.of("UTC"));
    private final Instant instant;

    /**
     * @since 0.1.0
     */
    public Timestamp(Instant instant) {
        this.instant = instant;
    }

    @Override
    public String toString() {
        return toString(instant);
    }

    /**
     * 获取之后一段时间的时间戳。
     *
     * @param amountToAdd 时间长度
     * @param unit        时间单位
     * @return 时间戳
     * @since 0.1.0
     */
    public Timestamp after(long amountToAdd, TemporalUnit unit) {
        return new Timestamp(instant.plus(amountToAdd, unit));
    }

    /**
     * 获取现在之前一段时间的时间戳。
     *
     * @param amountToSubtract 时间长度
     * @param unit             时间单位
     * @return 时间戳
     * @since 0.1.0
     */
    public Timestamp before(long amountToSubtract, TemporalUnit unit) {
        return new Timestamp(instant.minus(amountToSubtract, unit));
    }

    /**
     * 获取现在的时间戳。
     *
     * @return 时间戳
     * @since 0.1.0
     */
    public static Timestamp now() {
        return new Timestamp(Instant.now());
    }

    /**
     * @since 0.1.0
     */
    public static Timestamp beforeNow(long amountToSubtract, TemporalUnit unit) {
        return now().before(amountToSubtract, unit);
    }

    /**
     * @since 0.1.0
     */
    public static Timestamp afterNow(long amountToAdd, TemporalUnit unit) {
        return now().after(amountToAdd, unit);
    }

    /**
     * @since 0.1.0
     */
    public static String toString(Instant instant) {
        return formatter.format(instant);
    }
}