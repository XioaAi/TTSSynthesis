package com.neunit.tts.http

import android.util.Log
import com.google.gson.Gson
import com.neunit.tts.model.*
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

interface LongTTSSynthesisStatusCallBack {
    fun callBack(code: Int, msg: String, data: LongTTSResultStatusData?)
}

enum class TTSVoiceType {
    Female, Male
}

/**
 * @Description TTS合成请求
 * @Author ZhaoXiudong
 * @Date 06-08-2023 周四 16:41
 */
class TTSSynthesisInstance {
    companion object {
        private val tag = TTSSynthesisInstance::class.java.name

        private var secretId: String? = null
        private var secretKey: String? = null

        /**
         * 初始化Secret信息
         */
        @JvmStatic
        fun initTTSSecret(secretId: String, secretKey: String) {
            this.secretId = secretId
            this.secretKey = secretKey
        }


        /**
         * 基础语音合成
         */
        @JvmStatic
        fun ttsSynthesis(text: String, callBack: TTSSynthesisCallBack, voiceType: TTSVoiceType = TTSVoiceType.Male) {
            if (secretId == null || secretKey == null) {
                Log.e(tag, "请先初始化secret信息")
                return
            }
            val requestParams = toJSONObject(mapOf("text" to text,
                "session_id" to UUID.randomUUID().toString(),
                "voice_type" to if (voiceType == TTSVoiceType.Male) 0 else 1)).toString()
            val url = "https://winner-api.neunit.com:18053/cloud/tts/v1/text_to_voice"
            val client = OkHttpClient.Builder().build()
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body: RequestBody = requestParams.toRequestBody(mediaType)
            val request: Request =
                Request.Builder().url(url).headers(buildHeader(requestParams, secretId!!, secretKey!!)).post(body)
                    .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    Log.d(tag, "请求失败:${e.printStackTrace()}")
                    callBack.callBack(-1, e.message ?: "", null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string()
                    Log.d(tag, "请求成功:$result")
                    if (result != null) {
                        val ttsResultModel = Gson().fromJson(result, TTSResultModel::class.java)
                        callBack.callBack(ttsResultModel.code, ttsResultModel.msg, ttsResultModel.data)
                    } else {
                        Log.d(tag, "请求失败:接口返回信息为null")
                        callBack.callBack(-1, "", null)
                    }

                }
            })
        }

        /**
         * 长语音合成
         */
        @JvmStatic
        fun longTTSSynthesis(
            text: String,
            callBack: LongTTSSynthesisCallBack,
            voiceType: TTSVoiceType = TTSVoiceType.Male,
            callBackUrl: String? = null,
        ) {
            if (secretId == null || secretKey == null) {
                Log.e(tag, "请先初始化secret信息")
                return
            }
            val requestParams = toJSONObject(mapOf("text" to text,
                "callback_url" to (callBackUrl ?: ""),
                "voice_type" to if (voiceType == TTSVoiceType.Male) 0 else 1)).toString()
            val url = "https://winner-api.neunit.com:18053/cloud/tts/v1/create_tts_task"
            val client = OkHttpClient.Builder().build()
            val mediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val body: RequestBody = requestParams.toRequestBody(mediaType)
            val request: Request =
                Request.Builder().url(url).headers(buildHeader(requestParams, secretId!!, secretKey!!)).post(body)
                    .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    Log.d(tag, "请求失败:${e.printStackTrace()}")
                    callBack.callBack(-1, e.message ?: "", null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string()
                    Log.d(tag, "请求成功:$result")
                    if (result != null) {
                        val longTTSResultData = Gson().fromJson(result, LongTTSResultModel::class.java)
                        callBack.callBack(longTTSResultData.code, longTTSResultData.msg, longTTSResultData.data)
                    } else {
                        Log.d(tag, "请求失败:接口返回信息为null")
                        callBack.callBack(-1, "", null)
                    }
                }
            })
        }

        /**
         * 长语音合成结果查询
         */
        @JvmStatic
        fun longTTSSynthesisStatus(taskId: String, callBack: LongTTSSynthesisStatusCallBack) {
            if (secretId == null || secretKey == null) {
                Log.e(tag, "请先初始化secret信息")
                return
            }
            val url = "https://winner-api.neunit.com:18053/cloud/tts/v1/describe_tts_task_status?task_id=$taskId"
            Log.d(tag, "请求地址:$url")
            val requestParams = toJSONObject(mapOf("task_id" to taskId)).toString()
            val client = OkHttpClient.Builder().build()
            val request: Request =
                Request.Builder().url(url).headers(buildHeader(requestParams, secretId!!, secretKey!!)).get().build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: java.io.IOException) {
                    Log.d(tag, "请求失败:${e.printStackTrace()}")
                    callBack.callBack(-1, e.message ?: "", null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body?.string()
                    Log.d(tag, "请求成功:$result")
                    if (result != null) {
                        val model = Gson().fromJson(result, LongTTSResultStatusModel::class.java)
                        callBack.callBack(model.code, model.msg, model.data)
                    } else {
                        Log.d(tag, "请求失败:接口返回信息为null")
                        callBack.callBack(-1, "", null)
                    }

                }
            })
        }

        /**
         * 添加请求头参数
         */
        private fun buildHeader(paramsJsonStr: String, secretId: String, secretKey: String): Headers {
            val nonce = UUID.randomUUID().toString()
            val timestamp = (Date().time / 1000).toString()
            val waitSignStr = "${paramsJsonStr}_${nonce}_${timestamp}_${secretId}"
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