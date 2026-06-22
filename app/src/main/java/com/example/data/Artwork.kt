package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artworks")
data class Artwork(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val prompt: String,
    val originalPrompt: String = prompt,
    val style: String,
    val imageUrl: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isLiked: Boolean = false,
    val isCommunity: Boolean = false,
    val likesCount: Int = 0,
    val creatorName: String = "Creator",
    val aspectRatio: String = "1:1",
    val resolution: String = "1024x1024",
    val seed: Long = 0L
)
