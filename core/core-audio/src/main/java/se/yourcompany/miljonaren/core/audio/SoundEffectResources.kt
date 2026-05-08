package se.yourcompany.miljonaren.core.audio

internal object SoundEffectResources {
    val fileNameByEffect: Map<SoundEffect, String> = mapOf(
        SoundEffect.ANSWER_SELECTED to "answer_selected",
        SoundEffect.ANSWER_CORRECT to "answer_correct",
        SoundEffect.ANSWER_WRONG to "answer_wrong",
        SoundEffect.GAME_COMPLETE to "game_complete"
    )
}
