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
import com.kybers.xtream.data.entities.Channel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelCard(
    channel: Channel,
    onClick: (Channel) -> Unit,
    onFavoriteClick: (Channel) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(channel) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Channel Logo
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(channel.logoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Logo de ${channel.name}",
                modifier = Modifier
                    .size(56.dp)
                    .padding(end = 12.dp),
                contentScale = ContentScale.Fit
            )
            
            // Channel Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = channel.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (channel.groupName != null) {
                    Text(
                        text = channel.groupName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                if (channel.country != null || channel.language != null) {
                    Text(
                        text = listOfNotNull(channel.country, channel.language).joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            
            // Favorite Button
            IconButton(
                onClick = { onFavoriteClick(channel) }
            ) {
                Icon(
                    imageVector = if (channel.isFavorite) {
                        androidx.compose.material.icons.Icons.Default.Favorite
                    } else {
                        androidx.compose.material.icons.Icons.Default.FavoriteBorder
                    },
                    contentDescription = if (channel.isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                    tint = if (channel.isFavorite) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}