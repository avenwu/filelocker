package avenwu.net.filelocker

import android.support.v7.app.AppCompatActivity
import android.view.MenuItem

/**
 * Created by aven on 1/12/16.
 */
public abstract class BaseToolbarActivity : AppCompatActivity() {
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item)
    }

}