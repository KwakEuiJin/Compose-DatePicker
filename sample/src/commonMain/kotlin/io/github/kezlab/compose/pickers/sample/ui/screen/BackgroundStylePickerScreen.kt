package io.github.kezlab.compose.pickers.sample.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.kezlab.compose.pickers.PickerDefaults
import io.github.kezlab.compose.pickers.date.YearMonthPicker
import io.github.kezlab.compose.pickers.date.rememberYearMonthPickerState
import io.github.kezlab.compose.pickers.sample.formatTime12
import io.github.kezlab.compose.pickers.sample.getMonthContentDescription
import io.github.kezlab.compose.pickers.sample.getMonthName
import io.github.kezlab.compose.pickers.sample.getTimePeriodContentDescription
import io.github.kezlab.compose.pickers.time.TimePicker
import io.github.kezlab.compose.pickers.time.rememberTimePickerState
import io.github.kezlab.compose.pickers.time.TimeFormat
import io.github.kezlab.compose.pickers.currentDateTime
import compose.icons.FeatherIcons
import compose.icons.feathericons.Calendar
import compose.icons.feathericons.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BackgroundStylePickerScreen(
    onBackPressed: () -> Unit = {},
) {
    val now = remember { currentDateTime() }
    val yearMonthState = rememberYearMonthPickerState(
        initialDate = now.date
    )
    val timeState = rememberTimePickerState(
        initialTime = now.time,
        timeFormat = TimeFormat.HOUR_12
    )

    val selectedDateText = remember(yearMonthState.selectedYear, yearMonthState.selectedMonth) {
        "${getMonthName(yearMonthState.selectedMonth)} ${yearMonthState.selectedYear}"
    }
    val selectedTimeText =
        remember(timeState.selectedHour, timeState.selectedMinute, timeState.selectedPeriod) {
            formatTime12(timeState.selectedHour, timeState.selectedMinute, timeState.selectedPeriod)
        }

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "Background Style Sample",
                onBackPressed = onBackPressed
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SelectedValueCard(
                icon = FeatherIcons.Calendar,
                label = "Selected month",
                value = selectedDateText,
                supportingText = "YearMonthPicker styled without dividers"
            )

            SelectedValueCard(
                icon = FeatherIcons.Clock,
                label = "Selected time",
                value = selectedTimeText,
                supportingText = "TimePicker using the same state/update pattern"
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    YearMonthPicker(
                        state = yearMonthState,
                        spacingBetweenPickers = 8.dp,
                        format = PickerDefaults.yearMonthPickerFormat(
                            yearItemText = { "$it" },
                            monthItemText = { getMonthName(it) },
                            yearItemContentDescription = { "year $it" },
                            monthItemContentDescription = { getMonthContentDescription(it) }
                        ),
                        semantics = PickerDefaults.yearMonthPickerSemantics(
                            yearPickerLabel = "Year",
                            monthPickerLabel = "Month",
                            previousItemActionLabel = "Select previous value",
                            nextItemActionLabel = "Select next value"
                        ),
                        style = PickerDefaults.style(
                            textStyles = PickerDefaults.textStyles(
                                textStyle = TextStyle(
                                    fontSize = 18.sp
                                ),
                                selectedTextStyle = TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ),
                            isDividerVisible = false,
                            colors = PickerDefaults.colors(
                                textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                selectedItemBackgroundColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TimePicker(
                        state = timeState,
                        spacingBetweenPickers = 8.dp,
                        format = PickerDefaults.timePickerFormat(
                            hourItemText = { it.toString().padStart(2, '0') },
                            minuteItemText = { it.toString().padStart(2, '0') },
                            periodItemText = { getTimePeriodContentDescription(it) },
                            hourItemContentDescription = { "$it hour" },
                            minuteItemContentDescription = { "$it minute" },
                            periodItemContentDescription = { getTimePeriodContentDescription(it) }
                        ),
                        semantics = PickerDefaults.timePickerSemantics(
                            hourPickerLabel = "Hour",
                            minutePickerLabel = "Minute",
                            periodPickerLabel = "AM/PM",
                            previousItemActionLabel = "Select previous value",
                            nextItemActionLabel = "Select next value"
                        ),
                        style = PickerDefaults.style(
                            textStyles = PickerDefaults.textStyles(
                                textStyle = TextStyle(
                                    fontSize = 18.sp
                                ),
                                selectedTextStyle = TextStyle(
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            ),
                            isDividerVisible = false,
                            colors = PickerDefaults.colors(
                                textColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                selectedItemBackgroundColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    yearMonthState.selectDate(now.date)
                    timeState.selectTime(now.time)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Reset to launch values", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Preview
@Composable
fun PreviewBackgroundStylePickerScreen() {
    BackgroundStylePickerScreen()
}
