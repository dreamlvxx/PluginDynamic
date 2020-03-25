package com.example.plugindynamic

import android.Manifest.permission
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.tbruyelle.rxpermissions2.Permission
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileInputStream


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_load.setOnClickListener {
            loadPlugin()
        }
        tv_start.setOnClickListener {
            launchPluginActivity()
        }
        tv_write_apk.setOnClickListener {
            requestPermi()
        }
    }

    fun writeApk() {
        val file = Environment.getExternalStorageDirectory()
        val pushfile = File(file, "mypushfile.txt")
        val inss = FileInputStream(pushfile)
        val content = inss.read().toString()
        Log.i("sad", content)
    }

    fun loadPlugin() {
        try {
            PluginHelper.loadPlugin(this, classLoader)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "插件加载失败", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchPluginActivity() {
        var pluginActivityClass: Class<*>? = null
        try {
            pluginActivityClass = Class.forName("com.example.pluginapk.MainActivity")
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }

        if (pluginActivityClass == null) {
            Toast.makeText(this, "找不到PluginActivity", Toast.LENGTH_SHORT).show()
            return
        }
        val intent = Intent(this, pluginActivityClass)
        startActivity(intent)
    }

    fun requestPermi() {
        val rxPermissions = RxPermissions(this) // where this is an Activity instance
        rxPermissions
            .requestEach(
                permission.WRITE_EXTERNAL_STORAGE,
                permission.READ_EXTERNAL_STORAGE
            )
            .subscribe({ t: Permission ->
                if (t.granted){
                    writeApk()
                }
            })
    }
}
