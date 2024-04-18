import android.content.Context
import com.example.foty.somedependentlibraries.App
import com.example.foty.somedependentlibraries.download.DownloadEntity
import com.example.foty.somedependentlibraries.download.IDownloadManager
import com.example.foty.somedependentlibraries.download.IFileDownloadListener
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.model.FileDownloadStatus
import com.liulishuo.filedownloader.notification.BaseNotificationItem
import com.liulishuo.filedownloader.util.FileDownloadUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * 下载管理器
 */
class DownloadManager : IDownloadManager, FileDownloadListener(),
    CoroutineScope {

    private val newThread by lazy { newSingleThreadContext("SaveFile") }

    override val coroutineContext: CoroutineContext
        get() = newThread

    private val downloadImpl by lazy {
        FileDownloader.getImpl()
    }

    /**
     * (公开)监听器映射表
     */
    private val listenerMap by lazy { HashMap<Int, IFileDownloadListener>() }

    /**
     * 下载任务映射表
     */
    private val downloadMap by lazy { hashMapOf<String, Int>() }

    /**
     * 下载路径
     */
    // TODO: 补充路径
    private val downloadDir = ""

    /**
     * 初始化
     * @param context
     */
    init {
        FileDownloader.setup(App.getApplication())
    }

    /**
     * 文件下载
     */
    @Synchronized
    override fun downloadFile(
        url: String,
        key: String,
        fileName: String,
        fileLength: Long,
        mimeType: String,
        listener: IFileDownloadListener
    ) {
        downloadMap.getOrPut(key) {
            val filePath = getFileWithFileName(url, fileName)
            downloadImpl.create(url)
                .setPath(filePath, false)
                .setTag(DownloadEntity(key, fileName, fileLength, mimeType))
                .setListener(this)
                .setAutoRetryTimes(0)
                .start()
        }.run { listenerMap.put(this, listener) }
    }

    /**
     * 对外公开的Listener
     */
    override fun setDownloadListener(
        url: String,
        key: String,
        listener: IFileDownloadListener
    ): Boolean {
        return downloadMap[key]?.run { listenerMap.put(this, listener) } != null
    }

    override fun getDownloadSoFarBytes(key: String): Long {
        return downloadMap.get(key)?.let { downloadImpl.getSoFar(it) } ?: 0L
    }

    override fun getDownloadedFilePath(url: String, fileName: String): String {
        return File(getFileWithFileName(url, fileName)).run { if (exists()) absolutePath else "" }
    }

    override fun checkFileIsDownloaded(url: String, fileName: String): Boolean {
        val filePath = getFileWithFileName(url, fileName)
        if (File(filePath).exists()) return true
        return downloadImpl.getStatus(url, filePath) == FileDownloadStatus.completed
    }

    override fun checkFileIsDownloading(url: String, fileName: String): Boolean {
        val filePath = getFileWithFileName(url, fileName)
        val status = downloadImpl.getStatus(url, filePath)
        return status == FileDownloadStatus.progress || status == FileDownloadStatus.connected || status == FileDownloadStatus.pending
    }

    override fun cancelDownload(url: String, fileName: String, key: String) {
        downloadMap.get(key)?.run {
            val filePath = getFileWithFileName(url, fileName)
            downloadImpl.clear(this, filePath)
        }
    }

    override fun connected(
        task: BaseDownloadTask?,
        etag: String?,
        isContinue: Boolean,
        soFarBytes: Int,
        totalBytes: Int
    ) {
        super.connected(task, etag, isContinue, soFarBytes, totalBytes)
        task?.let {
            it.filename
            listenerMap[it.id]?.connected(etag, isContinue, soFarBytes, totalBytes)
        }
    }

    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
        task?.let {
            it.filename
            val length = if (totalBytes == 0) (it.tag as? DownloadEntity)?.fileLength?.toInt()
                ?: 0 else totalBytes
            listenerMap[it.id]?.connected("", true, soFarBytes, length)
        }
    }

    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
        task?.let {
            listenerMap[it.id]?.progress(soFarBytes, totalBytes, it.speed)
        }
    }

    override fun completed(task: BaseDownloadTask?) {
        task?.let {
            listenerMap[it.id]?.completed()
            listenerMap.remove(it.id)
            (it.tag as? DownloadEntity)?.run { downloadMap.remove(key) }
        }
    }

    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
        task?.let {
            listenerMap[it.id]?.paused(soFarBytes, totalBytes)
            listenerMap.remove(it.id)
            (it.tag as? DownloadEntity)?.run { downloadMap.remove(key) }

        }
    }

    override fun error(task: BaseDownloadTask?, e: Throwable?) {

        task?.let {
            listenerMap[it.id]?.error(e)
            listenerMap.remove(it.id)
            (it.tag as? DownloadEntity)?.run { downloadMap.remove(key) }
        }
    }

    override fun warn(task: BaseDownloadTask?) {
        task?.let {
            listenerMap[it.id]?.error(null)
            listenerMap.remove(it.id)
            (it.tag as? DownloadEntity)?.run { downloadMap.remove(key) }
        }
    }

    /**
     * 保存文件时使用单线程。防止复制的时候，出现文件名重复的问题
     */
    override fun saveFile(
        context: Context,
        sandPath: String,
        fileName: String,
        mimeType: String,
        callback: (String) -> Unit
    ) {
        launch(coroutineContext) {
            throw Exception("重写保存文件方法")
        }
    }

    /**
     * 保存文件
     */
    override fun saveMediaToDCIM(
        context: Context,
        sandPath: String,
        fileName: String,
        mimeType: String
    ) {
        launch(coroutineContext) {
            throw Exception("重写保存文件方法")
        }
    }

    override fun clearAllTaskData() {
        downloadImpl.clearAllTaskData()
    }

    override fun destroy() {
        listenerMap.clear()
        downloadMap.clear()
        downloadImpl.clearAllTaskData()
    }

    /**
     * 生成文件路径和名称
     */
    private fun getFileWithFileName(url: String, fileName: String): String {
        val targetFileName = FileDownloadUtils.generateFileName(url) + "/" + fileName
        return FileDownloadUtils.generateFilePath(downloadDir, targetFileName)
    }

}