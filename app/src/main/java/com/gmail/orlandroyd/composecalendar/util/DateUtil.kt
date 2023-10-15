package com.gmail.orlandroyd.composecalendar.util

import android.app.TimePickerDialog
import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun Date.getCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}

fun Context.showTimePickerDialog(
    currentDate: Date? = null,
    timeSelected: (Calendar) -> Unit,
    textColor: Int
) {
    val calendar = Calendar.getInstance()
    calendar.time = currentDate ?: Date()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val dialog = TimePickerDialog(this, { _, hourSelected, minuteSelected ->
        val calendarSelected = Calendar.getInstance()
        calendarSelected.time = calendar.time
        calendarSelected.set(Calendar.HOUR_OF_DAY, hourSelected)
        calendarSelected.set(Calendar.MINUTE, minuteSelected)
        timeSelected(calendarSelected)
    }, hour, minute, true)
    dialog.show()
    dialog.getButton(TimePickerDialog.BUTTON_POSITIVE).setResourceTextColor(textColor)
    dialog.getButton(TimePickerDialog.BUTTON_NEGATIVE).setResourceTextColor(textColor)
}

fun Date.getParsedDate(
    format: String = "yyyy-MM-dd HH:mm:ss",
    locale: Locale = Locale.getDefault()
): String {
    val df = SimpleDateFormat(format, locale)
    return df.format(this)
}

fun Calendar.sameDay(otherDay: Calendar): Boolean {
    val day1 = get(Calendar.DAY_OF_YEAR)
    val day2 = otherDay.get(Calendar.DAY_OF_YEAR)
    val year1 = get(Calendar.YEAR)
    val year2 = otherDay.get(Calendar.YEAR)
    return day1 == day2 && year1 == year2
}

fun Date.sameDay(otherDay: Date): Boolean {
    val calendar1 = getCalendar()
    val calendar2 = otherDay.getCalendar()
    val day1 = calendar1.get(Calendar.DAY_OF_YEAR)
    val day2 = calendar2.get(Calendar.DAY_OF_YEAR)
    val year1 = calendar1.get(Calendar.YEAR)
    val year2 = calendar2.get(Calendar.YEAR)
    return day1 == day2 && year1 == year2
}

fun Date.getFullMonthTextDate(locale: Locale = Locale.getDefault()): String {
    val day = getParsedDate("dd")
    val month = getParsedDate("MMMM").replaceFirstChar { it.uppercase() }
    val year = getParsedDate("yyyy")
    return "$day $month $year"
}

fun Calendar.between(range: Pair<Date, Date>): Boolean {
    val day = get(Calendar.DAY_OF_YEAR)
    val dayStart = range.first.getCalendar().get(Calendar.DAY_OF_YEAR)
    val dayEnd = range.second.getCalendar().get(Calendar.DAY_OF_YEAR)
    val year = get(Calendar.YEAR)
    val yearStart = range.first.getCalendar().get(Calendar.YEAR)
    val yearEnd = range.second.getCalendar().get(Calendar.YEAR)
    val wrong =
        year < yearStart || year > yearEnd || year == yearStart && day < dayStart || year == yearEnd && day > dayEnd
    return !wrong
}

fun Date?.getMonthText(locale: Locale = Locale.getDefault()): String {
    val text = this?.getParsedDate("MMM, yyyy")
    return text?.replaceFirstChar { it.uppercase() } ?: ""
}

fun Date.getCalendarMonthDays(): List<Date> {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val cells = mutableListOf<Date>()

    // Date moved to the first day of the month
    calendar[Calendar.DAY_OF_MONTH] = 1
    var monthBeginningCell = calendar[Calendar.DAY_OF_WEEK] - 2
    if (monthBeginningCell == -1) {
        monthBeginningCell = 6
    }

    // Date moved to the monday before or equal to the first day of the month
    calendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

    // Filling the calendar
    while (cells.size < monthBeginningCell) {
        cells.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    val initialMonth = calendar[Calendar.MONTH]
    while (initialMonth == calendar[Calendar.MONTH]) {
        cells.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    while (Calendar.MONDAY != calendar[Calendar.DAY_OF_WEEK]) {
        cells.add(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
    }
    return cells
}

fun TextView.setResourceTextColor(colorResource: Int) {
    setTextColor(ContextCompat.getColor(context, colorResource))
}