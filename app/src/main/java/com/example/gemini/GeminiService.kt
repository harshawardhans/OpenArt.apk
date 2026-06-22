package com.example.gemini

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val TAG = "GeminiService"
    private const val MODEL = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun enhancePrompt(prompt: String, style: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.e(TAG, "Gemini API Key is not configured.")
            return@withContext fallbackEnhancement(prompt, style)
        }

        val systemInstruction = "You are a master digital artist and prompt engineer. The user wants to generate an image based on their basic text and a style. " +
                "You must output ONLY a polished, expanded prompt optimized for Midjourney/Stable Diffusion (max 80 words) combining their idea and style with rich details. " +
                "Do NOT write any introduction, conversational text, quotes, or formatting. Just output the final prompt directly."

        val promptText = "Enhance this idea: '$prompt' in the artistic style of '$style'. Add lighting, mood, compositions, and beautiful details."

        try {
            // Build Gemini JSON request using standard JSONObject
            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply { put("text", promptText) })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                val systemInstructionObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    }
                    put("parts", partsArray)
                }
                put("systemInstruction", systemInstructionObj)

                val generationConfig = JSONObject().apply {
                    put("temperature", 0.7)
                }
                put("generationConfig", generationConfig)
            }

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = requestJson.toString().toRequestBody(mediaType)

            val urlWithKey = "$BASE_URL?key=$apiKey"
            val request = Request.Builder()
                .url(urlWithKey)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val bodyStr = response.body?.string() ?: ""
                    Log.e(TAG, "Gemini API Request failed: Code ${response.code}, Body: $bodyStr")
                    return@withContext fallbackEnhancement(prompt, style)
                }

                val responseBody = response.body?.string() ?: return@withContext fallbackEnhancement(prompt, style)
                val responseJson = JSONObject(responseBody)
                val candidates = responseJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val contentObj = candidate.optJSONObject("content")
                    if (contentObj != null) {
                        val parts = contentObj.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            if (!text.isNullOrBlank()) {
                                return@withContext text.trim()
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in enhancePrompt", e)
        }

        return@withContext fallbackEnhancement(prompt, style)
    }

    private fun fallbackEnhancement(prompt: String, style: String): String {
        // High quality manual enhancement if API is unavailable or offline
        val styleDetails = when (style) {
            "Anime / Manga" -> "highly detailed anime manga style, vibrant key visuals, beautiful soft studio lighting, gorgeous cel-shaded colors, masterpiece"
            "Cyberpunk" -> "futuristic cyberpunk aesthetic, dramatic neon glows, towering holo-billboards, wet asphalt reflecting pink and cyan light, highly detailed 8k cinematic"
            "Fantasy Illustration" -> "ethereal fantasy digital illustration, glowing magic particles, soft whimsical atmosphere, dynamic lighting, cinematic storybook realism"
            "Oil Painting" -> "classical fine art oil painting, visible canvas texture, rich palette knife brushstrokes, baroque dramatic chiaroscuro lighting, museum masterpiece"
            "3D Render" -> "ultra-realistic 3D octane render, Raytraced reflections, extreme material detail, subsurface scattering glass, ambient occlusions, 4k"
            "Minimalist Vector" -> "flat minimalist vector graphics design, bold solid shapes, retro negative space, aesthetic palette, clean crisp vectors, modern flat illustration"
            "Surrealism" -> "mind-bending surrealist art, dreamlike dreamscape scenery, Salvador Dali style physics, floating geometric crystals, intriguing visual metaphors"
            "Steampunk" -> "antique steampunk industrial design, elaborate brass clockworks, glowing copper steam pipes, atmospheric warm amber smoke, highly detailed mechanical details"
            "Watercolor Art" -> "delicate wet-on-wet watercolor painting, dripping color splashes, soft pastel blend, raw textured paper grain, minimalist artistic fine art"
            "Pixel Art" -> "cozy colorful 16-bit pixel art illustration, beautiful pixel grid alignment, retro game scene, vibrant lighting glows, aesthetic retro color scale"
            "Retro Synthwave" -> "80s outrun synthwave poster aesthetic, glowing wireframe grid vector, neon laser beams, futuristic wire sun sunset, cyberpunk nostalgic color theme"
            else -> "masterpiece quality, artistic fine touches, depth of field, incredible focus, masterpiece visual"
        }
        return "$prompt, $styleDetails"
    }
}
