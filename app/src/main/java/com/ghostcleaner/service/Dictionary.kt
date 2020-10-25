package com.ghostcleaner.service

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.os.ConfigurationCompat
import androidx.lifecycle.MutableLiveData
import de.siegmar.fastcsv.reader.CsvReader
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.BufferedReader
import java.io.File
import java.io.StringReader
import java.net.URL
import java.util.*
import kotlin.math.max

@SuppressLint("DefaultLocale")
object D : CoroutineScope {

    @Suppress("SpellCheckingInspection")
    private const val URL =
        "https://docs.google.com/spreadsheets/d/1jW8g4BFuy-v1HPfjGARr4UH2WQk4GYF0pcx4rAOig38/gviz/tq?tqx=out:csv"

    private val job = SupervisorJob()

    private val reader = CsvReader().apply {
        setContainsHeader(true)
    }

    private val map = Collections.synchronizedMap(hashMapOf<String, String>())

    val loading = MutableLiveData(0f)

    @Suppress("BlockingMethodInNonBlockingContext")
    fun download(context: Context) {
        job.cancelChildren()
        loading.value = 0f
        val assets = context.assets
        val backup = File(context.filesDir, "net.csv")
        val lang = ConfigurationCompat.getLocales(context.resources.configuration)[0]
            ?.language?.toLowerCase() ?: "en"
        launch {
            val defText = if (backup.exists()) {
                fillMap(lang, backup.readText(), true)
            } else {
                assets.open("def.csv").use {
                    it.bufferedReader().use(BufferedReader::readText)
                }
            }
            fillMap(lang, defText, false)
            backup.parentFile?.mkdirs()
            try {
                val text = URL(URL).openStream().use {
                    it.bufferedReader().use(BufferedReader::readText)
                }
                fillMap(lang, text, true)
                backup.writeText(text)
            } catch (e: Throwable) {
                if (backup.exists()) {
                    fillMap(lang, backup.readText(), true)
                }
            } finally {
                loading.postValue(-1f)
            }
        }
    }

    private fun fillMap(lang: String, text: String, progress: Boolean) {
        val csv = reader.read(StringReader(text))
        val col = max(1, csv.rows.getOrNull(0)?.fields
            ?.indexOfFirst { it.toLowerCase() == lang } ?: 1)
        csv.rows.forEachIndexed { i, row ->
            if (row.fieldCount > col) {
                map[row.getField(0)] = row.getField(col)
                if (progress) {
                    loading.postValue(100f * (i + 1) / csv.rowCount)
                }
            }
        }
    }

    operator fun get(key: String, vararg args: Any?): String {
        map[key]?.let {
            var value = it
            args.forEach { arg ->
                value = value.replaceFirst("{s}", arg.toString())
            }
            return value
        }
        return ""
    }

    override val coroutineContext = Dispatchers.IO + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        loading.postValue(-1f)
    }
}