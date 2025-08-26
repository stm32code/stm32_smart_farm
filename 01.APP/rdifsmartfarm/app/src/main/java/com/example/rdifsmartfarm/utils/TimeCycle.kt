package com.example.rdifsmartfarm.utils

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object TimeCycle {
    /**
     * 比较两个"yyyy-MM-dd HH:mm:ss"字符串大小
     *
     * @param dateTime1 String
     * @param dateTime2 String
     * @return 如果返回值小于 0，则表示 dateTime1 较小；如果返回值大于 0，则表示 dateTime1 较大；如果返回值等于 0，则表示两个日期时间相等
     */
    fun compareDateTime(dateTime1: String, dateTime2: String): Int {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val dt1 = LocalDateTime.parse(dateTime1, formatter)
        val dt2 = LocalDateTime.parse(dateTime2, formatter)

        return dt1.compareTo(dt2)
    }

    /**
     * 获取当前时间
     * return yyyy-MM-dd HH:mm:ss
     */
    fun getDateTime(): String {
        // 转换为中国时区
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT-8"))
        return transToString(System.currentTimeMillis())
    }

    /**
     * 时间戳转字符串
     *
     * @param time
     * @return String
     */
    fun transToString(time: Long): String {
        val date = Date(time)
        val format: DateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        return format.format(date)
    }

    /**
     * 字符串转时间戳
     *
     * @param date
     * @return Long
     */
    fun transToTimeStamp(date: String): Long {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date).time
    }
}