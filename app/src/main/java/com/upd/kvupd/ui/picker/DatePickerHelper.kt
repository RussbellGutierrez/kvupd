package com.upd.kvupd.ui.picker

import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DatePickerHelper {

    private val formatter = DateTimeFormatter.ISO_DATE // yyyy-MM-dd

    fun show(
        context: Context,
        initial: LocalDate = LocalDate.now(),
        onDateSelected: (String) -> Unit
    ) {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val date = LocalDate.of(year, month + 1, day)
                onDateSelected(date.format(formatter))
            },
            initial.year,
            initial.monthValue - 1,
            initial.dayOfMonth
        ).show()
    }
}