package com.example.foty.somedependentlibraries.download

interface IFileDownloadListener {

    fun progress(soFarBytes: Int, totalBytes: Int, speed: Int)

    fun completed()

    fun error(e: Throwable?)

    fun paused(soFarBytes: Int, totalBytes: Int)

    fun connected(eTag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int)
}

open class SimpleDownloadListener : IFileDownloadListener {
    override fun progress(soFarBytes: Int, totalBytes: Int, speed: Int) {}

    override fun completed() {}

    override fun error(e: Throwable?) {}

    override fun paused(soFarBytes: Int, totalBytes: Int) {}

    override fun connected(eTag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {}

}