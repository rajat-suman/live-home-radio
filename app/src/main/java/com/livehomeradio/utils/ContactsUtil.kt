package com.livehomeradio.utils

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import com.livehomeradio.recycleradapter.AbstractModel

object ContactsUtil {
    fun getContactList(contentResolver: ContentResolver?): ArrayList<ContactsModel> {
        val list = ArrayList<ContactsModel>()
        val cur: Cursor? =
            contentResolver?.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        if (cur != null && cur.count > 0) {
            while (cur.moveToNext()) {
                val id: String = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name: String = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur: Cursor? = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID.toString() + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur?.moveToNext()!!) {
                        val phoneNo: String = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )!!
                        list.add(ContactsModel(name = name, number = phoneNo))
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()
        list.sortBy { it.name }
        return list
    }

    data class ContactsModel(
        var name: String = "",
        var number: String = ""
    ) : AbstractModel()

}