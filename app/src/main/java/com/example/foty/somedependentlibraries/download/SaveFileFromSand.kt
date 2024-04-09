package com.videocreator.downloadimpl

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.OutputStream

object SaveFileFromSand {

    private const val BUFFER_SIZE = 1024 * 2


    suspend fun saveFileToDocument(
        context: Context,
        sandPath: String,
        fileName: String,
        mimeType: String
    ): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveFileToDocumentApiQ(context, sandPath, fileName, mimeType)
        } else {
            copyFileToEnvironment(sandPath, fileName, mimeType)
        }
    }

    suspend fun saveMediaToDCIM(
        context: Context,
        sandPath: String,
        fileName: String,
        mimeType: String
    ): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return saveMediaToDCIMApiQ(context, sandPath, fileName, mimeType)
        } else {
            return copyFileToEnvironment(sandPath, fileName, mimeType)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveFileToDocumentApiQ(
        context: Context,
        sandPath: String,
        fileName: String,
        mimeType: String
    ): String? {
        val contentValues = ContentValues()
        val timestamp = System.currentTimeMillis()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            contentValues.put(
                MediaStore.Files.FileColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS
            )
        }
        contentValues.put(MediaStore.Files.FileColumns.TITLE, fileName)
        contentValues.put(MediaStore.Files.FileColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.Files.FileColumns.MIME_TYPE, mimeType)
        contentValues.put(MediaStore.Files.FileColumns.DATE_TAKEN, timestamp)
        contentValues.put(MediaStore.Files.FileColumns.DATE_MODIFIED, timestamp)
        contentValues.put(MediaStore.Files.FileColumns.DATE_ADDED, timestamp)
//        contentValues.put(MediaStore.Files.FileColumns.DATA, sandPath)
        contentValues.put(MediaStore.Files.FileColumns.SIZE, File(sandPath).length())

        val uri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val externalUri = context.contentResolver.insert(uri, contentValues) ?: return null
        return copySandFileToExternalUri(context, sandPath, externalUri)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveMediaToDCIMApiQ(
        context: Context,
        sandPath: String,
        fileName: String,
        mimeType: String
    ): String? {
        val contentValues = ContentValues()
        val timestamp = System.currentTimeMillis()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        }
        contentValues.put(MediaStore.MediaColumns.TITLE, fileName)
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        contentValues.put(MediaStore.MediaColumns.DATE_TAKEN, timestamp)
        contentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, timestamp)
        contentValues.put(MediaStore.MediaColumns.DATE_ADDED, timestamp)
//        contentValues.put(MediaStore.MediaColumns.DATA, sandPath)
        contentValues.put(MediaStore.MediaColumns.SIZE, File(sandPath).length())

        val uri = if (mimeType.startsWith("image")) Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        else MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        val externalUri = context.contentResolver.insert(uri, contentValues) ?: return null
        return copySandFileToExternalUri(context, sandPath, externalUri)
    }

    /**
     * 从沙盒中保存文件到共享目录下
     */
    private fun copySandFileToExternalUri(
        context: Context,
        file: String,
        externalUri: Uri
    ): String? {
        val contentResolver = context.contentResolver
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var ret = false
        kotlin.runCatching {
            val buffer = ByteArray(BUFFER_SIZE)
            outputStream = contentResolver.openOutputStream(externalUri)
            inputStream = FileInputStream(file)
            val sandFile = File(file)
            if (sandFile.exists()) {
                var n = 0
                while (inputStream?.read(buffer, 0, BUFFER_SIZE)?.also { n = it } != -1) {
                    outputStream?.write(buffer, 0, n)
                }
                outputStream?.flush()
            }
            ret = true
            outputStream?.close()
            inputStream?.close()
        }.onFailure {
            outputStream?.close()
            inputStream?.close()
            ret = false
        }
        return if (ret) externalUri.toString() else null
    }

    /**
     * 如果出现重名的，则需要重新命名
     */
    private fun getFileNameWithAdjust(dirPath: String, fileName: String, count: Int = 0): String {
        val newFileName =
            fileName.substringBeforeLast(".") + (if (count == 0) "." else " ($count).") +
                    fileName.substringAfterLast(".")
        return if (File(dirPath, newFileName).exists()) {
            getFileNameWithAdjust(dirPath, fileName, count + 1)
        } else newFileName
    }

    /**
     * 复制文件到相应的目录
     */
    private fun copyFileToEnvironment(
        sandPath: String,
        fileName: String,
        mimeType: String
    ): String? {
        val dirPath = (if (mimeType.startsWith("image")) {
            Environment.DIRECTORY_DCIM
        } else if (mimeType.startsWith("video")) {
            Environment.DIRECTORY_MOVIES
        } else if (mimeType.startsWith("audio")) {
            Environment.DIRECTORY_MUSIC
        } else "").run {
            Environment.getExternalStoragePublicDirectory(this)
        }
        val newFile = getFileNameWithAdjust(dirPath.absolutePath, fileName)
        val newPath = File(dirPath, newFile)
        return File(sandPath).copyTo(newPath).run { if (exists()) newPath.absolutePath else null }
    }

    /**
     * 检测文件在本地是否存在
     */
    suspend fun checkFileIsExist(context: Context, uri: String): String? {
        return Uri.parse(uri).run {
            if (isAbsolute) getRealFilePath(context, this)
            else File(uri).exists().run { if (this) uri else null }
        }
    }

    private fun getRealFilePath(context: Context, uri: Uri): String? {
        val scheme = uri.scheme
        return if (scheme == null) uri.path
        else if (ContentResolver.SCHEME_FILE == scheme) uri.path
        else if (ContentResolver.SCHEME_CONTENT == scheme) {
            context.contentResolver.query(uri, null, null, null, null)?.run {
                if (moveToFirst()) {
                    val index = getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) return@run getString(index)
                }
                close()
                return@run null
            }
        } else null
    }
}