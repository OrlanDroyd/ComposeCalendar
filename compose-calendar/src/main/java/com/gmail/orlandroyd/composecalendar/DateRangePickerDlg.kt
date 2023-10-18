package com.gmail.orlandroyd.composecalendar

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmail.orlandroyd.composecalendar.util.getFullMonthTextDate
import com.gmail.orlandroyd.composecalendar.util.sameDay
import java.util.Date

@Composable
fun DateRangePickerDlg(
    visible: Boolean,
    title: String = "",
    txtSelectHour: String = "Seleccionar hora",
    acceptText: String = "Aceptar",
    primaryColor: Color = Color(0xFF4395D6),
    surfaceColor: Color = Color(0xFFFFFFFF),
    dialogColor: Color = Color(0xB1000000),
    secondaryColor: Color = Color(0xFF9F9E9E),
    dividerColor: Color = Color(0xFFE2E2E2),
    secondaryTextColor: Color = Color(0xFF9F9E9E),
    accentColor: Color = Color(0xFF4395D6),
    acceptTextColor: Color = Color(0xFFFFFFFF),
    primaryTextColor: Color = Color(0xFF1A1A1A),
    currentSelection: Pair<Date, Date>? = null,
    onDatesSelected: (Pair<Date, Date>) -> Unit = {},
    onClearFilter: () -> Unit = {},
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
        var selectedDates by remember {
            mutableStateOf(currentSelection)
        }
        var selectedDate by remember {
            mutableStateOf<Date?>(null)
        }
        Column(
            modifier = Modifier
                .graphicsLayer {
                    shape = RoundedCornerShape(15.dp)
                    clip = true
                }
                .background(surfaceColor)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(10.dp)
                    .height(48.dp),

                ) {
                Column(
                    Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .clickableNoRipple { onClose() }
                            .padding(start = 4.dp),
                        tint = primaryColor
                    )
                }

                Column(
                    modifier = Modifier.weight(2f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        color = primaryTextColor,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }

//                val clearTextColor = if (currentSelection != null) {
//                    primaryColor
//                } else {
//                    secondaryColor
//                }
                Column(
                    Modifier.weight(1f)
                ) {
//                    ClickableText(
//                        text = AnnotatedString(txtSelectHour),
//                        onClick = { onClearFilter() },
//                        modifier = Modifier
//                            .align(Alignment.End),
//                        style = TextStyle(
//                            color = clearTextColor,
//                        )
//                    )
                }
            }

            Divider(thickness = 1.dp, color = dividerColor)

            DatePicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                selectedDate = selectedDate,
                selectedDates = selectedDates,
                onDateClicked = { newDate ->
                    if (selectedDates == null && selectedDate == null) {
                        selectedDate = newDate
                    } else if (selectedDate != null) {
                        val current = selectedDate ?: Date()
                        if (current.sameDay(newDate)) {
                            selectedDate = null
                        } else {
                            selectedDates = if (current.time < newDate.time) {
                                Pair(selectedDate ?: Date(), newDate)
                            } else {
                                Pair(newDate, selectedDate ?: Date())
                            }
                            selectedDate = null
                        }
                    } else {
                        val startDate = selectedDates?.first ?: Date()
                        val endDate = selectedDates?.second ?: Date()
                        when {
                            newDate.sameDay(startDate) -> {
                                selectedDate = endDate
                                selectedDates = null
                            }

                            newDate.sameDay(endDate) -> {
                                selectedDate = startDate
                                selectedDates = null
                            }

                            newDate.time < startDate.time -> {
                                selectedDates = Pair(newDate, endDate)
                            }

                            else -> {
                                selectedDates = Pair(startDate, newDate)
                            }
                        }
                    }
                },
                isRange = true,
            )

            Divider(thickness = 1.dp, color = dividerColor)

            var dateText = ""
            (selectedDates?.first ?: selectedDate)?.let {
                dateText = it.getFullMonthTextDate()
            }
            selectedDates?.second?.let {
                dateText += " - "
                dateText += it.getFullMonthTextDate()
            }

            Text(
                text = dateText,
                fontSize = 16.sp,
                color = secondaryTextColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                textAlign = TextAlign.Center,
            )

            Text(
                text = acceptText,
                color = acceptTextColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 0.dp, 10.dp, 10.dp)
                    .graphicsLayer {
                        shape = RoundedCornerShape(4.dp)
                        clip = true
                    }
                    .background(accentColor)
                    .clickable {
                        selectedDates?.let { dates ->
                            onDatesSelected(dates)
                        }
                    }
                    .padding(10.dp)
            )
        }
    }
}
