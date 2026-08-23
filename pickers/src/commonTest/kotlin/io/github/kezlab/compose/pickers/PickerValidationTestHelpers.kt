package io.github.kezlab.compose.pickers

import io.github.kezlab.compose.pickers.date.YearMonthPickerState
import io.github.kezlab.compose.pickers.date.validateYearMonthPickerItems
import io.github.kezlab.compose.pickers.time.TimePickerState
import io.github.kezlab.compose.pickers.time.validateTimePickerItems
import io.github.kezlab.compose.pickers.time.TimePeriod

internal fun validateTimePickerItems(
    state: TimePickerState,
    minuteItems: List<Int>,
    hourItems: List<Int>,
    periodItems: List<TimePeriod>
) {
    validateTimePickerItems(
        state = state,
        items = TimePickerItems(
            minuteItems = minuteItems,
            hour24Items = hourItems,
            hour12Items = hourItems,
            periodItems = periodItems
        )
    )
}

internal fun validateYearMonthPickerItems(
    state: YearMonthPickerState,
    yearItems: List<Int>,
    monthItems: List<Int>
) {
    validateYearMonthPickerItems(
        state = state,
        items = YearMonthPickerItems(
            yearItems = yearItems,
            monthItems = monthItems
        )
    )
}
