package com.example.a5circularprogressbar

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var total = 100f
        var internal = 33f
        var external = 10f
        var oral = 40f
        var exam = 17f


        var percent1 = Math.round((internal / total) * 100).toFloat()
        var startPoint1 = 0F
        var endPoint1 = ((percent1 * 360F) / 100).toFloat()
        Log.i("MasterBalster", "percent1 :  ${percent1}, startPoint1 : ${startPoint1}, endPoint1 : ${endPoint1}")
        custom_circular_progress_bar1.setUpProgressBar(true, "internal", "%", percent1.toInt(), startPoint1, endPoint1)


        var percent2 = Math.round((external / total) * 100).toFloat()
        var startPoint2 = endPoint1
        var endPoint2 = Math.round((percent2 * 360f) / 100).toFloat()
        Log.i("MasterBalster", "percent2 :  ${percent2}, startPoint2 : ${startPoint2}, endPoint2 : ${endPoint2}")
        custom_circular_progress_bar2.setUpProgressBar(true, "external", "%", percent2.toInt(), startPoint2, endPoint2)

        var percent3 = Math.round(((oral / total) * 100)).toFloat()
        var startPoint3 = endPoint1 + endPoint2
        var endPoint3 = Math.round((percent3 * 360f) / 100).toFloat()
        Log.i("MasterBalster", "percent3 :  ${percent3}, startPoint3 : ${startPoint3}, endPoint3 : ${endPoint3}")
        custom_circular_progress_bar3.setUpProgressBar(true, "oral", "%", percent3.toInt(), startPoint3, endPoint3)

        var percent4 = Math.round((exam / total) * 100).toFloat()
        var startPoint4 = endPoint1 + endPoint2 + endPoint3
        var endPoint4 = Math.round((percent4 * 360f) / 100).toFloat()
        Log.i("MasterBalster", "percent4 :  ${percent4}, startPoint4 : ${startPoint4}, endPoint4 : ${endPoint4}")
        custom_circular_progress_bar4.setUpProgressBar(true, "exam", "%", percent4.toInt(), startPoint4, endPoint4)

        //Empty object
        custom_circular_progress_bar5.setUpProgressBar(true, "", "%", 0, 0f, 0f)

    }
}
