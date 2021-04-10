package com.livehomeradio.roomdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.livehomeradio.recycleradapter.AbstractModel

@Entity(
    tableName = "contacts"
)
data class Contacts(
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "number") var number: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var contactId: Long = 0
}