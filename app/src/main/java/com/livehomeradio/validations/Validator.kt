package com.livehomeradio.validations

interface Validator {
    fun isValid(): Boolean
    fun message(): String?
}