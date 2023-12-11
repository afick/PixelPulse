package hu.ait.pixelpulse.ui.screen.feed


import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import hu.ait.pixelpulse.R
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
                title = { Text(stringResource(R.string.pixel_pulse)) },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor =
                    MaterialTheme.colorScheme.secondaryContainer
                )
            )
        },
        modifier = Modifier.padding(bottom = 56.dp)
    ) {
        Column(modifier = Modifier.padding(it)) {

            if (postListState.value == MainScreenUIState.Init) {
                Text(text = stringResource(R.string.loading_txt))
            } else if (postListState.value is MainScreenUIState.Success) {
                LazyColumn {
                    items((postListState.value as MainScreenUIState.Success).postList) { postItem ->
                        PostCard(
                            post = postItem.post
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
) {
    val context = LocalContext.current
    Column {
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
                Text(text = post.location,
                    modifier = Modifier.clickable {
                        val gmmIntentUri =
                            Uri.parse("geo:0,0?q=${post.location}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                    })
            }
            Column (
                horizontalAlignment = Alignment.End
            ){
                Text(text = post.camera, fontWeight = FontWeight.Medium)
                Text(text = stringResource(R.string.ss_txt, post.shutterSpeed))
                Text(text = stringResource(R.string.iso_txt, post.iso))
                Text(text = stringResource(R.string.aperture_txt, post.aperture))
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
