package com.example.foty.somedependentlibraries.download

import android.content.Context

interface IDownloadManager {

    fun downloadFile(
        url: String,
        key: String,
        fileName: String,
        fileLength: Long,
        mimeType: String,
        listener: IFileDownloadListener
    )

    fun checkFileIsDownloaded(url: String, fileName: String): Boolean

    fun checkFileIsDownloading(url: String, fileName: String): Boolean

    fun getDownloadSoFarBytes(key: String): Long

    fun getDownloadedFilePath(url: String, fileName: String): String

    fun setDownloadListener(url: String, key: String, listener: IFileDownloadListener): Boolean

    fun cancelDownload(url: String, fileName: String, key: String)

    fun saveFile(
        context: Context,
        sandPath: String,
        fileName: String,
        mimeType: String,
        callback: (String) -> Unit = {}
    )

    fun saveMediaToDCIM(context: Context, sandPath: String, fileName: String, mimeType: String)

    fun clearAllTaskData()

    fun destroy()

}