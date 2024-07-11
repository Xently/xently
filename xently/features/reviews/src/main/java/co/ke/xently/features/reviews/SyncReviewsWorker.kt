package co.ke.xently.features.reviews

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.Operation
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import co.ke.xently.features.reviews.data.source.ReviewRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException
import java.util.concurrent.TimeUnit

@HiltWorker
class SyncReviewsWorker @AssistedInject constructor(
    @Assisted applicationContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: ReviewRepository,
) : CoroutineWorker(applicationContext, params) {
    override suspend fun doWork(): Result {
        return try {
            repository.syncWithServer()
            Result.success()
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Result.retry()
        }
    }

    companion object {
        fun start(context: Context): Operation {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresBatteryNotLow(true)
                .build()

            val syncReviewsWorkRequest =
                PeriodicWorkRequestBuilder<SyncReviewsWorker>(15, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build()

            return WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    SyncReviewsWorker::class.java.simpleName,
                    ExistingPeriodicWorkPolicy.KEEP,
                    syncReviewsWorkRequest,
                )
        }
    }
}