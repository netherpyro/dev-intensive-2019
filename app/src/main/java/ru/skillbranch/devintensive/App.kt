package ru.skillbranch.devintensive

import android.app.Application
import android.content.Context

/**
 * @author mmikhailov on 2019-08-04.
 */
class App : Application() {

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }



}