package com.foundation.example

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.foundation.widget.utils.ext.global.postMainDelayedLifecycle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        postMainDelayedLifecycle(this, 5000) {
            "测试".toast()
        }
    }
}