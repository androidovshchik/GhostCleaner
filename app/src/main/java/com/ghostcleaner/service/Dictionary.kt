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

@SuppressLint("DefaultLocale")
object D : CoroutineScope {

    @Suppress("SpellCheckingInspection")
    private const val URL =
        "https://docs.google.com/spreadsheets/d/1jW8g4BFuy-v1HPfjGARr4UH2WQk4GYF0pcx4rAOig38/gviz/tq?tqx=out:csv"

    private val job = SupervisorJob()

    private val reader = CsvReader()

    private val map = hashMapOf<String, String>()

    val loading = MutableLiveData(0f)

    @Suppress("BlockingMethodInNonBlockingContext")
    fun initialize(context: Context) {
        job.cancelChildren()
        loading.value = 0f
        val assets = context.assets
        val backup = File(context.filesDir, "net.csv")
        val lang = ConfigurationCompat.getLocales(context.resources.configuration)[0]
            ?.language?.toLowerCase() ?: "en"
        launch {
            val defText = assets.open("def.csv").use {
                it.bufferedReader().use(BufferedReader::readText)
            }
            fillMap(lang, defText)
            backup.parentFile?.mkdirs()
            try {
                val text = URL(URL).openStream().use {
                    it.bufferedReader().use(BufferedReader::readText)
                }
                backup.writeText(text)
                fillMap(lang, text)
            } catch (e: Throwable) {
                if (backup.exists()) {
                    fillMap(lang, backup.readText())
                }
            } finally {
                loading.postValue(-1f)
            }
        }
    }

    private fun fillMap(lang: String, text: String) {
        val csv = reader.read(StringReader(text))
        val col = csv.rows.getOrNull(0)?.fields
            ?.indexOfFirst { it.toLowerCase() == lang } ?: 1
        csv.rows.forEach {
            if (it.fieldCount > col) {
                map[it.getField(0)] = it.getField(col)
            }
        }
    }

    operator fun get(key: String, vararg args: Any?): String {
        if (loading.value!! < 0) {
            map[key]?.let {
                var value = it
                args.forEach { arg ->
                    value = value.replaceFirst("{s}", arg.toString())
                }
                return value
            }
        }
        return ""
    }

    override val coroutineContext = Dispatchers.IO + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        loading.postValue(-1f)
    }
}