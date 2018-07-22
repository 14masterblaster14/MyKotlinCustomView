package com.example.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.SeekBar
import com.example.a4valueselector.R
import kotlinx.android.synthetic.main.vertical_value_bar.view.*


class VerticalValueBar(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    companion object {

        private const val DEFAULT_MIN_VALUE = 0
        private const val DEFAULT_MAX_VALUE = 100000
        private const val DEFAULT_BUDGET_VALUE = 0
        private const val DEFAULT_THUMB_VERTICAL_BIAS = 0
    }

    private var minValue = DEFAULT_MIN_VALUE
    private var maxValue = DEFAULT_MAX_VALUE
    private var budgetValue = DEFAULT_BUDGET_VALUE


    init {

        val view = LayoutInflater.from(context).inflate(R.layout.vertical_value_bar, null)
        removeAllViews()
        addView(view)
        setInitialValues()
        configSeekBar()
    }

    private fun setInitialValues() {

        txt_min_price.text = (resources.getString(R.string.currency_symbol) + minValue.toString())
        txt_max_price.text = (resources.getString(R.string.currency_symbol) + maxValue.toString())

    }

    private fun configSeekBar() {

        price_range_seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                // Setting Selected Price

                var unitPriceValue = ((maxValue - minValue) / price_range_seekBar.max).toFloat()
                var selected_price = minValue + (unitPriceValue * progress)

                if (progress == 0) {
                    price_data_text.text = (resources.getString(R.string.currency_symbol) + minValue)
                } else {

                    price_data_text.text = (resources.getString(R.string.currency_symbol) + selected_price)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

    }

    fun setMinValue(newMinValue: Int) {

        if (newMinValue < DEFAULT_MIN_VALUE) {
            minValue = DEFAULT_MIN_VALUE
        } else {
            minValue = newMinValue
        }

        setInitialValues()
    }

    fun getMinValue(): Int {
        return minValue
    }

    fun setMaxValue(newMaxValue: Int) {

        if (newMaxValue > DEFAULT_MAX_VALUE) {
            maxValue = DEFAULT_MAX_VALUE
        } else {
            maxValue = newMaxValue
        }

        setInitialValues()
    }

    fun getMaxValue(): Int {
        return maxValue
    }

    fun setBudgetValue(newBudgetValue: Int) {

        if (newBudgetValue < minValue) {
            budgetValue = minValue
        } else if (newBudgetValue > maxValue) {
            budgetValue = maxValue
        } else {
            budgetValue = newBudgetValue
        }

        setProgressBar(budgetValue)
    }

    fun getBudgetValue(): Int {

        return budgetValue
    }

    private fun setProgressBar(budgetValue: Int) {

        var unitPriceValue = ((maxValue - minValue) / price_range_seekBar.max).toFloat()
        var progress = Math.round((budgetValue - minValue) / unitPriceValue)
        price_range_seekBar.progress = progress
        price_data_text.text = (resources.getString(R.string.currency_symbol) + budgetValue.toString())
    }

}