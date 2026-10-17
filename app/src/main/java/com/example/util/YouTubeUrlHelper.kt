package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.util.regex.Pattern

object YouTubeUrlHelper {

    // Regex to capture YouTube IDs from desktop, mobile, shorts, youtu.be, embed, and music links
    private val YOUTUBE_REGEX = Pattern.compile(
        "^.*(?:(?:youtu\\.be\\/|v\\/|vi\\/|u\\/\\w\\/|embed\\/|shorts\\/)|(?:(?:watch)?\\?v(?:i)?=|\\&v(?:i)?=))([^#\\&\\?]*).*",
        Pattern.CASE_INSENSITIVE
    )

    fun extractVideoId(url: String): String {
        val trimmed = url.trim()
        if (trimmed.length == 11 && trimmed.matches(Regex("^[a-zA-Z0-9_-]{11}$"))) {
            return trimmed
        }
        val matcher = YOUTUBE_REGEX.matcher(trimmed)
        return if (matcher.matches()) {
            val group = matcher.group(1)
            if (!group.isNullOrEmpty() && group.length == 11) group else ""
        } else {
            ""
        }
    }

    fun getThumbnailUrl(videoId: String): String {
        return if (videoId.isNotEmpty()) {
            "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        } else {
            ""
        }
    }

    fun getEmbedUrl(videoId: String): String {
        return "https://www.youtube-nocookie.com/embed/$videoId?autoplay=1&playsinline=1&controls=1&modestbranding=1&rel=0"
    }

    fun getCleanWebUrl(videoId: String): String {
        return "https://www.youtube.com/watch?v=$videoId"
    }

    fun openInYouTubeApp(context: Context, videoId: String) {
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId"))
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$videoId"))
        try {
            appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(appIntent)
        } catch (_: Exception) {
            webIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(webIntent)
        }
    }
}
