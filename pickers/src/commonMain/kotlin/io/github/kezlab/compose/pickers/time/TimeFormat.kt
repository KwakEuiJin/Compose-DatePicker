package io.github.kezlab.compose.pickers.time

/**
 * Hour presentation used by [TimePicker] and [TimePickerState].
 */
public enum class TimeFormat {
    /**
     * 12-hour format with an AM/PM column.
     */
    HOUR_12,

    /**
     * 24-hour format without an AM/PM column.
     */
    HOUR_24
}

/**
 * Half-day period selected by the AM/PM column of a 12-hour [TimePicker].
 */
public enum class TimePeriod {
    /**
     * AM period (Ante Meridiem).
     */
    AM,

    /**
     * PM period (Post Meridiem).
     */
    PM
}
