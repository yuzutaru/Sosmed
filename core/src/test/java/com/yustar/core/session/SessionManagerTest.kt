package com.yustar.core.session

import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class SessionManagerTest {

    @get:Rule
    val tmpFolder: TemporaryFolder = TemporaryFolder()

    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var fakeSharedPreferences: FakeSharedPreferences
    private lateinit var sessionManager: SessionManager

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private val USER_KEY = stringPreferencesKey("logged_user")

    @Before
    fun setUp() {
        dataStore = PreferenceDataStoreFactory.create(
            scope = testScope,
            produceFile = { tmpFolder.newFile("test.preferences_pb") }
        )
        fakeSharedPreferences = FakeSharedPreferences()
        sessionManager = SessionManager(dataStore, fakeSharedPreferences)
    }

    @Test
    fun `loggedUser emits correct value after login`() = runTest(testDispatcher) {
        // Given
        val expectedUser = "testuser"

        // When
        sessionManager.login(expectedUser)
        val result = sessionManager.loggedUser.first()

        // Then
        assertEquals(expectedUser, result)
    }

    @Test
    fun `logout clears dataStore and sharedPreferences`() = runTest(testDispatcher) {
        // Given
        sessionManager.login("user")
        sessionManager.saveTokens("access", "refresh")

        // When
        sessionManager.logout()

        // Then
        assertNull(sessionManager.loggedUser.first())
        assertNull(sessionManager.getAccessToken())
        assertNull(sessionManager.getRefreshToken())
    }

    @Test
    fun `saveTokens saves access and refresh tokens`() = runTest(testDispatcher) {
        // Given
        val accessToken = "access_token_123"
        val refreshToken = "refresh_token_456"

        // When
        sessionManager.saveTokens(accessToken, refreshToken)

        // Then
        assertEquals(accessToken, sessionManager.getAccessToken())
        assertEquals(refreshToken, sessionManager.getRefreshToken())
    }

    @Test
    fun `updateAccessToken updates only access token`() = runTest(testDispatcher) {
        // Given
        sessionManager.saveTokens("old_access", "refresh")
        val newAccessToken = "new_access"

        // When
        sessionManager.updateAccessToken(newAccessToken)

        // Then
        assertEquals(newAccessToken, sessionManager.getAccessToken())
        assertEquals("refresh", sessionManager.getRefreshToken())
    }

    @Test
    fun `getAccessToken returns null when not set`() {
        assertNull(sessionManager.getAccessToken())
    }

    // A simple fake SharedPreferences for testing
    private class FakeSharedPreferences : SharedPreferences {
        private var map = mutableMapOf<String, Any?>()

        override fun getAll(): Map<String, *> = map
        override fun getString(key: String, defValue: String?): String? = map[key] as? String ?: defValue
        override fun getStringSet(key: String, defValues: Set<String>?): Set<String>? = map[key] as? Set<String> ?: defValues
        override fun getInt(key: String, defValue: Int): Int = map[key] as? Int ?: defValue
        override fun getLong(key: String, defValue: Long): Long = map[key] as? Long ?: defValue
        override fun getFloat(key: String, defValue: Float): Float = map[key] as? Float ?: defValue
        override fun getBoolean(key: String, defValue: Boolean): Boolean = map[key] as? Boolean ?: defValue
        override fun contains(key: String): Boolean = map.containsKey(key)
        override fun edit(): SharedPreferences.Editor = FakeEditor()
        override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}
        override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {}

        inner class FakeEditor : SharedPreferences.Editor {
            private val tempMap = map.toMutableMap()

            override fun putString(key: String, value: String?): SharedPreferences.Editor { tempMap[key] = value; return this }
            override fun putStringSet(key: String, values: Set<String>?): SharedPreferences.Editor { tempMap[key] = values; return this }
            override fun putInt(key: String, value: Int): SharedPreferences.Editor { tempMap[key] = value; return this }
            override fun putLong(key: String, value: Long): SharedPreferences.Editor { tempMap[key] = value; return this }
            override fun putFloat(key: String, value: Float): SharedPreferences.Editor { tempMap[key] = value; return this }
            override fun putBoolean(key: String, value: Boolean): SharedPreferences.Editor { tempMap[key] = value; return this }
            override fun remove(key: String): SharedPreferences.Editor { tempMap.remove(key); return this }
            override fun clear(): SharedPreferences.Editor { tempMap.clear(); return this }
            override fun commit(): Boolean { map = tempMap; return true }
            override fun apply() { map = tempMap }
        }
    }
}
