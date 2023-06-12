package com.neunit.tts.model

/**
 * @Description 长语音合成返回数据
 * @Author ZhaoXiudong
 * @Date 06-08-2023 周四 15:21
 */
class LongTTSResultStatusModel(var rid: String, var code: Int, var msg: String, var data: LongTTSResultStatusData?)

class LongTTSResultStatusData(var task_id: String, var status: Int, var result_url: String, var error_msg: String)