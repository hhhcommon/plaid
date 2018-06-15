/*
 *   Copyright 2018 Google LLC
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package io.plaidapp.designernews.login.data

import android.content.SharedPreferences

import io.plaidapp.designernews.data.api.model.User

/**
 * Local storage for Designer News login related data.
 */
abstract class DesignerNewsLoginLocalDataSource {
    /**
     * Clear all data related to this Designer News instance: user data and access token
     */
    abstract fun clearData()

    /**
     * Auth token used for requests that require authentication
     */
    open var authToken: String? = null

    /**
     * Instance of the logged in user. If missing, an empty user is created.
     * TODO when the user is not logged in, return null, not an empty user
     */
    open var user: User = User.Builder().build()
}

/**
 * Local storage for Designer News login related data, implemented using SharedPreferences
 */
class SharedPreferencesDesignerNewsLoginLocalDataSource(private val prefs: SharedPreferences)
    : DesignerNewsLoginLocalDataSource() {

    override var authToken: String?
        get() = prefs.getString(KEY_ACCESS_TOKEN, null)
        set(value) {
            prefs.edit().putString(KEY_ACCESS_TOKEN, authToken).apply()
        }

    override var user: User
        get() {
            val userId = prefs.getLong(KEY_USER_ID, 0L)
            val username = prefs.getString(KEY_USER_NAME, null)
            val userAvatar = prefs.getString(KEY_USER_AVATAR, null)
            return User.Builder()
                    .setId(userId)
                    .setDisplayName(username)
                    .setPortraitUrl(userAvatar)
                    .build()
        }
        set(value) {
            val editor = prefs.edit()
            editor.putLong(KEY_USER_ID, value.id)
            editor.putString(KEY_USER_NAME, value.display_name)
            editor.putString(KEY_USER_AVATAR, value.portrait_url)
            editor.apply()
        }

    override fun clearData() {
        val editor = prefs.edit()
        editor.putString(KEY_ACCESS_TOKEN, null)
        editor.putLong(KEY_USER_ID, 0L)
        editor.putString(KEY_USER_NAME, null)
        editor.putString(KEY_USER_AVATAR, null)
        editor.apply()
    }

    companion object {
        const val DESIGNER_NEWS_PREF = "DESIGNER_NEWS_PREF"
        private const val KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN"
        private const val KEY_USER_ID = "KEY_USER_ID"
        private const val KEY_USER_NAME = "KEY_USER_NAME"
        private const val KEY_USER_AVATAR = "KEY_USER_AVATAR"
    }
}
