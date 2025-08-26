package com.example.rdifsmartfarm.entity

/**
 * 接收消息实体类
 */
data class Receive(
    var rfid: String?, // 上传的识别编码
    var temp_v: String?,//体温
    var heart: String?,//心率
    var temp: String?, // 环境温度
    var temp_t: String?,//环境温度阈值
    var humi: String?,//环境湿度
    var humi_t: String?, // 环境温度阈值
    var relay1: String?,//风扇状态，0为关1为开
    var relay2: String?, //LED状态，0为关1为开
    var mode: String?,
    var re_id: String? //要注册的id
)
