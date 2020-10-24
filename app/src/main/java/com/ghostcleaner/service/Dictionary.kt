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

object D : CoroutineScope {

    @Suppress("SpellCheckingInspection")
    private const val URL =
        "https://docs.google.com/spreadsheets/d/1jW8g4BFuy-v1HPfjGARr4UH2WQk4GYF0pcx4rAOig38/gviz/tq?tqx=out:csv"

    val loading = MutableLiveData<Boolean>()

    private val job = SupervisorJob()

    private val map = hashMapOf<String, String>()

    @SuppressLint("DefaultLocale")
    @Suppress("BlockingMethodInNonBlockingContext")
    fun download(context: Context) {
        job.cancelChildren()
        loading.value = true
        val assets = context.assets
        val backup = File(context.filesDir, "net.csv")
        val lang = ConfigurationCompat.getLocales(context.resources.configuration)[0]
            ?.language?.toLowerCase() ?: "en"
        launch {
            backup.parentFile?.mkdirs()
            val text = try {
                URL(URL).openStream().use {
                    it.bufferedReader().use(BufferedReader::readText)
                }.also {
                    backup.writeText(it)
                }
            } catch (e: Throwable) {
                if (backup.exists()) {
                    backup.readText()
                } else {
                    assets.open("def.csv").use {
                        it.bufferedReader().use(BufferedReader::readText)
                    }
                }
            }
            val csv = CsvReader().read(StringReader(text))
            val col = csv.rows.getOrNull(0)?.fields
                ?.indexOfFirst { it.toLowerCase() == lang } ?: 1
            csv.rows.forEach {
                if (it.fieldCount > col) {
                    map[it.getField(0)] = it.getField(col)
                }
            }
            loading.postValue(false)
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
        return key
    }

    override val coroutineContext = Dispatchers.IO + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
        loading.postValue(false)
    }
}