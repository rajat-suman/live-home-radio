package com.livehomeradio.realmdb
/*

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmResults


object RealmDb {
    private val realm by lazy { Realm.getDefaultInstance() }

*/
/*    fun insert(name: String) {
        realm.executeTransaction { realm: Realm ->
            val maxId: Number = realm.where(Demo::class.java).max("id")
            val nextId = maxId.toInt() + 1
            val note: Demo = realm.createObject(Demo::class.java, nextId)
            note.name = name
        }
    }*//*


    fun <T : RealmObject> getAll(clazz: Class<T>, result: (RealmResults<T>) -> Unit) {
        result(realm.where(clazz).findAllAsync())
    }

    fun <T : RealmObject> getById(clazz: Class<T>, id: Long, result: (RealmResults<T>) -> Unit) {
        val query = realm.where(clazz).equalTo("id", id)
        result(query.findAllAsync())
    }

    fun <T : RealmObject> delete(data: T) {
        realm.beginTransaction()
        data.deleteFromRealm()
        realm.commitTransaction()
    }

    fun <T : RealmObject> updateById(clazz: Class<T>, data: T) {
        realm.beginTransaction()
        realm.copyToRealmOrUpdate(data)
//        realm.insertOrUpdate(obj)
        realm.commitTransaction()
    }

*/
/*    @RealmModule(classes = [Demo::class, Demo2::class])
    internal class InitialModule

    val syncServerURL = "realm://localhost:9080/Dogs"
    val config = SyncCon.Builder("user", syncServerURL)
        .modules(InitialModule())
        .build()

    // Limit to initial object type
    val initialRealm = Realm.getInstance(config)*//*


}*/
