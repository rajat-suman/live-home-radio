package com.livehomeradio.roomdb

import androidx.room.*
import kotlinx.coroutines.flow.Flow


@Dao
interface ContactsDao {
    @Query("SELECT * FROM contacts")
    fun getContacts(): Flow<List<Contacts>>

    @Query("SELECT * FROM contacts WHERE name LIKE :search or number LIKE :search")
    fun searchContacts(search: String?): Flow<List<Contacts>>

    @Query("SELECT * FROM contacts WHERE number = :number LIMIT 1")
    fun getContact(number: String): Contacts

    @Query("SELECT EXISTS(SELECT 1 FROM contacts WHERE number = :number LIMIT 1)")
    fun isContactExist(number: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertContact(vararg contact: Contacts)

    @Query("UPDATE contacts SET name=:name WHERE id = :id")
    fun update(name: String?, id: Long)

    @Update
    fun update(contact: Contacts)

    @Delete
    fun delete(contact: Contacts)

    @Query("DELETE  FROM contacts")
    fun deleteAllContacts()
}
