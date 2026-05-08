package se.yourcompany.miljonaren.core.audio

interface GameAudioPlayer {
    fun play(soundEffect: SoundEffect)
    fun release()
}
