package com.jefisu.diary.core.util

import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

fun Long.toLocalDateTime(): LocalDateTime {
    return Timestamp(this * 1000).toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}