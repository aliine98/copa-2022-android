package me.dio.copa.catar.notification.scheduler.extensions

import android.content.Context
import androidx.work.*
import me.dio.copa.catar.domain.model.MatchDomain
import java.time.Duration
import java.time.LocalDateTime

class NotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val title = inputData.getString(TITLE) ?: throw IllegalArgumentException("Título obrigatório!")
        val message = inputData.getString(MESSAGE) ?: throw IllegalArgumentException("Mensagem obrigatória!")

        context.showNotification(title,message)

        return Result.success()
    }

    companion object {
        const val TITLE = "TITLE"
        const val MESSAGE = "MESSAGE"
        fun start(context: Context, matchDomain: MatchDomain) {
            val delay = Duration.between(LocalDateTime.now(),matchDomain.date).minusMinutes(5)

            val inputData = workDataOf(
                TITLE to "${matchDomain.name} está prestes a começar!",
                MESSAGE to "Hoje tem ${matchDomain.team1.flag} ${matchDomain.team1.displayName} X ${matchDomain.team2
                    .flag} ${matchDomain.team2.displayName}"
            )
            
            WorkManager.getInstance(context)
                .enqueueUniqueWork(matchDomain.id,ExistingWorkPolicy.KEEP, createRequest(delay,inputData))
        }

        fun cancel(context: Context,matchDomain: MatchDomain) {
            WorkManager.getInstance(context).cancelUniqueWork(matchDomain.id)
        }

        private fun createRequest(delay: Duration, inputData: Data): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<NotificationWorker>().setInitialDelay(delay).setInputData(inputData).build()
        }
    }
}