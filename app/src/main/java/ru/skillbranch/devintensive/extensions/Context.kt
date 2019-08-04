package ru.skillbranch.devintensive.extensions

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

/**
 * @author mmikhailov on 2019-08-04.
 */
fun Context.dpToPx(dp: Float) =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).roundToInt()