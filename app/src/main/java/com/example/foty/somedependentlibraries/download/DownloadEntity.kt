package com.example.foty.somedependentlibraries.download

/**
 * 封装下载对象
 */
class DownloadEntity(
    val key: String,
    val fileName: String,
    val fileLength: Long = 0L,
    val fileType: String = ""
)