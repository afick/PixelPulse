package hu.ait.pixelpulse.ui.screen.postupload

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import hu.ait.pixelpulse.R

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun PostUploadScreen () {
    var postCaption by rememberSaveable { mutableStateOf("") }
    var imageUri by remember {
        mutableStateOf<Uri?>(null)
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
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

//        bitmap.value?.let {
//            btm -> Image(
//                bitmap = btm.asImageBitmap(),
//                contentDescription = "Uploaded Image",
//                modifier = Modifier
//                    .size(200.dp)
//                    .padding(10.dp)
//            )
//        }

        Spacer(modifier = Modifier.height(12.dp))
//
//        Button(onClick = { launcher.launch("image/*") }) {
//            Text(text = "Upload Image")
//        }

    }







}