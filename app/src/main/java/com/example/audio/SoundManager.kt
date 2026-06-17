package com.example.audio

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager {
    private val sampleRate = 22050
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playTap() {
        scope.launch {
            synthTone(frequency = 700f, durationMs = 40, type = WaveType.SINE, decay = true)
        }
    }

    fun playSelection() {
        scope.launch {
            synthTone(frequency = 900f, durationMs = 30, type = WaveType.SINE, decay = true)
        }
    }

    fun playMatch(isExact: Boolean) {
        scope.launch {
            if (isExact) {
                synthTone(frequency = 1046.50f, durationMs = 60, type = WaveType.SINE, decay = true) // C6
            } else {
                synthTone(frequency = 783.99f, durationMs = 60, type = WaveType.SINE, decay = true) // G5
            }
        }
    }

    fun playError() {
        scope.launch {
            synthTone(frequency = 150f, durationMs = 180, type = WaveType.TRIANGLE, decay = true)
        }
    }

    fun playWin() {
        scope.launch {
            // Upward major arpeggio
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f)
            for (note in notes) {
                synthTone(frequency = note, durationMs = 90, type = WaveType.SINE, decay = true)
                Thread.sleep(40)
            }
            // Hold top chord optionally
            synthTone(frequency = 1046.50f, durationMs = 300, type = WaveType.SINE, decay = true)
        }
    }

    fun playLose() {
        scope.launch {
            // Downward minor descent
            val notes = listOf(392.00f, 349.23f, 311.13f, 261.63f)
            for (note in notes) {
                synthTone(frequency = note, durationMs = 150, type = WaveType.SAWTOOTH, decay = true)
                Thread.sleep(100)
            }
        }
    }

    enum class WaveType { SINE, SAWTOOTH, TRIANGLE }

    private fun synthTone(frequency: Float, durationMs: Int, type: WaveType = WaveType.SINE, decay: Boolean = false) {
        val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val samples = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val t = i.toFloat() / sampleRate
            val angle = 2.0 * Math.PI * frequency * t
            var value = when (type) {
                WaveType.SINE -> sin(angle)
                WaveType.SAWTOOTH -> {
                    val period = 1.0f / frequency
                    val progress = (t % period) / period
                    (2.0 * progress - 1.0)
                }
                WaveType.TRIANGLE -> {
                    val period = 1.0f / frequency
                    val progress = (t % period) / period
                    if (progress < 0.5) {
                        (4.0 * progress - 1.0)
                    } else {
                        (3.0 - 4.0 * progress)
                    }
                }
            }
            if (decay) {
                val factor = 1.0f - (i.toFloat() / numSamples)
                value *= factor
            }
            samples[i] = (value * 18000).toInt().toShort() // Maximum volume scaled carefully to avoid buzzing
        }

        try {
            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                samples.size * 2,
                AudioTrack.MODE_STATIC
            )
            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()
            // Wait for completion before cleanup
            Thread.sleep(durationMs.toLong() + 20)
            audioTrack.stop()
            audioTrack.release()
        } catch (e: Exception) {
            // Fail silently
        }
    }
}
