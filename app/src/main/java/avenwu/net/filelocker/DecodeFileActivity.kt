package avenwu.net.filelocker

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import java.util.*
import kotlin.text.lastIndexOf
import kotlin.text.substring
import kotlin.text.toLowerCase

/**
 * Created by aven on 1/8/16.
 */
class DecodeFileActivity : BaseToolbarActivity() {
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
                getDecodeFile(tmpSrc.name)?.let { des ->
                    mDesFileLabel?.text = des.absolutePath
                    mTaskList.add(decode(tmpSrc, des, { bytes, percent ->
                        mProgressView?.progress = percent?.times(100)?.toInt()
                    }, { result ->
                        result?.let {
                            if (result.success) {
                                mProgressView?.progress = 100
                                var size = getReadableSize(des.length().toDouble());
                                mDesFileLabel?.visibility = View.VISIBLE
                                mDesFileLabel?.text = des.absolutePath + "($size})"
                                alert("Decode completed!")
                                mDesFileLabel?.setOnClickListener({
                                    var viewFileIntent = Intent(Intent.ACTION_VIEW)
                                    var extension = des.absolutePath.substring(des.absolutePath.lastIndexOf(".") + 1, des.absolutePath.length);
                                    viewFileIntent.setDataAndType(Uri.fromFile(des), getNormalMime(extension.toLowerCase()))
                                    startActivity(viewFileIntent)
                                })
                            } else {
                                alert("Decode failed!!(${result.msg})")
                            }
                        }
                    }))
                }
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