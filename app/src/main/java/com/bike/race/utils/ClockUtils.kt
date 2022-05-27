package com.bike.race.utils

import android.text.format.DateFormat
import android.text.format.DateUtils
import java.util.concurrent.TimeUnit

class ClockUtils {

    fun getTimeFromMillis(duration: Long): String {
        return getTimeFromSecs(TimeUnit.MILLISECONDS.toSeconds(duration))
    }

    fun getTimeFromSecs(duration: Long): String {
        return DateUtils.formatElapsedTime(duration)
    }

    fun getRelativeTime(timeInMillis: Long): String {
        val relativeTimeSpanString: CharSequence? = DateUtils.getRelativeTimeSpanString(
            timeInMillis,
            System.currentTimeMillis(),
            DateUtils.FORMAT_SHOW_TIME.toLong()
        )
        return relativeTimeSpanString?.toString() ?: ""
    }

    fun getTimeFromDate(timeInMillis: Long): String {
        return DateFormat.format("hh:mm aa", timeInMillis).toString()
    }
}