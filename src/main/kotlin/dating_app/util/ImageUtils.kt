package dating_app.util

import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO

/**
 * Utility class for image-related operations.
 */
object ImageUtils {
    
    /**
     * Loads an image from a URL with proper error handling and timeouts.
     * 
     * @param imageUrl The URL of the image to load
     * @param userId The ID of the user (for error reporting)
     * @return The loaded BufferedImage or null if loading fails
     * @throws IOException if there's an error loading the image
     * @throws IllegalStateException if the URL is invalid or points to a non-image resource
     */
    @Throws(IOException::class, IllegalStateException::class)
    fun loadImageFromUrl(imageUrl: String, userId: String): BufferedImage {
        if (imageUrl.isBlank()) {
            throw IllegalStateException("Profile picture URL is empty for user: $userId")
        }

        val url = URL(imageUrl)
        val connection = url.openConnection().apply {
            connectTimeout = 10000
            readTimeout = 15000
            // Add proper headers to mimic a browser request
            setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            setRequestProperty("Accept", "image/webp,image/apng,image/*,*/*")
        }

        return connection.getInputStream().use { inputStream ->
            if (!connection.contentType?.startsWith("image/")!!) {
                throw IllegalStateException("URL does not point to a valid image. Content-Type: ${connection.contentType}")
            }
            
            ImageIO.read(inputStream)?.also { image ->
                // Validate that we actually got an image
                if (image.width <= 0 || image.height <= 0) {
                    throw IOException("Invalid image dimensions: ${image.width}x${image.height}")
                }
            } ?: throw IOException("Failed to decode image from URL: $imageUrl")
        }
    }
}
