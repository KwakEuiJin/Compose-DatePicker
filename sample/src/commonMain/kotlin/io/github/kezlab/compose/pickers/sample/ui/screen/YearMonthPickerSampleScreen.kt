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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.kezlab.compose.pickers.PickerDefaults
import io.github.kezlab.compose.pickers.date.YearMonth
import io.github.kezlab.compose.pickers.date.YearMonthPicker
import io.github.kezlab.compose.pickers.date.rememberYearMonthPickerState
import io.github.kezlab.compose.pickers.sample.getMonthContentDescription
import io.github.kezlab.compose.pickers.sample.getMonthName
import io.github.kezlab.compose.pickers.currentDate
import compose.icons.FeatherIcons
import compose.icons.feathericons.Calendar
import kotlinx.datetime.number

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun YearMonthPickerSampleScreen(
    onBackPressed: () -> Unit = {},
) {
    val currentDate = remember { currentDate() }
    val currentYearMonth = remember(currentDate) {
        YearMonth.from(currentDate)
    }
    val nextYearSameMonth = remember(currentDate) {
        YearMonth(
            year = currentDate.year + 1,
            month = currentDate.month.number
        )
    }
    val items = remember(currentYearMonth, nextYearSameMonth) {
        PickerDefaults.yearMonthPickerItems(
            yearItems = (currentYearMonth.year..nextYearSameMonth.year).toList(),
            minYearMonth = currentYearMonth,
            maxYearMonth = nextYearSameMonth
        )
    }
    val state = rememberYearMonthPickerState(
        items = items,
        initialDate = currentDate
    )

    // Calculate selected date text
    val selectedDateText by remember {
        derivedStateOf {
            "${getMonthName(state.selectedMonth)} ${state.selectedYear}"
        }
    }

    Scaffold(
        topBar = {
            SampleTopAppBar(
                title = "YearMonthPicker Sample",
                onBackPressed = onBackPressed
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SelectedValueCard(
                icon = FeatherIcons.Calendar,
                label = "Selected month",
                value = selectedDateText,
                supportingText = "Stored as ${state.selectedYearMonth.atDay()}"
            )

            Spacer(modifier = Modifier.height(16.dp))

            SampleActionRow {
                Button(
                    onClick = {
                        val yearMonth = YearMonth.from(currentDate())
                        state.selectYearMonth(yearMonth, items)
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
                        val yearMonth = YearMonth(
                            year = currentDate.year + 1,
                            month = currentDate.month.number
                        )
                        state.selectYearMonth(yearMonth, items)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Same month next year")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PickerPanel {
                YearMonthPicker(
                    modifier = Modifier.padding(horizontal = 12.dp),
                    state = state,
                    spacingBetweenPickers = 8.dp,
                    items = items,
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
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
