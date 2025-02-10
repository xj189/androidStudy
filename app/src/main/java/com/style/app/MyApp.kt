package com.style.app

/*import com.taobao.sophix.PatchStatus
import com.taobao.sophix.SophixManager
import com.taobao.sophix.listener.PatchLoadStatusListener*/

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.multidex.MultiDexApplication
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AppCompatDelegate
import android.util.Log
import com.style.data.app.AppActivityManager
import com.style.data.db.AppDatabase
import com.style.data.prefs.AppPrefsManager
import com.style.common_ui.refresh.MyAppRefreshLayout
import com.style.toast.ToastManager
import com.taobao.sophix.PatchStatus
import com.taobao.sophix.SophixManager


class MyApp : MultiDexApplication() {
    val TAG = javaClass.simpleName
    val rsaSecret = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQC7d1IQLzGZaRGbPvuUDNfCWj/9OIUc5QuowMU8gq9zsf07rgL30bYJmjc15+Jvvu2sPI9o+WzYXzUj8I/N8kek61N/Lvn505+iDQxd1C/F65g+6xci3/aTXwNiOFuzMu1Sg4sFM5/Caln1UthwDedKue9BSy55zi73IEhWm5Xr4YRYPupCP2bTTOl7tOiOf5Mc+qG22hxbDZMxgcobCYGu341HkGnRP13BzD1CpxNmplJanxqCdkrtSd36+5qWJDfx7QTDfZprcDGlMuBKfyQyjXj47lG3uNFR3MtrNdyxY/4Paza06qyQyRxh4UozvmLc1lU8N82Texx2m3VAiesXAgMBAAECggEBAIIgEgt/5srZHsqHTnP28jFLGg24FNX4uz4ZCQ+2mvkQ/HlXkWHZ5KUHsfkuC4SLoEilBNrV/9K1S5q12ewXl9mHK78lqYjbd3wx2Hqs1bhpj5Vk8/tWQL8M/blI64YY/JkBDBfBPMzjq6vZJLmrPPgm1keZWxpbn0gU1YGgn9FJ1pIC7BoUekwGZszmtLH7E00p0hG9budVuas5+UXJHSiCbphJrNOMYzvLLxrYJCTdYdVZCr0o/sWvB/+2I9rsa1y9DOLelrrogv/noD+sP+qrEmNsF8S33V0iRZbpQ6918k1Y3mV13QIUyhnapZuVy23K5rBuOVHMKrlsH/8K+9ECgYEA/UBYR0TiFfbGloQ7kwrhcSy+05bYMUXfjAht+c8WhVqPMqhtrnMIjY7wxAexwMgaFRnUS4777kBACcnED8pcRMJi+llrAlDsKFbJ+4kloypyU+9JW8SCc+tu5I9+TJhtnHZps3MHiCOf7f8SKP8D/cKyVfADJnef8k2BYm6h9B8CgYEAvYAxPsliwtrGuGxGdUzI5l7ASrKSkykB7OLQijUqu1tEIWiWQJeF8r6qDEPe78ijcW7DjGeosBgOsszZlo0t6asx/sEwZ7uHLsE/EEg+SUOD9kDrpBI4zRPdaAfTe61JxUrmq8pRj+xIA18YrDhwoqhrIyGzplCyCq1U7dXM6gkCgYBayWRcOD9sbEkI0GKi9fWXotjN9XePQmM/Sg5SzYBfUfWfzW10alyYkrORMsjwOUvQYLAQXjPGV1YdrC0TFBI7vAvLf56y99uRInvKJoJNmfveRxvfP/UcJTxdx2sZflNdEb4WzJfYoBzHLveNps4BfHh+Akq+0YePT2WgqdR5NQKBgHxtZ+hqI0b+j+0Ya0sqF9/r5yclCclG69S3OWcGcgCDdMDbA4118KXrO4zCI+gePfOFXzEZ/1Fg+oW8rEKGgNclabZ082upXqH8kYQHa55/jm5rQk6BCHrfyEk+mkZjCkVZYQz704JFCyPrlS+//3VOUMrInkVeRwlrtpBUCjDBAoGBAK+HMwPXd+tAqGlomZ/7sSX3KjNzo4OCNsQ4Uj1m+XqotCxkb8vvQZ5OIwfUXjv4QB4EXh0qoQsW6/xU1KPQ91iDezPNyNR+A70C6RxBPRVK6fdDdHAxs8smN7uFobOnTuPtl70nwllZmxC5UxnuEm9G/Ot2io7qBYbIK0t1LX+M"

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        //initHotfix()
    }

    override fun onCreate() {
        super.onCreate()
        //Thread.setDefaultUncaughtExceptionHandler(AppCrashHandler())
        AppActivityManager.getInstance().init(this)
        AppPrefsManager.getInstance().init(this)
        //room不会自动检查数据库版本升级，所以需要手动操作一次
        AppDatabase.getInstance(this).testRoomDao.getCount()
        initRefreshView()
    }

    private fun initRefreshView() {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)//启用矢量图兼容
        MyAppRefreshLayout.init()
    }

    private fun initHotfix() {
        var appVersion = "1.0.0"
        try {
            appVersion = this.packageManager.getPackageInfo(this.packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                .setEnableDebug(true)
                .setSecretMetaData(null, null, rsaSecret)
                .setPatchLoadStatusStub { mode, code, info, handlePatchVersion ->
                    val msg = StringBuilder("").append("Mode:").append(mode)
                            .append(" Code:").append(code)
                            .append(" Info:").append(info)
                            .append(" HandlePatchVersion:").append(handlePatchVersion).toString()
                    Log.e(TAG, "onLoad->$msg")
                    // 补丁加载回调通知
                    if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                        // 表明补丁加载成功
                    } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                        // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                        // 建议: 用户可以监听进入后台事件, 然后调用killProcessSafely自杀，以此加快应用补丁，详见1.3.2.3
                        SophixManager.getInstance().killProcessSafely()
                    } else {
                        // 其它错误信息, 查看PatchStatus类说明
                    }
                }.initialize()
    }


    //在application中使用//不让其他应用接收到广播
    fun sendLocalBroadcast(intent: Intent) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent)
    }

    fun registerLocalReceiver(receiver: BroadcastReceiver, filter: IntentFilter) {
        //只能接收到LocalBroadcastManager.getInstance(LoginActivity.this).sendBroadcast(bIntent);发送的广播。接收不到系统广播或其他app的广播
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter)
    }

    fun unregisterLocalReceiver(receiver: BroadcastReceiver) {
        androidx.localbroadcastmanager.content.LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }


}
