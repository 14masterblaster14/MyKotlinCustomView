package com.example.a6fancontroller

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 *
 * Custom view renders a multi-position "dial". Each click advances to the
 * next dial position. Initially set to 4 selections (0-3), with
 * 0 = Off.
 *
 */
class DialView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object;

    private var mSelectionCount = 4                // Default number of selections
    private var mWidth: Float = 0F                // Custom view width
    private val mHeight: Float = 0F               // Custom view height

    private val mTextPaint = Paint()               // For text in the view
    private val mDialPaint = Paint()               // For dial circle in the view

    private var mRadius: Float = 0F               // Radius of the dial
    private var mActiveSelection = 0               // The active selection

    // String buffer for dial labels and float for ComputeXY result
    private var mTempLabel: String = "z"
    private var mTempResult = FloatArray(2)

    // Challenge: The color is set in attributes
    private var mFanOnColor: Int = Color.CYAN                // Dial color set in the attributes
    private var mFanOffColor: Int = Color.GRAY              // Dial color set in the attributes

    init {
        attrs?.let { setupAttributes(attrs) }
        setupPaint()

        // Initialize current selection (where the dial's "indicator" is
        // pointing).
        mActiveSelection = 0

        // Set up onClick listener for this view.
        // Rotates between each of the different selection
        // states on each click.
        setOnClickListener { clicked() }
    }

    private fun clicked() {

        // Rotate selection forward to the next valid choice.
        mActiveSelection = (mActiveSelection + 1) % mSelectionCount

        // Set dial background color to green if selection is >= 1.
        if (mActiveSelection >= 1) {
            mDialPaint.color = mFanOnColor
        } else {
            mDialPaint.color = mFanOffColor
        }
        // Redraw the view.
        invalidate()
    }

    private fun setupAttributes(attrs: AttributeSet?) {

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DialView, 0, 0)
        mFanOnColor = typedArray.getColor(R.styleable.DialView_fanOnColor, mFanOnColor)
        mFanOffColor = typedArray.getColor(R.styleable.DialView_fanOffColor, mFanOffColor)
        mSelectionCount = typedArray.getInt(R.styleable.DialView_selectionIndicators, mSelectionCount)

        typedArray.recycle()

    }

    private fun setupPaint() {
        mTextPaint.isAntiAlias = true
        mTextPaint.color = Color.BLACK
        mTextPaint.style = Paint.Style.FILL_AND_STROKE
        mTextPaint.textAlign = Paint.Align.CENTER
        mTextPaint.textSize = 40f

        mDialPaint.isAntiAlias = true
        mDialPaint.color = mFanOffColor
    }

    /**
     * Sets the selection count, which is the number of
     * selection indicators on the dial, based on
     * user's choice. Also invalidates custom view to
     * force redraw.
     * @param count The user-chosen count of indicators
     */
    fun setSelectionCount(count: Int) {
        this.mSelectionCount = count
        this.mActiveSelection = 0
        mDialPaint.color = mFanOffColor
        invalidate()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        // Calculate the radius from the width and height.
        var mWidth = w
        var mHeight = h
        mRadius = (Math.min(mWidth, mHeight) / 2 * 0.8).toFloat()
        //super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the dial.
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), mRadius, mDialPaint)

        // Draw the text labels.
        val labelRadius: Float = mRadius + 20f
        var label = mTempLabel

        for (mCount in 0..(mSelectionCount - 1)) {
            // Call computeXYForPosition with selection count, radius, and
            // boolean isLabel set to true (for text labels).
            var xyData: FloatArray = computeXYForPosition(mCount, labelRadius, true)
            var x: Float = xyData[0]
            var y: Float = xyData[1]
            //label.length = 4
            label += mCount
            canvas.drawText(label, 0, label.length, x, y, mTextPaint)
        }

        // Draw the indicator mark.
        val markerRadius: Float = mRadius - 35
        // Call computeXYForPosition with active selection, marker radius, and
        // boolean isLabel set to false (for marker indicator).
        var xyData: FloatArray = computeXYForPosition(mActiveSelection, markerRadius, false)
        var x: Float = xyData[0]
        var y: Float = xyData[1]
        canvas.drawCircle(x, y, 20f, mTextPaint)
    }

    /**
     * Compute the X/Y-coordinates for a label or indicator,
     * given the position number and radius
     * where the label anf marker should be drawn.
     *
     * @param pos Zero-based position index
     * @param radius Radius where label/indicator is to be drawn.
     * @param isLabel True if for text labels, false if for indicator marker.
     * @return 2-element array. Element 0 is X-coordinate, element 1 is Y-coordinate.
     */

    fun computeXYForPosition(pos: Int, radius: Float, isLabel: Boolean): FloatArray {

        var result: FloatArray = mTempResult
        var startAngle: Double = 0.0
        var angle: Double = 0.0

        if (mSelectionCount > 4) {
            // If greater than 4 selections, draw around entire dial.
            startAngle = Math.PI * (3 / 2)  //Math.PI * (3/2d)  Angles are in Radians
            angle = startAngle + (pos * (Math.PI / mSelectionCount))
            result[0] = (radius * Math.cos(angle * 2)).toFloat() + (mWidth / 2)
            var temp = (radius * Math.sin(angle * 2)).toFloat() + (mHeight / 2)

            if ((angle > Math.toRadians(360.0)) && isLabel) {
                result[1] = temp + 20
            }
        } else {

            // Draw around the upper half of the dial.
            startAngle = Math.PI * (9 / 8)    //Math.PI * (9/8d)
            angle = startAngle + (pos * (Math.PI / mSelectionCount))
            result[0] = (radius * Math.cos(angle)).toFloat() + (mWidth / 2)
            result[1] = (radius * Math.sin(angle)).toFloat() + (mHeight / 2)
        }
        return result
    }


}