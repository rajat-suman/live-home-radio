package com.livehomeradio.networkcalls

import android.util.Log
import androidx.collection.LruCache
import retrofit2.Response
import javax.inject.Inject

class CacheUtil @Inject constructor() : LruCache<ApiKeys, Response<Any>>(1024) {

    override fun entryRemoved(
        evicted: Boolean,
        key: ApiKeys,
        oldValue: Response<Any>,
        newValue: Response<Any>?
    ) {
        super.entryRemoved(evicted, key, oldValue, newValue)
        Log.d("entryRemoved", "entryRemoved")
    }

    override fun sizeOf(key: ApiKeys, value: Response<Any>): Int {
        Log.d("sizeOf", "sizeOf")
        return super.sizeOf(key, value)
    }

}