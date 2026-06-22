package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.data.Artwork
import com.example.ui.theme.*
import com.example.viewmodel.ArtViewModel
import com.example.viewmodel.GenerationState

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ArtViewModel,
    modifier: Modifier = Modifier,
    onNavigateToHistory: () -> Unit,
    onNavigateToCommunity: () -> Unit
) {
    val context = LocalContext.current
    val prompt by viewModel.prompt.collectAsStateWithLifecycle()
    val selectedStyle by viewModel.selectedStyle.collectAsStateWithLifecycle()
    val aspectRatio by viewModel.aspectRatio.collectAsStateWithLifecycle()
    val isEnhancing by viewModel.isEnhancing.collectAsStateWithLifecycle()
    val generationState by viewModel.generationState.collectAsStateWithLifecycle()

    val scrollState = rememberScrollState()

    // Loading Screen Tips Cycle
    val loadingTips = listOf(
        "Fusing digital neural pathways...",
        "Blending style guidelines with your concept...",
        "Structuring cosmic visual grids...",
        "Polishing brushstroke definitions...",
        "Synthesizing high-resolution pigments..."
    )
    var currentTipIndex by remember { mutableStateOf(0) }
    
    LaunchedEffect(generationState) {
        if (generationState is GenerationState.Loading) {
            currentTipIndex = 0
            while (true) {
                kotlinx.coroutines.delay(3000)
                currentTipIndex = (currentTipIndex + 1) % loadingTips.size
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(CosmicDarkBackground, CosmicDarkBackground, Color(0xFF130630))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
        ) {
            // Screen Header
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(PrimaryNeon, SecondaryCyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "Art Generator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToHistory,
                        modifier = Modifier.testTag("nav_to_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.History,
                            contentDescription = "History Creations",
                            tint = TextPrimary
                        )
                    }
                    IconButton(
                        onClick = onNavigateToCommunity,
                        modifier = Modifier.testTag("nav_to_community_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Explore,
                            contentDescription = "Explore",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )

            HorizontalDivider(color = BorderGlow, thickness = 0.5.dp)

            if (generationState is GenerationState.Loading) {
                // RENDER EXTREMELY COOL FUTURISTIC LOADING SCREEN
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Box(
                            modifier = Modifier.size(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val infiniteTransition = rememberInfiniteTransition()
                            val angle by infiniteTransition.animateFloat(
                                initialValue = 0f,
                                targetValue = 360f,
                                animationSpec = infiniteRepeatable(
                                    animation = keyframes { durationMillis = 2000 },
                                    repeatMode = RepeatMode.Restart
                                )
                            )

                            // Double layered spinning arcs
                            CircularProgressIndicator(
                                progress = 0.75f,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(angle),
                                strokeWidth = 6.dp,
                                color = PrimaryNeon,
                                trackColor = BorderGlow
                            )
                            CircularProgressIndicator(
                                progress = 0.5f,
                                modifier = Modifier
                                    .size(150.dp)
                                    .rotate(-angle * 1.5f),
                                strokeWidth = 4.dp,
                                color = SecondaryCyan,
                                trackColor = Color.Transparent
                            )
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = null,
                                tint = SecondaryCyan,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Text(
                            text = "Forging Masterpiece",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = loadingTips[currentTipIndex],
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        )
                    }
                }
            } else if (generationState is GenerationState.Success) {
                // SHOW POPUP DETAILS OF GENERATED IMAGE
                val artwork = (generationState as GenerationState.Success).artwork
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Frame Card for Image
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(
                                when (artwork.aspectRatio) {
                                    "16:9" -> 1.77f
                                    "9:16" -> 0.56f
                                    "3:4" -> 0.75f
                                    else -> 1f
                                }
                            )
                            .clip(RoundedCornerShape(20.dp))
                            .border(BorderStroke(1.5.dp, SecondaryCyan), RoundedCornerShape(20.dp)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        SubcomposeAsyncImage(
                            model = artwork.imageUrl,
                            contentDescription = artwork.prompt,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(CosmicInputBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = SecondaryCyan)
                                }
                            }
                        )
                    }

                    // Prompt text container
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CosmicCardBackground),
                        border = BorderStroke(1.dp, BorderGlow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Artwork Info",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = SecondaryCyan
                                )
                                Text(
                                    text = artwork.style,
                                    fontSize = 12.sp,
                                    color = AccentMagenta,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(Color(0x33FF007F), Color(0x339E00FF))
                                            ),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = artwork.prompt,
                                fontSize = 15.sp,
                                color = Color.White,
                                lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AspectRatio, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(artwork.aspectRatio, fontSize = 12.sp, color = TextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.PhotoSizeSelectActual, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(artwork.resolution, fontSize = 12.sp, color = TextSecondary)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Casino, null, modifier = Modifier.size(16.dp), tint = TextSecondary)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("#${artwork.seed}", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                        }
                    }

                    // Action grid buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.exportToDeviceGallery(context, artwork) },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("export_artwork_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Export", color = Color.White)
                        }

                        Button(
                            onClick = { viewModel.shareToSocial(context, artwork) },
                            colors = ButtonDefaults.buttonColors(containerColor = CosmicInputBackground),
                            border = BorderStroke(1.dp, BorderGlow),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("share_artwork_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = SecondaryCyan)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Share", color = SecondaryCyan)
                        }
                    }

                    Button(
                        onClick = { viewModel.publishToCommunity(artwork) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0x2000F5D4)),
                        border = BorderStroke(1.dp, SecondaryCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("publish_to_community_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null, tint = SecondaryCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Publish to Community Studio", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetState() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                        border = BorderStroke(1.dp, BorderGlow),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Fresh Digital Art")
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                // MAIN GENERATIVE WORKSPACE FORM
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    
                    // Welcome card / banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryNeon, AccentMagenta)
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    null,
                                    tint = SparkleGold,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "No-Cost Digital Atelier",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Create beautiful high-resolution custom digital artworks completely free. No subscription or credits required.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Prompt Input Label
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Describe your vision",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                        
                        // Enhance with AI button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isEnhancing) CosmicInputBackground else Color(0x3300F5D4)
                                )
                                .border(
                                    BorderStroke(
                                        1.dp,
                                        if (isEnhancing) BorderGlow else SecondaryCyan
                                    ),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = !isEnhancing) { viewModel.enhancePromptWithGemini() }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("ai_enhance_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isEnhancing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = SecondaryCyan
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Enhancing...", fontSize = 12.sp, color = TextSecondary)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = SecondaryCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Enhance with Gemini AI", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Prompt Text Field
                    OutlinedTextField(
                        value = prompt,
                        onValueChange = { viewModel.onPromptChange(it) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp)
                            .testTag("prompt_input_field"),
                        placeholder = {
                            Text(
                                "An epic cosmic astronaut holding a glowing neon blue crystal key, floating beside a celestial nebula galaxy portals...",
                                color = TextSecondary.copy(alpha = 0.5f)
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CosmicInputBackground,
                            unfocusedContainerColor = CosmicInputBackground,
                            focusedBorderColor = PrimaryNeon,
                            unfocusedBorderColor = BorderGlow,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = SecondaryCyan
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Section Style picker
                    Text(
                        text = "Choose Artistic Style",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )

                    // Styles horizontal flow
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(viewModel.stylesList) { styleName ->
                            val isSelected = styleName == selectedStyle
                            Box(
                                modifier = Modifier
                                    .width(135.dp)
                                    .height(78.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        brush = if (isSelected) {
                                            Brush.linearGradient(colors = listOf(PrimaryNeon, AccentMagenta))
                                        } else {
                                            Brush.linearGradient(colors = listOf(CosmicCardBackground, CosmicInputBackground))
                                        }
                                    )
                                    .border(
                                        BorderStroke(
                                            if (isSelected) 1.5.dp else 1.dp,
                                            if (isSelected) SecondaryCyan else BorderGlow
                                        ),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .clickable { viewModel.onStyleChange(styleName) }
                                    .padding(8.dp),
                                contentAlignment = Alignment.BottomStart
                            ) {
                                // Add beautiful style icon representation
                                val styleIcon = when (styleName) {
                                    "Anime / Manga" -> Icons.Default.Face
                                    "Cyberpunk" -> Icons.Default.Bolt
                                    "Fantasy Illustration" -> Icons.Default.AutoAwesome
                                    "Oil Painting" -> Icons.Default.Brush
                                    "3D Render" -> Icons.Default.Layers
                                    "Minimalist Vector" -> Icons.Default.Category
                                    "Surrealism" -> Icons.Default.BubbleChart
                                    "Steampunk" -> Icons.Default.Settings
                                    "Watercolor Art" -> Icons.Default.Palette
                                    "Pixel Art" -> Icons.Default.GridOn
                                    else -> Icons.Default.MusicNote
                                }

                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Icon(
                                        imageVector = styleIcon,
                                        contentDescription = null,
                                        tint = if (isSelected) Color.White else SecondaryCyan.copy(alpha = 0.7f),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Text(
                                    text = styleName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Section Dimension Ratio
                    Text(
                        text = "Dimensions & Layout",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )

                    // Aspect ratio chips row
                    val ratios = listOf(
                        Triple("1:1", "Square", Icons.Default.CropSquare),
                        Triple("16:9", "Horizontal", Icons.Default.Crop),
                        Triple("9:16", "Vertical", Icons.Default.Crop),
                        Triple("3:4", "Portrait", Icons.Default.Crop)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ratios.forEach { (ratio, label, icon) ->
                            val isSelected = ratio == aspectRatio
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(65.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) Color(0x1F00F5D4) else CosmicCardBackground)
                                    .border(
                                        BorderStroke(
                                            if (isSelected) 1.5.dp else 1.dp,
                                            if (isSelected) SecondaryCyan else BorderGlow
                                        ),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { viewModel.onAspectRatioChange(ratio) }
                                    .padding(6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) SecondaryCyan else TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = ratio,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color.White else TextSecondary
                                    )
                                    Text(
                                        text = label,
                                        fontSize = 9.sp,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // GEN MASTERPIECE BUTTON
                    Button(
                        onClick = { viewModel.generateArtwork() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp)
                            .testTag("generate_artwork_button"),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "✨ Forg & Synthesize ✨",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
        }
    }
}
