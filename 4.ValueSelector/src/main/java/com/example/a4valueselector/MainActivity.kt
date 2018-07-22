package com.example.a4valueselector

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

// https://www.intertech.com/Blog/android-custom-view-tutorial-part-1-combining-existing-views/

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        valueSelector.setMinValue(0)
        valueSelector.setMaxValue(100)
        valueSelector.setValue(70)

        valueBar.setMaxValue(100)
        valueBar.setAnimation(true)
        valueBar.setAnimationDuration(7000L)

        updateButton.setOnClickListener { updateValueBar() }

        showValueBar.setOnClickListener { startActivity(Intent(this, VerticalValueBarActivity::class.java)) }
    }

    private fun updateValueBar() {
        valueBar.setValue(valueSelector.getValue())
        //valueBarJava.setValue(valueSelector.getValue())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
