package com.jeffreyliu.duckit

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jeffreyliu.duckit.ktor.PostsService
import kotlinx.coroutines.runBlocking

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

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
        assertEquals("com.jeffreyliu.duckit", appContext.packageName)
    }


    @Test
    fun testApiCall() {
        val service = PostsService.create()
        runBlocking {

            val posts = service.getPosts()
            posts?.let {
                Log.d("jeff", "post size is ${it.posts.size}")
                assertNotEquals(it.posts.size, 0)
            }
        }
    }
}