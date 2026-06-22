package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
import androidx.compose.ui.text.style.TextAlign
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
fun HistoryScreen(
    viewModel: ArtViewModel,
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onLoadToPrompt: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val historyList by viewModel.historyList.collectAsStateWithLifecycle()

    var selectedArtworkForDetail by remember { mutableStateOf<Artwork?>(null) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

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
            // Header Bar
            TopAppBar(
                title = {
                    Text(
                        text = "My Works Detail",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 20.sp
                    )
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
                actions = {
                    if (historyList.isNotEmpty()) {
                        IconButton(
                            onClick = { showClearConfirmDialog = true },
                            modifier = Modifier.testTag("clear_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Clear History",
                                tint = AccentMagenta
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )

            HorizontalDivider(color = BorderGlow, thickness = 0.5.dp)

            if (historyList.isEmpty()) {
                // EMPTY STATE
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
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(Color(0x1A9E00FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PhotoLibrary,
                                contentDescription = null,
                                tint = PrimaryNeon,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Text(
                            text = "No Masterpieces Yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "Your generated digital art will be stored locally here in high resolution. Go back and synthesize your first concept!",
                            fontSize = 14.sp,
                            color = TextSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = onNavigateBack,
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Create Artwork", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // GALLERY GRID
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(historyList, key = { it.id }) { artwork ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CosmicCardBackground),
                            border = BorderStroke(1.dp, BorderGlow),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedArtworkForDetail = artwork }
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
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
                                                CircularProgressIndicator(
                                                    color = SecondaryCyan,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                            }
                                        }
                                    )
                                    // Style label tag
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(6.dp)
                                            .background(Color(0xCC080213), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = artwork.style,
                                            color = SecondaryCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                PaddingValues(8.dp).let {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = artwork.prompt,
                                            fontSize = 12.sp,
                                            lineHeight = 15.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color.White,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.shareToSocial(context, artwork) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Share,
                                                    contentDescription = "Share",
                                                    tint = TextSecondary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.exportToDeviceGallery(context, artwork) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Download,
                                                    contentDescription = "Download",
                                                    tint = TextSecondary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                            IconButton(
                                                onClick = { viewModel.deleteArtwork(artwork) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    tint = AccentMagenta,
                                                    modifier = Modifier.size(16.dp)
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

        // CONFIRM DIALOG FOR CLEAR HISTORY
        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                title = { Text("Wipe Local Atelier History?") },
                text = { Text("Are you sure you want to permanently delete all your generated digital art from this device? This action cannot be undone.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllHistory()
                            showClearConfirmDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentMagenta)
                    ) {
                        Text("Delete All", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = CosmicCardBackground,
                titleContentColor = Color.White,
                textContentColor = TextSecondary
            )
        }

        // EXPANDED DETAIL VIEW DIALOG
        selectedArtworkForDetail?.let { artwork ->
            AlertDialog(
                onDismissRequest = { selectedArtworkForDetail = null },
                confirmButton = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.onPromptChange(artwork.prompt)
                                viewModel.onStyleChange(artwork.style)
                                viewModel.onAspectRatioChange(artwork.aspectRatio)
                                selectedArtworkForDetail = null
                                onLoadToPrompt()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryNeon),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Replay, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Recreate", color = Color.White, fontSize = 12.sp)
                        }
                        Button(
                            onClick = {
                                viewModel.publishToCommunity(artwork)
                                selectedArtworkForDetail = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0x3300F5D4)),
                            border = BorderStroke(1.dp, SecondaryCyan),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Icon(Icons.Default.Publish, null, tint = SecondaryCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Publish", color = Color.White, fontSize = 12.sp)
                        }
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { selectedArtworkForDetail = null },
                        border = BorderStroke(1.dp, BorderGlow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close Details", color = TextSecondary)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.2f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(BorderStroke(1.dp, BorderGlow), RoundedCornerShape(12.dp))
                        ) {
                            SubcomposeAsyncImage(
                                model = artwork.imageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = artwork.style.uppercase(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = SecondaryCyan
                            )
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(artwork.prompt))
                                    Toast.makeText(context, "Prompt copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Prompt",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = artwork.prompt,
                            fontSize = 13.sp,
                            color = Color.White,
                            lineHeight = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text("Ratio: ${artwork.aspectRatio}", fontSize = 11.sp, color = TextSecondary)
                            Text("Res: ${artwork.resolution}", fontSize = 11.sp, color = TextSecondary)
                            Text("Seed: ${artwork.seed}", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                },
                containerColor = CosmicCardBackground,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
