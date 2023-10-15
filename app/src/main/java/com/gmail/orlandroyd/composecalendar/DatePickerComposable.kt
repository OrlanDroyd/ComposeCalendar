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
import androidx.compose.ui.tooling.preview.Preview
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


@Preview(backgroundColor = 0xfff, showBackground = true)
@Composable
fun CalendarView() {
    DatePicker(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        selectedDate = Date()
    )
}

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
                verticalAlignment = CenterVertically,
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
                    verticalAlignment = CenterVertically,
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

@Composable
fun DateRangePickerDialog(
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
                verticalAlignment = CenterVertically,
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

@Composable
private fun DatePicker(
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
private fun DateRangePickerWeek(
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
private fun BaseCenteredDialog(
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