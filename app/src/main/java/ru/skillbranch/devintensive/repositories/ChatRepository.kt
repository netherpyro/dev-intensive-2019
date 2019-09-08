package ru.skillbranch.devintensive.repositories

import androidx.lifecycle.MutableLiveData
import ru.skillbranch.devintensive.data.managers.CacheManager
import ru.skillbranch.devintensive.models.data.Chat

object ChatRepository {

    private val chats = CacheManager.loadChats()

    fun loadChats(): MutableLiveData<List<Chat>> {
        return chats
    }

    fun update(chat: Chat) {
        val copy = chats.value!!.toMutableList()
        val idx = copy.indexOfFirst { chat.id == it.id }
        if (idx == -1) {
            return
        }
        copy[idx] = chat
        chats.value = copy
    }

    fun find(chatId: String): Chat? {
        return chats.value?.firstOrNull { it.id == chatId }
    }
}
