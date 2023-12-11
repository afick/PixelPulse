package hu.ait.pixelpulse.ui.screen.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(
    profileViewModel: ProfileViewModel = ProfileViewModel()
) {

    var editing by remember {
        mutableStateOf(false)
    }

    TopAppBar(
        title = { Text(text = "Profile") },
        actions = {
            IconButton(onClick = { editing = !editing }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Edit Profile"
                )
            }
        })

    if (editing) {
        EditProfile(profileViewModel)
    } else {
        ViewProfile(profileViewModel)
    }



}

@Composable
fun ViewProfile() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column {
            Text(text = "Username: ")
            Text(text = "Bio")
            Text(text = "Location")
        }
    }

}

@Composable
fun EditProfile() {
    TODO("Not yet implemented")
}
