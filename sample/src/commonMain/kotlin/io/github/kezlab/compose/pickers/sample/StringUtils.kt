package io.github.kezlab.compose.pickers.sample

import io.github.kezlab.compose.pickers.time.TimePeriod
import kotlinx.datetime.LocalTime

/**
 * Sample-wide display strings.
 *
 * These are intentionally English-only. The sample ships a Web (Wasm) target whose bundled font
 * has no CJK glyphs, so non-Latin sample text renders as missing-glyph boxes in the browser demo
 * and in the screenshots generated from it.
 */

internal fun formatTime12(hour: Int?, minute: Int?, period: TimePeriod?): String {
    val h = hour ?: 12
    val m = minute ?: 0
    val p = period ?: TimePeriod.AM
    return "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')} ${getTimePeriodContentDescription(p)}"
}

internal fun formatTime12(time: LocalTime): String {
    val period = if (time.hour >= 12) TimePeriod.PM else TimePeriod.AM
    val hour = time.hour % 12
    val displayHour = if (hour == 0) 12 else hour
    return formatTime12(displayHour, time.minute, period)
}

internal fun formatTime24(hour: Int?, minute: Int?): String {
    val h = hour ?: 0
    val m = minute ?: 0
    return "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
}

internal fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "Jan"
        2 -> "Feb"
        3 -> "Mar"
        4 -> "Apr"
        5 -> "May"
        6 -> "Jun"
        7 -> "Jul"
        8 -> "Aug"
        9 -> "Sep"
        10 -> "Oct"
        11 -> "Nov"
        12 -> "Dec"
        else -> "Unknown"
    }
}

internal fun getMonthContentDescription(month: Int): String {
    return when (month) {
        in 1..12 -> "${getMonthName(month)}, month $month"
        else -> "Unknown month"
    }
}

internal fun getTimePeriodContentDescription(period: TimePeriod): String {
    return when (period) {
        TimePeriod.AM -> "AM"
        TimePeriod.PM -> "PM"
    }
}
