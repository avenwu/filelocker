package avenwu.net.filelocker

import android.app.Activity
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.support.v4.os.AsyncTaskCompat
import android.util.Base64
import java.io.*
import java.nio.charset.Charset
import kotlin.collections.toString
import kotlin.text.contentEquals
import kotlin.text.endsWith
import kotlin.text.indexOf
import kotlin.text.toByteArray

/**
 * Created by aven on 1/11/16.
 */

val EXTENSION = ".encode"
val FOLDER = "fileLocker"

public fun getPublicDir(): File {
    var dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    if (!dir.exists()) {
        dir.mkdir()
    }
    var fileLocker = File(dir, FOLDER);
    if (!fileLocker.exists()) {
        fileLocker.mkdir()
    }
    return fileLocker
}

public fun getEncodeFile(fileName: String): File {
    var encodeName = Base64.encodeToString(fileName.toByteArray(), Base64.DEFAULT) + EXTENSION
    var des = File(getPublicDir(), encodeName)
    return des
}

public fun getDecodeFile(fileName: String): File? {
    var index = fileName.indexOf(EXTENSION)
    if (index > -1) {
        var decodeName = Base64.decode(fileName.subSequence(0, index).toString(), Base64.DEFAULT)
        var des = File(getPublicDir(), decodeName.toString(Charset.defaultCharset()))
        return des
    }
    return null
}

public fun getEncodeFileList(): Array<File>? {
    var dir = getPublicDir()
    var files = dir.listFiles({ dir, fileName ->
        fileName.endsWith(EXTENSION)
    })
    return files;
}

public fun encode(src: File, des: File, progress: (Long?, Float?) -> Unit, result: (Result?) -> Unit): Task {
    var task = object : Task(progress, result) {
        override fun translate(c: Byte): Byte {
            return c.plus('1'.toByte()).toByte()
        }
    }
    AsyncTaskCompat.executeParallel(task, src, des)
    return task
}

public fun decode(src: File, des: File, progress: (Long?, Float?) -> Unit, result: (Result?) -> Unit): Task {
    var task = object : Task(progress, result) {
        override fun translate(c: Byte): Byte {
            return c.minus('1'.toByte()).toByte()
        }
    }
    AsyncTaskCompat.executeParallel(task, src, des)
    return task
}

abstract class Task(progress: (Long?, Float?) -> Unit, result: (Result?) -> Unit) : AsyncTask<File, Long, Result>() {
    var progressBlock: (Long?, Float?) -> Unit
    var resultBlock: (Result?) -> Unit;

    init {
        progressBlock = progress;
        resultBlock = result;
    }

    override fun doInBackground(vararg params: File?): Result? {
        if (params.size > 0 && params[0] != null && params[1] != null) {
            var src = params[0]
            var des = params[1]
            var inputStream: InputStream
            var outputStream: OutputStream
            try {

                inputStream = FileInputStream(src)
                outputStream = FileOutputStream(des)
                var buffer = ByteArray(1024 * 8)
                var length = 0
                var timestamp = System.currentTimeMillis()
                var totalLength = src?.length()
                var readLength = 0L;
                try {
                    do {
                        length = inputStream.read(buffer)
                        if (length > 0) {
                            for (i in 0..length - 1) {
                                val c = buffer[i]
                                val d = translate(c)
                                buffer[i] = d
                            }
                            outputStream.write(buffer, 0, length)
                            readLength += length.toLong()
                            if (System.currentTimeMillis() - timestamp >= 1000) {
                                timestamp = System.currentTimeMillis()
                                publishProgress(readLength, totalLength)
                            }
                        }
                    } while (length > 0)
                    return Result(true, "Complete")
                } catch(e: Exception) {
                    return Result(false, "IO exception while reading file")
                }
            } catch(e: Exception) {
                return Result(false, "File can not be found")
            }
        }
        return Result(false, "File can not be null")
    }

    override fun onProgressUpdate(vararg values: Long?) {
        progressBlock.invoke(values[0], values[0]?.div(values[1] as Float))
    }

    override fun onPostExecute(result: Result?) {
        resultBlock.invoke(result)
    }

    internal abstract fun translate(c: Byte): Byte
}

public data class Result(var success: Boolean, var msg: String);

val ONE_KB: Long = 1024
val ONE_MB = ONE_KB * ONE_KB
val ONE_GB = ONE_KB * ONE_MB

public fun getReadableSize(bytes: Double): String {
    val df = java.text.DecimalFormat("#.00")
    var fileSizeString = ""
    if (bytes < ONE_KB) {
        fileSizeString = df.format(bytes) + "B"
    } else if (bytes < ONE_MB) {
        fileSizeString = df.format(bytes / ONE_KB) + "KB"
    } else if (bytes < ONE_GB) {
        fileSizeString = df.format(bytes / ONE_MB) + "MB"
    } else {
        fileSizeString = df.format(bytes / ONE_GB) + "G"
    }
    return fileSizeString
}


public fun getNormalMime(extension: String): String {
    var images = arrayOf("png", "jpg", "jpeg", "git", "bmp")
    for (i in images) {
        if (i.contentEquals(extension)) {
            return "image/$i"
        }
    }
    var videos = arrayOf("mp4", "avi", "rmvb", "rm", "3gp")
    for (i in videos) {
        if (i.contentEquals(extension)) {
            return "video/$i"
        }
    }
    return "*/$extension"
}

fun Activity.getFile(path: String): File {
    try {
        var convertedPath = ContentUriToFile.getPath(this, Uri.parse(path))
        return File(convertedPath)
    } catch(e: Exception) {
        e.printStackTrace()
    }
    return File(path)
}