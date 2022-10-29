package com.example.chatwme

import androidx.benchmark.macro.ExperimentalBaselineProfilesApi
import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.chatwme", appContext.packageName)
    }

    @OptIn(ExperimentalBaselineProfilesApi::class)
    class TrivialBaselineProfileBenchmark {
        @get:Rule
        val baselineProfileRule = BaselineProfileRule()

        @Test
        fun startup() = baselineProfileRule.collectBaselineProfile(
            packageName = "com.example.app",
            profileBlock = {
                startActivityAndWait()
            }
        )
    }


}