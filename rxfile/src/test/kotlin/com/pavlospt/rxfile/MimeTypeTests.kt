package com.pavlospt.rxfile

import android.util.JsonReader
import android.webkit.MimeTypeMap
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowMimeTypeMap
import java.net.URLConnection

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [26], shadows = [ShadowMimeTypeMap::class])
class MimeTypeTests {

    var extensions = mutableMapOf<String, String>()

    @Before
    fun loadMimeType() {
        val mimeTypeMap = Shadows.shadowOf(ShadowMimeTypeMap.getSingleton())
        mimeTypeMap.clearMappings()
        extensions.clear()

        // mime type is based on
        // https://android.googlesource.com/platform/frameworks/base/+/froyo/core/java/android/webkit/MimeTypeMap.java
        val resource = javaClass.classLoader.getResourceAsStream("mime_type_7.json")
        val jsonReader = JsonReader(resource.reader())
        jsonReader.beginObject()
        while (jsonReader.hasNext()) {
            val mimeType = jsonReader.nextName()
            val extension = jsonReader.nextString()
            extensions[extension] = mimeType

            mimeTypeMap.addExtensionMimeTypMapping(extension, mimeType)
        }
    }

    @Test
    fun testWebkitMimeType() {
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        extensions.keys.forEach {
            val getMimeTypeFromExtension = mimeTypeMap.getMimeTypeFromExtension(it)
            Assert.assertEquals(extensions[it], getMimeTypeFromExtension)
        }
    }

    @Test
    fun testGuessContentTypeFromName() {
        val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        extensions.keys.forEach {
            val guessContentTypeFromName = URLConnection.guessContentTypeFromName("file.${it}")
            Assert.assertEquals(extensions[it], guessContentTypeFromName)
        }
    }
}