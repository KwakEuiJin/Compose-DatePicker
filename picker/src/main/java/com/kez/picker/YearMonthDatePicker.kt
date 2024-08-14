package com.kez.picker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kez.picker.model.YearMonth
import com.kez.picker.util.MONTH_RANGE
import com.kez.picker.util.YEAR_RANGE

@Composable
fun YearMonthDatePicker(
    modifier: Modifier = Modifier,
    yearPickerState: PickerState = rememberPickerState(),
    monthPickerState: PickerState = rememberPickerState(),
    initYearMonth: YearMonth = YearMonth.now(),
    yearItems: List<String> = YEAR_RANGE,
    monthItems: List<String> = MONTH_RANGE,
    visibleItemsCount: Int = 3,
    itemPadding: PaddingValues = PaddingValues(8.dp),
    textStyle: TextStyle = TextStyle(fontSize = 16.sp),
    selectedTextStyle: TextStyle = TextStyle(fontSize = 24.sp),
    dividerColor: Color = LocalContentColor.current,
    fadingEdgeGradient: Brush = Brush.verticalGradient(
        0f to Color.Transparent,
        0.5f to Color.Black,
        1f to Color.Transparent
    ),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    dividerThickness: Dp = 2.dp,
    dividerShape: Shape = RoundedCornerShape(10.dp),
    spacingBetweenPickers: Dp = 20.dp,
    pickerWidth: Dp = 100.dp
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {

            val initialYearIndex = remember {
                yearItems.indexOf(initYearMonth.year.toString())
            }
            val initialMonthIndex = remember {
                monthItems.indexOf(initYearMonth.month.toString())
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacingBetweenPickers, Alignment.CenterHorizontally),
            ) {
                Picker(
                    state = yearPickerState,
                    modifier = Modifier.width(pickerWidth),
                    items = yearItems,
                    startIndex = initialYearIndex,
                    visibleItemsCount = visibleItemsCount,
                    textModifier = Modifier.padding(itemPadding),
                    textStyle = textStyle,
                    selectedTextStyle = selectedTextStyle,
                    dividerColor = dividerColor,
                    itemPadding = itemPadding,
                    fadingEdgeGradient = fadingEdgeGradient,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = verticalAlignment,
                    dividerThickness = dividerThickness,
                    dividerShape = dividerShape
                )
                Picker(
                    state = monthPickerState,
                    items = monthItems,
                    startIndex = initialMonthIndex,
                    visibleItemsCount = visibleItemsCount,
                    modifier = Modifier.width(pickerWidth),
                    textStyle = textStyle,
                    selectedTextStyle = selectedTextStyle,
                    textModifier = Modifier.padding(itemPadding),
                    dividerColor = dividerColor,
                    itemPadding = itemPadding,
                    fadingEdgeGradient = fadingEdgeGradient,
                    horizontalAlignment = horizontalAlignment,
                    verticalAlignment = verticalAlignment,
                    dividerThickness = dividerThickness,
                    dividerShape = dividerShape
                )
            }
        }
    }
}
