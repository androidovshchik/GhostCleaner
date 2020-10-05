package com.ghostcleaner.service

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.jetbrains.anko.wifiManager

class BatteryManager(context: Context) : CoroutineScope {

    private val job = SupervisorJob()

    private val wifiManager = context.wifiManager

    private val contentResolver = context.contentResolver

    fun applyNormalMode() {
        Settings.System.putInt(contentResolver, "accelerometer_rotation", 1)
        Settings.System.putInt(contentResolver, "screen_brightness", 255)
        ContentResolver.setMasterSyncAutomatically(true)
    }

    fun applyUltraMode() {
        val defaultAdapter = BluetoothAdapter.getDefaultAdapter()
        if (defaultAdapter?.isEnabled == true) {
            defaultAdapter.disable()
        }
        Settings.System.putInt(contentResolver, "accelerometer_rotation", 0)
        Settings.System.putInt(contentResolver, "screen_brightness", 30)
        ContentResolver.setMasterSyncAutomatically(false)
    }

    fun applyExtremeMode() {
        if (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = false
        }
        val defaultAdapter = BluetoothAdapter.getDefaultAdapter()
        if (defaultAdapter?.isEnabled == true) {
            defaultAdapter.disable()
        }
        Settings.System.putInt(contentResolver, "accelerometer_rotation", 0)
        Settings.System.putInt(contentResolver, "screen_brightness", 20)
        ContentResolver.setMasterSyncAutomatically(false)
    }

    fun a(str: String?, cVar: C0165c) {
        if (d[str] != null) {
            com.crashlytics.android.a.a(IllegalStateException("Energy listener with this tag has been already set"))
        }
        d[str] = cVar
        if (f4470b == null) {
            f4470b = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    e = intent.getIntExtra(AppLovinEventTypes.USER_COMPLETED_LEVEL, 0)
                    val unused = e
                    for (onLifetimeChange in d.values) {
                        val cVar = this@c
                        val b2 = cVar.a(cVar.e)
                        val cVar2 = this@c
                        onLifetimeChange!!.onLifetimeChange(b2, cVar2.b(cVar2.e))
                    }
                }
            }
            f4469a!!.registerReceiver(f4470b, IntentFilter("android.intent.action.BATTERY_CHANGED"))
            return
        }
        cVar.onLifetimeChange(a(e), b(e))
    }

    fun a(str: String?) {
        d.remove(str)
        if (d.isEmpty()) {
            f4469a!!.unregisterReceiver(f4470b)
            f4470b = null
        }
    }

    fun a() {
        a(a.NORMAL)
        a(true)
        Settings.System.putInt(f4469a!!.contentResolver, "screen_brightness", 255)
        ContentResolver.setMasterSyncAutomatically(true)
    }

    fun b() {
        a(a.ULTRA)
        e()
        a(false)
        Settings.System.putInt(f4469a!!.contentResolver, "screen_brightness", 30)
        ContentResolver.setMasterSyncAutomatically(false)
    }

    constructor() {
        a(a.EXTREME)
        f()
        e()
        a(false)
        Settings.System.putInt(f4469a!!.contentResolver, "screen_brightness", 20)
        ContentResolver.setMasterSyncAutomatically(false)
    }

    private fun e() {
        val defaultAdapter = BluetoothAdapter.getDefaultAdapter()
        if (defaultAdapter != null && defaultAdapter.isEnabled) {
            defaultAdapter.disable()
        }
    }

    private fun f() {
        val wifiManager = f4469a!!.applicationContext.getSystemService("wifi") as WifiManager
        if (wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = false
        }
    }

    fun a(activity: Activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            val intent = Intent("android.settings.action.MANAGE_WRITE_SETTINGS")
            intent.data = Uri.parse("package:" + activity.packageName)
            activity.startActivity(intent)
            return
        }
        androidx.core.app.a.a(activity, arrayOf("android.permission.WRITE_SETTINGS"), 1324)
    }

    fun d(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            Settings.System.canWrite(f4469a)
        } else ContextCompat.checkSelfPermission(f4469a!!, "android.permission.WRITE_SETTINGS") == 0
    }

    private fun a(z: Boolean) {
        Settings.System.putInt(f4469a!!.contentResolver, "accelerometer_rotation", if (z) 1 else 0)
    }

    private fun g(): a {
        val string = c!!.getString("dgg", null as String?) ?: return a.NORMAL
        return com.megaphone.cleaner.b.c.a.valueOf(string)
    }

    private fun a(aVar: a) {
        c!!.edit().putString("dgg", aVar.toString()).apply()
    }

    /* access modifiers changed from: private */
    fun a(i: Int): b {
        if (i <= 5) {
            return b(15, 145, 235)
        }
        if (i <= 10) {
            return b(30, 185, 360)
        }
        if (i <= 15) {
            return b(45, 230, 505)
        }
        if (i <= 25) {
            return b(90, 285, 775)
        }
        if (i <= 35) {
            return b(140, 380, 1142)
        }
        if (i <= 50) {
            return b(320, 565, 1320)
        }
        if (i <= 65) {
            return b(450, 661, 1695)
        }
        if (i <= 75) {
            return b(550, 865, 1855)
        }
        return if (i <= 85) {
            b(855, 1030, 2285)
        } else b(1245, 1800, 3895)
    }

    /* renamed from: com.megaphone.cleaner.b.c$2  reason: invalid class name */ /* compiled from: EnergySavingManager */
    internal object AnonymousClass2 {
        /* renamed from: a  reason: collision with root package name */
        /* synthetic */ val f7223a = IntArray(com.megaphone.cleaner.b.c.a.values().size)

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|6) */ /* JADX WARNING: Code restructure failed: missing block: B:7:?, code lost:
            return;
         */
        /* JADX WARNING: Failed to process nested try/catch */ /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        init {
            /*
                com.megaphone.cleaner.b.c$a[] r0 = com.megaphone.cleaner.b.c.a.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                f7223a = r0
                int[] r0 = f7223a     // Catch:{ NoSuchFieldError -> 0x0014 }
                com.megaphone.cleaner.b.c$a r1 = com.megaphone.cleaner.b.c.a.NORMAL     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = f7223a     // Catch:{ NoSuchFieldError -> 0x001f }
                com.megaphone.cleaner.b.c$a r1 = com.megaphone.cleaner.b.c.a.ULTRA     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                return
            */
            throw UnsupportedOperationException("Method not decompiled: com.megaphone.cleaner.b.c.AnonymousClass2.<clinit>():void")
        }
    }

    /* access modifiers changed from: private */
    fun b(i: Int): Int {
        val a2 = a(i)
        val i2 = AnonymousClass2.f7223a[g().ordinal]
        if (i2 == 1) {
            return a2.f7226a
        }
        return if (i2 != 2) {
            a2.c
        } else a2.f7227b
    }

    /* compiled from: EnergySavingManager */
    class b {
        /* access modifiers changed from: private */ /* renamed from: a  reason: collision with root package name */
        var f7226a = 0

        /* access modifiers changed from: private */ /* renamed from: b  reason: collision with root package name */
        var f7227b = 0

        /* access modifiers changed from: private */
        var c = 0

        private constructor(i: Int, i2: Int, i3: Int) {
            f7226a = i
            f7227b = i2
            c = i3
        }

        fun a(): Int {
            return f7226a
        }

        constructor() {
            return f7227b
        }

        fun c(): Int {
            return c
        }
    }
}