package hu.ait.pixelpulse.ui.screen.postupload

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

@Composable
fun PostUploadScreen () {
    var postCaption by rememberSaveable { mutableStateOf("") }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }



}