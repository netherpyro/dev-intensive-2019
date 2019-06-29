package ru.skillbranch.devintensive.utils

import ru.skillbranch.devintensive.extensions.TimeUnits
import kotlin.math.abs

/**
 * @author mmikhailov on 2019-06-27.
 */
object Utils {

    fun parseFullName(fullName: String?): Pair<String, String> {
        if (fullName.isNullOrBlank()) return "null" to "null"

        val parts = fullName.trim()
            .split(" ")
        val firstName = parts.getOrNull(0) ?: "null"
        val lastName = parts.getOrNull(1) ?: "null"

        return firstName to lastName
    }

    fun getPluralStringValue(value: Long, units: TimeUnits): String {
        return getStringOfPlural(getPlural(value), units)
    }

    private fun getStringOfPlural(plural: Plural, units: TimeUnits): String {
        return when (units) {
            TimeUnits.SECOND -> {
                when (plural) {
                    Utils.Plural.ONE -> "секунда"
                    Utils.Plural.FEW -> "секунды"
                    Utils.Plural.MANY -> "секунд"
                    Utils.Plural.OTHER -> "секунд"
                }
            }
            TimeUnits.MINUTE -> {
                when (plural) {
                    Utils.Plural.ONE -> "минута"
                    Utils.Plural.FEW -> "минуты"
                    Utils.Plural.MANY -> "минут"
                    Utils.Plural.OTHER -> "минут"
                }
            }
            TimeUnits.HOUR -> {
                when (plural) {
                    Utils.Plural.ONE -> "час"
                    Utils.Plural.FEW -> "часа"
                    Utils.Plural.MANY -> "часов"
                    Utils.Plural.OTHER -> "часов"
                }
            }
            TimeUnits.DAY -> {
                when (plural) {
                    Utils.Plural.ONE -> "день"
                    Utils.Plural.FEW -> "дня"
                    Utils.Plural.MANY -> "дней"
                    Utils.Plural.OTHER -> "дней"
                }
            }
        }
    }

    private fun getPlural(value: Long): Plural {
        if (abs(value % 100) in 10..19) {
            return Utils.Plural.MANY
        }

        return when (abs(value % 10)) {
            0L -> Utils.Plural.MANY
            1L -> Utils.Plural.ONE
            2L -> Utils.Plural.FEW
            3L -> Utils.Plural.FEW
            4L -> Utils.Plural.FEW
            in 5L..9L -> Utils.Plural.MANY
            else -> Utils.Plural.OTHER
        }
    }

    private enum class Plural {
        ONE, FEW, MANY, OTHER
    }
}