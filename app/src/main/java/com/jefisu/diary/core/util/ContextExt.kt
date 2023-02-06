package com.jefisu.diary.core.util

import android.content.Context
import android.content.pm.PackageManager

fun Context.getMetaData(key: String): String {
    val applicationInfo = packageManager.getApplicationInfo(
        packageName,
        PackageManager.GET_META_DATA
    )
    return applicationInfo.metaData.getString(key)!!
}