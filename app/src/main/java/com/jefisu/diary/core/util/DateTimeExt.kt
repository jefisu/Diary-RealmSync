package com.jefisu.diary.core.util

import java.sql.Timestamp
import java.time.LocalDate
import java.time.ZoneId

fun Long.toLocalDate(): LocalDate {
    return Timestamp(this).toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}