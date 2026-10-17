package com.example

import com.example.util.YouTubeUrlHelper
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testYouTubeIdExtraction() {
        val standardUrl = "https://www.youtube.com/watch?v=2WPCLda_erI"
        assertEquals("2WPCLda_erI", YouTubeUrlHelper.extractVideoId(standardUrl))

        val shortUrl = "https://youtu.be/kffacxfA7G4"
        assertEquals("kffacxfA7G4", YouTubeUrlHelper.extractVideoId(shortUrl))

        val shortsUrl = "https://www.youtube.com/shorts/7e8x0u0J4bE"
        assertEquals("7e8x0u0J4bE", YouTubeUrlHelper.extractVideoId(shortsUrl))

        val directId = "2WPCLda_erI"
        assertEquals("2WPCLda_erI", YouTubeUrlHelper.extractVideoId(directId))
    }

    @Test
    fun testEmbedUrlGeneration() {
        val embed = YouTubeUrlHelper.getEmbedUrl("2WPCLda_erI")
        assert(embed.contains("2WPCLda_erI"))
        assert(embed.startsWith("https://www.youtube-nocookie.com/embed/"))
    }
}
