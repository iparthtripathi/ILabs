package com.pratik.iiits.Timetable

data class ClassSchedule(
    val subject: String = "",
    val time: String = "",
    val room: String = "",
    val dayOfWeek: String = ""
)

data class DaySchedule(
    val dayOfWeek: String = "",
    val schedules: List<ClassSchedule> = emptyList()
)
