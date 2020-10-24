package com.ghostcleaner.service

import android.content.Context
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

    @Suppress("BlockingMethodInNonBlockingContext")
    fun initialize(context: Context) {
        job.cancelChildren()
        loading.value = true
        val assets = context.assets
        val backup = File(context.filesDir, "net.csv")
        launch {
            backup.parentFile?.mkdirs()
            val text = try {
                URL(URL).openStream().use {
                    it.bufferedReader().use(BufferedReader::readText)
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
            val reader = CsvReader()
            val csv = reader.read(StringReader(text))
            csv.getRow()
            loading.postValue(false)
        }
    }

    operator fun get(key: String, vararg args: Any?): String {
        map[key]?.let {
            args.forEach {
                key.replace("{s}", it)
            }
            if (args.isNotEmpty()) {
                return
            }
            return it
        }
        return key
    }

    override val coroutineContext = Dispatchers.IO + job + CoroutineExceptionHandler { _, e ->
        Timber.e(e)
    }
}