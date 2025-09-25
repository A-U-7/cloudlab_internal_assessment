package dating_app.model

import dating_app.service.ProfilePictureService
import java.awt.image.BufferedImage

/**
 * Represents a user in a dating application with lazy-loaded profile picture.
 *
 * This class handles the expensive operation of fetching profile pictures
 * only when explicitly requested, optimizing memory and network usage.
 *
 * @property userId Unique identifier for the user
 * @property userName Display name of the user
 * @property profilePictureUrl URL to fetch the profile picture from
 * @property bio User's profile description
 */
class DatingUser(
    val userId: String,
    val userName: String,
    private val profilePictureUrl: String,
    val bio: String = ""
) {
    private val _profilePicture: BufferedImage? by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        try {
            ProfilePictureService.loadProfilePictureSafely(userId, profilePictureUrl, userName)
        } catch (e: Exception) {
            null
        }
    }

    val profilePicture: BufferedImage?
        get() = _profilePicture

    val hasProfilePicture: Boolean
        get() = _profilePicture != null

    override fun toString(): String {
        return "dating_app.model.DatingUser(userId='$userId', userName='$userName', hasPicture=$hasProfilePicture)"
    }

    companion object {
        fun createValidatedUser(
            userId: String,
            userName: String,
            profilePictureUrl: String,
            bio: String = ""
        ): DatingUser {
            require(userId.isNotBlank()) { "User ID cannot be blank" }
            require(userName.isNotBlank()) { "User name cannot be blank" }

            return DatingUser(userId, userName, profilePictureUrl, bio)
        }
    }
}

fun DatingUser.getProfileSummary(): String {
    return buildString {
        append(userName)
        if (bio.isNotBlank()) {
            append(" - $bio")
        }
        if (hasProfilePicture) {
            append(" clicked ! ")
        }
    }
}
