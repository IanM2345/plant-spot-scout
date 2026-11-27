package com.iangachoki.plantspotscout.data

import android.graphics.Bitmap
import com.iangachoki.plantspotscout.BuildConfig
import com.iangachoki.plantspotscout.model.PlantMatch
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
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.roundToInt

/** Longest side of the uploaded photo, in pixels. */
private const val MAX_SIDE = 1000

private val http = OkHttpClient.Builder().callTimeout(20, TimeUnit.SECONDS).build()

/** Sends [photo] to Pl@ntNet. Returns null on any failure; detailed errors come later. */
suspend fun identifyPlant(photo: Bitmap): List<PlantMatch>? = withContext(Dispatchers.IO) {
    try {
        val body = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("images", "plant.jpg", jpegBytes(photo).toRequestBody("image/jpeg".toMediaType()))
            .addFormDataPart("organs", "auto")
            .build()
        val url = "https://my-api.plantnet.org/v2/identify/all".toHttpUrl().newBuilder()
            .addQueryParameter("api-key", BuildConfig.PLANTNET_API_KEY)
            .addQueryParameter("lang", "en")
            .build()
        http.newCall(Request.Builder().url(url).post(body).build()).execute().use { response ->
            if (response.isSuccessful) response.body?.string()?.let(::parseTopResults) else null
        }
    } catch (e: IOException) {
        null
    } catch (e: JSONException) {
        null
    }
}

private fun jpegBytes(photo: Bitmap): ByteArray {
    val scale = MAX_SIDE.toFloat() / max(photo.width, photo.height)
    val scaled = if (scale < 1f) {
        Bitmap.createScaledBitmap(photo, (photo.width * scale).roundToInt(), (photo.height * scale).roundToInt(), true)
    } else photo
    return ByteArrayOutputStream().also { scaled.compress(Bitmap.CompressFormat.JPEG, 85, it) }.toByteArray()
}
