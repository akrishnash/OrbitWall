package com.orbitwall.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    // Animation states
    var showContent by remember { mutableStateOf(false) }
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")
    
    // Star rotation animation
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "star_rotation"
    )
    
    // Pulsing glow animation
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )
    
    // Fade in animation
    val alpha by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "fade_in"
    )
    
    LaunchedEffect(Unit) {
        delay(300)
        showContent = true
        delay(1500) // Show splash for 1.5 seconds
        onSplashComplete()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0A0E27), // Deep space blue
                        Color(0xFF1A1F3A), // Darker blue
                        Color(0xFF000000)  // Black
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Animated stars in background
        StarField(rotation = rotation)
        
        // Main content
        Column(
            modifier = Modifier
                .alpha(alpha)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // OrbitWall text with glow effect
            Text(
                text = "OrbitWall",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .alpha(glowAlpha)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle
            Text(
                text = "Satellite Wallpapers",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun StarField(rotation: Float) {
    // Create multiple animated stars - simplified version
    Box(modifier = Modifier.fillMaxSize()) {
        // Simple star pattern - just show a few twinkling stars
        repeat(15) { index ->
            Star(
                x = (index * 7.3f) % 100f / 100f,
                y = (index * 11.7f) % 100f / 100f,
                size = (2 + index % 4).dp,
                delay = index * 100,
                rotation = rotation
            )
        }
    }
}

@Composable
private fun Star(
    x: Float,
    y: Float,
    size: androidx.compose.ui.unit.Dp,
    delay: Int,
    rotation: Float = 0f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "star_twinkle")
    
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500 + delay, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(twinkle)
            .offset(
                x = ((x - 0.5f) * 800).dp,
                y = ((y - 0.5f) * 800).dp
            )
    ) {
        Canvas(
            modifier = Modifier.size(size)
        ) {
            drawCircle(
                color = Color.White,
                radius = size.toPx() / 2
            )
        }
    }
}

private data class Star(
    val x: Float,
    val y: Float,
    val size: androidx.compose.ui.unit.Dp,
    val delay: Int
)

