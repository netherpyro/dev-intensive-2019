package ru.skillbranch.devintensive.models

/**
 * @author mmikhailov on 2019-06-27.
 */
class Chat(
        val id: String,
        val members: MutableList<User> = mutableListOf(),
        val messages: MutableList<BaseMessage> = mutableListOf()
)