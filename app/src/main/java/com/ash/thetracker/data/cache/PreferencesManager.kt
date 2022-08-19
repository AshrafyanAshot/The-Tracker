package com.ash.thetracker.data.cache

import android.content.Context
import android.content.SharedPreferences
import com.ash.thetracker.App
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PreferencesManager {

    private const val PREF_NAME = "the_tracker_cache"
    private val instance: SharedPreferences by lazy { App.applicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    private val editor: SharedPreferences.Editor by lazy { instance.edit() }

    private fun setString(key: String, value: String) {
        editor.putString(key, value).apply()
    }

    private fun setInt(key: String, value: Int) {
        editor.putInt(key, value).apply()
    }

    private fun setFloat(key: String, value: Float) {
        editor.putFloat(key, value).apply()
    }

    private fun setLong(key: String, value: Long) {
        editor.putLong(key, value).apply()
    }

    private fun setBoolean(key: String, value: Boolean?) {
        editor.putBoolean(key, value!!).apply()
    }

    private fun setObject(key: String, list: List<String>) {
        editor.putString(key, Gson().toJson(list)).apply()
    }

    private fun <T> setObject(key: String, c: T) {
        editor.putString(key, Gson().toJson(c)).apply()
    }

    private fun getString(key: String, defaultValue: String = ""): String {
        return instance.getString(key, defaultValue)!!
    }

    private fun getInt(key: String, defaultValue: Int = -1): Int {
        return instance.getInt(key, defaultValue)
    }

    private fun getFloat(key: String, defaultValue: Float = -1F): Float {
        return instance.getFloat(key, defaultValue)
    }

    private fun getLong(key: String, defaultValue: Long = 0L): Long {
        return instance.getLong(key, defaultValue)
    }

    private fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return instance.getBoolean(key, defaultValue)
    }

    private fun getObject(key: String): List<String>? {
        return Gson().fromJson<List<String>>(
            instance.getString(key, null),
            object : TypeToken<List<String>>() {}.type
        )
    }

    private fun <T> getObject(key: String, c: Class<T>): T? {
        val value = instance.getString(key, null)
        return if (value != null) Gson().fromJson(value, c) else null
    }

    private fun remove(key: String) {
        editor.remove(key).apply()
    }

    private fun clear(): Boolean {
        return editor.clear().commit()
    }

    var isTracking: Boolean
        get() = getBoolean(PreferencesKeys.IS_TRACKING, defaultValue = false)
        set(value) = setBoolean(PreferencesKeys.IS_TRACKING, value)

}
