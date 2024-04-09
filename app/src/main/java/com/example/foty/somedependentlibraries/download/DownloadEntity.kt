package com.example.foty.somedependentlibraries.download

class DownloadEntity(
    val key: String,
    val fileName: String,
    val fileLength: Long = 0L,
    val fileType: String = ""
)