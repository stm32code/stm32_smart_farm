package com.example.rdifsmartfarm.entity

/**
 * 发送指令实体类
 */
data class Send(
    var cmd: Int, //指令码
    var r_id: Int? = null,//
    var relay: Int? = null,//
    var mode: Int? = null,
    var temp_t: Int? = null,
    var humi_t: Int? = null
)

