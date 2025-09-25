package dating_app.service

import dating_app.util.ImageUtils
import java.awt.image.BufferedImage

/**
 * Service responsible for loading and caching profile pictures.
 */
object ProfilePictureService {
    private val imageCache = mutableMapOf<String, BufferedImage>()
    
    /**
     * Safely loads a profile picture with error handling and caching.
     */
    fun loadProfilePictureSafely(
        userId: String,
        profilePictureUrl: String,
        userName: String
    ): BufferedImage? {
        return try {
            // Check cache first
            imageCache[userId]?.let { return it }
            
            // Try loading from network
            val image = ImageUtils.loadImageFromUrl(profilePictureUrl, userId)
            image?.let { imageCache[userId] = it }
            image
        } catch (e: Exception) {
            handlePictureLoadError(e, userId, userName, profilePictureUrl)
            null
        }
    }
    
    private fun handlePictureLoadError(
        exception: Exception,
        userId: String,
        userName: String,
        profilePictureUrl: String
    ) {
        when (exception) {
            is java.io.IOException -> {
                println("Network error loading profile picture for $userName: ${exception.message}")
            }
            is IllegalStateException -> {
                println("Configuration error for user $userId: ${exception.message}")
            }
            is SecurityException -> {
                println("Security related exception loading picture for $userName: ${exception.message}")
            }
            else -> {
                println("Unexpected error loading profile picture: ${exception.javaClass.simpleName} - ${exception.message}")
            }
        }
        
        logPictureLoadFailure(exception, userId, userName, profilePictureUrl)
    }
    
    private fun logPictureLoadFailure(
        exception: Exception,
        userId: String,
        userName: String,
        profilePictureUrl: String
    ) {
        println(
            """
            Failed to load profile picture for user:
            - User ID: $userId
            - Name: $userName
            - URL: $profilePictureUrl
            - Error: ${exception.javaClass.simpleName}
            - Message: ${exception.message}
            - Timestamp: ${System.currentTimeMillis()}
            """.trimIndent()
        )
    }
}
