package avenwu.net.filelocker

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.os.AsyncTaskCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.io.File
import java.util.*

/**
 * Created by aven on 1/11/16.
 */
public class EncodedListActivity : AppCompatActivity() {

    var mTaskList = ArrayList<Any>()
    var mData: Array<File>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encoded_list_layout)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        var recyclerView = findViewById(R.id.recyclerview) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun getItemCount(): Int {
                mData?.let {
                    return it.size
                }
                return 0
            }

            override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
                mData?.let {
                    if (it.size > position) {
                        holder?.bind(it[position].absolutePath)
                    }
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder? {
                return ViewHolder(View.inflate(parent?.context, R.layout.file_item, null))
            }
        }

        var task = object : AsyncTask<Void, Void, Array<File>>() {
            override fun doInBackground(vararg params: Void?): Array<File>? {
                return getEncodeFileList()
            }

            override fun onPostExecute(result: Array<File>?) {
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
            itemView.setOnClickListener({ v ->
                v.tag?.let {
                    var i = Intent(v.context, DecodeFileActivity::class.java)
                    i.setData(Uri.parse(it as String))
                    v.context.startActivity(i)
                }
            })
        }

        fun bind(text: String) {
            name?.text = text
            itemView.tag = text
        }
    }
}