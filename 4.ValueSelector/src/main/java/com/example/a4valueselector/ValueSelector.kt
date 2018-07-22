package com.example.a4valueselector

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.value_selector.view.*


//It will crash for Kitkat and earlier versions
/*class ValueSelector @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyle :Int = 0,
        defStyleRes : Int = 0) : RelativeLayout(context,attributeSet, defStyle, defStyleRes) {*/

class ValueSelector : RelativeLayout {

    @JvmOverloads
    constructor(
            context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int = 0)
            : super(context, attributeSet, defStyle)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attributeSet: AttributeSet? = null,
            defStyle: Int,
            defStyleRes: Int)
            : super(context, attributeSet, defStyle, defStyleRes)


    private var minValue = Integer.MIN_VALUE
    private var maxValue = Integer.MAX_VALUE

    private var plusButtonIsPressed = false
    private var minusButtonIsPressed = false
    private var mHandler: Handler

    companion object {
        private const val REPEAT_INTERVAL_MS = 100L
    }


    init {
        LayoutInflater.from(context)
                .inflate(R.layout.value_selector, this, true)
        // orientation = VERTICAL


        mHandler = Handler()
        //val valueTextView = rootView.valueTextView
        minusButton.setOnClickListener { decrementValue() }
        minusButton.setOnLongClickListener { onLongDecrement() }
        minusButton.setOnTouchListener { view: View, motionEvent: MotionEvent -> decrementTouch(view, motionEvent) }

        plusButton.setOnClickListener { incrementValue() }
        plusButton.setOnLongClickListener { onLongIncrement() }
        plusButton.setOnTouchListener { view: View, motionEvent: MotionEvent -> incrementTouch(view, motionEvent) }

    }

    private fun decrementTouch(view: View, motionEvent: MotionEvent): Boolean {
        if ((motionEvent.action == MotionEvent.ACTION_UP) || (motionEvent.action == MotionEvent.ACTION_CANCEL)) {
            minusButtonIsPressed = false
        }
        return false
    }


    private fun decrementValue() {

        var currentValue = (valueTextView.text.toString()).toInt()
        if (currentValue > minValue) {

            valueTextView.setText((currentValue - 1).toString())
        }
    }

    private fun onLongDecrement(): Boolean {

        minusButtonIsPressed = true
        mHandler.post(Autodecrementor())
        return false
    }

    private fun incrementTouch(view: View, motionEvent: MotionEvent): Boolean {
        if ((motionEvent.action == MotionEvent.ACTION_UP) || (motionEvent.action == MotionEvent.ACTION_CANCEL)) {
            plusButtonIsPressed = false
        }
        return false
    }

    private fun incrementValue() {

        var currentValue = (valueTextView.text.toString()).toInt()
        if (currentValue < maxValue) {

            valueTextView.setText((currentValue + 1).toString())
        }
    }


    private fun onLongIncrement(): Boolean {

        plusButtonIsPressed = true
        mHandler.post(Autoincrementor())
        return false
    }


    fun getMinValue(): Int {
        return minValue
    }

    fun setMinValue(minValue: Int) {
        this.minValue = minValue
    }

    fun getMaxValue(): Int {
        return maxValue
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
    }

    fun getValue(): Int {
        return (valueTextView.text.toString()).toInt()
    }

    fun setValue(newValue: Int) {
        var value = newValue

        if (newValue < minValue) {
            value = minValue
        } else if (newValue > maxValue) {
            value = maxValue
        }

        valueTextView.setText(value.toString())
    }

    private inner class Autoincrementor : Runnable {
        override fun run() {
            if (plusButtonIsPressed) {
                incrementValue()
                mHandler.postDelayed(Autoincrementor(), REPEAT_INTERVAL_MS)
            }
        }

    }

    private inner class Autodecrementor : Runnable {
        override fun run() {
            if (minusButtonIsPressed) {
                decrementValue()
                mHandler.postDelayed(Autodecrementor(), REPEAT_INTERVAL_MS)
            }
        }

    }
}