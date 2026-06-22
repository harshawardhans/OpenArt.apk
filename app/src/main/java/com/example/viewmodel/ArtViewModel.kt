package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Artwork
import com.example.data.ArtworkDatabase
import com.example.data.ArtworkRepository
import com.example.gemini.GeminiService
import com.example.utils.ImageUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.net.URLEncoder
import kotlin.random.Random

sealed interface GenerationState {
    object Idle : GenerationState
    object Loading : GenerationState
    data class Success(val artwork: Artwork) : GenerationState
    data class Error(val message: String) : GenerationState
}

class ArtViewModel(
    application: Application,
    private val repository: ArtworkRepository
) : AndroidViewModel(application) {

    // Input States
    private val _prompt = MutableStateFlow("")
    val prompt: StateFlow<String> = _prompt.asStateFlow()

    private val _selectedStyle = MutableStateFlow("Anime / Manga")
    val selectedStyle: StateFlow<String> = _selectedStyle.asStateFlow()

    private val _aspectRatio = MutableStateFlow("1:1") // "1:1", "16:9", "9:16", "3:4"
    val aspectRatio: StateFlow<String> = _aspectRatio.asStateFlow()

    private val _isEnhancing = MutableStateFlow(false)
    val isEnhancing: StateFlow<Boolean> = _isEnhancing.asStateFlow()

    // Active Generation State
    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    // Database Flows
    val historyList: StateFlow<List<Artwork>> = repository.historyArtworks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val communityList: StateFlow<List<Artwork>> = repository.communityArtworks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Style List
    val stylesList = listOf(
        "Anime / Manga",
        "Cyberpunk",
        "Fantasy Illustration",
        "Oil Painting",
        "3D Render",
        "Minimalist Vector",
        "Surrealism",
        "Steampunk",
        "Watercolor Art",
        "Pixel Art",
        "Retro Synthwave"
    )

    fun onPromptChange(newPrompt: String) {
        _prompt.value = newPrompt
    }

    fun onStyleChange(newStyle: String) {
        _selectedStyle.value = newStyle
    }

    fun onAspectRatioChange(newRatio: String) {
        _aspectRatio.value = newRatio
    }

    /**
     * Call Gemini to expand the prompt into a rich detailed digital art prompt.
     */
    fun enhancePromptWithGemini() {
        val currentPrompt = _prompt.value
        if (currentPrompt.isBlank()) {
            Toast.makeText(getApplication(), "Enter a prompt first to enhance!", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _isEnhancing.value = true
            val enhanced = GeminiService.enhancePrompt(currentPrompt, _selectedStyle.value)
            _prompt.value = enhanced
            _isEnhancing.value = false
            Toast.makeText(getApplication(), "Prompt enhanced with Gemini AI!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Builds Pollinations URL and launches image generation.
     */
    fun generateArtwork() {
        val userPrompt = _prompt.value
        if (userPrompt.isBlank()) {
            Toast.makeText(getApplication(), "Please enter an art prompt", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _generationState.value = GenerationState.Loading

            try {
                val seed = Random.nextLong(100000, 9999999)
                val cleanStyle = _selectedStyle.value
                val cleanRatio = _aspectRatio.value
                
                // Set resolution dimensions based on ratio
                val (w, h) = when (cleanRatio) {
                    "16:9" -> Pair(1280, 720)
                    "9:16" -> Pair(720, 1280)
                    "3:4" -> Pair(768, 1024)
                    else -> Pair(1024, 1024)
                }

                // Append style tag in prompt to ensure beautiful pollinations outputs
                val styleModifier = when (cleanStyle) {
                    "Anime / Manga" -> "anime key visual, beautiful detailed drawing, japanese cel-animation palette"
                    "Cyberpunk" -> "cyberpunk neon grid, wet glass reflections, high-tech cybernetic detailing, pink and cyan hues"
                    "Fantasy Illustration" -> "ethereal high-fantasy digital illustration, glowing dust particles, mystical forest stream, magical atmosphere"
                    "Oil Painting" -> "classical fine art oil painting, deep visible canvas texture, dramatic lighting"
                    "3D Render" -> "3D octane render, photorealistic glass spheres, ambient glow, hyperrealistic cinematic details"
                    "Minimalist Vector" -> "minimalist vector art, clean flat solid geometry, modern aesthetic palette, corporate illustration style"
                    "Surrealism" -> "surrealist dreamscape layout, salvador dali dripping clock vibe, flying fish, dream geometry"
                    "Steampunk" -> "warm glowing amber steampunk apparatus, brass mechanisms, leather, cogs and steam vents, ornate details"
                    "Watercolor Art" -> "flowing wet watercolor illustration, bright color splashes, paper canvas aesthetic"
                    "Pixel Art" -> "cozy cute retro 16-bit pixel art, vibrant gaming scenery, rich sprites"
                    "Retro Synthwave" -> "80s outrun synthwave sun, laser grid horizon, neon sportscar, magenta sky"
                    else -> ""
                }

                val finalAestheticPrompt = "$userPrompt, $styleModifier, high resolution, award winning masterpiece, perfect composition"
                val encodedPrompt = URLEncoder.encode(finalAestheticPrompt, "UTF-8")
                val finalImageUrl = "https://image.pollinations.ai/p/$encodedPrompt?width=$w&height=$h&nologo=true&seed=$seed"

                val newArtwork = Artwork(
                    prompt = userPrompt,
                    originalPrompt = finalAestheticPrompt,
                    style = cleanStyle,
                    imageUrl = finalImageUrl,
                    isLiked = false,
                    isCommunity = false,
                    likesCount = 0,
                    aspectRatio = cleanRatio,
                    resolution = "${w}x${h}",
                    seed = seed,
                    timestamp = System.currentTimeMillis()
                )

                // Save to historical DB
                val insertedId = repository.insertArtwork(newArtwork)
                val savedArtwork = newArtwork.copy(id = insertedId.toInt())

                _generationState.value = GenerationState.Success(savedArtwork)
                Toast.makeText(getApplication(), "Art generated successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                _generationState.value = GenerationState.Error("Error generating art: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Toggle like state of any artwork.
     */
    fun toggleLike(artwork: Artwork) {
        viewModelScope.launch {
            val updated = artwork.copy(
                isLiked = !artwork.isLiked,
                likesCount = if (artwork.isLiked) artwork.likesCount - 1 else artwork.likesCount + 1
            )
            repository.updateArtwork(updated)
        }
    }

    /**
     * Publishes an artwork to the community gallery.
     */
    fun publishToCommunity(artwork: Artwork) {
        viewModelScope.launch {
            val creators = listOf("NeonKnight", "AI_Alchemist", "DigitalValkyrie", "ChromaGlow", "InkVoyager", "SynthMuse")
            val randomCreator = creators[Random.nextInt(creators.size)]
            
            // Create a cloned version that is flagged as community-visible
            val communityArt = artwork.copy(
                id = 0, // Reset id to insert as a fresh new card for community
                isCommunity = true,
                likesCount = Random.nextInt(10, 85),
                creatorName = randomCreator,
                timestamp = System.currentTimeMillis()
            )
            
            repository.insertArtwork(communityArt)
            Toast.makeText(getApplication(), "Published to Community Gallery!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteArtwork(artwork: Artwork) {
        viewModelScope.launch {
            repository.deleteArtwork(artwork)
            Toast.makeText(getApplication(), "Deleted creation", Toast.LENGTH_SHORT).show()
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            Toast.makeText(getApplication(), "History cleared!", Toast.LENGTH_SHORT).show()
        }
    }

    fun exportToDeviceGallery(context: Context, artwork: Artwork) {
        viewModelScope.launch {
            Toast.makeText(context, "Downloading PNG artwork...", Toast.LENGTH_SHORT).show()
            val filename = artwork.prompt.take(15) + "_creation"
            val success = ImageUtils.saveImageToGallery(context, artwork.imageUrl, filename)
            if (success) {
                Toast.makeText(context, "Downloaded successfully as PNG to Pictures/DigitalArtGenerator!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Download failed. Please check internet connection.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun shareToSocial(context: Context, artwork: Artwork) {
        ImageUtils.shareArtwork(context, artwork.prompt, artwork.style, artwork.imageUrl)
    }

    fun resetState() {
        _generationState.value = GenerationState.Idle
    }
}

/**
 * Factory class to instantiate the ViewModel with proper Context-initialized repositories.
 */
class ArtViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtViewModel::class.java)) {
            val application = context.applicationContext as Application
            // Build database with a flow coroutine scope
            val database = ArtworkDatabase.getDatabase(application, kotlinx.coroutines.GlobalScope)
            val repository = ArtworkRepository(database.artworkDao())
            return ArtViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
