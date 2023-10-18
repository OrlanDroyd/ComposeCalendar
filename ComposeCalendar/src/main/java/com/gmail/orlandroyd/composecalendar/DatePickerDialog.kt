package com.gmail.orlandroyd.composecalendar

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmail.orlandroyd.composecalendar.util.getCalendar
import com.gmail.orlandroyd.composecalendar.util.getParsedDate
import com.gmail.orlandroyd.composecalendar.util.showTimePickerDialog
import java.util.Calendar
import java.util.Date

@Composable
fun DatePickerDialog(
    visible: Boolean,
    title: String = "",
    showSetHours: Boolean = false,
    titleColor: Color = Color(0xFF4395D6),
    dialogColor: Color = Color(0xB1000000),
    primaryColor: Color = Color(0xFF4395D6),
    primaryTextColor: Color = Color(0xFF1A1A1A),
    secondaryTextColor: Color = Color(0xFF9F9E9E),
    surfaceColor: Color = Color(0xFFFFFFFF),
    dividerColor: Color = Color(0xFFE2E2E2),
    iconsColor: Color = Color(0xFF9F9E9E),
    acceptTextColor: Color = Color(0xFFFFFFFF),
    accentColor: Color = Color(0xFF4395D6),
    acceptText: String = "Aceptar",
    messageFutureHours: String = "La hora debe estar en el futuro",
    messageSelectedHours: String = "Seleccionar hora",
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
        },
    ) {
        var selectedDate by remember {
            mutableStateOf(currentSelection)
        }
        var selectedTime by remember {
            mutableStateOf(currentSelection)
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
                modifier = Modifier.padding(10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = null,
                    modifier = Modifier
                        .size(24.dp)
                        .clickableNoRipple { onClose() },
                    tint = primaryColor
                )

                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp,
                    modifier = Modifier.weight(1f),
                    color = titleColor
                )

                Spacer(modifier = Modifier.width(24.dp))
            }

            Divider(thickness = 1.dp, color = dividerColor)

            DatePicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                selectedDate = selectedDate,
                onDateClicked = {
                    selectedDate = it
                    selectedTime = null
                }
            )

            Divider(thickness = 1.dp, color = dividerColor)

            if (showSetHours) {
                val context = LocalContext.current
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(10.dp)
                        .graphicsLayer {
                            shape = RoundedCornerShape(4.dp)
                            clip = true
                        }
                        .border(
                            width = 1.dp,
                            color = dividerColor,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .clickableNoRipple {
                            selectedDate?.let { date ->
                                val dateCalendar = date.getCalendar()
                                val currentTime =
                                    selectedTime?.getCalendar() ?: Calendar.getInstance()
                                currentTime[Calendar.YEAR] = dateCalendar[Calendar.YEAR]
                                currentTime[Calendar.DAY_OF_YEAR] =
                                    dateCalendar[Calendar.DAY_OF_YEAR]
                                context.showTimePickerDialog(
                                    currentDate = currentTime.time,
                                    timeSelected = { timeCalendar ->
                                        selectedDate?.let {
                                            val today = Calendar.getInstance()
                                            val selected = Calendar.getInstance()
                                            selected.time = today.time

                                            selected[Calendar.YEAR] =
                                                timeCalendar[Calendar.YEAR]
                                            selected[Calendar.DAY_OF_YEAR] =
                                                timeCalendar[Calendar.DAY_OF_YEAR]
                                            selected[Calendar.HOUR_OF_DAY] =
                                                timeCalendar[Calendar.HOUR_OF_DAY]
                                            selected[Calendar.MINUTE] =
                                                timeCalendar[Calendar.MINUTE]
                                            if (selected.timeInMillis > today.timeInMillis) {
                                                selectedTime = timeCalendar.time
                                            } else {
                                                Toast
                                                    .makeText(
                                                        context,
                                                        messageFutureHours,
                                                        Toast.LENGTH_SHORT
                                                    )
                                                    .show()
                                            }
                                        }
                                    },
                                    textColor = R.color.grey
                                )
                            }
                        }
                        .padding(10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_clock),
                        contentDescription = null,
                        Modifier.size(24.dp),
                        tint = iconsColor
                    )
                    val textColor =
                        if (selectedTime == null) secondaryTextColor else primaryTextColor
                    val text =
                        selectedTime?.getParsedDate("HH:mm") ?: messageSelectedHours
                    Text(
                        text = text,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 10.dp),
                    )
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dropdown),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = primaryColor
                    )
                }
            }

            Text(
                text = acceptText,
                color = acceptTextColor,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 0.dp, 10.dp, 10.dp)
                    .graphicsLayer {
                        shape = RoundedCornerShape(4.dp)
                        clip = true
                    }
                    .background(accentColor)
                    .clickable {
                        selectedDate?.let { time ->
                            onDateSelected(time)
                        }
                    }
                    .padding(10.dp)
            )
        }
    }
}
