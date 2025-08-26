package com.example.rdifsmartfarm.utils

import android.content.Context
import android.util.Log
import com.example.rdifsmartfarm.entity.Send

import com.google.gson.Gson
import com.itfitness.mqttlibrary.MQTTHelper

object Common {

    //    const val PORT = "6002" // mqtt服务器端口号
    //    const val SERVER_ADDRESS = "183.230.40.39"// mqtt 服务器地址
//    const val SERVER_ADDRESS = "192.168.1.11"// mqtt 服务器地址
    const val PORT = "1883" // mqtt服务器端口号
    const val SERVER_ADDRESS = "iot-06z00axdhgfk24n.mqtt.iothub.aliyuncs.com"// mqtt 服务器地址
    const val URL = "tcp://$SERVER_ADDRESS:$PORT" // mqtt连接地址
    const val RECEIVE_TOPIC = "/broadcast/h9sj4vs2jeY/test2" // 接收消息订阅的主题 - 下位机发送消息的主题
    const val PUSH_TOPIC = "/broadcast/h9sj4vs2jeY/test1" // 推送消息的主题 - 下位机接收消息的主题
    const val DRIVER_ID =
        "h9sj4vs2jeY.smartapp|securemode=2,signmethod=hmacsha256,timestamp=1713769575854|" // mqtt id
    const val DRIVER_NAME = "smartapp&h9sj4vs2jeY" // mqtt 用户名 （oneNET中为产品ID）
    const val DRIVER_PASSWORD =
        "cfe5d27d3e2e66501545b2b1c2feb2ed3fc690688ce9f9a9b919e22da4cb03ce" // mqtt 鉴权或者密码
    const val DRIVER_ID_HARDWARE = "1213859959" // mqtt 硬件id
    const val API_KEY = "taz207NiCU4k7hPrzf0oyH8ZMHl3m1+CehgOz0VhP/Y=" // （oneNET） APIkey
    var HARDWARE_ONLINE = false // 硬件在线标志位
    var mqttHelper: MQTTHelper? = null // mqtt 连接服务函数


    /***
     * @brief 包装发送函数，只有建立了连接才发送消息
     */
    fun sendMessage(context: Context, cmd: Int, vararg data: String): String {
        return if (mqttHelper == null || !mqttHelper!!.connected) {
            MToast.mToast(context, "未建立连接")
            ""
        } else {
            try {
                val send = Send(cmd = cmd)
                when (cmd) {
                    1 -> {
                        send.r_id = data[0].toInt()
                    }

                    2 -> {
                        send.relay = data[0].toInt()
                    }

                    3 -> {
                        send.relay = data[0].toInt()
                    }

                    4 -> {
                        send.mode = data[0].toInt()
                    }

                    5 -> {
                        send.temp_t = data[0].toInt()
                        send.humi_t = data[1].toInt()
                    }
                }
                val result = Gson().toJson(send)
                mqttHelper!!.publish(PUSH_TOPIC, result, 1)
                result
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("发送错误", e.message.toString())
                MToast.mToast(context, "数据发送失败")
                ""
            }
        }
    }

}