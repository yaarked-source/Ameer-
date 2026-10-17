package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SoundBeepHelper(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        } catch (_: Exception) {
            // Audio hardware fallback
        }
    }

    fun playRoundStartBell() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // High boxing ring starting gong
                toneGenerator?.startTone(ToneGenerator.TONE_SUP_RINGTONE, 350)
                vibrateDoublePulse()
                delay(400)
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 450)
            } catch (_: Exception) {}
        }
    }

    fun playRoundEndBell() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                // Triple bell ring at end of round
                repeat(3) {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 200)
                    delay(260)
                }
                vibrateHeavyPulse()
            } catch (_: Exception) {}
        }
    }

    fun playTenSecondWarning() {
        try {
            // Wood clapper / warning pips
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 120)
        } catch (_: Exception) {}
    }

    fun playStrikeHitSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
            vibrateImpact()
        } catch (_: Exception) {}
    }

    fun playMetronomeBeat() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_DTMF_0, 45)
        } catch (_: Exception) {}
    }

    private fun vibrateImpact() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v?.vibrate(35)
                }
            }
        } catch (_: Exception) {}
    }

    private fun vibrateDoublePulse() {
        try {
            val timings = longArrayOf(0, 100, 80, 150)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                v?.vibrate(VibrationEffect.createWaveform(timings, -1))
            }
        } catch (_: Exception) {}
    }

    private fun vibrateHeavyPulse() {
        try {
            val timings = longArrayOf(0, 180, 100, 180, 100, 250)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                v?.vibrate(VibrationEffect.createWaveform(timings, -1))
            }
        } catch (_: Exception) {}
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
