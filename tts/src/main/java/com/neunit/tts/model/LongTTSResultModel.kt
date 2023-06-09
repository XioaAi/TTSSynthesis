package com.neunit.tts.model

/**
 * @Description 长语音合成返回数据
 * @Author ZhaoXiudong
 * @Date 06-08-2023 周四 15:21
 */
class LongTTSResultModel(var rid: String, var code: Int, var msg: String, var data: LongTTSResultData?)

class LongTTSResultData(var task_id: String)