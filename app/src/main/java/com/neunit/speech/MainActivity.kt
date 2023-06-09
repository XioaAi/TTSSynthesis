package com.neunit.speech

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.neunit.tts.http.TTSSynthesisCallBack
import com.neunit.tts.http.TTSSynthesisInstance
import com.neunit.tts.model.TTSResultData
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tts_synthesis_btn.setOnClickListener {
            val data = mapOf<String, Any?>("text" to tts_input.text.toString(),
                "session_id" to UUID.randomUUID().toString(),
                "voice_type" to 0)
            val secretId = ""
            val secretKey = ""
            TTSSynthesisInstance.ttsSynthesis(data, secretId, secretKey, object : TTSSynthesisCallBack {
                override fun callBack(code: Int, msg: String, data: TTSResultData?) {
                    Log.e(tag, "接收到合成结果:${data?.audio}")
                }
            })
        }
    }
}