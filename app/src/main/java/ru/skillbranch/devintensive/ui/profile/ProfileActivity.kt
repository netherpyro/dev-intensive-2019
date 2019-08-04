package ru.skillbranch.devintensive.ui.profile

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*
import ru.skillbranch.devintensive.R

class ProfileActivity : AppCompatActivity() {


    companion object {
        const val KEY_EDIT_MODE = "KEY_EDIT_MODE"
    }

    private var editMode = false
    lateinit var viewFields: Map<String, TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBoolean(KEY_EDIT_MODE, editMode)
    }

    private fun initViews(savedInstanceState: Bundle?) {
        viewFields = mapOf(
                "nickName" to tv_nick_name,
                "rank" to tv_rating,
                "firstName" to et_first_name,
                "lastName" to et_last_name,
                "about" to et_about,
                "repository" to et_repo,
                "rating" to tv_rating,
                "respect" to tv_respect)

        editMode = savedInstanceState?.getBoolean(KEY_EDIT_MODE, false) ?: false
        showCurrentMode(editMode)

        btn_edit.setOnClickListener {
            editMode = !editMode
            showCurrentMode(editMode)
        }
    }

    private fun showCurrentMode(editMode: Boolean) {
        val info = viewFields.filter {
            setOf("firstName", "lastName", "about", "repository").contains(it.key)
        }

        info.forEach {
            val v = it.value as EditText
            v.isFocusable = editMode
            v.isFocusableInTouchMode = editMode
            v.isEnabled = editMode
            v.background.alpha = if (editMode) 255 else 0
        }

        ic_eye.visibility = if (editMode) View.GONE else View.VISIBLE
        til_about.isCounterEnabled = editMode

        with(btn_edit) {
            val filter: ColorFilter? = if (editMode) {
                PorterDuffColorFilter(getThemeAccentColor(), PorterDuff.Mode.SRC_IN)
            } else null

            val icon = if (editMode) {
                resources.getDrawable(R.drawable.ic_save_black_24dp, theme)
            } else {
                resources.getDrawable(R.drawable.ic_edit_black_24dp, theme)
            }

            background.colorFilter = filter
            setImageDrawable(icon)
        }
    }

    private fun getThemeAccentColor(): Int {
        val value = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, value, true)
        return value.data
    }

}
