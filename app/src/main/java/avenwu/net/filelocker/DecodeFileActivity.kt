package avenwu.net.filelocker

import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import java.io.File
import java.net.URI
import java.util.*
import kotlin.text.startsWith

/**
 * Created by aven on 1/8/16.
 */
class DecodeFileActivity : AppCompatActivity() {
    var mSrcFileLabel: TextView? = null
    var mDesFileLabel: TextView? = null
    var mProgressView: ProgressBar? = null
    var mButtonConfirm: Button? = null
    var mTaskList = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.decode_file_edit_layout)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        mSrcFileLabel = findViewById(R.id.tv_file_name) as TextView
        mDesFileLabel = findViewById(R.id.tv_file_name_encode) as TextView
        mProgressView = findViewById(R.id.progressBar) as ProgressBar
        mButtonConfirm = findViewById(R.id.btn_translate) as Button

        intent.dataString?.let { uri ->
            var tmp = getFile(uri)
            if (!tmp.exists()) {
                alert("File not exist!!($uri)")
                return
            }

            var size = getReadableSize(tmp.length().toDouble())
            mSrcFileLabel?.text = tmp.absolutePath + "($size)"

            mButtonConfirm?.setOnClickListener({
                var tmpSrc = getFile(uri)
                getDecodeFile(it.context, tmpSrc.name)?.let { des ->
                    mDesFileLabel?.text = des.absolutePath
                    mTaskList.add(decode(tmpSrc, des, { bytes, percent ->
                        mProgressView?.progress = percent?.times(100)?.toInt()
                    }, { result ->
                        result?.let {
                            if (result.success) {
                                mProgressView?.progress = 100
                                var size = getReadableSize(des.length().toDouble());
                                mDesFileLabel?.text = des.absolutePath + "($size})"
                                alert("Decode completed!")
                            } else {
                                alert("Decode failed!!(${result.msg})")
                            }
                        }
                    }))
                }
            })
        }
    }

    fun getFile(path: String): File {
        if (path.startsWith("file://")) {
            return File(URI.create(path))
        } else {
            return File(path)
        }
    }

    fun alert(msg: String) {
        Snackbar.make(mSrcFileLabel, msg, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        for (task in mTaskList) {
            if (task is AsyncTask<*, *, *>) {
                task.cancel(true)
            }
        }
    }
}