package com.android.testrecordbackground

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.permissionx.guolindev.PermissionX


class MainActivity : AppCompatActivity() {
    var isRunning = false
    var button: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button = findViewById(R.id.btnStartService)
        button?.setOnClickListener {
            PermissionX.init(this)
                .permissions(
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        startService()
                        Toast.makeText(this, "All permissions are granted", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(
                            this,
                            "These permissions are denied: $deniedList",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    fun startService() {
        if (isRunning) {
            button?.text = "Start Now"
            isRunning = false
            //Stop service
            val serviceIntent = Intent(
                applicationContext,
                ForegroundService::class.java
            )
            stopService(serviceIntent)
        } else {
            isRunning = true
            button?.text = "Running"
            val WAVPath =
                applicationContext.getExternalFilesDir(null)?.absolutePath + "/FinalAudio.wav"
            //Start service
            val serviceIntent = Intent(
                applicationContext,
                ForegroundService::class.java
            )
            serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android")
            serviceIntent.putExtra("WAVPath", WAVPath)
            ContextCompat.startForegroundService(this, serviceIntent)
        }
    }
}