package com.korelin.openfoodfacts.utils

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import java.util.Calendar
import java.util.TimeZone

object CalendarHelper {

    fun addProductToCalendar(context: Context, productName: String?, barcode: String) {
        val calendar = Calendar.getInstance()
        val startMillis = calendar.timeInMillis
        val endMillis = calendar.timeInMillis + 30 * 60 * 1000 // +30 минут

        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, 1)
            put(CalendarContract.Events.TITLE, "Напомнить о продукте: ${productName ?: "Без названия"}")
            put(CalendarContract.Events.DESCRIPTION, "Штрих-код: $barcode\nПодробнее: https://world.openfoodfacts.org/product/$barcode")
            put(CalendarContract.Events.DTSTART, startMillis)
            put(CalendarContract.Events.DTEND, endMillis)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
        }

        try {
            val uri = context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
            if (uri != null) {
                android.widget.Toast.makeText(context, "Событие добавлено в календарь", android.widget.Toast.LENGTH_SHORT).show()
            } else {
                android.widget.Toast.makeText(context, "Ошибка добавления в календарь", android.widget.Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            android.widget.Toast.makeText(context, "Нет разрешения на запись в календарь", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}