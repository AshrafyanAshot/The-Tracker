package com.ash.thetracker.shared

import android.util.Log

/** Common */
fun Any?.toLog(tag: String = "toLog") {
    Log.e(tag, this.toString())
}
