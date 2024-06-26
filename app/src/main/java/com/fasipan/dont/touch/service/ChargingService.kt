package com.fasipan.dont.touch.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import com.fasipan.dont.touch.R
import com.fasipan.dont.touch.ui.splash.SplashActivity
import com.fasipan.dont.touch.utils.Constants
import com.fasipan.dont.touch.utils.SharePreferenceUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class ChargingService : Service(), SensorEventListener {

    private val SAMPLE_RATE = 44100
    private val MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(
        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
    )
    //private val CLAP_THRESHOLD = 20000

    private var audioRecord: AudioRecord? = null
    private var shouldListen = false


    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null


    private var isOnScreen = MutableLiveData(true)
    private lateinit var someObserver: LifecycleObserver

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        initListener()
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager?.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI)
        return START_STICKY
    }

    private fun initListener() {
        isOnScreen.value =
            (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isKeyguardLocked
        val sharedPreferences = getSharedPreferences("data_app_ghost_radar", Context.MODE_PRIVATE)

        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                if (key == "isScreenOn") {
                    Log.e("truongpa", "initListener: isScreenOn Change")
                    if (isLockShow()) {
                        startListening()
                    } else {
                        shouldListen = false
                        audioRecord?.stop()
                        audioRecord?.release()
                    }
                }
            }
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun isLockShow(): Boolean {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardLocked
    }


    private fun startListening() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        if (!isLockShow()) return

        if (shouldListen) return

        Log.e("truongpa", "startListening: running")

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            MIN_BUFFER_SIZE
        )
        shouldListen = true

        val CLAP_THRESHOLD = tinhDoNhayClap()

        GlobalScope.launch(Dispatchers.IO) {
            audioRecord?.startRecording()
            val buffer = ShortArray(MIN_BUFFER_SIZE)
            var lastPeak = 0
            while (shouldListen) {
                try {
                    audioRecord?.read(buffer, 0, MIN_BUFFER_SIZE)
                    val maxAmplitude = buffer.maxOrNull() ?: 0
                    if (maxAmplitude > CLAP_THRESHOLD && maxAmplitude - lastPeak > CLAP_THRESHOLD) {
                        sendBroadcast(Intent("action_clap"))
                    }
                    lastPeak = maxAmplitude.toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startWithNotification() else startForeground(
            1, Notification()
        )

        registerReceiver(chargingListener, IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction("action_touch")
            addAction("action_clap")
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        })
    }

    override fun onDestroy() {
        mSensorManager?.unregisterListener(this)
        super.onDestroy()
        unregisterNetworkChanges()
        shouldListen = false
        audioRecord?.stop()
        audioRecord?.release()
    }

    private fun unregisterNetworkChanges() {
        try {
            unregisterReceiver(chargingListener)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    private val chargingListener by lazy {
        ChargingListener()
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent?.sensor?.type != 1 || (sensorEvent.values[0] * sensorEvent.values[0] + sensorEvent.values[1] * sensorEvent.values[1] + sensorEvent.values[1] * sensorEvent.values[1]) / 96.17039f < 1.0f) {
            return
        }
        sendBroadcast(Intent("action_touch"))
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }


    private fun tinhDoNhayClap(): Int {
        val value = 20000
        val sensitivity = SharePreferenceUtils.getSensitivity()
        val lech = 50 - sensitivity

        val ans = value + (value / 4) * lech / 50

        return ans
    }

    fun tinhDoNhayTouch() {

    }

}

fun Service.startWithNotification() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE
        )
        val manager = (getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(notificationChannel)

        val intentOpenApp = Intent(this, SplashActivity::class.java).apply {}
        val pendingIntentOpenApp = PendingIntent.getActivity(
            this,
            0,
            intentOpenApp,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this, Constants.NOTIFICATION_CHANNEL_ID
        ).apply {
            priority = NotificationCompat.PRIORITY_LOW
        }
        val notification = notificationBuilder.setOngoing(true).setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(Constants.NOTIFICATION_DETAILS)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE).setContentIntent(pendingIntentOpenApp)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC).build()
        startForeground(2, notification)
    }

}