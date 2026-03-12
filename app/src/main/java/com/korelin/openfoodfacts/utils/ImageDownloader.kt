package com.korelin.openfoodfacts.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

object ImageDownloader {

    /**
     * Скачивает изображение используя HttpURLConnection
     */
    fun downloadImage(context: Context, imageUrl: String?, fileName: String) {
        if (imageUrl.isNullOrEmpty()) {
            Toast.makeText(context, "Нет изображения для скачивания", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val bitmap = downloadBitmap(imageUrl)
                if (bitmap != null) {
                    withContext(Dispatchers.Main) {
                        saveImageToGallery(context, bitmap, fileName)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Скачивает изображение используя Coil
     */
    fun downloadImageWithCoil(context: Context, imageUrl: String?, fileName: String) {
        if (imageUrl.isNullOrEmpty()) {
            Toast.makeText(context, "Нет изображения для скачивания", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imageLoader = ImageLoader(context)
                val request = ImageRequest.Builder(context)
                    .data(imageUrl)
                    .allowHardware(false)
                    .build()

                val result = imageLoader.execute(request)
                if (result is SuccessResult) {
                    val drawable = result.drawable
                    val bitmap = when (drawable) {
                        is BitmapDrawable -> drawable.bitmap
                        else -> {
                            val bitmap = Bitmap.createBitmap(
                                drawable.intrinsicWidth.takeIf { it > 0 } ?: 100,
                                drawable.intrinsicHeight.takeIf { it > 0 } ?: 100,
                                Bitmap.Config.ARGB_8888
                            )
                            val canvas = android.graphics.Canvas(bitmap)
                            drawable.setBounds(0, 0, canvas.width, canvas.height)
                            drawable.draw(canvas)
                            bitmap
                        }
                    }
                    withContext(Dispatchers.Main) {
                        saveImageToGallery(context, bitmap, fileName)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Скачивает Bitmap из URL
     */
    private fun downloadBitmap(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            connection.doInput = true
            connection.connect()

            val inputStream = connection.inputStream
            BitmapFactory.decodeStream(inputStream).also {
                inputStream.close()
                connection.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Сохраняет изображение в галерею
     */
    private suspend fun saveImageToGallery(context: Context, bitmap: Bitmap, fileName: String) {
        val sanitizedFileName = fileName.replace("[^a-zA-Z0-9]".toRegex(), "_")
        val timestamp = System.currentTimeMillis()
        val imageFileName = "${sanitizedFileName}_$timestamp.jpg"

        withContext(Dispatchers.IO) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    saveToMediaStore(context, bitmap, imageFileName)
                } else {
                    saveToExternalStorage(context, bitmap, imageFileName)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Сохраняет в MediaStore (Android 10+)
     */
    private fun saveToMediaStore(context: Context, bitmap: Bitmap, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/OpenFoodFacts")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                CoroutineScope(Dispatchers.Main).launch {
                    Toast.makeText(context, "✅ Изображение сохранено в Галерею", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    /**
     * Сохраняет во внешнее хранилище (Android 9 и ниже)
     */
    private fun saveToExternalStorage(context: Context, bitmap: Bitmap, fileName: String) {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appDir = File(picturesDir, "OpenFoodFacts")

        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        val file = File(appDir, fileName)

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

            // Уведомляем галерею о новом файле
            context.sendBroadcast(
                android.content.Intent(
                    android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    android.net.Uri.fromFile(file)
                )
            )

            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "✅ Изображение сохранено в папку OpenFoodFacts", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Проверяет, есть ли разрешение на запись
     */
    fun hasStoragePermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Получает список необходимых разрешений в зависимости от версии Android
     */
    fun getStoragePermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            arrayOf(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }
}