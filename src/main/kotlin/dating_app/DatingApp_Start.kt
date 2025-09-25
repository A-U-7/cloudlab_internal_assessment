package dating_app

import dating_app.model.DatingUser
import dating_app.model.getProfileSummary
import java.io.File
import javax.imageio.ImageIO

fun main() {
    println("Dating App")


    val user = DatingUser(
        userId = "1",
        userName = "Random",
        profilePictureUrl = "https://images.pexels.com/photos/1181676/pexels-photo-1181676.jpeg",
        bio = "Software developer who is working on dating app "
    )

    // Display user information
    println("User: ${user.userName}")
    println("Bio: ${user.bio}")

    // Access the profile picture (lazy loading happens here)
    println("Loading profile picture...")
    val profilePicture = user.profilePicture

    if (user.hasProfilePicture) {
        println("Successfully profile picture loaded!")
        println("   - Width: ${profilePicture?.width}px")
        println("   - Height: ${profilePicture?.height}px")
        

        try {
            val outputFile = File("profile_picture.jpg")
            ImageIO.write(profilePicture, "jpg", outputFile)
            println("   - Profile picture saved to: ${outputFile.absolutePath}")
        } catch (e: Exception) {
            println("Could not save profile picture: ${e.message}")
        }
    } else {
        println("Could not load profile picture")
    }


    println("Profile Summary:")
    println(user.getProfileSummary())
}