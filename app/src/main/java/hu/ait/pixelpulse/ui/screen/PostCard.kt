package hu.ait.pixelpulse.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import hu.ait.pixelpulse.R
import hu.ait.pixelpulse.data.Post


@Composable
fun PostCard(
    post: Post,
    onRemoveItem: () -> Unit,
    removable: Boolean
) {
    val context = LocalContext.current

    Column(
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = post.author, fontWeight = FontWeight.Medium,
                    fontSize = 26.sp
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
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
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
                    .aspectRatio(.8f),
                contentScale = ContentScale.Crop
            )
        }

        Text(text = post.caption, modifier = Modifier.padding(16.dp))
        if (removable) {
            TextButton(
                onClick = { onRemoveItem() },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = stringResource(R.string.delete_post_btn))
            }
        }
        Divider( // Add a line between items
            modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
        )
    }

}