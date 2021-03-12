package pt.isel.tests.drag.initMenu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import pt.isel.tests.drag.R

class InitMenuActivity : AppCompatActivity() {

    companion object{
        fun activityIntent(context: Context) = Intent(context, InitMenuActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_init_menu)
    }

    fun offlineMode(view: View) = startActivity(activityIntent(this))
    fun onlineMode(view: View) = startActivity(activityIntent(this))
}