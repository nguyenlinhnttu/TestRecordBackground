package com.android.testrecordbackground

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import java.io.IOException

class ForegroundService : Service() {
    private var recorder: MediaRecorder? = null

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
        private const val TAG = "ForegroundService"
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val input = intent.getStringExtra("inputExtra")
        //Từ Android 0 chúng ta cần tạo 1 notification.
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Foreground Service : Record")
            .setContentText(input)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .build()
        startForeground(1, notification)
        //Start Recoder.
        Log.i(TAG, "Starting the audio stream")
        startStreaming()
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        Log.i(TAG, "Stopping the audio stream")
        recorder?.stop()
        recorder?.release()
        super.onDestroy()
    }

    @Nullable
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    private fun startStreaming() {
        Log.i(TAG, "Starting in foreground service)")
        val WAVPath = applicationContext.getExternalFilesDir(null)?.absolutePath + "/FinalAudio.wav"
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncodingBitRate(16)
            setAudioSamplingRate(44100)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(WAVPath)

            try {
                prepare()
                start()
            } catch (e: IOException) {
                Log.e("IOException", "prepare() failed")
            }
        }
    }
}