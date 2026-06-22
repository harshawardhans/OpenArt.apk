package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URLEncoder

@Database(entities = [Artwork::class], version = 1, exportSchema = false)
abstract class ArtworkDatabase : RoomDatabase() {
    abstract fun artworkDao(): ArtworkDao

    companion object {
        @Volatile
        private var INSTANCE: ArtworkDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ArtworkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ArtworkDatabase::class.java,
                    "artwork_database"
                )
                .addCallback(ArtworkDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class ArtworkDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val dao = database.artworkDao()
                    
                    // Curated High-Fidelity Masterpieces to Seed Community Gallery
                    val seedArtworks = listOf(
                        Artwork(
                            prompt = "A cosmic astronaut playing a neon purple guitar on a crescent moon",
                            originalPrompt = "A cosmic astronaut playing a neon purple guitar on a crescent moon, cyberpunk galaxy background, high resolution 3d render",
                            style = "3D Render",
                            imageUrl = "https://image.pollinations.ai/p/A_cosmic_astronaut_playing_a_neon_purple_guitar_on_a_crescent_moon_cyberpunk_galaxy_background_high_resolution_3d_render?width=1024&height=1024&nologo=true&seed=15234",
                            isCommunity = true,
                            likesCount = 345,
                            creatorName = "StellarAstro",
                            aspectRatio = "1:1",
                            resolution = "1024x1024",
                            seed = 15234L,
                            timestamp = System.currentTimeMillis() - 3600000 * 5 // 5 hours ago
                        ),
                        Artwork(
                            prompt = "A cozy fantasy treehouse library wrapped in glowing gold vines",
                            originalPrompt = "A cozy fantasy treehouse library wrapped in glowing gold vines, sunlight filtering through stained glass windows, digital watercolor illustration",
                            style = "Watercolor Art",
                            imageUrl = "https://image.pollinations.ai/p/A_cozy_fantasy_treehouse_library_wrapped_in_glowing_gold_vines_sunlight_filtering_through_stained_glass_windows_digital_watercolor_illustration?width=1024&height=1024&nologo=true&seed=42152",
                            isCommunity = true,
                            likesCount = 512,
                            creatorName = "ScribbleWitch",
                            aspectRatio = "1:1",
                            resolution = "1024x1024",
                            seed = 42152L,
                            timestamp = System.currentTimeMillis() - 3600000 * 12 // 12 hours ago
                        ),
                        Artwork(
                            prompt = "A magical iridescent crystal fox walking across a glowing bioluminescent stream",
                            originalPrompt = "A magical iridescent crystal fox walking across a glowing bioluminescent forest stream, fantasy oil painting style, hyper-detailed",
                            style = "Fantasy Illustration",
                            imageUrl = "https://image.pollinations.ai/p/A_magical_iridescent_crystal_fox_walking_across_a_glowing_bioluminescent_forest_stream_fantasy_oil_painting_style_hyper_detailed?width=1024&height=1024&nologo=true&seed=89110",
                            isCommunity = true,
                            likesCount = 289,
                            creatorName = "PixelSorcerer",
                            aspectRatio = "1:1",
                            resolution = "1024x1024",
                            seed = 89110L,
                            timestamp = System.currentTimeMillis() - 3600000 * 24 // 1 day ago
                        ),
                        Artwork(
                            prompt = "A futuristic retro-synthwave street with flying Delorean cars",
                            originalPrompt = "A futuristic retro-synthwave street with neon-lit skyscraper towers, flying Delorean cars, and a massive pink sunset, 80s aesthetic poster",
                            style = "Retro Synthwave",
                            imageUrl = "https://image.pollinations.ai/p/A_futuristic_retro_synthwave_street_with_neon_lit_skyscraper_towers_flying_Delorean_cars_and_a_massive_pink_sunset_80s_aesthetic_poster?width=1024&height=1024&nologo=true&seed=11152",
                            isCommunity = true,
                            likesCount = 421,
                            creatorName = "CyberGhost",
                            aspectRatio = "1:1",
                            resolution = "1024x1024",
                            seed = 11152L,
                            timestamp = System.currentTimeMillis() - 3600000 * 48 // 2 days ago
                        ),
                        Artwork(
                            prompt = "A majestic cloud palace with waterfalls cascading into the sky",
                            originalPrompt = "A majestic cloud palace with waterfalls cascading into the sky, soft pastel sunrise rays, highly detailed anime fantasy landscape art",
                            style = "Anime / Manga",
                            imageUrl = "https://image.pollinations.ai/p/A_majestic_cloud_palace_with_waterfalls_cascading_into_the_sky_soft_pastel_sunrise_rays_highly_detailed_anime_fantasy_landscape_art?width=1024&height=1024&nologo=true&seed=23955",
                            isCommunity = true,
                            likesCount = 684,
                            creatorName = "Dreamweaver",
                            aspectRatio = "1:1",
                            resolution = "1024x1024",
                            seed = 23955L,
                            timestamp = System.currentTimeMillis() - 3600000 * 72 // 3 days ago
                        )
                    )
                    dao.insertArtworks(seedArtworks)
                }
            }
        }
    }
}
