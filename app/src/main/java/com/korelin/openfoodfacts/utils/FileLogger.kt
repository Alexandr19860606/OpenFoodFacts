package com.korelin.openfoodfacts.utils

import android.content.Context
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FileLogger {

    private const val TAG = "FileLogger"
    private const val LOG_FILE_NAME = "app_crash_log.txt"

    private var logFile: File? = null

    fun init(context: Context) {
        try {
            // Сохраняем в публичную директорию Downloads (доступно без root)
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            logFile = File(downloadsDir, LOG_FILE_NAME)

            // Создаем файл если его нет
            if (logFile?.exists() == false) {
                logFile?.createNewFile()
            }

            // Пишем заголовок при инициализации
            writeLog("=== APP STARTED at ${getCurrentTimestamp()} ===")
            Log.d(TAG, "Лог-файл создан: ${logFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при инициализации логгера: ${e.message}")

            // Fallback - внутреннее хранилище
            try {
                logFile = File(context.filesDir, LOG_FILE_NAME)
                if (logFile?.exists() == false) {
                    logFile?.createNewFile()
                }
                writeLog("=== APP STARTED at ${getCurrentTimestamp()} (internal) ===")
            } catch (e2: Exception) {
                Log.e(TAG, "Ошибка при инициализации fallback логгера: ${e2.message}")
            }
        }
    }

    fun d(tag: String, message: String) {
        Log.d(tag, message)
        writeLog("D/$tag: $message")
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
        writeLog("E/$tag: $message")
        throwable?.let {
            writeLog("EXCEPTION: ${it.javaClass.simpleName}: ${it.message}")
            it.stackTrace.forEach { stack ->
                writeLog("    at $stack")
            }
        }
    }

    private fun writeLog(message: String) {
        try {
            logFile?.let { file ->
                FileWriter(file, true).use { writer ->
                    writer.append("$message\n")
                    writer.flush()
                }
            }
        } catch (e: IOException) {
            // Если не можем записать в файл, хотя бы в лог
            Log.e(TAG, "Не удалось записать в лог-файл: ${e.message}")
        }
    }

    private fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)
        return sdf.format(Date())
    }

    fun getLogFilePath(): String? {
        return logFile?.absolutePath
    }

    fun clearLog() {
        try {
            logFile?.writeText("")
            writeLog("=== LOG CLEARED at ${getCurrentTimestamp()} ===")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при очистке лога: ${e.message}")
        }
    }
}