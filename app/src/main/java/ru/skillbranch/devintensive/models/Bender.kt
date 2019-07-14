package ru.skillbranch.devintensive.models

import androidx.core.text.isDigitsOnly

/**
 * @author mmikhailov on 2019-07-14.
 */
class Bender(var status: Status = Status.NORMAL, var question: Question = Question.NAME) {

    private var counter = 0

    fun askQuestion() = question.question

    fun listenAnswer(answer: String): Pair<String, Triple<Int, Int, Int>> {
        val resultStr: String

        if (question == Question.IDLE) {
            resultStr = question.question
        } else if (!question.validate(answer)) {
            resultStr = "${question.validationError}\n${question.question}"
        } else if (question.answers.contains(answer.toLowerCase())) {
            question = question.nextQuestion()
            resultStr = "Отлично - ты справился\n${question.question}"

        } else {
            ++counter

            if (counter > 3) {
                status = Status.NORMAL
                question = Question.NAME
                counter = 0
                resultStr = "Это неправильный ответ. Давай все по новой\n${question.question}"
            } else {
                status = status.nextStatus()
                resultStr = "Это неправильный ответ\n${question.question}"
            }
        }

        return resultStr to status.color
    }

    enum class Status(val color: Triple<Int, Int, Int>) {
        NORMAL(Triple(255, 255, 255)),
        WARNING(Triple(255, 120, 0)),
        DANGER(Triple(255, 60, 60)),
        CRITICAL(Triple(255, 0, 0));

        fun nextStatus(): Status {
            return if (ordinal < values().lastIndex) {
                values()[ordinal + 1]
            } else {
                values()[0]
            }
        }
    }

    enum class Question(val question: String, val answers: List<String>, val validationError: String) {
        NAME("Как меня зовут?",
                listOf("бендер", "bender"),
                "Имя должно начинаться с заглавной буквы") {

            override fun nextQuestion() = PROFESSION
            override fun validate(text: String) = text.isNotEmpty() && text[0].isUpperCase()
        },
        PROFESSION("Назови мою профессию?",
                listOf("сгибальщик", "bender"),
                "Профессия должна начинаться со строчной буквы") {

            override fun nextQuestion() = MATERIAL
            override fun validate(text: String) = text.isNotEmpty() && text[0].isUpperCase().not()
        },
        MATERIAL("Из чего я сделан?",
                listOf("металл", "дерево", "metal", "iron", "wood"),
                "Материал не должен содержать цифр") {

            override fun nextQuestion() = BDAY
            override fun validate(text: String): Boolean {
                for (c in text) {
                    if (c.isDigit()) {
                        return false
                    }
                }

                return true
            }
        },
        BDAY("Когда меня создали?",
                listOf("2993"),
                "Год моего рождения должен содержать только цифры") {

            override fun nextQuestion() = SERIAL
            override fun validate(text: String) = text.isDigitsOnly()
        },
        SERIAL("Мой серийный номер?",
                listOf("2716057"),
                "Серийный номер содержит только цифры, и их 7") {

            override fun nextQuestion() = IDLE
            override fun validate(text: String) = text.isDigitsOnly() && text.length == 7
        },
        IDLE("На этом все, вопросов больше нет",
                listOf(),
                "") {
            override fun nextQuestion() = IDLE
            override fun validate(text: String) = true
        };

        abstract fun nextQuestion(): Question
        abstract fun validate(text: String): Boolean
    }
}