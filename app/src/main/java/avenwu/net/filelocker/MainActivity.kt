package avenwu.net.filelocker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import kotlin.text.endsWith

class MainActivity : BaseToolbarActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        toolbar.navigationIcon = null
        setSupportActionBar(toolbar)
        supportActionBar.setDisplayShowHomeEnabled(false)

        intent.dataString?.let {
            var uri = it
            var i: Intent
            if (uri.endsWith(EXTENSION)) {
                i = Intent(this, DecodeFileActivity::class.java)
            } else {
                i = Intent(this, EncodeFileActivity::class.java)
            }
            i.setData(Uri.parse(uri))
            startActivity(i)
        }
        findViewById(R.id.btn_show_encode_list).setOnClickListener({
            startActivity(Intent(this, EncodedListActivity::class.java))
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_settings) {
            //            startActivity(Intent(this, EncodedListActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
