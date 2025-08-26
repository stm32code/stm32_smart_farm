#include "git.h"

Data_TypeDef Data_init;						  // 设备数据结构体
Threshold_Value_TypeDef threshold_value_init; // 设备阈值设置结构体
Device_Satte_Typedef device_state_init;		  // 设备状态

extern int32_t n_sp02;		 // SPO2 value
extern int8_t ch_spo2_valid; // indicator to show if the SP02 calculation is valid
extern int32_t n_heart_rate; // heart rate value
extern int8_t ch_hr_valid;	 // indicator to show if the heart rate calculation is valid
extern uint8_t ucArray_ID [ 4 ];   /*先后存放IC卡的类型和UID(IC卡序列号)*/

DHT11_Data_TypeDef DHT11_Data;

// 获取数据参数
mySta Read_Data(Data_TypeDef *Device_Data)
{
	Read_DHT11(&DHT11_Data); // 获取温湿度数据
	Device_Data->temperatuer = DHT11_Data.temp_int + DHT11_Data.temp_deci * 0.01;
	Device_Data->humiditr = DHT11_Data.humi_int + DHT11_Data.humi_deci * 0.01;

	// 温度测量
	Data_init.Temp_Test = DS18B20_GetTemp_MatchRom(ucDs18b20Id); // 获取温度
	if (Data_init.Temp_Test > 1)
	{
		Data_init.temperatuer_1 = Data_init.Temp_Test;
	}

	return MY_SUCCESSFUL;
}
// 初始化
mySta Reset_Threshole_Value(Threshold_Value_TypeDef *Value, Device_Satte_Typedef *device_state)
{

// longitude_sum = 111.434380;
// latitude_sum = 27.197769;
//  // 写
//  W_Test();
	// 读
	R_Test();
	// 状态重置
	device_state->check_device = 0;

	return MY_SUCCESSFUL;
}
// 更新OLED显示屏中内容
mySta Update_oled_massage()
{
#if OLED // 是否打开
	char str[50];
	// 注册界面
	if(Data_init.Page == 1){
		sprintf(str, "Register  ");
		OLED_ShowCH(0, 1, (unsigned char *)str);
		sprintf ( str, "ID : %02X%02X%02X%02X -",
										ucArray_ID [ 0 ],
										ucArray_ID [ 1 ],
										ucArray_ID [ 2 ],
										ucArray_ID [ 3 ] );
		OLED_ShowCH(0, 3, (unsigned char *)str);
	}else{
		if (20 < n_heart_rate && n_heart_rate < 150 && ch_spo2_valid)
		{
			sprintf(str, "Heart: %03d    ", n_heart_rate);
			OLED_ShowCH(0, 0, (unsigned char *)str);
		}
		else
		{
			sprintf(str, "Heart: %03d    ", 0);
			OLED_ShowCH(0, 0, (unsigned char *)str);

		}
		sprintf(str, "T: %.1f  H: %.1f ", Data_init.temperatuer,Data_init.humiditr);
		OLED_ShowCH(0, 2, (unsigned char *)str);
		sprintf(str, "体温: %0.2f  ℃ ", Data_init.temperatuer_1);
		OLED_ShowCH(0, 4, (unsigned char *)str);
		//sprintf(str, "ID: %d",device_state_init.RFID);

		sprintf ( str, "ID : %02X%02X%02X%02X -",
										ucArray_ID [ 0 ],
										ucArray_ID [ 1 ],
										ucArray_ID [ 2 ],
										ucArray_ID [ 3 ] );
		OLED_ShowCH(0, 6, (unsigned char *)str);
	}
	
#endif

	return MY_SUCCESSFUL;
}

