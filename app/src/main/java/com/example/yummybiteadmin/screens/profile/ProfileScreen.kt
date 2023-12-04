import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yummybiteadmin.R
import com.example.yummybiteadmin.screens.adddishes.showImagePickerDialog

@Composable
fun ProfileScreen() {
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("John Doe") }
    var email by remember { mutableStateOf("two@gmail.com") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        ProfileImage(
            isEditing = isEditing,
            onImageSelected = {
                // Handle image selection logic here
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        ProfileDetailItem(label = "Name", value = name, isEditing = isEditing) {
            name = it
        }
        ProfileDetailItem(label = "Email", value = email, isEditing = isEditing) {
            email = it
        }

        Spacer(modifier = Modifier.height(16.dp))

        EditSaveButton(
            isEditing = isEditing,
            onToggleEditing = {
                isEditing = !isEditing
            }
        )
    }
}

@Composable
fun ProfileImage(isEditing: Boolean, onImageSelected: () -> Unit) {
    val context = LocalContext.current
    var imageUri by remember { mutableStateOf("dummy_image_url") }

    Box(
        modifier = Modifier
            .size(120.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colors.primary)
            .clickable {
                if (isEditing) {
                    // Open the image picker dialog when clicked
                   // showImagePickerDialog(context, onImageSelected)
                }
            }
    ) {
        Image(
            painter = painterResource(id = R.drawable.baseline_person_24), // Replace with your dummy image
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String, isEditing: Boolean, onValueChange: (String) -> Unit) {
    if (isEditing) {
        BasicTextField(
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
                capitalization = KeyboardCapitalization.Words
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Handle keyboard done action if needed
                }
            ),
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    } else {
        Text(
            text = "$label: $value",
            style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun EditSaveButton(isEditing: Boolean, onToggleEditing: () -> Unit) {
    IconButton(
        onClick = {
            onToggleEditing()
        },
        modifier = Modifier
            .background(MaterialTheme.colors.secondary)
            .padding(8.dp)
            .clip(MaterialTheme.shapes.medium)
    ) {
        Icon(
            imageVector = if (isEditing) Icons.Default.Save else Icons.Default.Edit,
            contentDescription = if (isEditing) "Save" else "Edit",
            tint = LocalContentColor.current
        )
    }
}
