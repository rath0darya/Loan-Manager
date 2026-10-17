package com.rath0darya.loanmanager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class LoanReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        val loanId = inputData.getLong("loanId", -1L)
        val person = inputData.getString("personName") ?: "your contact"
        if (loanId < 0) return Result.failure()

        val channelId = "loan_due_reminders"
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "Loan due reminders", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Local reminders for loan due dates"
                }
            )
        }
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Loan payment reminder")
            .setContentText("Check the loan record for $person.")
            .setStyle(NotificationCompat.BigTextStyle().bigText("A loan record for $person has reached its reminder date. Open Loan Manager to review the balance and record any payment."))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        return try {
            NotificationManagerCompat.from(applicationContext).notify((loanId % Int.MAX_VALUE).toInt(), notification)
            Result.success()
        } catch (_: SecurityException) {
            Result.success()
        }
    }
}
