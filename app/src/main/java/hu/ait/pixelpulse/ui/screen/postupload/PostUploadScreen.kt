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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
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
fun PostUploadScreen(
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

    var camera by remember{
        mutableStateOf("")
    }

    var ss by remember {
        mutableStateOf("Auto")
    }

    var iso by remember {
        mutableStateOf("Auto")
    }

    var aperture by remember {
        mutableStateOf("Auto")
    }

    var ssExpanded by remember {
        mutableStateOf(false)
    }
    var isoExpanded by remember {
        mutableStateOf(false)
    }
    var apertureExpanded by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        imageUri?.let {
            val source = ImageDecoder.createSource(context.contentResolver, imageUri!!)
            bitmap.value = ImageDecoder.decodeBitmap(source)
        }

        if (imageUri == null) {
            Spacer(modifier = Modifier.height(40.dp))
            Column(
                modifier = Modifier
                    .clickable { launcher.launch("image/*") },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.imgupload),
                    contentDescription = "upload image",
                    modifier = Modifier
                        .size(150.dp)
                )
                Text(text = "Upload Image")
                Spacer(modifier = Modifier.height(40.dp))
            }

        } else {
            Column(
                modifier = Modifier
                    .clickable { launcher.launch("image/*") }
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Uploaded Image",
                    modifier = Modifier
                        .size(220.dp)
                        .padding(12.dp)
                )
                Text(text = "Choose different image")
            }
        }
        OutlinedTextField(
            value = postCaption, onValueChange = { postCaption = it },
            label = { Text(text = "Caption") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        OutlinedTextField(
            value = location, onValueChange = { location = it },
            label = { Text(text = "Location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )
        OutlinedTextField(
            value = camera, onValueChange = { camera = it },
            label = { Text(text = "Camera") },
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        )
        ExposedDropdownMenuBox(expanded = isoExpanded, onExpandedChange = { isoExpanded = it }) {
            TextField(
                value = iso, onValueChange = {},
                label = { Text(text = "ISO") },
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
                    .menuAnchor().align(Alignment.CenterHorizontally), // TODO: Alignment doesnt work
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isoExpanded)
                },

                )
            ExposedDropdownMenu(
                expanded = isoExpanded,
                onDismissRequest = { isoExpanded = false }) {

                DropdownMenuItem(
                    text = {
                        Text(text = "Auto")
                    },
                    onClick = {
                        iso = "Auto"
                        isoExpanded = false
                    }
                )
                for (i in listOf(50, 100, 200, 400, 800, 1600, 3200, 6400, 12800, 25600)) {
                    DropdownMenuItem(
                        text = {
                            Text(text = i.toString())
                        },
                        onClick = {
                            iso = i.toString()
                            isoExpanded = false
                        }
                    )
                }
            }
        }
        ExposedDropdownMenuBox(expanded = ssExpanded, onExpandedChange = { ssExpanded = it }) {
            TextField(
                value = ss, onValueChange = {},
                label = { Text(text = "Shutter Speed") },
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 16.dp).fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = ssExpanded)
                },

                )
            ExposedDropdownMenu(expanded = ssExpanded, onDismissRequest = { ssExpanded = false }) {

                DropdownMenuItem(
                    text = {
                        Text(text = "Auto")
                    },
                    onClick = {
                        ss = "Auto"
                        ssExpanded = false
                    }
                )
                for (i in listOf(
                    2,
                    4,
                    8,
                    10,
                    30,
                    40,
                    50,
                    80,
                    100,
                    125,
                    200,
                    250,
                    400,
                    500,
                    800,
                    1000,
                    1200,
                    1600,
                    2000,
                    2500,
                    3000,
                    4000
                )) {
                    DropdownMenuItem(
                        text = {
                            Text(text = "1/$i")
                        },
                        onClick = {
                            ss = "1/$i"
                            ssExpanded = false
                        }
                    )
                }
            }
        }
        ExposedDropdownMenuBox(
            expanded = apertureExpanded,
            onExpandedChange = { apertureExpanded = it }) {
            TextField(
                value = aperture, onValueChange = {},
                label = { Text(text = "Aperture") },
                modifier = Modifier
                    .padding(horizontal = 12.dp).fillMaxWidth()
                    .menuAnchor(),
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = isoExpanded)
                },

                )
            ExposedDropdownMenu(
                expanded = apertureExpanded,
                onDismissRequest = { apertureExpanded = false }) {

                DropdownMenuItem(
                    text = {
                        Text(text = "Auto")
                    },
                    onClick = {
                        aperture = "Auto"
                        apertureExpanded = false
                    }
                )
                for (i in listOf(1.4, 1.8, 2.0, 2.8, 4.0, 5.6, 8, 11, 14, 18, 22)) {
                    DropdownMenuItem(
                        text = {
                            Text(text = "F/$i")
                        },
                        onClick = {
                            aperture = "F/$i"
                            apertureExpanded = false
                        }
                    )
                }
            }
        }


        Button(
            onClick = {

                if (imageUri != null) {
                    viewModel
                        .uploadPostImage(
                            context.contentResolver,
                            imageUri!!,
                            postCaption,
                            location,
                            camera = if (camera != "") camera else "Unknown",
                            shutterSpeed = ss,
                            iso = iso,
                            aperture = aperture
                        )
                }
            },
            modifier = Modifier.padding(12.dp)
        ) {
            Text(text = "Upload Post")
        }

        when (viewModel.writePostUiState) {
            is WritePostUiState.LoadingPostUpload -> CircularProgressIndicator()
            is WritePostUiState.PostUploadSuccess -> {
                Text(text = "Post uploaded.")
            }

            is WritePostUiState.ErrorDuringPostUpload ->
                Text(
                    text =
                    "${(viewModel.writePostUiState as WritePostUiState.ErrorDuringPostUpload).error}"
                )

            is WritePostUiState.LoadingImageUpload -> CircularProgressIndicator()
            is WritePostUiState.ImageUploadSuccess -> {
                Text(text = "Image uploaded, starting post upload.")
            }

            is WritePostUiState.ErrorDuringImageUpload ->
                Text(text = "${(viewModel.writePostUiState as WritePostUiState.ErrorDuringImageUpload).error}")

            is WritePostUiState.NavigateToNextScreen -> {
                navController.navigate("feed")
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