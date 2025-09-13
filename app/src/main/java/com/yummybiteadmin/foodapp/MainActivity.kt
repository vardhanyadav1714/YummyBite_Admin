package com.yummybiteadmin.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.yummybiteadmin.foodapp.navigation.YummyBitesNavigation
import com.yummybiteadmin.foodapp.ui.theme.YummyBiteAdminTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YummyBiteAdminTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    YummyBiteAdminAndroidApp()
                }
            }
        }
    }
}
@Composable
fun YummyBiteAdminAndroidApp(modifier: Modifier =Modifier.fillMaxSize()) {
     YummyBitesNavigation()
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    YummyBiteAdminTheme {
        Greeting("Android")
    }
}