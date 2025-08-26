package com.example.rdifsmartfarm.entity

data class RFID(
    var rid: Int?,
    var fid: String?,
    var temp_v: Float? = null,
    var heart: Int? = null,
    var temp: Float? = null,
    var humi: Float? = null,
    var createDateTime: String? = null // 最后的更新时间
)
