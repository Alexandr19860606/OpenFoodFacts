package com.korelin.openfoodfacts.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

object ImageDownloader {

    // Используем WeakReference для предотвращения утечек
    private var imageLoaderRef: WeakReference<ImageLoader>? = null

    // Альтернатива limitedParallelism - создаем свой пул потоков
    private val downloadDispatcher = Executors.newFixedThreadPool(2).asCoroutineDispatcher()

    // Отменяем предыдущие загрузки при новом запросе
    private var currentJob: Job? = null

    /**
     * Скачивает изображение используя Coil с защитой от утечек
     */
    fun downloadImageWithCoil(
        context: Context,
        imageUrl: String?,
        fileName: String,
        onComplete: (() -> Unit)? = null
    ) {
        if (imageUrl.isNullOrEmpty()) {
            Toast.makeText(context, "Нет изображения для скачивания", Toast.LENGTH_SHORT).show()
            return
        }

        // Отменяем предыдущую загрузку
        currentJob?.cancel()

        currentJob = CoroutineScope(downloadDispatcher + Job()).launch {
            try {
                // Таймаут на всю операцию
                withTimeout(15000L) {
                    // Создаем ImageLoader с правильной конфигурацией
                    val imageLoader = ImageLoader.Builder(context)
                        .memoryCachePolicy(coil.request.CachePolicy.ENABLED)
                        .diskCachePolicy(coil.request.CachePolicy.ENABLED)
                        .respectCacheHeaders(false)
                        .build()

                    // Сохраняем weak reference
                    imageLoaderRef = WeakReference(imageLoader)

                    val request = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .allowHardware(false)
                        .memoryCacheKey("download_$fileName")
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
                            onComplete?.invoke()
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Явно очищаем ресурсы
                    imageLoaderRef?.clear()
                }
            } catch (e: TimeoutCancellationException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Превышено время загрузки", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                imageLoaderRef?.clear()
                imageLoaderRef = null
            }
        }
    }

    /**
     * Альтернативный метод: скачивает изображение используя HttpURLConnection
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
                    saveToExternalStorageLegacy(context, bitmap, imageFileName)
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
    @Suppress("DEPRECATION")
    private fun saveToExternalStorageLegacy(context: Context, bitmap: Bitmap, fileName: String) {
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val appDir = File(picturesDir, "OpenFoodFacts")

        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        val file = File(appDir, fileName)

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)

            // Сканируем файл, чтобы он появился в галерее
            MediaScannerConnection.scanFile(
                context,
                arrayOf(file.absolutePath),
                arrayOf("image/jpeg"),
                null
            )

            CoroutineScope(Dispatchers.Main).launch {
                Toast.makeText(context, "✅ Изображение сохранено в папку OpenFoodFacts", Toast.LENGTH_LONG).show()
            }
        }
    }

    // Отменяем все текущие загрузки
    fun cancelAllDownloads() {
        currentJob?.cancel()
        imageLoaderRef?.clear()
        imageLoaderRef = null
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