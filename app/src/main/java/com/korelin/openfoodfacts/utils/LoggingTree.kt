package com.korelin.openfoodfacts.utils

import timber.log.Timber

// Класс для отладки
class DebugTree : Timber.DebugTree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, tag, message, t)
        // Всегда пишем в файл, но FileLogger сам решает, что делать
        FileLogger.d(tag ?: "Timber", message)
    }
}

// Класс для релиза
class ReleaseTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // В релизе логируем только ошибки
        if (priority == android.util.Log.ERROR) {
            // Здесь можно отправить в Crashlytics
        }
    }
}