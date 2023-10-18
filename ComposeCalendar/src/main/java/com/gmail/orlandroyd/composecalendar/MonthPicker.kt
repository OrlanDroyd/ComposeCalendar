package com.gmail.orlandroyd.composecalendar

import android.util.DisplayMetrics
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ComposeCalendar(
    minDate: Date? = null,
    maxDate: Date? = null,
    currentDate: Date? = null,
    locale: Locale = Locale.getDefault(),
    title: String = "",
    onDateSelected: (Date) -> Unit = {},
    onClose: () -> Unit = {},
    showOnlyMonth: Boolean = false,
    showOnlyYear: Boolean = false,
    themeColor: Color = Color(0xFF4395D6),
    negativeButtonTitle: String = "CANCEL",
    positiveButtonTitle: String = "OK",
    monthSelectedColor: Color = Color(0x80FFFFFF),
) {
    if (showOnlyMonth && showOnlyYear) {
        throw IllegalStateException("'showOnlyMonth' and 'showOnlyYear' states cannot be true at the same time")
    } else {

        var minYear = 1970
        var minMonth = 0
        var maxYear = 2100
        var maxMonth = 11
        minDate?.let {
            val calendarMin = Calendar.getInstance()
            calendarMin.time = minDate
            minMonth = calendarMin.get(Calendar.MONTH)
            minYear = calendarMin.get(Calendar.YEAR)
        }
        maxDate?.let {
            val calendarMax = Calendar.getInstance()
            calendarMax.time = maxDate
            maxMonth = calendarMax.get(Calendar.MONTH)
            maxYear = calendarMax.get(Calendar.YEAR)
        }

        val (height, setHeight) = remember {
            mutableStateOf(0)
        }

        val calendar = Calendar.getInstance(locale)
        currentDate?.let {
            calendar.time = currentDate
        }
        val currentMonth = calendar.get(Calendar.MONTH)
        var currentYear = calendar.get(Calendar.YEAR)

        if (minYear > currentYear) {
            currentYear = minYear
        }
        if (maxYear < currentYear) {
            currentYear = maxYear
        }

        val months = (DateFormatSymbols(locale).shortMonths).toList()
        val monthList = months.mapIndexed { index, name ->
            MonthData(name = name, index = index)
        }
        val (selectedMonth, setMonth) = remember {
            mutableStateOf(
                MonthData(
                    name = DateFormatSymbols(locale).shortMonths[currentMonth],
                    index = currentMonth
                )
            )
        }
        val (selectedYear, setYear) = remember {
            mutableStateOf(currentYear)
        }
        val (showMonths, setShowMonths) = remember {
            mutableStateOf(!showOnlyYear)
        }

        val calendarDate = Calendar.getInstance()
        var selectedDate by remember {
            mutableStateOf(calendarDate.time)
        }

        LaunchedEffect(key1 = selectedYear, key2 = selectedMonth) {
            calendarDate.set(Calendar.YEAR, selectedYear)
            calendarDate.set(Calendar.MONTH, selectedMonth.index)
            selectedDate = calendarDate.time
        }
        LaunchedEffect(key1 = selectedYear) {
            if (selectedYear == minYear) {
                if (selectedMonth.index < minMonth) {
                    setMonth(monthList[minMonth])
                }
            }
            if (selectedYear == maxYear) {
                if (selectedMonth.index > maxMonth) {
                    setMonth(monthList[maxMonth])
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                CalendarHeader(
                    selectedMonth = selectedMonth.name,
                    selectedYear = selectedYear,
                    showMonths = showMonths,
                    setShowMonths = setShowMonths,
                    title = title,
                    showOnlyMonth = showOnlyMonth,
                    showOnlyYear = showOnlyYear,
                    themeColor = themeColor,
                    monthSelectedColor = monthSelectedColor
                )
                Crossfade(targetState = showMonths, label = "") {
                    when (it) {
                        true -> CalendarMonthView(
                            selectedMonth = selectedMonth,
                            setMonth = setMonth,
                            minMonth = minMonth,
                            maxMonth = maxMonth,
                            setShowMonths = setShowMonths,
                            minYear = minYear,
                            maxYear = maxYear,
                            selectedYear = selectedYear,
                            monthList = monthList,
                            setHeight = setHeight,
                            showOnlyMonth = showOnlyMonth,
                            themeColor = themeColor
                        )

                        false -> CalendarYearView(
                            selectedYear = selectedYear,
                            setYear = setYear,
                            minYear = minYear,
                            maxYear = maxYear,
                            height = height,
                            themeColor = themeColor
                        )
                    }
                }
                CalendarBottom(
                    onPositiveClick = { onDateSelected(selectedDate) },
                    onCancelClick = onClose,
                    themeColor = themeColor,
                    negativeButtonTitle = negativeButtonTitle,
                    positiveButtonTitle = positiveButtonTitle
                )
            }
        }
    }
}


data class MonthData(
    val name: String,
    val index: Int
)

interface SelectDateListener {
    fun onDateSelected(date: Date)
    fun onCanceled()
}

@Composable
fun CalendarYearView(
    selectedYear: Int,
    setYear: (Int) -> Unit,
    minYear: Int,
    maxYear: Int,
    height: Int,
    themeColor: Color
) {
    val years = IntRange(minYear, maxYear).toList()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val selectedIndex = years.indexOf(selectedYear)
    val metrics = LocalContext.current.resources.displayMetrics

    val mHeight =
        (if (height == 0) 5 * (metrics.heightPixels) / 10 else height) / (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    LazyColumn(
        state = listState, modifier = Modifier
            .fillMaxWidth()
            .requiredHeight((mHeight + 40).dp)  //40dp for padding in MonthView
        , horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(years) { year ->
            Text(text = year.toString(),
                fontSize = if (year == selectedYear) 35.sp else 30.sp,
                color = if (year == selectedYear) themeColor else Color.Black,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .clickable {
                        setYear(year)
                    })
        }
        scope.launch {
            listState.animateScrollToItem(selectedIndex)
        }
    }
}

@Composable
fun CalendarMonthView(
    monthList: List<MonthData>,
    selectedMonth: MonthData,
    setMonth: (MonthData) -> Unit,
    minMonth: Int,
    maxMonth: Int,
    minYear: Int,
    maxYear: Int,
    selectedYear: Int,
    setShowMonths: (Boolean) -> Unit,
    setHeight: (Int) -> Unit,
    showOnlyMonth: Boolean,
    themeColor: Color
) {

    val NUMBER_OF_ROW_ITEMS = 3
    var numberOfElement = 0

    LazyColumn(modifier = Modifier
        .padding(horizontal = 20.dp)
        .padding(vertical = 20.dp)
        .onGloballyPositioned { setHeight(it.size.height) }) {
        items(items = monthList.chunked(NUMBER_OF_ROW_ITEMS)) { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for ((index, item) in rowItems.withIndex()) {
                    MonthItem(
                        month = item,
                        index = index,
                        numberOfElement = numberOfElement,
                        rowSize = NUMBER_OF_ROW_ITEMS,
                        selectedMonth = selectedMonth.name,
                        setMonth = setMonth,
                        minMonth = minMonth,
                        maxMonth = maxMonth,
                        minYear = minYear,
                        maxYear = maxYear,
                        selectedYear = selectedYear,
                        setShowMonths = setShowMonths,
                        showOnlyMonth = showOnlyMonth,
                        themeColor = themeColor
                    )
                    numberOfElement += 1
                }
            }
        }
    }
}

@Composable
fun MonthItem(
    month: MonthData,
    selectedMonth: String,
    setMonth: (MonthData) -> Unit,
    index: Int,
    numberOfElement: Int,
    rowSize: Int,
    minMonth: Int,
    maxMonth: Int,
    minYear: Int,
    maxYear: Int,
    selectedYear: Int,
    setShowMonths: (Boolean) -> Unit,
    showOnlyMonth: Boolean,
    themeColor: Color
) {
    val enabled = checkDate(
        minYear = minYear,
        maxYear = maxYear,
        selectedYear = selectedYear,
        maxMonth = maxMonth,
        minMonth = minMonth,
        numberOfElement = numberOfElement
    )
    Box(
        modifier = Modifier
            .background(
                color = if (month.name == selectedMonth) themeColor else Color.Transparent,
                shape = RoundedCornerShape(100)
            )
            .fillMaxWidth(1f / (rowSize - index + 1f))
            .aspectRatio(1f)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                enabled = enabled
            ) {
                setMonth(month)
                if (!showOnlyMonth) {
                    setShowMonths(false)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = month.name.uppercase(),
            color = if (enabled && month.name == selectedMonth) Color.White
            else if (enabled) Color.Black
            else Color.Gray
        )
    }
}

private fun checkDate(
    minYear: Int,
    maxYear: Int,
    selectedYear: Int,
    minMonth: Int,
    maxMonth: Int,
    numberOfElement: Int
): Boolean {
    if (minMonth == 0) return true
    if (minYear == maxYear) return numberOfElement in minMonth..maxMonth
    if (selectedYear == minYear) {
        return numberOfElement >= minMonth
    } else if (selectedYear == maxYear) {
        if (numberOfElement > maxMonth) return false
    }
    return true
}

@Composable
fun CalendarHeader(
    selectedMonth: String,
    selectedYear: Int,
    showMonths: Boolean,
    setShowMonths: (Boolean) -> Unit,
    title: String,
    showOnlyMonth: Boolean,
    showOnlyYear: Boolean,
    themeColor: Color,
    monthSelectedColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(themeColor),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 16.dp),
            color = Color.White
        )
        Row {
            if (!showOnlyYear) {
                Text(
                    text = selectedMonth.uppercase(),
                    fontSize = 35.sp,
                    modifier = Modifier
                        .padding(
                            bottom = 20.dp,
                            start = if (showOnlyMonth) 0.dp else 30.dp,
                            end = if (showOnlyMonth) 0.dp else 10.dp
                        )
                        .clickableNoRipple { setShowMonths(true) },
                    color = if (showMonths) Color.White else monthSelectedColor
                )
            }
            if (!showOnlyMonth) {
                Text(
                    text = selectedYear.toString(),
                    fontSize = 35.sp,
                    modifier = Modifier
                        .padding(
                            bottom = 20.dp,
                            start = if (showOnlyYear) 0.dp else 10.dp,
                            end = if (showOnlyYear) 0.dp else 30.dp
                        )
                        .clickableNoRipple { setShowMonths(false) },
                    color = if (showMonths) monthSelectedColor else Color.White
                )
            }
        }
    }
}

@Composable
fun CalendarBottom(
    onPositiveClick: () -> Unit,
    onCancelClick: () -> Unit,
    themeColor: Color,
    negativeButtonTitle: String,
    positiveButtonTitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(text = negativeButtonTitle,
            color = themeColor,
            modifier = Modifier
                .clickableNoRipple { onCancelClick() }
                .padding(10.dp))
        Text(text = positiveButtonTitle,
            color = themeColor,
            modifier = Modifier
                .clickableNoRipple { onPositiveClick() }
                .padding(10.dp))
    }
}

fun Modifier.clickableNoRipple(onClick: () -> Unit) = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
    ) {
        onClick()
    }
}