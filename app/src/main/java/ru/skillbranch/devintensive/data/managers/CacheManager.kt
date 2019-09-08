package ru.skillbranch.devintensive.data.managers

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.Chat
import ru.skillbranch.devintensive.models.data.User
import ru.skillbranch.devintensive.utils.DataGenerator

/**
 * @author mmikhailov on 2019-09-02.
 */
object CacheManager {

    private val chats: MutableLiveData<List<Chat>> = mutableLiveData(DataGenerator.stabChats)
    private val users: MutableLiveData<List<User>> = mutableLiveData(DataGenerator.stabUsers)

    fun loadChats(): MutableLiveData<List<Chat>> {
        return chats
    }

    fun findUserByIds(ids: List<String>): List<User> {
        return users.value!!.filter { ids.contains(it.id) }
    }

    fun nextChatId(): String {
        return "${chats.value!!.size}"
    }

    fun insertChat(chat: Chat) {
        val copy = chats.value!!.toMutableList()
        copy.add(chat)
        chats.value = copy
    }
}