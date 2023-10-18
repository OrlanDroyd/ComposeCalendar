package com.gmail.orlandroyd.composecalendar

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.util.Date
import java.util.Locale

@Composable
fun MonthYearPickerDialog(
    visible: Boolean,
    textTile: String = "Fecha de caducidad",
    dialogColor: Color = Color(0xB1000000),
    accentColor: Color = Color(0xFFF39D00),
    currentSelection: Date? = null,
    onDateSelected: (Date) -> Unit = {},
    onClose: () -> Unit = {},
) {
    if (visible) {
        BackHandler {
            onClose()
        }
    }
    BaseCenteredDialog(
        visible = visible,
        dialogColor = dialogColor,
        onOutsideTouch = {
            onClose()
        }
    ) {
        ComposeCalendar(
            title = textTile,
            currentDate = currentSelection,
            maxDate = Date(),
            locale = Locale.getDefault(),
            onDateSelected = onDateSelected,
            onClose = onClose,
            themeColor = accentColor
        )
    }
}
