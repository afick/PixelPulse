package hu.ait.pixelpulse.data

data class Post(
    var uid: String = "",
    var author: String = "",
    var caption: String = "",
    var imgUrl: String = "",
    var location: String = "",
    var camera: String = "",
    var shutterSpeed: String = "",
    var aperture: String = "",
    var iso: String = "",
)

data class Camera(
    var uid: String = "",
    var name: String = "",
    var brand: String = "",
    var model: String = "",
    var imgUrl: String = ""
)

data class Lens(
    var uid: String = "",
    var brand: String = "",
    var model: String = "",
    var focalLength: String = "",
    var aperture: String = ""
)
data class PostWithId(
    val postId: String,
    val post: Post
)