package com.kybers.xtream.presentation.compose.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.kybers.xtream.data.entities.Vod

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VodCard(
    vod: Vod,
    onClick: (Vod) -> Unit,
    onFavoriteClick: (Vod) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(vod) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(vod.posterUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Poster de ${vod.title}",
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Crop
            )
            
            // VOD Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = vod.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (vod.year != null) {
                    Text(
                        text = vod.year.toString(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (vod.rating != null) {
                    Text(
                        text = "⭐ ${String.format("%.1f", vod.rating)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                if (vod.genres != null) {
                    Text(
                        text = vod.genres,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (vod.plot != null) {
                    Text(
                        text = vod.plot,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            
            // Favorite Button
            IconButton(
                onClick = { onFavoriteClick(vod) }
            ) {
                Icon(
                    imageVector = if (vod.isFavorite) {
                        androidx.compose.material.icons.Icons.Default.Favorite
                    } else {
                        androidx.compose.material.icons.Icons.Default.FavoriteBorder
                    },
                    contentDescription = if (vod.isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (vod.isFavorite) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}