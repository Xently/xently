package co.ke.xently.libraries.ui.core

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

fun Context.openUrl(url: String?, vararg backupUrls: String?) {
    val urls = buildList {
        add(url)
        addAll(backupUrls)
    }

    for (u in urls) {
        val uri = u?.let { Uri.parse(it) }
            ?: continue

        val intent = Intent().apply {
            action = Intent.ACTION_VIEW
            data = uri
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            startActivity(intent)
            break
        } catch (_: ActivityNotFoundException) {
            continue
        }
    }
}
