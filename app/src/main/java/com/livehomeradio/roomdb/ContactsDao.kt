package com.livehomeradio.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactsDao {
    @Query("SELECT * FROM contacts")
    fun getContacts(): Flow<List<Contacts>>

    @Query("SELECT * FROM contacts WHERE name LIKE :search or number LIKE :search")
    fun searchContacts(search: String?): Flow<List<Contacts>>

    @Query ("SELECT * FROM contacts WHERE number = :number LIMIT 1")
    fun getContact (number: String) : Contacts

    @Query("SELECT EXISTS(SELECT 1 FROM contacts WHERE number = :number LIMIT 1)")
    fun isContactExist(number: String): Flow<Boolean>

    @Insert
    fun insertContact(contact: Contacts): Long

    @Query("DELETE  FROM contacts")
    fun deleteAllContacts()
}
