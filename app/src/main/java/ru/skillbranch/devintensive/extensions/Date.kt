package ru.skillbranch.devintensive.extensions

import ru.skillbranch.devintensive.utils.Utils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 * @author mmikhailov on 2019-06-27.
 */

const val SECOND = 1000L
const val MINUTE = 60L * SECOND
const val HOUR = 60L * MINUTE
const val DAY = 24L * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy"): String {
    return SimpleDateFormat(pattern, Locale("ru"))
        .format(this)
}

fun Date.shortFormat(): String {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"

    return format(pattern)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY

    return day1 == day2
}
fun Date.add(value: Int, timeUnits: TimeUnits = TimeUnits.SECOND): Date {
    time += when (timeUnits) {
        TimeUnits.SECOND -> SECOND * value
        TimeUnits.MINUTE -> MINUTE * value
        TimeUnits.HOUR -> HOUR * value
        TimeUnits.DAY -> DAY * value
    }

    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val diffMs = date.time - this.time
    val diffMsAbs = abs(diffMs)
    val past = diffMs >= 0

    var interval = Interval.UNKNOWN
    for (item in Interval.values()) {
        if (diffMsAbs in item.mSecRange) {
            interval = item
            break
        }
    }

    val resultBody = when (interval) {
        Interval.JUST -> if (past) "только что" else "через секунду" // the body is ready to use as result
        Interval.SECOND_FEW -> "несколько секунд"
        Interval.MINUTE_ONE -> "минуту"
        Interval.MINUTE_EXACT -> {
            val value = diffMsAbs / MINUTE
            val unit = Utils.getPluralStringValue(value, TimeUnits.MINUTE)
            "$value $unit"
        }
        Interval.HOUR_ONE -> "час"
        Interval.HOUR_EXACT -> {
            val value = diffMsAbs / HOUR
            val unit = Utils.getPluralStringValue(value, TimeUnits.HOUR)
            "$value $unit"
        }
        Interval.DAY_ONE -> "день"
        Interval.DAY_EXACT -> {
            val value = diffMsAbs / DAY
            val unit = Utils.getPluralStringValue(value, TimeUnits.DAY)
            "$value $unit"
        }
        Interval.YEARS -> if (past) "более года назад" else "более чем через год" // the body is ready to use as result
        else -> throw IllegalArgumentException("Cannot parse difference")
    }

    if (interval == Interval.JUST || interval == Interval.YEARS) {
        return resultBody
    }

    return if (past) {
        "$resultBody назад"
    } else {
        "через $resultBody"
    }
}

enum class TimeUnits {
    SECOND, MINUTE, HOUR, DAY;

    fun plural(value: Int): String {
        return value.toString().plus(" ").plus(Utils.getPluralStringValue(value.toLong(), this))
    }
}

enum class Interval(val mSecRange: LongRange) {
    UNKNOWN(Long.MIN_VALUE..-1L),
    JUST(0L..SECOND),
    SECOND_FEW(SECOND..(45 * SECOND)),
    MINUTE_ONE((45 * SECOND)..(75 * SECOND)),
    MINUTE_EXACT((75 * SECOND)..(45 * MINUTE)),
    HOUR_ONE((45 * MINUTE)..(75 * MINUTE)),
    HOUR_EXACT((75 * MINUTE)..(22 * HOUR)),
    DAY_ONE((22 * HOUR)..(26 * HOUR)),
    DAY_EXACT((26 * HOUR)..(360 * DAY)),
    YEARS((360 * DAY)..Long.MAX_VALUE)
}