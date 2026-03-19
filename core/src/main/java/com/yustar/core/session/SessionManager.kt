package com.yustar.core.session

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.map

class SessionManager(
    private val dataStore: DataStore<Preferences>
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
    }
}
