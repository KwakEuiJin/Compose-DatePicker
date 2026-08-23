package io.github.kezlab.compose.pickers.sample.ui.screen.quantity

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.kezlab.compose.pickers.WheelPicker
import io.github.kezlab.compose.pickers.PickerDefaults
import io.github.kezlab.compose.pickers.PickerStyle

@Composable
internal fun QuantityUnitPicker(
    state: QuantityUnitPickerState,
    items: QuantityUnitItems,
    onSelectedQuantityUnitChange: (QuantityUnitSelection) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    layout: QuantityUnitPickerLayout = QuantityUnitPickerLayout(),
    spacingBetweenPickers: Dp = 8.dp,
    style: PickerStyle = PickerDefaults.style()
) {
    remember(items, state, state.selectedSelection) {
        require(items.contains(state.selectedSelection)) {
            "QuantityUnitPicker selectedSelection=${state.selectedSelection} must be present in " +
                    "items. Create state with rememberQuantityUnitPickerState(items, ...) or call " +
                    "state.selectSelection(value, items) before composing."
        }
    }

    fun commit(nextSelection: QuantityUnitSelection) {
        commitQuantityUnitSelection(
            state = state,
            nextSelection = nextSelection,
            onSelectionCommitted = onSelectedQuantityUnitChange
        )
    }

    val selectedUnit = state.selectedUnit
    val quantityItems = remember(items, selectedUnit) {
        items.quantitiesFor(selectedUnit)
    }
    val quantityFormat = remember(selectedUnit) {
        PickerDefaults.itemFormat<Int>(
            itemText = { quantity -> "$quantity ${selectedUnit.symbol}" },
            itemContentDescription = selectedUnit::spokenQuantity
        )
    }
    val quantitySemantics = remember(selectedUnit) {
        PickerDefaults.semantics(
            pickerLabel = when (selectedUnit) {
                QuantityUnit.GRAM -> "Quantity in grams"
                QuantityUnit.KILOGRAM -> "Quantity in kilograms"
            },
            previousItemActionLabel = "Select previous quantity",
            nextItemActionLabel = "Select next quantity"
        )
    }
    val unitFormat = remember {
        PickerDefaults.itemFormat<QuantityUnit>(
            itemText = QuantityUnit::symbol,
            itemContentDescription = { it.displayName }
        )
    }
    val unitSemantics = remember {
        PickerDefaults.semantics(
            pickerLabel = "Mass unit",
            previousItemActionLabel = "Select previous mass unit",
            nextItemActionLabel = "Select next mass unit"
        )
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            space = spacingBetweenPickers,
            alignment = Alignment.CenterHorizontally
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        layout.columnOrder.forEach { column ->
            key(column, state, items, state.selectedSelection, enabled) {
                when (column) {
                    // Dependent columns must not react to a value that is still moving, so the
                    // live callback stays empty and only the settled selection commits.
                    QuantityUnitColumn.QUANTITY -> WheelPicker(
                        items = quantityItems,
                        selectedItem = state.selectedQuantity,
                        onSelectedItemChange = {},
                        modifier = Modifier.weight(2f),
                        enabled = enabled,
                        format = quantityFormat,
                        style = style,
                        semantics = quantitySemantics,
                        isInfinity = false,
                        onSelectionSettled = { quantity ->
                            if (quantity != state.selectedQuantity) {
                                commit(
                                    items.repairedSelectionAfterQuantity(
                                        currentSelection = state.selectedSelection,
                                        quantity = quantity
                                    )
                                )
                            }
                        }
                    )

                    QuantityUnitColumn.UNIT -> WheelPicker(
                        items = items.unitItems,
                        selectedItem = state.selectedUnit,
                        onSelectedItemChange = {},
                        modifier = Modifier.weight(1f),
                        enabled = enabled,
                        format = unitFormat,
                        style = style,
                        semantics = unitSemantics,
                        isInfinity = false,
                        onSelectionSettled = { unit ->
                            if (unit != state.selectedUnit) {
                                commit(
                                    items.repairedSelectionAfterUnit(
                                        currentSelection = state.selectedSelection,
                                        unit = unit
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
