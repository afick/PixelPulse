package hu.ait.pixelpulse.ui.screen.feed
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import hu.ait.pixelpulse.data.Post
import hu.ait.pixelpulse.data.PostWithId
import hu.ait.pixelpulse.ui.screen.postupload.WritePostScreenViewModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class FeedScreenViewModel : ViewModel() {
    var currentUserId: String
    init {
        currentUserId = Firebase.auth.currentUser!!.uid
    }


    private val db = FirebaseFirestore.getInstance()

    fun getUserEmailByUid(uid: String): LiveData<String> {
        val emailLiveData = MutableLiveData<String>()

        // Assuming 'users' is your collection where user data is stored
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // Assuming 'email' is the field where the user's email is stored
                    val email = document.getString("email")
                    email?.let {
                        emailLiveData.value = it
                    }
                } else {
                    // Handle the case where the user doesn't exist
                    emailLiveData.value = "Email not found"
                }
            }
            .addOnFailureListener {
                // Handle any errors here
                emailLiveData.value = "Error fetching email"
            }

        return emailLiveData
    }

    fun deletePost(postKey: String) {
        FirebaseFirestore.getInstance().collection(
            WritePostScreenViewModel.COLLECTION_POSTS
        ).document(postKey).delete()
    }

    fun postsList() = callbackFlow {
        val snapshotListener =
            FirebaseFirestore.getInstance().collection(WritePostScreenViewModel.COLLECTION_POSTS)
                .addSnapshotListener() { snapshot, e ->
                    val response = if (snapshot != null) {
                        val postList = snapshot.toObjects(Post::class.java)
                        val postWithIdList = mutableListOf<PostWithId>()

                        postList.forEachIndexed { index, post ->
                            postWithIdList.add(PostWithId(snapshot.documents[index].id, post))
                        }

                        MainScreenUIState.Success(
                            postWithIdList
                        )
                    } else {
                        MainScreenUIState.Error(e?.message.toString())
                    }

                    trySend(response) // emit this value through the flow
                }
        awaitClose {
            snapshotListener.remove()
        }
    }
}


sealed interface MainScreenUIState {
    object Init : MainScreenUIState
    data class Success(val postList: List<PostWithId>) : MainScreenUIState
    data class Error(val error: String?) : MainScreenUIState
}