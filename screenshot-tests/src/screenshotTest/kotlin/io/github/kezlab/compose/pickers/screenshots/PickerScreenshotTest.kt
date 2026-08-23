package io.github.kezlab.compose.pickers.screenshots

import android.content.res.Configuration
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import io.github.kezlab.compose.pickers.PickerDefaults
import io.github.kezlab.compose.pickers.date.DatePicker
import io.github.kezlab.compose.pickers.date.DateRangePicker
import io.github.kezlab.compose.pickers.date.YearMonth
import io.github.kezlab.compose.pickers.date.YearMonthPicker
import io.github.kezlab.compose.pickers.date.rememberDatePickerState
import io.github.kezlab.compose.pickers.date.rememberDateRangePickerState
import io.github.kezlab.compose.pickers.date.rememberYearMonthPickerState
import io.github.kezlab.compose.pickers.duration.DurationPicker
import io.github.kezlab.compose.pickers.duration.rememberDurationPickerState
import io.github.kezlab.compose.pickers.time.TimePicker
import io.github.kezlab.compose.pickers.time.rememberTimePickerState
import io.github.kezlab.compose.pickers.time.TimeFormat
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/*
 * Reference images are keyed by the test function name *and* the @Preview arguments, so both are
 * part of the baseline identity. See docs/testing/compose-screenshot-tests.md before renaming a
 * function or changing a @Preview argument.
 *
 * Every case uses fixed values instead of the clock so the rendered output is deterministic.
 */

private const val PICKER_WIDTH_DP = 360
private const val PICKER_HEIGHT_DP = 260

/** 2024 is a leap year, so February has 29 days: the day column must offer 29 and stop there. */
private val LEAP_DAY = LocalDate(2024, 2, 29)

@PreviewTest
@Preview(
    name = "date_picker_selected_date",
    widthDp = PICKER_WIDTH_DP,
    heightDp = PICKER_HEIGHT_DP
)
@Composable
private fun DatePickerScreenshot() {
    PickerScreenshotTheme {
        DatePicker(
            state = rememberDatePickerState(initialDate = LEAP_DAY),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewTest
@Preview(
    name = "date_picker_dark",
    widthDp = PICKER_WIDTH_DP,
    heightDp = PICKER_HEIGHT_DP,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun DatePickerDarkScreenshot() {
    PickerScreenshotTheme {
        DatePicker(
            state = rememberDatePickerState(initialDate = LEAP_DAY),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewTest
@Preview(
    name = "date_picker_disabled",
    widthDp = PICKER_WIDTH_DP,
    heightDp = PICKER_HEIGHT_DP
)
@Composable
private fun DatePickerDisabledScreenshot() {
    PickerScreenshotTheme {
        DatePicker(
            state = rememberDatePickerState(initialDate = LEAP_DAY),
            enabled = false,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewTest
@Preview(
    name = "date_picker_large_font",
    widthDp = PICKER_WIDTH_DP,
    heightDp = PICKER_HEIGHT_DP,
    fontScale = 1.5f
)
@Composable
private fun DatePickerLargeFontScreenshot() {
    PickerScreenshotTheme {
        DatePicker(
            state = rememberDatePickerState(initialDate = LEAP_DAY),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewTest
@Preview(
    name = "time_picker_12_hour",
    widthDp = PICKER_WIDTH_DP,
    heightDp = PICKER_HEIGHT_DP
)
@Composable
private fun TimePickerScreenshot() {
    PickerScreenshotTheme {
        TimePicker(
            state = rememberTimePickerState(
                initialTime = LocalTime(13, 5),
                timeFormat = TimeFormat.HOUR_12
            ),
            format = PickerDefaults.timePickerFormat(minuteItemText = { it.toString().padStart(2, '0') }),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewTest
@Preview(
    name = "time_picker_24_hour",
    widthDp = PICKER_WIDTH_DP,
    heightDp = PICKER_HEIGHT_DP
)
@Composable
private fun TimePicker24HourScreenshot() {
    PickerScreenshotTheme {
        TimePicker(
            state = rememberTimePickerState(
                initialTime = LocalTime(13, 5),
                timeFormat = TimeFormat.HOUR_24
            ),
            format = PickerDefaults.timePickerFormat(minuteItemText = { it.toString().padStart(2, '0') }),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewTest
@Preview(
    name = "year_month_picker_selected_month",
    widthDp = PICKER_WIDTH_DP,
    heightDp = PICKER_HEIGHT_DP
)
@Composable
private fun YearMonthPickerScreenshot() {
    PickerScreenshotTheme {
        YearMonthPicker(
            state = rememberYearMonthPickerState(initialYearMonth = YearMonth(2024, 6)),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewTest
@Preview(
    name = "duration_picker_selected_duration",
    widthDp = PICKER_WIDTH_DP,
    heightDp = PICKER_HEIGHT_DP
)
@Composable
private fun DurationPickerScreenshot() {
    PickerScreenshotTheme {
        DurationPicker(
            state = rememberDurationPickerState(initialDuration = 2.hours + 30.minutes),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@PreviewTest
@Preview(
    name = "date_range_picker_selected_range",
    widthDp = PICKER_WIDTH_DP,
    // The range picker stacks two full pickers plus their labels. A short preview silently clips
    // the end-date half, which would leave that half unverified.
    heightDp = 520
)
@Composable
private fun DateRangePickerScreenshot() {
    PickerScreenshotTheme {
        DateRangePicker(
            state = rememberDateRangePickerState(
                initialStartDate = LocalDate(2024, 6, 15),
                initialEndDate = LocalDate(2024, 6, 21)
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun PickerScreenshotTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface(modifier = Modifier.fillMaxSize(), content = content)
    }
}
