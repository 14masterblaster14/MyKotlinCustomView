package com.example.a2piano

import android.content.Context
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import android.util.SparseArray
import java.io.InputStream


class AudioSoundPlayer(var context: Context) {

    companion object {
        private const val MAX_VOLUME = 109
        private const val CURRENT_VOLUME = 90
    }

    private var threadMap: SparseArray<PlayThread>? = SparseArray()

    private var SOUND_MAP: SparseArray<String> = SparseArray()

    init {
        // white keys sounds
        SOUND_MAP.put(1, "1")
        SOUND_MAP.put(2, "2")
        SOUND_MAP.put(3, "3")
        SOUND_MAP.put(4, "4")
        SOUND_MAP.put(5, "5")
        SOUND_MAP.put(6, "6")
        SOUND_MAP.put(7, "7")
        SOUND_MAP.put(8, "8")
        SOUND_MAP.put(9, "9")
        SOUND_MAP.put(10, "10")
        SOUND_MAP.put(11, "11")
        SOUND_MAP.put(12, "12")
        SOUND_MAP.put(13, "13")
        SOUND_MAP.put(14, "14")
        // black keys sounds
        SOUND_MAP.put(15, "15")
        SOUND_MAP.put(16, "16")
        SOUND_MAP.put(17, "17")
        SOUND_MAP.put(18, "18")
        SOUND_MAP.put(19, "19")
        SOUND_MAP.put(20, "20")
        SOUND_MAP.put(21, "21")
        SOUND_MAP.put(22, "22")
        SOUND_MAP.put(23, "23")
        SOUND_MAP.put(24, "24")
    }

    fun playNote(note: Int) {
        if (!isNotePlaying(note)) {
            var thread: PlayThread = PlayThread(note)
            thread.start()
            threadMap?.put(note, thread)
        }
    }

    fun stopNote(note: Int) {
        var thread: PlayThread = PlayThread(note)
        if (thread != null) {
            threadMap?.remove(note)
        }
    }

    fun isNotePlaying(note: Int): Boolean {
        return threadMap?.get(note) != null
    }

    inner class PlayThread(var note: Int) : Thread() {

        lateinit var audioTrack: AudioTrack

        override fun run() {
            //super.run()
            try {
                var path: String = SOUND_MAP.get(note) + ".wav"
                var assetManager = context.assets
                var assetFileDescriptor = assetManager.openFd(path)
                var fileSize: Long = assetFileDescriptor.length
                var bufferSize = 4096
                val buffer = ByteArray(bufferSize)

                audioTrack = AudioTrack(AudioManager.STREAM_MUSIC, 44100,
                        AudioFormat.CHANNEL_CONFIGURATION_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM)

                val logVolume = (1 - Math.log(MAX_VOLUME.toDouble() - CURRENT_VOLUME)
                        / Math.log(MAX_VOLUME.toDouble())).toFloat()

                audioTrack.setStereoVolume(logVolume, logVolume)

                audioTrack.play()
                var audioStream: InputStream? = null
                val headerOffset = 0x2C
                var bytesWritten: Long = 0
                var bytesRead = 0

                audioStream = assetManager.open(path)
                audioStream.read(buffer, 0, headerOffset)

                while (bytesWritten < fileSize - headerOffset) {
                    bytesRead = audioStream.read(buffer, 0, bufferSize)
                    bytesWritten += audioTrack.write(buffer, 0, bytesRead)
                }

                audioTrack.stop()
                audioTrack.release()

            } catch (e: Exception) {
                Log.i("MasterBlaster", "Error Occured")
            } finally {
                if (audioTrack != null) {
                    audioTrack.release()
                }
            }
        }
    }
}
