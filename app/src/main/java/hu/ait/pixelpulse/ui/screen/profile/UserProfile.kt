package hu.ait.pixelpulse.ui.screen.profile

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import hu.ait.pixelpulse.R
import hu.ait.pixelpulse.data.Post
import hu.ait.pixelpulse.ui.screen.feed.MainScreenUIState
import hu.ait.pixelpulse.ui.screen.feed.PostCard

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(
    profileViewModel: ProfileViewModel = ProfileViewModel()
) {

    var editing by remember {
        mutableStateOf(false)
    }

    val postListState = profileViewModel.getUsersPosts().collectAsState(
        initial = ProfileUIState.Init
    )
    val context = LocalContext.current
    var image by remember {
        mutableStateOf(profileViewModel.getUserProfilePicUrl())
    }
    var displayName by remember {
        mutableStateOf(profileViewModel.getUserDisplayName())
    }

    var newImage by remember {
        mutableStateOf(false)
    }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    Column(
        modifier = Modifier.padding(bottom = 56.dp)
    ) {
        TopAppBar(title = { Text(text = "Profile") },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            actions = {
                IconButton(onClick = { editing = !editing }) {
                    Icon(
                        imageVector = Icons.Filled.Edit, contentDescription = "Edit Profile"
                    )
                }
            })


        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    OutlinedTextField(value = displayName,
                        onValueChange = { displayName = it },
                        modifier = Modifier.size(width = 160.dp, height = 60.dp),
                        readOnly = !editing,
                        label = { Text(text = "Username") })
                }
                Spacer(modifier = Modifier.fillMaxWidth(0.5f))
                Box(modifier = if (editing) Modifier.clickable {

                        launcher.launch("image/*")
                        newImage = true

                } else Modifier) {
                    if (newImage) {
                        AsyncImage(
                            model = imageUri.toString(),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(12.dp)
                        )
                    } else if (image != "null") {
                        AsyncImage(
                            model = image,
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(12.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.blankprofile),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(100.dp)
                                .padding(12.dp)
                        )
                    }
                }
            }
            if (editing) {
                Button(onClick = {
                    profileViewModel.updateDisplayName(displayName)
                    if (newImage) {
                        profileViewModel.updateProfilePic(context.contentResolver, imageUri!!)
                        image = imageUri.toString()
                    }
                    editing = false
                },
                    modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(text = "Save Changes")
                }
            }
        }
        if (postListState.value == ProfileUIState.Init) {
            Text(text = "No posts yet...")
        } else if (postListState.value is ProfileUIState.Success) {
            LazyColumn {
                items((postListState.value as ProfileUIState.Success).postList) { postItem ->
                    ProfilePostCard(
                        post = postItem.post,
                        onRemoveItem = { profileViewModel.deletePost(postItem.postId) },
                    )
                }
            }
        }
    }


}


@Composable
fun ProfilePostCard(
    post: Post,
    onRemoveItem: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column (
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = post.author, fontWeight = FontWeight.Medium,
                    fontSize = 20.sp
                )
                Text(text = post.location)
            }
            Column (
                horizontalAlignment = Alignment.End
            ){
                Text(text = post.camera, fontWeight = FontWeight.Medium)
                Text(text = "SS: ${post.shutterSpeed}")
                Text(text = "ISO: ${post.iso}")
                Text(text = "Aperture: ${post.aperture}")
            }
        }



        // Display the image
        if (post.imgUrl != "") {
            AsyncImage(
                model = post.imgUrl,
                contentDescription = "selected image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
        }
        TextButton(onClick = { onRemoveItem() },
            colors = ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )) {
            Text(text = "Delete Post")
        }
    }

}