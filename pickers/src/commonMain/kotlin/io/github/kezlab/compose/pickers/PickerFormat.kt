package io.github.kezlab.compose.pickers

import androidx.compose.runtime.Immutable
import io.github.kezlab.compose.pickers.time.TimePeriod

/**
 * Value formatting for one [Picker] column.
 *
 * [itemText] controls visible item text. [itemContentDescription] controls the accessibility
 * value description when screen reader output should differ from visible text. When
 * [itemContentDescription] is null, [Picker] uses [itemText] for the accessibility value too.
 *
 * @param itemText Text displayed for each item value.
 * @param itemContentDescription Optional accessibility description for each item value. When null,
 * [itemText] is used as the default value description.
 * @see PickerDefaults.itemFormat
 */
@Immutable
data class PickerItemFormat<T : Any>(
    val itemText: (T) -> String = { it.toString() },
    val itemContentDescription: ((T) -> String)? = null
)

/**
 * Value formatting for [io.github.kezlab.compose.pickers.time.TimePicker].
 *
 * @param hour Formatting for hour values.
 * @param minute Formatting for minute values.
 * @param period Formatting for AM/PM values.
 * @see PickerDefaults.timePickerFormat
 */
@Immutable
data class TimePickerFormat(
    val hour: PickerItemFormat<Int>,
    val minute: PickerItemFormat<Int>,
    val period: PickerItemFormat<TimePeriod>
)

/**
 * Value formatting for [io.github.kezlab.compose.pickers.date.DatePicker].
 *
 * @param year Formatting for year values.
 * @param month Formatting for month values.
 * @param day Formatting for day values.
 * @see PickerDefaults.datePickerFormat
 */
@Immutable
data class DatePickerFormat(
    val year: PickerItemFormat<Int>,
    val month: PickerItemFormat<Int>,
    val day: PickerItemFormat<Int>
)

/**
 * Value formatting for [io.github.kezlab.compose.pickers.date.YearMonthPicker].
 *
 * @param year Formatting for year values.
 * @param month Formatting for month values.
 * @see PickerDefaults.yearMonthPickerFormat
 */
@Immutable
data class YearMonthPickerFormat(
    val year: PickerItemFormat<Int>,
    val month: PickerItemFormat<Int>
)
