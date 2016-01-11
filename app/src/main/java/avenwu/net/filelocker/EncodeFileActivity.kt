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

/**
 * Created by aven on 1/8/16.
 */
class EncodeFileActivity : AppCompatActivity() {
    var mSrcFileLabel: TextView? = null
    var mDesFileLabel: TextView? = null
    var mProgressView: ProgressBar? = null
    var mButtonConfirm: Button? = null
    var mTaskList = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lock_file_edit_layout)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        mSrcFileLabel = findViewById(R.id.tv_file_name) as TextView
        mDesFileLabel = findViewById(R.id.tv_file_name_encode) as TextView
        mProgressView = findViewById(R.id.progressBar) as ProgressBar
        mButtonConfirm = findViewById(R.id.btn_translate) as Button

        intent.dataString?.let { uri ->
            var tmp = File(URI.create(uri))
            if (!tmp.exists()) {
                alert("File not exist!!($uri)")
                return
            }

            var size = getReadableSize(tmp.length().toDouble())
            mSrcFileLabel?.text = tmp.absolutePath + "($size)"

            mButtonConfirm?.setOnClickListener({
                var tmpSrc = File(URI.create(uri))
                var des = getEncodeFile(it.context, tmpSrc.name)
                mDesFileLabel?.text = des.absolutePath
                mTaskList.add(encode(tmpSrc, des, { bytes, percent ->
                    mProgressView?.progress = percent?.times(100)?.toInt()
                }, { result ->
                    result?.let {
                        if (result.success) {
                            mProgressView?.progress = 100
                            var size = getReadableSize(des.length().toDouble());
                            mDesFileLabel?.text = des.absolutePath + "($size})"
                            alert("Encode completed!")
                        } else {
                            alert("Encode failed!!(${result.msg})")
                        }
                    }
                }))
            })
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