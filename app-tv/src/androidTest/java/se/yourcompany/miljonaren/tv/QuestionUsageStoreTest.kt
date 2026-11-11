package se.yourcompany.miljonaren.tv

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import se.yourcompany.miljonaren.data.repository.SharedPreferencesQuestionUsageStore

@RunWith(AndroidJUnit4::class)
class QuestionUsageStoreTest {
    @Test
    fun completedQuestionUse_survivesNewStoreInstance() {
        val base = ApplicationProvider.getApplicationContext<Context>()
        val testName = "question_usage_test_${UUID.randomUUID()}"
        val isolated = object : ContextWrapper(base) {
            override fun getApplicationContext(): Context = this
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences =
                base.getSharedPreferences(testName, mode)
        }
        try {
            SharedPreferencesQuestionUsageStore(isolated).save(listOf(listOf("q1", "q2"), listOf("q3")))
            assertEquals(
                listOf(listOf("q1", "q2"), listOf("q3")),
                SharedPreferencesQuestionUsageStore(isolated).load()
            )
        } finally {
            base.deleteSharedPreferences(testName)
        }
    }
}
