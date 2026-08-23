package io.github.kezlab.compose.pickers.sample.ui.screen

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kezlab.compose.pickers.PickerDefaults
import io.github.kezlab.compose.pickers.date.DateRange
import io.github.kezlab.compose.pickers.date.DateRangePicker
import io.github.kezlab.compose.pickers.date.rememberDateRangePickerState
import io.github.kezlab.compose.pickers.sample.getMonthContentDescription
import io.github.kezlab.compose.pickers.sample.getMonthName
import io.github.kezlab.compose.pickers.currentDate
import compose.icons.FeatherIcons
import compose.icons.feathericons.Calendar
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateRangePickerSampleScreen(
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "DateRangePicker Sample",
                onBackPressed = onBackPressed
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val today = remember { currentDate() }
            val yearStart = remember(today.year) { LocalDate(today.year, 1, 1) }
            val yearEnd = remember(today.year) { LocalDate(today.year, 12, 31) }
            val todayRange = remember(today) {
                DateRange(startDate = today, endDate = today)
            }
            val monthRange = remember(today) {
                DateRange(
                    startYear = today.year,
                    startMonth = today.month.number,
                    startDay = 1,
                    endYear = today.year,
                    endMonth = today.month.number,
                    endDay = 31
                )
            }
            val items = remember(today.year, yearStart, yearEnd) {
                PickerDefaults.datePickerItems(
                    yearItems = listOf(today.year),
                    minDate = yearStart,
                    maxDate = yearEnd
                )
            }
            val state = rememberDateRangePickerState(
                items = items,
                initialDateRange = monthRange
            )
            var lastUserRangeText by rememberSaveable {
                mutableStateOf("No user change")
            }
            val selectedRange = state.selectedDateRange
            val selectionKind = if (selectedRange.isSingleDay) {
                "Single day"
            } else {
                "Date range"
            }

            SelectedValueCard(
                icon = FeatherIcons.Calendar,
                label = "Selected range",
                value = selectedRange.asText(),
                supportingText = "$selectionKind. Days selected: ${selectedRange.dayCount}. " +
                        "Last user change: $lastUserRangeText. Selectable year: ${today.year}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SampleActionRow {
                Button(
                    onClick = {
                        state.selectDateRange(
                            dateRange = monthRange,
                            items = items
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = FeatherIcons.Calendar,
                        contentDescription = null
                    )
                    IconTextGap()
                    Text("This month")
                }

                OutlinedButton(
                    onClick = {
                        state.selectDateRange(
                            dateRange = todayRange,
                            items = items
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Today only")
                }

                OutlinedButton(
                    onClick = {
                        state.selectDateRange(
                            startDate = yearEnd,
                            endDate = yearStart,
                            items = items
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Year bounds")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PickerPanel {
                DateRangePicker(
                    state = state,
                    items = items,
                    onSelectedDateRangeChange = { selectedRange ->
                        lastUserRangeText = selectedRange.asText()
                    },
                    format = PickerDefaults.datePickerFormat(
                        yearItemText = { "$it" },
                        monthItemText = { getMonthName(it) },
                        dayItemText = { "$it" },
                        monthItemContentDescription = { getMonthContentDescription(it) }
                    ),
                    layout = PickerDefaults.datePickerLayout(
                        yearWeight = 1.05f,
                        monthWeight = 1.55f,
                        dayWeight = 0.75f
                    ),
                    spacingBetweenPickers = 8.dp,
                    startLabel = { Text("Start date") },
                    endLabel = { Text("End date") },
                    semantics = PickerDefaults.dateRangePickerSemantics(
                        start = PickerDefaults.datePickerSemantics(
                            yearPickerLabel = "Start year",
                            monthPickerLabel = "Start month",
                            dayPickerLabel = "Start day"
                        ),
                        end = PickerDefaults.datePickerSemantics(
                            yearPickerLabel = "End year",
                            monthPickerLabel = "End month",
                            dayPickerLabel = "End day"
                        )
                    )
                )
            }
        }
    }
}

private fun DateRange.asText(): String =
    "$startDate..$endDate"
