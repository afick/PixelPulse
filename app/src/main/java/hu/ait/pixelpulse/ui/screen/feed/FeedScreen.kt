package hu.ait.pixelpulse.ui.screen.feed


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import hu.ait.pixelpulse.data.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    feedScreenViewModel: FeedScreenViewModel = viewModel(),
) {
    val postListState = feedScreenViewModel.postsList().collectAsState(
        initial = MainScreenUIState.Init
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pixel Pulse") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.secondaryContainer
                ),
                actions = {
                    IconButton(
                        onClick = { }
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = "Info")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {

            if (postListState.value == MainScreenUIState.Init) {
                Text(text = "Init...")
            } else if (postListState.value is MainScreenUIState.Success) {
                LazyColumn {
                    items((postListState.value as MainScreenUIState.Success).postList) { postItem ->
                        val userEmail = feedScreenViewModel.getUserEmailByUid(postItem.post.uid)

                        PostCard(
                            post = postItem.post,
                            userEmail = userEmail.value,
                            onRemoveItem = { feedScreenViewModel.deletePost(postItem.postId) },
                            currentUserId = feedScreenViewModel.currentUserId
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    userEmail: String?,
    onRemoveItem: () -> Unit,
    currentUserId: String
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = userEmail ?: "Unknown",
                modifier = Modifier.padding(end = 8.dp)
            )
            if (currentUserId == post.uid) {
                IconButton(
                    onClick = { onRemoveItem() },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
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
    }
}
