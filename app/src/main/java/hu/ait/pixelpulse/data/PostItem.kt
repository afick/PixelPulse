package hu.ait.pixelpulse.data

data class Post(
    var uid: String = "",
    var authorId: String = "",
    var author: String = "",
    var caption: String = "",
    var imgUrl: String = "",
    var location: String = "",
    var camera: String = "",
    var shutterSpeed: String = "",
    var aperture: String = "",
    var iso: String = "",
)

data class PostWithId(
    val postId: String,
    val post: Post
)