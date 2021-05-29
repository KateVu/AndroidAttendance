package com.katevu.attendance.utils

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import com.google.common.io.BaseEncoding

class NfcUtils {
    companion object {
        private val mimeType: String = ""

        fun getUID(intent: Intent): String {
            val myTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            return BaseEncoding.base16().encode(myTag?.id)
        }

    }
}