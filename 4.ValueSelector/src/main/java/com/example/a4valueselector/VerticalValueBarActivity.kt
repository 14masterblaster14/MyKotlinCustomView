package com.example.a4valueselector

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_vertical_value_bar.*

class VerticalValueBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vertical_value_bar)

        verticalValueBar.setMinValue(40000)
        verticalValueBar.setMaxValue(70000)
        verticalValueBar.setBudgetValue(65000)
    }
}
