package se.yourcompany.miljonaren.tv

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import se.yourcompany.miljonaren.core.audio.SoundEffect
import se.yourcompany.miljonaren.core.audio.SoundPoolGameAudioPlayer

@RunWith(AndroidJUnit4::class)
class SoundAssetsTest {
    @Test
    fun soundAssetsAreBundledAndPlayerCanBeReleased() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        listOf("answer_selected", "answer_correct", "answer_wrong", "game_complete").forEach { name ->
            assertNotEquals(0, context.resources.getIdentifier(name, "raw", context.packageName))
        }
        val player = SoundPoolGameAudioPlayer(context)
        SoundEffect.values().forEach { player.play(it) }
        player.release()
    }
}
