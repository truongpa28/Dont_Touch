package com.fasipan.dont.touch.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.widget.Toast
import com.fasipan.dont.touch.R

object MediaPlayerUtils {


    var mp: MediaPlayer? = null
    private var runnable: Runnable? = null
    private var handler: Handler? = null

    fun startMediaPlayer(
        context: Context,
        data: String,
        volume: Float = 1f,
        showToast: Boolean = true
    ) {
        try {
            releaseMediaPlayer()
            mp = MediaPlayer()
            mp?.setDataSource(
                context,
                Uri.parse(data)
            )
            mp?.setVolume(volume, volume)
            mp!!.prepare()
            mp!!.start()
            mp!!.setOnCompletionListener {

            }
            val handler: Handler? = handler
            runnable?.let { handler?.removeCallbacks(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            if (showToast) {
                Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT)
                    .show()
            }

        }
    }

    fun playGhostSound(context: Context, data: Int, actionDone: () -> Unit) {
        try {
            releaseMediaPlayer()
            mp = MediaPlayer()
            mp?.setDataSource(
                context,
                Uri.parse("android.resource://${context.packageName}/$data")
            )
            mp?.setVolume(1f, 1f)
            mp!!.prepare()
            mp!!.start()
            mp!!.setOnCompletionListener {
                actionDone()
            }
            val handler: Handler? = handler
            runnable?.let { handler?.removeCallbacks(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun mediaPause() {
        mp?.pause()
    }

    fun mediaResume() {
        mp?.start()
    }

    fun isPlaying(): Boolean {
        return mp?.isPlaying ?: false
    }

    private fun releaseMediaPlayer() {
        val mediaPlayer: MediaPlayer? = mp
        if (mediaPlayer != null) {
            with(mediaPlayer) { stop() }
            mp?.release()
            mp = null
        }
        val handler = handler
        runnable?.let { handler?.removeCallbacks(it) }
    }

    fun stopMediaPlayer() {
        val mediaPlayer: MediaPlayer? = mp
        if (mediaPlayer != null) {
            with(mediaPlayer) { stop() }
            mp?.release()
            mp = null
        }
        val handler = handler
        runnable?.let { handler?.removeCallbacks(it) }
    }

    fun setVolume(volume: Float = 1f) {
        mp?.setVolume(volume, volume)
    }


    fun playAudioSoundRecord(context: Context, data: String, actionDone: () -> Unit) {
        try {
            releaseMediaPlayer()
            mp = MediaPlayer()
            mp?.setDataSource(
                context,
                Uri.parse(data)
            )
            mp?.setVolume(1f, 1f)
            mp!!.prepare()
            mp!!.start()
            mp!!.setOnCompletionListener {
                actionDone()
            }
            val handler: Handler? = handler
            runnable?.let { handler?.removeCallbacks(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT)
                .show()
        }
    }
}