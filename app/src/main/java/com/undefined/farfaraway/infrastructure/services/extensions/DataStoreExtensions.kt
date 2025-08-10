package com.undefined.farfaraway.infrastructure.services.extensions

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.undefined.farfaraway.core.Constants.USER_PREFERENCES

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = USER_PREFERENCES
)
