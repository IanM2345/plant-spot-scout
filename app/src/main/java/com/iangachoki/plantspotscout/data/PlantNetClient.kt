package com.iangachoki.plantspotscout.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import com.iangachoki.plantspotscout.BuildConfig
import com.iangachoki.plantspotscout.model.Identification
import com.iangachoki.plantspotscout.model.IdentifyError
import com.iangachoki.plantspotscout.model.errorForStatus
import com.iangachoki.plantspotscout.model.parseTopResults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

/** Longest side of the uploaded photo, in pixels. */
private const val MAX_SIDE = 1000

private val http = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS).build()

/** Sends the photo file to Pl@ntNet and deletes it. Network, HTTP, JSON and photo problems come back as [Identification.Failed]. */
suspend fun identifyPlant(photo: File): Identification = withContext(Dispatchers.IO) {
    try {
        val jpeg = try {
            jpegBytes(photo)
        } catch (e: IOException) {
            return@withContext Identification.Failed(IdentifyError.OTHER) // unreadable photo, not a network problem
        }
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("images", "plant.jpg", jpeg.toRequestBody("image/jpeg".toMediaType()))
            .addFormDataPart("organs", "auto")
            .build()
        val url = "https://my-api.plantnet.org/v2/identify/all".toHttpUrl().newBuilder()
            .addQueryParameter("api-key", BuildConfig.PLANTNET_API_KEY)
            .addQueryParameter("lang", "en")
            .build()
        http.newCall(Request.Builder().url(url).post(body).build()).execute().use { response ->
            if (!response.isSuccessful) {
                Identification.Failed(errorForStatus(response.code))
            } else {
                // A 200 with no results means the same as a 404: nothing found.
                parseTopResults(response.body?.string().orEmpty()).let {
                    if (it.isEmpty()) Identification.Failed(IdentifyError.NOT_FOUND) else Identification.Found(it)
                }
            }
        }
    } catch (e: IOException) {
        Identification.Failed(IdentifyError.OFFLINE) // no connection, DNS failure or the 20 s timeout
    } catch (e: JSONException) {
        Identification.Failed(IdentifyError.OTHER)
    } finally {
        photo.delete()
    }
}

private fun jpegBytes(file: File): ByteArray {
    // Decode at a power-of-two fraction first so a 12 MP photo never sits in memory at full size.
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        .also { BitmapFactory.decodeFile(file.path, it) }
    val sample = Integer.highestOneBit(max(1, max(bounds.outWidth, bounds.outHeight) / MAX_SIDE))
    val photo = BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply { inSampleSize = sample })
        ?: throw IOException("Unreadable photo")
    // Camera apps often save portrait shots sideways and only record the turn in EXIF, so undo it here.
    val matrix = Matrix()
    // An unreadable EXIF block just means no rotation; only a failed decode should stop the upload.
    val orientation = try {
        ExifInterface(file.path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    } catch (e: IOException) {
        ExifInterface.ORIENTATION_NORMAL
    }
    when (orientation) {
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
        ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
        ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
        ExifInterface.ORIENTATION_TRANSPOSE -> { matrix.postRotate(90f); matrix.postScale(-1f, 1f) }
        ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
        ExifInterface.ORIENTATION_TRANSVERSE -> { matrix.postRotate(-90f); matrix.postScale(-1f, 1f) }
        ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
    }
    val scale = min(1f, MAX_SIDE.toFloat() / max(photo.width, photo.height))
    matrix.postScale(scale, scale)
    val upright = Bitmap.createBitmap(photo, 0, 0, photo.width, photo.height, matrix, true)
    return ByteArrayOutputStream().also { upright.compress(Bitmap.CompressFormat.JPEG, 85, it) }.toByteArray()
}
