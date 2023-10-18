package com.gmail.orlandroyd.composecalendar

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gmail.orlandroyd.composecalendar.util.between
import com.gmail.orlandroyd.composecalendar.util.getCalendar
import com.gmail.orlandroyd.composecalendar.util.getCalendarMonthDays
import com.gmail.orlandroyd.composecalendar.util.getFullMonthTextDate
import com.gmail.orlandroyd.composecalendar.util.getMonthText
import com.gmail.orlandroyd.composecalendar.util.getParsedDate
import com.gmail.orlandroyd.composecalendar.util.sameDay
import com.gmail.orlandroyd.composecalendar.util.showTimePickerDialog
import java.util.*


@Composable
fun DatePicker(
    modifier: Modifier = Modifier,
    selectedDate: Date? = null,
    selectedDates: Pair<Date, Date>? = null,
    monthTextSize: TextUnit = 14.sp,
    cellTextSize: TextUnit = 14.sp,
    onDateClicked: (Date) -> Unit = {},
    isRange: Boolean = false
) {
    val nonNullMonth = selectedDate ?: Date()
    var month by remember {
        mutableStateOf(nonNullMonth)
    }
    val weeks = month.getCalendarMonthDays().chunked(7)
    Column(
        modifier = modifier,
    ) {
        DatePickerHeader(
            selectedMonth = month,
            monthTextSize = monthTextSize,
            weekDaysTextSize = cellTextSize,
            onMonthChange = {
                month = it
            },
        )
        weeks.forEach { days ->
            if (!isRange || selectedDates == null) {
                DatePickerWeek(
                    days = days,
                    selectedDate = selectedDate,
                    textSize = cellTextSize,
                    onSelectionChange = onDateClicked,
                    currentMonth = month.getCalendar().get(Calendar.MONTH)
                )
            } else {
                DateRangePickerWeek(
                    days = days,
                    selectedDates = selectedDates,
                    textSize = cellTextSize,
                    onSelectionChange = onDateClicked,
                    currentMonth = month.getCalendar().get(Calendar.MONTH)
                )
            }
        }
    }
}