// 更新设备状态
mySta Update_device_massage()
{
	// 自动模式

	if ((n_heart_rate> 120 && n_heart_rate< 150) 
			|| Data_init.temperatuer_1 > 38.6)
	{
		Beepout = ~Beepout;
	}
	else
	{
		Beepout  = 0;
	}
	// 温度不正常
	if (Data_init.humiditr > threshold_value_init.humi_value ||  device_state_init.Relay1)
	{
		relay1out = 1;
	}else{
		relay1out = 0;
	}
	// 湿度不正常
	if ( Data_init.temperatuer < threshold_value_init.temp_value || device_state_init.Relay2)
	{
		relay2out = 1;
	}else{
		relay2out = 0;
	}

	
	// 触发主动报警
	if((n_heart_rate> 120 && n_heart_rate< 150) || Data_init.temperatuer>38.6){
		device_state_init.waring = 1;
	}else{
		device_state_init.waring = 0;
	}
		


	return MY_SUCCESSFUL;
}

// 定时器
void Automation_Close(void)
{
	// 实现30s自动切换界面
}
// 检测按键是否按下
static U8 num_on = 0;
static U8 key_old = 0;
void Check_Key_ON_OFF()
{
	U8 key;
	key = KEY_Scan(1);
	// 与上一次的键值比较 如果不相等，表明有键值的变化，开始计时
	if (key != 0 && num_on == 0)
	{
		key_old = key;
		num_on = 1;
	}
	if (key != 0 && num_on >= 1 && num_on <= Key_Scan_Time) // 25*10ms
	{
		num_on++; // 时间记录器
	}
	if (key == 0 && num_on > 0 && num_on < Key_Scan_Time) // 短按
	{
		switch (key_old)
		{
		case KEY1_PRES:
			printf("Key1_Short\n");

			break;


		default:
			break;
		}
		num_on = 0;
	}
	else if (key == 0 && num_on >= Key_Scan_Time) // 长按
	{
		switch (key_old)
		{
		case KEY1_PRES:
			printf("Key1_Long\n");


			break;

		default:
			break;
		}
		num_on = 0;
	}
}
// 解析json数据
mySta massage_parse_json(char *message)
{

	cJSON *cjson_test = NULL; // 检测json格式
	// cJSON *cjson_data = NULL; // 数据
	const char *massage;
	// 定义数据类型
	u8 cjson_cmd; // 指令,方向

	/* 解析整段JSO数据 */
	cjson_test = cJSON_Parse(message);
	if (cjson_test == NULL)
	{
		// 解析失败
		printf("parse fail.\n");
		return MY_FAIL;
	}

	/* 依次根据名称提取JSON数据（键值对） */
	cjson_cmd = cJSON_GetObjectItem(cjson_test, "cmd")->valueint;
	/* 解析嵌套json数据 */
	//cjson_data = cJSON_GetObjectItem(cjson_test, "data");

	switch (cjson_cmd)
	{
	case 0x01: // 消息包

		Data_init.Page = cJSON_GetObjectItem(cjson_test, "r_id")->valueint;
		ucArray_ID[0] = 0;
		ucArray_ID[1] = 0;
		ucArray_ID[2] = 0;
		ucArray_ID[3] = 0;
	
		OLED_Clear();

		break;
	case 0x02: // 消息包
		device_state_init.Relay1 = cJSON_GetObjectItem(cjson_test, "relay")->valueint;
		if (Connect_Net && Data_init.App == 0) {
        Data_init.App = 1;
    }
		break;
	case 0x03: // 数据包
		
		device_state_init.Relay2  = cJSON_GetObjectItem(cjson_test, "relay")->valueint;
		if (Connect_Net && Data_init.App == 0) {
        Data_init.App = 1;
    }
		
		break;
	case 0x04: // 数据包
		Data_init.Flage = cJSON_GetObjectItem(cjson_test, "mode")->valueint;
	
		if (Connect_Net && Data_init.App == 0) {
        Data_init.App = 1;
    }
		break;
	case 0x05: // 数据包
		threshold_value_init.temp_value = cJSON_GetObjectItem(cjson_test, "temp_t")->valueint;
		threshold_value_init.humi_value = cJSON_GetObjectItem(cjson_test, "humi_t")->valueint;

		if (Connect_Net && Data_init.App == 0) {
        Data_init.App = 1;
    }
		W_Test();
		break;
	default:
		break;
	}

	/* 清空JSON对象(整条链表)的所有数据 */
	cJSON_Delete(cjson_test);

	return MY_SUCCESSFUL;
}
