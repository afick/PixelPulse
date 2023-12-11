package hu.ait.pixelpulse.ui.screen.profile

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileViewModel : ViewModel() {
    private var auth: FirebaseAuth = Firebase.auth

    fun getUserDisplayName(): String {
        return auth.currentUser!!.displayName?: auth.currentUser!!.uid!!
    }

    fun getUserProfilePicUrl(): String {
        return auth.currentUser!!.photoUrl.toString()
    }

    fun updateDisplayName(newDisplayName: String) {
        auth.currentUser!!.updateProfile(com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setDisplayName(newDisplayName)
            .build())
    }

    fun updateProfilePicUrl(newProfilePicUrl: String) {
        auth.currentUser!!.updateProfile(com.google.firebase.auth.UserProfileChangeRequest.Builder()
            .setPhotoUri(android.net.Uri.parse(newProfilePicUrl))
            .build())
    }
}