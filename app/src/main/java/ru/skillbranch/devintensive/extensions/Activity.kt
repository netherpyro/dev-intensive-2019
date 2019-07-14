package ru.skillbranch.devintensive.extensions

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import kotlin.math.roundToInt

/**
 * @author mmikhailov on 2019-07-14.
 */
fun View.hideKeyboard() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(this.windowToken, 0)
}


fun Activity.hideKeyboard() {
    window.decorView.hideKeyboard()
}

fun Activity.isKeyboardOpen(): Boolean {
    val keyboardVisibleThresholdDp = 100f
    val r = Rect()

    val activityRoot = getActivityRoot(this)
    val visibleThreshold = convertDpToPx(this, keyboardVisibleThresholdDp).roundToInt()

    activityRoot.getWindowVisibleDisplayFrame(r)

    val heightDiff = activityRoot.rootView.height - r.height()

    return heightDiff > visibleThreshold
}

fun Activity.isKeyboardClosed(): Boolean {
    return isKeyboardOpen().not()
}

fun getActivityRoot(activity: Activity): View {
    return (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
}

fun convertDpToPx(context: Context, dp: Float): Float {
    val res = context.resources

    return dp * (res.displayMetrics.densityDpi / 160f)
}