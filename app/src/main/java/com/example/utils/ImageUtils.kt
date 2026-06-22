package com.example.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object ImageUtils {
    private const val TAG = "ImageUtils"
    private val client = OkHttpClient()

    /**
     * Downloads an image from a URL and saves it to the device's public photo gallery.
     */
    suspend fun saveImageToGallery(context: Context, imageUrl: String, filename: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(imageUrl).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "Failed to download image: ${response.code}")
                    return@withContext false
                }
                val bytes = response.body?.bytes() ?: return@withContext false
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@withContext false
                
                return@withContext insertImageIntoGallery(context, bitmap, filename)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error saving image to gallery", e)
            false
        }
    }

    private fun insertImageIntoGallery(context: Context, bitmap: Bitmap, title: String): Boolean {
        val resolver = context.contentResolver
        val filename = "${title.take(30).replace(Regex("[^a-zA-Z0-9]"), "_")}_${System.currentTimeMillis()}.jpg"
        
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.TITLE, title)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DigitalArtGenerator")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        var imageUri: Uri? = null
        return try {
            imageUri = resolver.insert(imageCollection, contentValues) ?: return false
            resolver.openOutputStream(imageUri).use { outputStream ->
                if (outputStream == null) return false
                val success = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                if (!success) return false
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(imageUri, contentValues, null, null)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write image to MediaStore", e)
            imageUri?.let { uri ->
                try {
                    resolver.delete(uri, null, null)
                } catch (ex: Exception) {
                    Log.e(TAG, "Failed to clean up incomplete image", ex)
                }
            }
            false
        }
    }

    /**
     * Prepares a standard system Share intent to broadcast artwork prompts & links to other apps.
     */
    fun shareArtwork(context: Context, prompt: String, style: String, imageUrl: String) {
        val shareBody = "Look at this spectacular digital masterpiece I created using AI Art Generator!\n\n" +
                "🎨 Prompt: \"$prompt\"\n" +
                "🎭 Style: $style\n" +
                "🔗 View full resolution artwork: $imageUrl\n\n" +
                "Create yours too without any subscription or limits!"
        
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "My AI Art Creation")
            putExtra(Intent.EXTRA_TEXT, shareBody)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share Masterpiece via"))
    }
}
