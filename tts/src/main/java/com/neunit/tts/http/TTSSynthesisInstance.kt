package com.neunit.tts.http

import android.util.Log
import com.google.gson.Gson
import com.neunit.tts.model.LongTTSResultData
import com.neunit.tts.model.LongTTSResultModel
import com.neunit.tts.model.TTSResultData
import com.neunit.tts.model.TTSResultModel
import com.neunit.tts.sign.SignUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.*

interface TTSSynthesisCallBack {
    fun callBack(code: Int, msg: String, data: TTSResultData?)
}

interface LongTTSSynthesisCallBack {
    fun callBack(code: Int, msg: String, data: LongTTSResultData?)
}

/**
 * @Description TTS合成请求
 * @Author ZhaoXiudong
 * @Date 06-08-2023 周四 16:41
 */
class TTSSynthesisInstance {
    companion object {
        private val tag = TTSSynthesisInstance::class.java.name

        /**
         * 基础语音合成
         */
        fun ttsSynthesis(
            params: Map<String, Any?>,
            secretId: String,
            secretKey: String,
            ttsSynthesisCallBack: TTSSynthesisCallBack,
        ) {
            val requestParams = toJSONObject(params).toString()
            val url = "https://winner-api.neunit.com:18053/cloud/tts/v1/text_to_voice"
            val client = OkHttpClient.Builder().build()
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body: RequestBody = requestParams.toRequestBody(mediaType)
            val request: Request =
                Request.Builder().url(url).headers(buildHeader(params, secretId, secretKey)).post(body).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    Log.d(tag, "请求失败:${e.printStackTrace()}")
                    ttsSynthesisCallBack.callBack(-1, e.message ?: "", null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string()
                    Log.d(tag, "请求成功:$result")
                    if (result != null) {
                        val ttsResultModel = Gson().fromJson(result, TTSResultModel::class.java)
                        if (ttsResultModel.code == 0) {
                            Log.d(tag, "请求成功:${ttsResultModel.data?.session_id}")
                            ttsSynthesisCallBack.callBack(ttsResultModel.code, ttsResultModel.msg, ttsResultModel.data)
                        } else {
                            Log.d(tag, "请求失败:${ttsResultModel.msg}")
                            ttsSynthesisCallBack.callBack(ttsResultModel.code, ttsResultModel.msg, null)
                        }
                    }

                }
            })
        }

        /**
         * 长语音合成
         */
        fun longTTSSynthesis(
            params: Map<String, Any?>,
            secretId: String,
            secretKey: String,
            longTTSSynthesisCallBack: LongTTSSynthesisCallBack,
        ) {

            val requestParams = toJSONObject(params).toString()
            val url = "https://winner-api.neunit.com:18053/cloud/tts/v1/create_tts_task"
            val client = OkHttpClient.Builder().build()
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body: RequestBody = requestParams.toRequestBody(mediaType)
            val request: Request =
                Request.Builder().url(url).headers(buildHeader(params, secretId, secretKey)).post(body).build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    Log.d(tag, "请求失败:${e.printStackTrace()}")
                    longTTSSynthesisCallBack.callBack(-1, e.message ?: "", null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string()
                    Log.d(tag, "请求成功:$result")
                    if (result != null) {
                        val longTTSResultData = Gson().fromJson(result, LongTTSResultModel::class.java)
                        if (longTTSResultData.code == 0) {
                            Log.d(tag, "请求成功:${longTTSResultData.data?.task_id}")
                            longTTSSynthesisCallBack.callBack(longTTSResultData.code,
                                longTTSResultData.msg,
                                longTTSResultData.data)
                        } else {
                            Log.d(tag, "请求失败:${longTTSResultData.msg}")
                            longTTSSynthesisCallBack.callBack(longTTSResultData.code, longTTSResultData.msg, null)
                        }
                    }

                }
            })
        }

        /**
         * 添加请求头参数
         */
        private fun buildHeader(requestParams: Map<String, Any?>, secretId: String, secretKey: String): Headers {
            val nonce = UUID.randomUUID().toString()
            val timestamp = Date().time.toString()
            val waitSignStr = "${requestParams}_${nonce}_${timestamp}_${secretId}"
            Log.d(tag, "待签名字符串:$waitSignStr")
            val sign = SignUtils.strToHMacSHA256(SignUtils.strToSHA256(waitSignStr), secretKey)
            Log.d(tag, "签名成功:$sign")
            return Headers.Builder().add("Authorization", sign).add("X-NC-SecretId", secretId).add("X-NC-Nonce", nonce)
                .add("X-NC-Timestamp", timestamp).build()
        }

        private fun toJSONObject(params: Map<String, Any?>): JSONObject {
            val param = JSONObject()
            params.forEach {
                param.put(it.key, it.value)
            }
            return param
        }
    }
}