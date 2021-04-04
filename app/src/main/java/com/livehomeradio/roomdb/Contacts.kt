package com.livehomeradio.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.livehomeradio.recycleradapter.AbstractModel

@Entity(
    tableName = "contacts"
)
data class Contacts(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "number") val number: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var contactId: Long = 0
}