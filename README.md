## TTS 语音合成 集成方式

### 1. 在跟目录的build.gradle添加如下代码

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

### 2. 在项目的build.gradle 添加 TTS 合成 依赖

```
dependencies {
    implementation 'com.github.XioaAi:TTSSynthesis:1.0.0'
}
```

### 3. 在Application中（或使用TTS合成功能前）初始化secret信息

```
TTSSynthesisInstance.initTTSSecret(secretId,secretKey)
```

### 3. 通过 TTSSynthesisInstance 进行语音合成
* 回调均为子线程

```
基础语音合成

TTSSynthesisInstance.ttsSynthesis(text, object : TTSSynthesisCallBack {
    override fun callBack(code: Int, msg: String, data: TTSResultData?) {
        Log.d(tag, "接收到合成结果:${data?.audio}")
    }
})
```

```
长语音合成

TTSSynthesisInstance.longTTSSynthesis(text, object : LongTTSSynthesisCallBack {
    override fun callBack(code: Int, msg: String, data: LongTTSResultData?) {
        Log.d(tag, "接收到合成结果:${data?.task_id}")
    }
})
```

```
查询长语音合成结果

TTSSynthesisInstance.longTTSSynthesisStatus(taskId, object : LongTTSSynthesisStatusCallBack {
    override fun callBack(code: Int, msg: String, data: LongTTSResultStatusData?) {
        Log.d(tag, "查询到合成状态:${data?.status}")
    }
})

```




