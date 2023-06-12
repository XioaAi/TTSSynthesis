package com.neunit.tts

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.neunit.tts.http.LongTTSSynthesisCallBack
import com.neunit.tts.http.LongTTSSynthesisStatusCallBack
import com.neunit.tts.http.TTSSynthesisCallBack
import com.neunit.tts.http.TTSSynthesisInstance
import com.neunit.tts.model.LongTTSResultData
import com.neunit.tts.model.LongTTSResultStatusData
import com.neunit.tts.model.TTSResultData
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val tag = MainActivity::class.java.name

    val secretId = ""
    val secretKey = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TTSSynthesisInstance.initTTSSecret(secretId, secretKey)

        tts_synthesis_btn.setOnClickListener {
            TTSSynthesisInstance.ttsSynthesis("nihao", object : TTSSynthesisCallBack {
                override fun callBack(code: Int, msg: String, data: TTSResultData?) {
                    Log.d(tag, "接收到合成结果:${data?.audio}")
                }
            })
        }

        long_tts_synthesis_btn.setOnClickListener {

            TTSSynthesisInstance.longTTSSynthesis(tts_input.text.toString(), object : LongTTSSynthesisCallBack {
                override fun callBack(code: Int, msg: String, data: LongTTSResultData?) {
                    Log.d(tag, "接收到合成结果:${data?.task_id}")

                    TTSSynthesisInstance.longTTSSynthesisStatus(data?.task_id ?: "",
                        object : LongTTSSynthesisStatusCallBack {
                            override fun callBack(code: Int, msg: String, data: LongTTSResultStatusData?) {
                                Log.d(tag, "查询到合成状态:${data?.status}")
                            }
                        })
                }
            })
        }
    }
}