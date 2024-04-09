package com.videocreator.downloadimpl

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.aipai.util.activitystack.IActivityStackManager
import com.feiteng.lieyou.lego.router.lieyouApplication
import com.feiteng.lieyou.lego.router.lieyouGetService
import com.liulishuo.filedownloader.model.FileDownloadStatus
import com.liulishuo.filedownloader.notification.BaseNotificationItem

private const val FILE_DOWNLOAD_CHANNEL_ID = "FileDownloadChannelId"
private const val FILE_DOWNLOAD_CHANNEL_NAME = "FileDownload"

class FileNotificationItem(id: Int, title: String, desc: String) :
    BaseNotificationItem(id, title, desc) {

    /**
     * 获取metaData可能有兼容问题，部分机型会崩溃
     */
    private fun getManifestMetadata(): Bundle = kotlin.runCatching {
        lieyouApplication().packageManager.getApplicationInfo(
            lieyouApplication().packageName,
            PackageManager.GET_META_DATA
        ).metaData
    }.getOrNull() ?: Bundle.EMPTY

    private val builder by lazy {
        (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            checkAndCreateChannel()
            NotificationCompat.Builder(lieyouApplication(), FILE_DOWNLOAD_CHANNEL_ID)
        } else {
            NotificationCompat.Builder(lieyouApplication())
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setPriority(NotificationCompat.PRIORITY_MIN)
        }).run {
            setSound(null)
            setContentTitle(getTitle())
            setContentText(desc)
            setSmallIcon(smallIcon)
            setOnlyAlertOnce(true) // 只需要提醒一次，不需要反复提醒用户上传的进度
        }
    }

    private val smallIcon by lazy {
        getManifestMetadata().getInt("com.google.firebase.messaging.default_notification_icon", 0)
    }

    override fun show(statusChanged: Boolean, status: Int, isShowProgress: Boolean) {
        if (lieyouGetService<IActivityStackManager>().isForeground){
            cancel()
            return
        }
        when (status.toByte()) {
            FileDownloadStatus.pending -> {
                builder.setProgress(total, sofar, true)
            }
            FileDownloadStatus.started -> {
                builder.setProgress(total, sofar, true)
            }
            FileDownloadStatus.progress -> {
                builder.setProgress(total, sofar, total <= 0)
            }
            FileDownloadStatus.retry -> {
                builder.setProgress(total, sofar, true)
            }
            FileDownloadStatus.error -> {
                builder.setProgress(total, sofar, false)
            }
            FileDownloadStatus.paused -> {
                builder.setProgress(total, sofar, false)
            }
            FileDownloadStatus.completed -> {
                builder.setProgress(total, sofar, false)
            }
            FileDownloadStatus.warn -> {
                builder.setProgress(total, sofar, false)
            }
        }
        builder.setContentTitle(title)
            .setContentText(desc)
            .setSmallIcon(R.drawable.ic_common_launcher)
        manager.notify(id, builder.build())

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAndCreateChannel() {
        if (manager?.getNotificationChannel(FILE_DOWNLOAD_CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            FILE_DOWNLOAD_CHANNEL_ID,
            FILE_DOWNLOAD_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        channel.setSound(null,null)
        channel.setShowBadge(false) // 不计算角标数
        manager?.createNotificationChannel(channel)
    }

}