package com.avwaveaf.gratify.util

import android.annotation.SuppressLint
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object MemoryDateFormatter {

    // SimpleDateFormat patterns for different cases
    @SuppressLint("ConstantLocale")
    private val fullDateFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    @SuppressLint("ConstantLocale")
    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    fun format(date: Date): String {
        val now = Calendar.getInstance()

        // Get the time difference in days
        val diff = TimeUnit.MILLISECONDS.toDays(now.timeInMillis - date.time).toInt()

        Log.i("Date_debug", "diff: ${diff}")

        return when {
            diff < 1 && isSameDay(now, date) -> "Today at ${timeFormat.format(date)}"
            diff == 1 -> "Yesterday at ${timeFormat.format(date)}"
            diff < 7 -> "$diff days ago at ${timeFormat.format(date)}"
            diff < 14 -> "Last week at ${timeFormat.format(date)}"
            else -> fullDateFormat.format(date)
        }
    }

    private fun isSameDay(cal: Calendar, date: Date): Boolean {
        val target = Calendar.getInstance()
        target.time = date
        return cal.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)
    }
}