@Composable
private fun DatePickerHeader(
    selectedMonth: Date,
    monthTextSize: TextUnit = 14.sp,
    weekDaysTextSize: TextUnit = 14.sp,
    onMonthChange: (Date) -> Unit,
    primaryTextColor: Color = Color(0xFF1A1A1A),
    secondaryTextColor: Color = Color(0xFF9F9E9E),
    dividerColor: Color = Color(0xFFE2E2E2),
) {
    Row(
        Modifier
            .fillMaxWidth()
    ) {
        val calendar = Calendar.getInstance()
        calendar.time = selectedMonth
        Icon(
            painter = painterResource(id = R.drawable.ic_calendar_previous),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .align(CenterVertically)
                .padding(12.dp, 5.dp, 12.dp, 5.dp)
                .size(24.dp)
                .clickableNoRipple {
                    calendar.add(Calendar.MONTH, -1)
                    onMonthChange(calendar.time)
                }
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.ic_calendar),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .align(CenterVertically)
                .padding(15.dp, 0.dp, 0.dp, 0.dp)
        )
        Text(
            text = selectedMonth.getMonthText(),
            fontSize = monthTextSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(CenterVertically)
                .padding(10.dp, 0.dp, 15.dp, 0.dp),
            color = primaryTextColor
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(id = R.drawable.ic_calendar_next),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier
                .align(CenterVertically)
                .padding(12.dp, 5.dp, 12.dp, 5.dp)
                .size(24.dp)
                .clickableNoRipple {
                    val myCalendar = Calendar.getInstance()
                    myCalendar.time = selectedMonth
                    myCalendar.add(Calendar.MONTH, 1)
                    onMonthChange(myCalendar.time)
                }
        )
    }
    Spacer(modifier = Modifier.height(5.dp))
    Row(Modifier.fillMaxWidth()) {
        Text(
            text = "L",
            fontSize = weekDaysTextSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = secondaryTextColor,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        Text(
            text = "M",
            fontSize = weekDaysTextSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = secondaryTextColor,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        Text(
            text = "X",
            fontSize = weekDaysTextSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = secondaryTextColor,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
        )
        Text(
            text = "J",
            fontSize = weekDaysTextSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = secondaryTextColor,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        Text(
            text = "V",
            fontSize = weekDaysTextSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = secondaryTextColor,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        Text(
            text = "S",
            fontSize = weekDaysTextSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = secondaryTextColor,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
        )
        Text(
            text = "D",
            fontSize = weekDaysTextSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = secondaryTextColor,
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f),
        )
    }
    Divider(thickness = 1.dp, color = dividerColor)
}

@Composable
private fun DatePickerWeek(
    days: List<Date>,
    selectedDate: Date?,
    currentMonth: Int,
    primaryTextColor: Color = Color(0xFF1A1A1A),
    backgroundColor: Color = Color(0xFFF7F9FA),
    primaryColor: Color = Color(0xFF4395D6),
    textSize: TextUnit = 14.sp,
    onSelectionChange: (Date) -> Unit = {},
) {
    val calendarSelected = selectedDate?.getCalendar()
    val daysCalendar = days.map {
        val calendar = Calendar.getInstance()
        calendar.time = it
        calendar
    }
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = CenterVertically
    ) {
        daysCalendar.forEach { day ->
            val text = if (day[Calendar.MONTH] == currentMonth) {
                day[Calendar.DAY_OF_MONTH].toString()
            } else {
                ""
            }
            val textColor: Color
            val modifier: Modifier
            when {
                calendarSelected == null || !day.sameDay(calendarSelected) -> {
                    textColor = primaryTextColor
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                }

                else -> {
                    textColor = backgroundColor
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .graphicsLayer {
                            shape = CircleShape
                            clip = true
                        }
                        .background(primaryColor)
                }
            }
            Box(modifier = modifier.clickableNoRipple {
                if (text.isNotEmpty()) {
                    onSelectionChange(day.time)
                }
            }, contentAlignment = Center) {
                Text(
                    text = text,
                    fontSize = textSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = textColor,
                )
            }
        }
    }
}

@Composable
fun DateRangePickerWeek(
    days: List<Date>,
    currentMonth: Int,
    selectedDates: Pair<Date, Date>,
    textSize: TextUnit = 14.sp,
    primaryColor: Color = Color(0xFF4395D6),
    backgroundColor: Color = Color(0xFFF7F9FA),
    blueTransparentColor: Color = Color(0xCCE9F5FC),
    primaryTextColor: Color = Color(0xFF1A1A1A),
    onSelectionChange: (Date) -> Unit = {},
) {
    val startSelected = selectedDates.first.getCalendar()
    val endSelected = selectedDates.second.getCalendar()
    val daysCalendar = days.map {
        val calendar = Calendar.getInstance()
        calendar.time = it
        calendar
    }
    Row(Modifier.fillMaxWidth()) {
        daysCalendar.forEach { day ->
            val text = if (day[Calendar.MONTH] == currentMonth) {
                day[Calendar.DAY_OF_MONTH].toString()
            } else {
                ""
            }
            val textColor: Color
            val modifier: Modifier
            when {
                day.sameDay(startSelected) -> {
                    textColor = backgroundColor
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .graphicsLayer {
                            shape = RoundedCornerShape(
                                topStartPercent = 50,
                                topEndPercent = 0,
                                bottomEndPercent = 0,
                                bottomStartPercent = 50
                            )
                            clip = true
                        }
                        .background(blueTransparentColor)
                        .graphicsLayer {
                            shape = CircleShape
                            clip = true
                        }
                        .background(primaryColor)
                }

                day.sameDay(endSelected) -> {
                    textColor = backgroundColor
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .graphicsLayer {
                            shape = RoundedCornerShape(
                                topStartPercent = 0,
                                topEndPercent = 50,
                                bottomEndPercent = 50,
                                bottomStartPercent = 0
                            )
                            clip = true
                        }
                        .background(blueTransparentColor)
                        .graphicsLayer {
                            shape = CircleShape
                            clip = true
                        }
                        .background(primaryColor)
                }

                day.between(selectedDates) -> {
                    textColor = primaryTextColor
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(blueTransparentColor)
                }

                else -> {
                    textColor = primaryTextColor
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                }
            }
            Box(modifier = modifier.clickableNoRipple {
                if (text.isNotEmpty()) {
                    onSelectionChange(day.time)
                }
            }, contentAlignment = Center) {
                Text(
                    text = text,
                    fontSize = textSize,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = textColor,
                )
            }

        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun BaseCenteredDialog(
    visible: Boolean,
    dialogColor: Color,
    onOutsideTouch: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        modifier = Modifier
            .fillMaxSize(),
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            contentAlignment = Center,
            modifier = Modifier
                .fillMaxSize()
                .clickableNoRipple { onOutsideTouch() }
                .background(dialogColor)
        ) {
            Box(
                modifier = Modifier
                    .wrapContentHeight()
                    .statusBarsPadding()
                    .padding(horizontal = 15.dp, vertical = 15.dp)
                    .animateEnterExit(
                        enter = slideInVertically(),
                        exit = slideOutVertically()
                    )
                    .clickableNoRipple { }
            ) {
                content()
            }
        }
    }
}