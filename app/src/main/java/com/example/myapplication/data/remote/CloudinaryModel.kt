package com.example.myapplication.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.android.policy.GlobalUploadPolicy
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine
import com.example.myapplication.data.config.CloudinaryConfig.API_KEY
import com.example.myapplication.data.config.CloudinaryConfig.API_SECRET
import com.example.myapplication.data.config.CloudinaryConfig.CLOUD_NAME


object CloudinaryModel {

    private var isInitialized = false

    fun init(context: Context, cloudName: String, apiKey: String, apiSecret: String) {
        if (!isInitialized) {
            val config = mapOf(
                "cloud_name" to CLOUD_NAME,
                "api_key" to API_KEY,
                "api_secret" to API_SECRET
            )
            MediaManager.init(context, config)
            MediaManager.get().globalUploadPolicy = GlobalUploadPolicy.defaultPolicy()
            isInitialized = true
        }
    }

    suspend fun uploadImage(
        bitmap: Bitmap,
        name: String,
        folder: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ): Boolean {
        return suspendCancellableCoroutine { continuation ->
            if (!isInitialized) {
                Log.e("Cloudinary", "MediaManager is not initialized!")
                onError("MediaManager is not initialized!")
                continuation.resume(false)
                return@suspendCancellableCoroutine
            }

            val file: File = bitmap.toFile(name)
            Log.d("Cloudinary", "Uploading to Cloudinary. File path: ${file.path}")

            MediaManager.get().upload(file.path)
                .option("folder", folder)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {}

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>) {
                        val url = resultData["secure_url"] as? String
                        if (url != null) {
                            Log.d("Cloudinary", "Upload successful. URL: $url")
                            onSuccess(url)
                            continuation.resume(true)
                        } else {
                            Log.e("Cloudinary", "Upload failed: No URL returned")
                            onError("Upload failed: No URL returned")
                            continuation.resume(false)
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Log.e("Cloudinary", "Upload failed. Error: ${error?.description}")
                        onError(error?.description ?: "Unknown error")
                        continuation.resume(false)
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {}
                })
                .dispatch()
        }
    }

    private fun Bitmap.toFile(name: String): File {
        val file = File.createTempFile(name, ".jpg")
        val outputStream = FileOutputStream(file)
        this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()
        return file
    }
}