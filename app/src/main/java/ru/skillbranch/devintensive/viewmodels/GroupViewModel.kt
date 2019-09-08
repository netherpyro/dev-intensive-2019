package ru.skillbranch.devintensive.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.extensions.mutableLiveData
import ru.skillbranch.devintensive.models.data.UserItem
import ru.skillbranch.devintensive.repositories.GroupRepository

/**
 * @author mmikhailov on 2019-09-05.
 */
class GroupViewModel : ViewModel() {
    private val groupRepository = GroupRepository
    private val query = mutableLiveData("")
    private val userItems = mutableLiveData(loadUsers())
    private val selectedItems =
        Transformations.map(userItems) { users -> users.filter { it.isSelected } }

    fun getUsersData(): LiveData<List<UserItem>> {
        val filterF: () -> List<UserItem> = {
            val queryStr = query.value!!
            val users = userItems.value!!

            if (queryStr.isNotEmpty()) {
                users.filter { it.fullName.contains(queryStr, true) }
            } else {
                users
            }
        }

        return MediatorLiveData<List<UserItem>>()
            .apply {
                addSource(userItems) { value = filterF() }
                addSource(query) { value = filterF() }
            }
    }

    fun getSelectedData(): LiveData<List<UserItem>> = selectedItems

    fun handleSelectedItem(userId: String) {
        userItems.value = userItems.value!!.map {
            if (it.id == userId) {
                it.copy(isSelected = !it.isSelected)
            } else {
                it
            }
        }
    }

    private fun loadUsers(): List<UserItem> = groupRepository.loadUsers().map { it.toUserItem() }

    fun handleRemoveChip(userId: String) {
        userItems.value = userItems.value!!.map {
            if (it.id == userId) {
                it.copy(isSelected = false)
            } else {
                it
            }
        }
    }

    fun handleSearchQuery(text: String) {
        query.value = text
    }

    fun handleCreateGroup() {
        groupRepository.createChat(selectedItems.value!!)
    }
}