package com.livehomeradio.utils

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import com.livehomeradio.roomdb.Contacts
import com.livehomeradio.views.BaseActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ContactsUtil {
    fun getContactList(contentResolver: ContentResolver?) {

        CoroutineScope(Dispatchers.IO).launch {
            val list = ArrayList<Contacts>()
            BaseActivity.db.contactsDao().deleteAllContacts()
            val cur: Cursor? =
                contentResolver?.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
                )
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
                            var phoneNo: String = pCur.getString(
                                pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )!!
                            phoneNo = phoneNo.replace(" ", "").replace("+", "")
                            val contacts =
                                Contacts(name = name,
                                    number = phoneNo)
                            list.add(contacts)
                        }
                        pCur.close()
                    }
                }
            }
            cur?.close()
            BaseActivity.db.contactsDao().insertContact(*list.toTypedArray())
            contactList = list
        }
    }

}