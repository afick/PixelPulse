package hu.ait.pixelpulse.ui.screen.postupload

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import hu.ait.pixelpulse.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PostUploadScreen (
    viewModel: WritePostScreenViewModel = viewModel(),
    navController: NavController
) {
    var postCaption by rememberSaveable { mutableStateOf("") }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var location by rememberSaveable {
        mutableStateOf("")
    }

    val bitmap = remember { mutableStateOf<Bitmap?>(null) }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract =
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        imageUri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
            bitmap.value = ImageDecoder.decodeBitmap(source)
        }

        if (imageUri == null) {
            Spacer(modifier = Modifier.height(120.dp))
            Image(painter = painterResource(id = R.drawable.imgupload),
                contentDescription = "upload image",
                modifier = Modifier
                    .size(150.dp)
                    .clickable { launcher.launch("image/*") })
            Text(text = "Upload Image",
                modifier = Modifier
                    .clickable { launcher.launch("image/*") })
        } else {
            AsyncImage(
                model = imageUri,
                contentDescription = "Uploaded Image",
                modifier = Modifier.fillMaxWidth()
            )
            TextButton(onClick = { launcher.launch("image/*") }) {
                Text(text = "Choose different image")
            }
        }
        OutlinedTextField(value = postCaption, onValueChange = {postCaption = it},
            label = { Text(text = "Post Caption") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )
        OutlinedTextField(value = location, onValueChange = {location = it},
            label = { Text(text = "Location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        Button(onClick = {
            if (imageUri != null) {
                viewModel
                    .uploadPostImage(
                        context.contentResolver,
                        imageUri!!,
                        postCaption,
                        location
                    )
            }
        }) {
            Text(text = "Upload Post")
        }

        when (viewModel.writePostUiState) {
            is WritePostUiState.LoadingPostUpload -> CircularProgressIndicator()
            is WritePostUiState.PostUploadSuccess -> {
                Text(text = "Post uploaded.")
            }
            is WritePostUiState.ErrorDuringPostUpload ->
                Text(text =
                "${(viewModel.writePostUiState as WritePostUiState.ErrorDuringPostUpload).error}")

            is WritePostUiState.LoadingImageUpload -> CircularProgressIndicator()
            is WritePostUiState.ImageUploadSuccess -> {
                Text(text = "Image uploaded, starting post upload.")
            }
            is WritePostUiState.ErrorDuringImageUpload ->
                Text(text = "${(viewModel.writePostUiState as WritePostUiState.ErrorDuringImageUpload).error}")
            is WritePostUiState.NavigateToNextScreen -> {
                navController.navigateUp()
            }
            else -> {}
        }
    }
}

class ComposeFileProvider : FileProvider(
    R.xml.filepaths
) {
    companion object {
        fun getImageUri(context: Context): Uri {
            val directory = File(context.cacheDir, "images")
            directory.mkdirs()
            val file = File.createTempFile(
                "selected_image_",
                ".jpg",
                directory,
            )
            val authority = context.packageName + ".fileprovider"
            return getUriForFile(
                context,
                authority,
                file,
            )
        }
    }
}