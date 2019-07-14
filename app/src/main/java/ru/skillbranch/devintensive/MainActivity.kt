package ru.skillbranch.devintensive

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.skillbranch.devintensive.extensions.hideKeyboard
import ru.skillbranch.devintensive.models.Bender

class MainActivity : AppCompatActivity() {

    companion object {
        private const val KEY_STATUS = "KEY_STATUS"
        private const val KEY_QUESTION = "KEY_QUESTION"
    }

    private lateinit var bender: Bender

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val status = savedInstanceState?.getSerializable(KEY_STATUS) as? Bender.Status ?: Bender.Status.NORMAL
        val question = savedInstanceState?.getSerializable(KEY_QUESTION) as? Bender.Question ?: Bender.Question.NAME

        bender = Bender(status, question)

        val (r, g, b) = bender.status.color
        iv_bender.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)

        tv_text.text = bender.askQuestion()
        iv_send.setOnClickListener { clickSend() }
        et_message.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
                clickSend()
                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.putSerializable(KEY_STATUS, bender.status)
        outState?.putSerializable(KEY_QUESTION, bender.question)
    }

    private fun clickSend() {
        val answer = et_message.text.toString()
        val (phrase, color) = bender.listenAnswer(answer)
        et_message.setText("")
        val (r, g, b) = color
        iv_bender.setColorFilter(Color.rgb(r, g, b), PorterDuff.Mode.MULTIPLY)
        tv_text.text = phrase
    }
}
