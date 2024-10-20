package top.spco.spcobot.wiki.core.action.parameter;

import java.time.temporal.TemporalUnit;

/**
 * 表示到期时间，用于指定某操作或权限的到期时间。
 *
 * @author SpCo
 * @version 0.1.0
 * @since 0.1.0
 */
public final class Expiry {
    private final String expiry;

    private Expiry(String expiry) {
        this.expiry = expiry;
    }

    @Override
    public String toString() {
        return expiry;
    }

    /**
     * 返回一个表示永不过期的到期时间对象。
     *
     * @return 表示永久到期时间的 {@code Expiry} 对象
     * @since 0.1.0
     */
    public static Expiry forever() {
        return new Expiry("infinite");
    }

    /**
     * 创建一个从当前时间起经过指定时间后的到期时间对象。
     *
     * @param after 过期的时间间隔
     * @param unit  时间间隔的单位
     * @return 表示该到期时间的 {@code Expiry} 对象
     * @since 0.1.0
     */
    public static Expiry after(int after, TemporalUnit unit) {
        return new Expiry(Timestamp.now().after(after, unit).toString());
    }

    /**
     * 创建一个从当前时间起指定分钟数后的到期时间对象。
     *
     * @param minutes 过期的分钟数
     * @return 表示该到期时间的 {@code Expiry} 对象
     * @throws IllegalArgumentException 如果传入的分钟数小于等于 0
     * @since 0.1.0
     */
    public static Expiry durationOfMinutes(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Minutes must be positive");
        }
        return new Expiry(minutes + " minutes");
    }

    /**
     * 创建一个从当前时间起指定小时数后的到期时间对象。
     *
     * @param hours 过期的小时数
     * @return 表示该到期时间的 {@code Expiry} 对象
     * @throws IllegalArgumentException 如果传入的小时数小于等于 0
     * @since 0.1.0
     */
    public static Expiry durationOfHours(int hours) {
        if (hours <= 0) {
            throw new IllegalArgumentException("Hours must be positive");
        }
        return new Expiry(hours + " hours");
    }

    /**
     * 创建一个从当前时间起指定天数后的到期时间对象。
     *
     * @param days 过期的天数
     * @return 表示该到期时间的 {@code Expiry} 对象
     * @throws IllegalArgumentException 如果传入的天数小于等于 0
     * @since 0.1.0
     */
    public static Expiry durationOfDays(int days) {
        if (days <= 0) {
            throw new IllegalArgumentException("Days must be positive");
        }
        return new Expiry(days + " days");
    }

    /**
     * 创建一个从当前时间起指定周数后的到期时间对象。
     *
     * @param weeks 过期的周数
     * @return 表示该到期时间的 {@code Expiry} 对象
     * @throws IllegalArgumentException 如果传入的周数小于等于 0
     * @since 0.1.0
     */
    public static Expiry durationOfWeeks(int weeks) {
        if (weeks <= 0) {
            throw new IllegalArgumentException("Weeks must be positive");
        }
        return new Expiry(weeks + " weeks");
    }

    /**
     * 创建一个从当前时间起指定月数后的到期时间对象。
     *
     * @param months 过期的月数
     * @return 表示该到期时间的 {@code Expiry} 对象
     * @throws IllegalArgumentException 如果传入的月数小于等于 0
     * @since 0.1.0
     */
    public static Expiry durationOfMonths(int months) {
        if (months <= 0) {
            throw new IllegalArgumentException("Months must be positive");
        }
        return new Expiry(months + " months");
    }

    /**
     * 创建一个从当前时间起指定年数后的到期时间对象。
     *
     * @param years 过期的年数
     * @return 表示该到期时间的 {@code Expiry} 对象
     * @throws IllegalArgumentException 如果传入的年数小于等于 0
     * @since 0.1.0
     */
    public static Expiry durationOfYears(int years) {
        if (years <= 0) {
            throw new IllegalArgumentException("Years must be positive");
        }
        return new Expiry(years + " years");
    }
}