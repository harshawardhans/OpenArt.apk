package com.example.data

import kotlinx.coroutines.flow.Flow

class ArtworkRepository(private val artworkDao: ArtworkDao) {
    val allArtworks: Flow<List<Artwork>> = artworkDao.getAllArtworks()
    val historyArtworks: Flow<List<Artwork>> = artworkDao.getHistoryArtworks()
    val communityArtworks: Flow<List<Artwork>> = artworkDao.getCommunityArtworks()

    suspend fun getArtworkById(id: Int): Artwork? = artworkDao.getArtworkById(id)

    suspend fun insertArtwork(artwork: Artwork): Long = artworkDao.insertArtwork(artwork)

    suspend fun updateArtwork(artwork: Artwork) = artworkDao.updateArtwork(artwork)

    suspend fun deleteArtwork(artwork: Artwork) = artworkDao.deleteArtwork(artwork)

    suspend fun clearHistory() = artworkDao.clearHistory()
}
