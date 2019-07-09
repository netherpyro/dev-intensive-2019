package ru.skillbranch.devintensive.utils

import ru.skillbranch.devintensive.extensions.TimeUnits
import kotlin.math.abs

/**
 * @author mmikhailov on 2019-06-27.
 */
object Utils {

    fun parseFullName(fullName: String?): Pair<String?, String?> {
        if (fullName.isNullOrBlank()) return null to null

        val parts = fullName.trim()
            .split(" ")
        val firstName = parts.getOrNull(0)
        val lastName = parts.getOrNull(1)

        return firstName to lastName
    }

    fun toInitials(firstName: String?, lastName: String?): String? {
        val leftInitial = firstName?.trim()
            ?.firstOrNull()
            ?.toUpperCase()
        val rightInitial = lastName?.trim()
            ?.firstOrNull()
            ?.toUpperCase()

        if (leftInitial == null && rightInitial == null) {
            return null
        }

        return "${leftInitial ?: ""}${rightInitial ?: ""}"
    }

    fun transliteration(payload: String, divider: String = " "): String {
        var result = payload.trim()

        for (char in payload.asIterable()) {
            val upperCase = char.isUpperCase()

            val translitedChar = if (upperCase) {
                getTranslitedStr(char.toLowerCase()).capitalize()
            } else {
                getTranslitedStr(char)
            }

            result = result.replace(char.toString(), translitedChar)
        }

        return result.replace(Regex("""\s+"""), divider)
    }

    private fun getTranslitedStr(char: Char): String {
        return when (char) {
            'а' -> "a"
            'б' -> "b"
            'в' -> "v"
            'г' -> "g"
            'д' -> "d"
            'е' -> "e"
            'ё' -> "e"
            'ж' -> "zh"
            'з' -> "z"
            'и' -> "i"
            'й' -> "i"
            'к' -> "k"
            'л' -> "l"
            'м' -> "m"
            'н' -> "n"
            'о' -> "o"
            'п' -> "p"
            'р' -> "r"
            'с' -> "s"
            'т' -> "t"
            'у' -> "u"
            'ф' -> "f"
            'х' -> "h"
            'ц' -> "c"
            'ч' -> "ch"
            'ш' -> "sh"
            'щ' -> "sh"
            'ъ' -> ""
            'ы' -> "i"
            'ь' -> ""
            'э' -> "e"
            'ю' -> "yu"
            'я' -> "ya"
            else -> char.toString()
        }
    }

    fun getPluralStringValue(value: Long, units: TimeUnits): String {
        return getStringOfPlural(getPlural(value), units)
    }

    private fun getStringOfPlural(plural: Plural, units: TimeUnits): String {
        return when (units) {
            TimeUnits.SECOND -> {
                when (plural) {
                    Utils.Plural.ONE -> "секунду"
                    Utils.Plural.FEW -> "секунды"
                    Utils.Plural.MANY -> "секунд"
                    Utils.Plural.OTHER -> "секунд"
                }
            }
            TimeUnits.MINUTE -> {
                when (plural) {
                    Utils.Plural.ONE -> "минуту"
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