package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.repositories.PreferencesRepository

/**
 * @author mmikhailov on 2019-08-04.
 */
class ProfileViewModel : ViewModel() {

    private val repository: PreferencesRepository = PreferencesRepository
    private val profileData = MutableLiveData<Profile>()

    companion object {
        private const val TAG = "ProfileViewModel"
    }

    init {
        Log.d(TAG, "init::")
        profileData.value = repository.getProfile()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared::")
    }

    fun getProfileData(): LiveData<Profile> = profileData

    fun saveProfileData(profile: Profile) {
        repository.saveProfile(profile)
        profileData.value = profile
    }
}