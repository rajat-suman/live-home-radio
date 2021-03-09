package com.livehomeradio.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class DataStoreUtil @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val coRoutineExceptionHandler: CoroutineExceptionHandler
) {
    /** dataStore*/
    fun <T> saveData(key: Preferences.Key<T>, value: T) {
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler).launch {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    fun <T> readData(key: Preferences.Key<T>, valueIs: (T?) -> Unit) {
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler).launch {
            dataStore.edit {
                CoroutineScope(Dispatchers.Main).launch {
                    valueIs(it[key])
                }
            }
        }
    }

    fun clearDataStore() {
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler).launch {
            dataStore.edit { preferences ->
                preferences.clear()
            }
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("clearDataStore", "clearDataStore")
//                openActivity(Intent(this@clearDataStore, Login::class.java), true)
            }
        }
    }

    fun <T> removeKey(key: Preferences.Key<T>) {
        CoroutineScope(Dispatchers.IO + coRoutineExceptionHandler).launch {
            dataStore.edit { preferences ->
                preferences.remove(key)
            }
        }
    }

}

