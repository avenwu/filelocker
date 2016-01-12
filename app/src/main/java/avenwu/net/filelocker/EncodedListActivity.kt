package avenwu.net.filelocker

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.os.AsyncTaskCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*
import kotlin.collections.forEach

/**
 * Created by aven on 1/11/16.
 */
public class EncodedListActivity : BaseToolbarActivity() {

    var mTaskList = ArrayList<Any>()
    var mData: ArrayList<FileItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encoded_list_layout)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar.setDisplayShowHomeEnabled(true)
        var recyclerView = findViewById(R.id.recyclerview) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
            }
        })
        recyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun getItemCount(): Int {
                mData?.let {
                    return it.size
                }
                return 0
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
                mData?.let {
                    if (it.size > position) {
                        if (holder is HeaderViewHolder) {
                            holder.bind(it[position])
                        } else if (holder is ViewHolder) {
                            holder.bind(it[position])
                        }
                    }
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
                if ( viewType == 0) {
                    return ViewHolder(View.inflate(parent?.context, R.layout.file_item, null))
                } else {
                    return HeaderViewHolder(View.inflate(parent?.context, R.layout.item_header, null))
                }
            }

            override fun getItemViewType(position: Int): Int {
                return mData?.get(position)?.type ?: 1
            }
        }

        var task = object : AsyncTask<Void, Void, ArrayList<FileItem>>() {
            override fun doInBackground(vararg params: Void?): ArrayList<FileItem>? {
                var dataList = ArrayList<FileItem>()
                getNormalFileList()?.let {
                    dataList.add(FileItem("Normal", false, 1))
                    it.forEach {
                        dataList.add(FileItem(it.absolutePath, false, 0))
                    }
                }
                getEncodeFileList()?.let {
                    dataList.add(FileItem("Encode", false, 1))
                    it.forEach {
                        dataList.add(FileItem(it.absolutePath, true, 0))
                    }
                }
                return dataList
            }

            override fun onPostExecute(result: ArrayList<FileItem>?) {
                mData = result
                recyclerView.adapter.notifyDataSetChanged()
            }
        }
        AsyncTaskCompat.executeParallel(task)
        mTaskList.add(task)
    }

    override fun onDestroy() {
        super.onDestroy()
        for (task in mTaskList) {
            if (task is AsyncTask<*, *, *>) {
                task.cancel(true)
            }
        }
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = null

        init {
            name = itemView.findViewById(R.id.tv_file_name) as TextView
            name?.setOnClickListener({ v ->
                v.tag?.let { data ->
                    if (data is FileItem) {
                        if (data.encoded) {
                            var i = Intent(v.context, DecodeFileActivity::class.java)
                            i.setData(Uri.parse(data.name))
                            v.context.startActivity(i)
                        } else {
                            var i = Intent(v.context, EncodeFileActivity::class.java)
                            i.setData(Uri.parse(data.name))
                            v.context.startActivity(i)
                        }
                    }
                }
            })
        }

        fun bind(data: FileItem) {
            name?.text = data.name
            name?.tag = data
        }
    }

    internal class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var name: TextView? = null

        init {
            name = itemView.findViewById(R.id.tv_header_label) as TextView
            itemView.setOnClickListener({
                it.context.openFolder();
            })
            var l = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT)
            itemView.layoutParams = l
        }

        fun bind(data: FileItem) {
            name?.text = data.name
        }
    }
}