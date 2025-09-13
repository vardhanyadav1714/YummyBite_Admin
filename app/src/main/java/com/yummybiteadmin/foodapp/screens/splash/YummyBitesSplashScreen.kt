package com.yummybiteadmin.foodapp.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yummybiteadmin.foodapp.navigation.YummyBitesScreens
import kotlinx.coroutines.delay


@Composable
fun YummyBitesSplashScreen(navController: NavHostController, image1: Int, image2: Int) {
    var scale by remember { mutableStateOf(0.5f) }

    val scaleAnimatable = remember { Animatable(0.5f) }
    LaunchedEffect(Unit) {
        scaleAnimatable.animateTo(0.9f, animationSpec = tween(durationMillis = 800))
        delay(2000L)
        navController.navigate(YummyBitesScreens.LoginScreen.name)
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Image 1 (Background Image)
        val bgPainter: Painter = painterResource(image1)
        Image(
            painter = bgPainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Image 2 (Animating Image)
        val animatingPainter: Painter = painterResource(image2)
        Image(
            painter = animatingPainter,
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .scale(scaleAnimatable.value),
            contentScale = ContentScale.Fit
        )
    }
}