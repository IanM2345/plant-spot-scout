package com.iangachoki.plantspotscout.ui.spots

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.iangachoki.plantspotscout.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * The spot's photo, or a placeholder icon when it has none or the file can't be read.
 */
@Composable
fun SpotPhoto(photoUri: String?, modifier: Modifier = Modifier, size: Dp = 72.dp) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, photoUri) {
        value = photoUri?.let { uri ->
            withContext(Dispatchers.IO) {
                Uri.parse(uri).path?.let { BitmapFactory.decodeFile(it) }?.asImageBitmap()
            }
        }
    }
    val shape = RoundedCornerShape(12.dp)
    val photo = bitmap

    if (photo != null) {
        Image(
            bitmap = photo,
            contentDescription = stringResource(R.string.spot_photo),
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(shape)
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(shape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_photo),
                contentDescription = stringResource(R.string.spot_photo_placeholder),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}
