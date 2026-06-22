package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtworkDao {
    @Query("SELECT * FROM artworks ORDER BY timestamp DESC")
    fun getAllArtworks(): Flow<List<Artwork>>

    @Query("SELECT * FROM artworks WHERE isCommunity = 0 ORDER BY timestamp DESC")
    fun getHistoryArtworks(): Flow<List<Artwork>>

    @Query("SELECT * FROM artworks WHERE isCommunity = 1 ORDER BY likesCount DESC, timestamp DESC")
    fun getCommunityArtworks(): Flow<List<Artwork>>

    @Query("SELECT * FROM artworks WHERE id = :id LIMIT 1")
    suspend fun getArtworkById(id: Int): Artwork?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtwork(artwork: Artwork): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtworks(artworks: List<Artwork>)

    @Update
    suspend fun updateArtwork(artwork: Artwork)

    @Delete
    suspend fun deleteArtwork(artwork: Artwork)

    @Query("DELETE FROM artworks WHERE isCommunity = 0")
    suspend fun clearHistory()
}
