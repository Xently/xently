package co.ke.xently.business

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        val tree = if (BuildConfig.DEBUG) {
            object : Timber.DebugTree() {
                /*override fun createStackElementTag(element: StackTraceElement): String {
                    return "Class:${super.createStackElementTag(element)}: Line: ${element.lineNumber}, Method: ${element.methodName}"
                }*/
            }
        } else {
            object : Timber.Tree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    if (priority == Log.ERROR || priority == Log.WARN) {
                        t?.also(Firebase.crashlytics::recordException)
                            ?: tag?.also {
                                Firebase.crashlytics.setCustomKey(it, message)
                            }
                            ?: Firebase.crashlytics.log(message)
                    }
                }
            }
        }

        Timber.plant(tree)
    }
}