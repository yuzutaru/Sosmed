package com.yustar.core.session

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class SessionManager(
    private val dataStore: DataStore<Preferences>,
    private val sharedPreferences: SharedPreferences
) {
    private val USER_KEY = stringPreferencesKey("logged_user")

    val loggedUser = dataStore.data.map {
        it[USER_KEY]
    }

    suspend fun login(username: String) {
        dataStore.edit {
            it[USER_KEY] = username
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it.remove(USER_KEY)
        }
        sharedPreferences.edit().clear().apply()
    }

    fun saveTokens(accessToken: String, refreshToken: String) {
        sharedPreferences.edit().apply {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
    }

    fun updateAccessToken(accessToken: String) {
        sharedPreferences.edit().apply {
            putString("access_token", accessToken)
            apply()
        }
    }

    fun getAccessToken(): String? = sharedPreferences.getString("access_token", null)
    fun getRefreshToken(): String? = sharedPreferences.getString("refresh_token", null)
}
