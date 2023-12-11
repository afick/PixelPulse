package hu.ait.pixelpulse.ui.screen.profile

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import hu.ait.pixelpulse.R
import hu.ait.pixelpulse.ui.screen.PostCard

@RequiresApi(Build.VERSION_CODES.P)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(
    profileViewModel: ProfileViewModel = ProfileViewModel(),
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
        TopAppBar(title = {
            Text(
                text = stringResource(R.string.profile_txt),
                fontWeight = FontWeight.SemiBold
            )
        },
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor =
                MaterialTheme.colorScheme.onTertiaryContainer,
                titleContentColor = MaterialTheme.colorScheme.tertiaryContainer,
                actionIconContentColor = MaterialTheme.colorScheme.tertiaryContainer
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
                        label = { Text(text = stringResource(R.string.username_txt)) })
                }
                Spacer(modifier = Modifier.fillMaxWidth(0.5f))
                Box(modifier = if (editing) Modifier.clickable {

                    launcher.launch("image/*")
                    newImage = true

                } else Modifier) {
                    if (newImage && imageUri != null) {
                        AsyncImage(
                            model = imageUri.toString(),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),

                            )
                    } else if (image != "null") {
                        AsyncImage(
                            model = image,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)

                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.blankprofile),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)

                        )
                    }
                }
            }
            if (editing) {
                Button(
                    onClick = {
                        profileViewModel.updateDisplayName(displayName)
                        if (imageUri == null)
                            newImage = false
                        if (newImage) {
                            profileViewModel.updateProfilePic(context.contentResolver, imageUri!!)
                            image = imageUri.toString()
                        }
                        editing = false
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(text = stringResource(R.string.save_changes_btn))
                }
            }
        }
        if (postListState.value == ProfileUIState.Init) {
            Text(text = stringResource(R.string.no_posts_yet))
        } else if (postListState.value is ProfileUIState.Success) {
            if ((postListState.value as ProfileUIState.Success).postList.isEmpty()) {
                Text(
                    text = stringResource(R.string.no_posts_yet),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            LazyColumn {
                items((postListState.value as ProfileUIState.Success).postList) { postItem ->
                    PostCard(
                        post = postItem.post,
                        onRemoveItem = { profileViewModel.deletePost(postItem.postId) },
                        removable = true
                    )

                }
            }
        }
    }


}

