package com.livehomeradio.utils

interface HomeListener {
    fun isBottomSheet(boolean: Boolean)
    fun isHome(boolean: Boolean)
    fun isBookings(boolean: Boolean)
    fun isNotification(boolean: Boolean)
    fun isPayment(boolean: Boolean)
}