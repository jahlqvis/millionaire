package se.yourcompany.miljonaren.core.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

class SoundPoolGameAudioPlayer(
    context: Context
) : GameAudioPlayer {

    private val appContext = context.applicationContext
    private val soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(4)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
        )
        .build()

    private val soundIdByEffect: Map<SoundEffect, Int> =
        SoundEffectResources.fileNameByEffect.mapNotNull { (effect, fileName) ->
            val resId = appContext.resources.getIdentifier(fileName, "raw", appContext.packageName)
            if (resId == 0) {
                null
            } else {
                effect to soundPool.load(appContext, resId, 1)
            }
        }.toMap()

    override fun play(soundEffect: SoundEffect) {
        val soundId = soundIdByEffect[soundEffect] ?: return
        soundPool.play(
            soundId,
            1f,
            1f,
            1,
            0,
            1f
        )
    }

    override fun release() {
        soundPool.release()
    }
}
