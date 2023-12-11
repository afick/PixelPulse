package hu.ait.pixelpulse.ui.screen.feed


import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.ait.pixelpulse.R
import hu.ait.pixelpulse.ui.screen.PostCard


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
                title = {
                    Text(
                        stringResource(R.string.pixel_pulse),
                        fontWeight = FontWeight.SemiBold
                    )
                },
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
                            post = postItem.post,
                            {},
                            false
                        )
                    }
                }
            }
        }
    }
}
