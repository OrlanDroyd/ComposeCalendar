package com.gmail.orlandroyd.calendar_example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.gmail.orlandroyd.calendar_example.ui.theme.ComposeCalendarTheme

//import com.gmail.orlandroyd.composecalendar.DatePickerDlg
//import com.gmail.orlandroyd.composecalendar.DateRangePickerDlg
//import com.gmail.orlandroyd.composecalendar.MonthYearPickerDlg
//import com.gmail.orlandroyd.composecalendar.util.getFullMonthTextDate

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeCalendarTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val context = LocalContext.current

                    var isVisibleDatePickerDialog by remember {
                        mutableStateOf(false)
                    }

                    var isVisibleDatePickerDialogHours by remember {
                        mutableStateOf(false)
                    }

                    var isVisibleDateRangePickerDialog by remember {
                        mutableStateOf(false)
                    }

                    var isVisibleMonthYearPickerDialog by remember {
                        mutableStateOf(false)
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = { isVisibleDatePickerDialog = true }) {
                            Text("DatePickerDialog")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { isVisibleDatePickerDialogHours = true }) {
                            Text("DatePickerDialog + Hours")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { isVisibleDateRangePickerDialog = true }) {
                            Text("DateRangePickerDialog")
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { isVisibleMonthYearPickerDialog = true }) {
                            Text("MonthYearPickerDialog")
                        }
                    }

                    /*
                    DatePickerDlg(
                        visible = isVisibleDatePickerDialog,
                        onClose = { isVisibleDatePickerDialog = false },
                        onDateSelected = {
                            isVisibleDatePickerDialog = false
                            Toast.makeText(context, it.getFullMonthTextDate(), Toast.LENGTH_SHORT).show()
                        }
                    )

                    DatePickerDlg(
                        visible = isVisibleDatePickerDialogHours,
                        onClose = { isVisibleDatePickerDialogHours = false },
                        showSetHours = true,
                        onDateSelected = {
                            isVisibleDatePickerDialogHours = false
                            Toast.makeText(context, it.getFullMonthTextDate(), Toast.LENGTH_SHORT).show()
                        }
                    )

                    DateRangePickerDlg(
                        visible = isVisibleDateRangePickerDialog,
                        onClose = { isVisibleDateRangePickerDialog = false },
                        onDatesSelected = {
                            isVisibleDateRangePickerDialog = false
                            Toast.makeText(
                                context,
                                "${it.first.getFullMonthTextDate()} - ${it.second.getFullMonthTextDate()}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )

                    MonthYearPickerDlg(
                        visible = isVisibleMonthYearPickerDialog,
                        onClose = { isVisibleMonthYearPickerDialog = false },
                        onDateSelected = {
                            isVisibleMonthYearPickerDialog = false
                            Toast.makeText(context, it.getFullMonthTextDate(), Toast.LENGTH_SHORT).show()
                        }
                    )
                    */
                }
            }
        }
    }
}