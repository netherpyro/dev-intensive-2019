package ru.skillbranch.devintensive.extensions

/**
 * @author mmikhailov on 2019-07-08.
 */

fun String.truncate(truncateNum: Int = 16): String {
    if (truncateNum <= 0) return this

    val pad = "..."
    val thisTrimmed = this.trim()

    return if (thisTrimmed.length > truncateNum) {
        thisTrimmed.substring(0, truncateNum)
            .trim()
            .plus(pad)
    } else {
        thisTrimmed
    }
}

fun String.stripHtml() = this.replace(Regex("""<[^>]*>"""), "")
    .replace(Regex("""\s{2,}"""), " ")