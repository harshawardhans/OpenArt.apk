package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.data.Artwork
import com.example.ui.theme.*
import com.example.viewmodel.ArtViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    viewModel: ArtViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onLoadToPrompt: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val communityList by viewModel.communityList.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = CosmicBackgroundGradient
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Community Studio",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Explore & Recreate Trending Artworks",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )

            HorizontalDivider(color = BorderGlow, thickness = 0.5.dp)

            if (communityList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = SecondaryCyan)
                        Text("Retrieving community canvas...", color = TextSecondary)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(communityList, key = { it.id }) { artwork ->
                        // Pinterest/Instagram Style Feed Card
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicCardBackground),
                            border = BorderStroke(1.dp, BorderGlow),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("community_artwork_card_${artwork.id}")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                // Author Header Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Custom visual avatar identifier
                                    val initials = artwork.creatorName.take(2).uppercase()
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(PrimaryNeon, AccentMagenta)
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = initials,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "@${artwork.creatorName}",
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Creator",
                                            fontSize = 10.sp,
                                            color = TextSecondary
                                        )
                                    }
                                    // Style visual pill badge
                                    Text(
                                        text = artwork.style,
                                        fontSize = 10.sp,
                                        color = SecondaryCyan,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .background(Color(0x2200F5D4), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Aspect ratio bounded Art Card
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1.2f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(BorderStroke(0.5.dp, BorderGlow), RoundedCornerShape(12.dp)),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize()) {
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
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Prompt description
                                Text(
                                    text = artwork.prompt,
                                    fontSize = 13.sp,
                                    color = Color.White,
                                    lineHeight = 18.sp,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Interaction button board
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Like Action
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable { viewModel.toggleLike(artwork) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (artwork.isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                                            contentDescription = "Like",
                                            tint = if (artwork.isLiked) AccentMagenta else TextSecondary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${artwork.likesCount}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (artwork.isLiked) Color.White else TextSecondary
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        // Copy Action
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(artwork.prompt))
                                                Toast.makeText(context, "Art prompt copied!", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Outlined.ContentCopy,
                                                contentDescription = "Copy Prompt",
                                                tint = TextSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        // Re-Forge Action (load prompt setup and recreate variance)
                                        IconButton(
                                            onClick = {
                                                viewModel.onPromptChange(artwork.prompt)
                                                viewModel.onStyleChange(artwork.style)
                                                viewModel.onAspectRatioChange(artwork.aspectRatio)
                                                onLoadToPrompt()
                                                Toast.makeText(context, "Loaded into workspace!", Toast.LENGTH_SHORT).show()
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Cyclone,
                                                contentDescription = "Recreate Art",
                                                tint = SecondaryCyan,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        // Save/Export Action
                                        IconButton(
                                            onClick = { viewModel.exportToDeviceGallery(context, artwork) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = "Download Art",
                                                tint = TextSecondary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
