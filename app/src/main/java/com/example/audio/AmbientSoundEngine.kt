package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.model.AmbientSound
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.sin

class AmbientSoundEngine {
    @Volatile
    private var audioTrack: AudioTrack? = null
    private var soundJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    private var currentSound: AmbientSound = AmbientSound.NONE

    @Volatile
    private var isPlaying: Boolean = false

    @Volatile
    private var volume: Float = 0.7f

    private val lock = Any()

    fun play(sound: AmbientSound) {
        synchronized(lock) {
            if (sound == currentSound && audioTrack != null) return
            stopInternal()

            if (sound == AmbientSound.NONE) {
                currentSound = AmbientSound.NONE
                return
            }

            currentSound = sound
            val sampleRate = 22050
            val channelConfig = if (sound == AmbientSound.BINAURAL_ALPHA) {
                AudioFormat.CHANNEL_OUT_STEREO
            } else {
                AudioFormat.CHANNEL_OUT_MONO
            }

            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                channelConfig,
                AudioFormat.ENCODING_PCM_16BIT
            )
            val bufferSize = minBufferSize.coerceAtLeast(4096)

            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(channelConfig)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                track.setVolume(volume)
                track.play()
                audioTrack = track
                isPlaying = true

                soundJob = scope.launch {
                    try {
                        when (sound) {
                            AmbientSound.RAINFALL -> generateRain(track, sampleRate, bufferSize)
                            AmbientSound.WHITE_NOISE -> generateWhiteNoise(track, bufferSize)
                            AmbientSound.BINAURAL_ALPHA -> generateBinauralAlpha(track, sampleRate, bufferSize)
                            AmbientSound.LOFI_BEATS -> generateLofiPulse(track, sampleRate, bufferSize)
                            AmbientSound.NONE -> {}
                        }
                    } catch (e: Exception) {
                        // Coroutine cancelled or track closed gracefully
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                stopInternal()
            }
        }
    }

    fun setVolume(vol: Float) {
        volume = vol.coerceIn(0f, 1f)
        try {
            audioTrack?.setVolume(volume)
        } catch (e: Exception) {
            // Ignore volume set on released track
        }
    }

    fun stop() {
        synchronized(lock) {
            stopInternal()
        }
    }

    private fun stopInternal() {
        isPlaying = false
        currentSound = AmbientSound.NONE
        soundJob?.cancel()
        soundJob = null

        val track = audioTrack
        audioTrack = null
        if (track != null) {
            try {
                if (track.state == AudioTrack.STATE_INITIALIZED) {
                    track.pause()
                    track.flush()
                }
            } catch (e: Exception) {
                // Ignore pause/flush exceptions
            }
            try {
                track.release()
            } catch (e: Exception) {
                // Ignore release exceptions
            }
        }
    }

    private suspend fun generateWhiteNoise(track: AudioTrack, bufferSize: Int) {
        val buffer = ShortArray(bufferSize)
        val random = Random()
        var lastVal = 0.0

        while (currentCoroutineContext().isActive && isPlaying) {
            for (i in buffer.indices) {
                val white = (random.nextDouble() * 2.0 - 1.0)
                lastVal = (lastVal + (0.05 * white)) / 1.05
                val sample = (lastVal * 12000.0).coerceIn(-32767.0, 32767.0)
                buffer[i] = sample.toInt().toShort()
            }
            if (!writeSafe(track, buffer)) break
        }
    }

    private suspend fun generateRain(track: AudioTrack, sampleRate: Int, bufferSize: Int) {
        val buffer = ShortArray(bufferSize)
        val random = Random()
        var pinkState = 0.0
        var dropDecay = 0.0

        while (currentCoroutineContext().isActive && isPlaying) {
            for (i in buffer.indices) {
                val noise = random.nextDouble() * 2.0 - 1.0
                pinkState = 0.94 * pinkState + 0.06 * noise

                if (random.nextInt(350) == 0) {
                    dropDecay = 1.0
                }
                dropDecay *= 0.985

                val dropSine = sin(i * 0.15) * dropDecay
                val combined = (pinkState * 0.75 + dropSine * 0.25) * 15000.0
                buffer[i] = combined.coerceIn(-32767.0, 32767.0).toInt().toShort()
            }
            if (!writeSafe(track, buffer)) break
        }
    }

    private suspend fun generateBinauralAlpha(track: AudioTrack, sampleRate: Int, bufferSize: Int) {
        val buffer = ShortArray(bufferSize * 2)
        var phaseLeft = 0.0
        var phaseRight = 0.0
        val freqLeft = 200.0
        val freqRight = 210.0
        val stepLeft = (2.0 * PI * freqLeft) / sampleRate
        val stepRight = (2.0 * PI * freqRight) / sampleRate

        while (currentCoroutineContext().isActive && isPlaying) {
            var idx = 0
            while (idx < buffer.size) {
                val left = (sin(phaseLeft) * 12000.0).toInt().toShort()
                val right = (sin(phaseRight) * 12000.0).toInt().toShort()
                buffer[idx++] = left
                buffer[idx++] = right

                phaseLeft = (phaseLeft + stepLeft) % (2.0 * PI)
                phaseRight = (phaseRight + stepRight) % (2.0 * PI)
            }
            if (!writeSafe(track, buffer)) break
        }
    }

    private suspend fun generateLofiPulse(track: AudioTrack, sampleRate: Int, bufferSize: Int) {
        val buffer = ShortArray(bufferSize)
        var phase = 0.0
        var lfoPhase = 0.0
        val baseFreq = 146.83
        val step = (2.0 * PI * baseFreq) / sampleRate
        val lfoStep = (2.0 * PI * 0.5) / sampleRate

        while (currentCoroutineContext().isActive && isPlaying) {
            for (i in buffer.indices) {
                val wave = sin(phase) + 0.4 * sin(phase * 1.5) + 0.3 * sin(phase * 2.0)
                val lfo = 0.6 + 0.4 * sin(lfoPhase)
                val sample = (wave * lfo * 8000.0).coerceIn(-32767.0, 32767.0)
                buffer[i] = sample.toInt().toShort()

                phase = (phase + step) % (2.0 * PI)
                lfoPhase = (lfoPhase + lfoStep) % (2.0 * PI)
            }
            if (!writeSafe(track, buffer)) break
        }
    }

    private fun writeSafe(track: AudioTrack, buffer: ShortArray): Boolean {
        if (!isPlaying) return false
        return try {
            if (track.state == AudioTrack.STATE_INITIALIZED && track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                val res = track.write(buffer, 0, buffer.size)
                res >= 0
            } else {
                false
            }
        } catch (e: Exception) {
            // AudioTrack released or native pointer invalidated concurrently
            false
        }
    }
}
