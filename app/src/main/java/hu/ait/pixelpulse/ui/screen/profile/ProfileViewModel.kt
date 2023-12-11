package hu.ait.pixelpulse.ui.screen.profile

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import hu.ait.pixelpulse.data.Post
import hu.ait.pixelpulse.data.PostWithId
import hu.ait.pixelpulse.ui.screen.postupload.WritePostScreenViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.UUID

class ProfileViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    companion object {
        const val COLLECTION_POSTS = "posts"
    }

    var profileUiState: ProfileUIState by mutableStateOf(ProfileUIState.Init)

    fun getUserDisplayName(): String {
        return auth.currentUser!!.displayName ?: auth.currentUser!!.uid!!
    }

    fun getUserProfilePicUrl(): String {
        return auth.currentUser!!.photoUrl.toString()
    }

    fun updateDisplayName(newDisplayName: String) {
        auth.currentUser!!.updateProfile(
            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build()
        )
    }

    fun updateProfilePicUrl(newProfilePicUrl: String) {
        auth.currentUser!!.updateProfile(
            com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setPhotoUri(android.net.Uri.parse(newProfilePicUrl))
                .build()
        )
    }


    fun getUsersPosts() =
        callbackFlow {
            val snapshotListener =
                FirebaseFirestore.getInstance()
                    .collection(WritePostScreenViewModel.COLLECTION_POSTS)
                    .addSnapshotListener() { snapshot, e ->
                        val response = if (snapshot != null) {
                            val postList = snapshot.toObjects(Post::class.java)
                            val postWithIdList = mutableListOf<PostWithId>()

                            postList.forEachIndexed { index, post ->
                                postWithIdList.add(PostWithId(snapshot.documents[index].id, post))
                            }

                            Log.d("posts", auth.currentUser!!.uid)

                            // Create a copy of the list before applying retainAll
                            val postsForCurrentUser = postList.toList().filter { post ->
                                post.authorId == auth.currentUser!!.uid
                            }

//
//                            postList.retainAll {
//                                post -> post.authorId == auth.currentUser!!.uid
//                            }

                            ProfileUIState.Success(
                                postsForCurrentUser.map {
                                    PostWithId(
                                        snapshot.documents[postList.indexOf(
                                            it
                                        )].id, it
                                    )
                                }
                            )
                        } else {
                            ProfileUIState.Error(e?.message.toString())
                        }

                        trySend(response) // emit this value through the flow
                    }
            awaitClose {
                snapshotListener.remove()
            }

        }

    fun deletePost(postKey: String) {
        FirebaseFirestore.getInstance().collection(
            WritePostScreenViewModel.COLLECTION_POSTS
        ).document(postKey).delete()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun updateProfilePic(
        contentResolver: ContentResolver,
        imageUri: Uri
    ) {
        viewModelScope.launch {
            profileUiState = ProfileUIState.LoadingImageUpload

            val source = ImageDecoder.createSource(contentResolver, imageUri)
            val bitmap = ImageDecoder.decodeBitmap(source)

            val baos = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val imageInBytes = baos.toByteArray()

            // prepare the empty file in the cloud
            val storageRef = FirebaseStorage.getInstance().reference
            val newImage = URLEncoder.encode(
                UUID.randomUUID().toString(), "UTF-8"
            ) + ".jpg"
            val newImagesRef = storageRef.child("images/$newImage")

            // upload the jpeg byte array to the created empty file
            newImagesRef.putBytes(imageInBytes)
                .addOnFailureListener { e ->
                    profileUiState =
                        ProfileUIState.ErrorDuringImageUpload(e.message)
                    Log.d("upload error", "uploadPostImage: $e")
                }.addOnSuccessListener {
                    profileUiState = ProfileUIState.ImageUploadSuccess


                    newImagesRef.downloadUrl.addOnCompleteListener(
                        object : OnCompleteListener<Uri> {
                            override fun onComplete(task: Task<Uri>) {
                                // the public URL of the image is: task.result.toString()
                                updateProfilePicUrl(task.result.toString())
                            }
                        })
                }
        }
    }
}


sealed interface ProfileUIState {
    object Init : ProfileUIState
    data class Success(val postList: List<PostWithId>) : ProfileUIState
    data class Error(val error: String?) : ProfileUIState
    object LoadingImageUpload : ProfileUIState
    data class ErrorDuringImageUpload(val error: String?) : ProfileUIState
    object ImageUploadSuccess : ProfileUIState


}