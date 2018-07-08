package com.example.a0basics

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
//https://guides.codepath.com/android/defining-custom-views

/*
Creating custom views is centered around five primary aspects that we may need to control or modify:

Attributes - Defining custom XML attributes for your view and using them to control behavior with TypedArray

Drawing - Control the rendering of the view on screen visually by overriding the onDraw method.

Measurement - Control the content dimensions of the view on screen by overriding the onMeasure method.

Interaction - Control the ways the user can interact with the view with the onTouchEvent and gestures.

Persistence - Storing and restoring state on configuration changes to avoid losing the state
              with onSaveInstanceState and onRestoreInstanceState
*/

class ShapeSelectorView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object {
        private const val DEFAULT_SHAPE_COLOR = Color.BLUE
        private const val DEFAULT_DISPLAY_SHAPE_NAME = false
    }

    private var shapes = listOf<String>("Square","Circle","Triangle")
    private var currentShapIndex = 0
    private var shapeColor = DEFAULT_SHAPE_COLOR
    private var displayShapeName = DEFAULT_DISPLAY_SHAPE_NAME
    private var shapeWidth = 100f
    private var shapeHeight = 100f
    private var textXOffset = 0
    private var textYOffset = 50
    private val paint = Paint()
    private val trianglePath = Path()

    init {
        setupAttributes(attrs)
        setupPaint()
    }


    private fun setupAttributes(attrs: AttributeSet?) {

        //Obtain a typedArray of attributes
        val typedArray = context.theme.obtainStyledAttributes(attrs,R.styleable.ShapeSelectorView,0,0)

        // Extract custom attributes into member variables
        try {
            shapeColor = typedArray.getColor(R.styleable.ShapeSelectorView_shapeColor, DEFAULT_SHAPE_COLOR)
            displayShapeName = typedArray.getBoolean(R.styleable.ShapeSelectorView_displayShapeName, DEFAULT_DISPLAY_SHAPE_NAME)
        } finally {

            // TypedArray objects are shared and must be recycled.
            typedArray.recycle()
        }
    }

    private fun setupPaint() {
        paint.isAntiAlias = true  // make lines/ corners smooth and not distorted
        paint.style = Paint.Style.FILL
        paint.color = shapeColor
        paint.textSize = 30f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        var shapeSelected  = shapes[currentShapIndex]

        if(shapeSelected.equals("Square")){
            canvas?.drawRect(0f,0f,shapeWidth,shapeHeight,paint)
            textXOffset = 14
        }else if (shapeSelected.equals("Circle")){
            canvas?.drawCircle(shapeWidth/2, shapeHeight/2, shapeWidth/2, paint)
            textXOffset = 14
        }else if (shapeSelected.equals("Triangle")){
            canvas?.drawPath(getTrianglePath(),paint)
            textXOffset = 14
        }

        if (displayShapeName){
           // canvas?.drawText("Square",shapeWidth + textXOffset, shapeHeight + textXOffset, paint)
            canvas?.drawText(shapeSelected,shapeWidth + textXOffset, (shapeHeight/2) + textXOffset, paint)
        }
    }

    private fun getTrianglePath(): Path? {
        trianglePath.moveTo(0f,shapeHeight)
        trianglePath.lineTo(shapeWidth,shapeHeight)
        trianglePath.lineTo(shapeWidth/2,0f)
        return trianglePath
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        // Defines the extra padding for the shape name text
        val textPadding = 10f
        val contentWidth = shapeWidth

        // Resolve the width based on our minimum and the measure spec
        var  minWidth = contentWidth + paddingLeft + paddingRight
        var width = resolveSizeAndState(minWidth.toInt(),widthMeasureSpec,0)

        // Ask for a height that would let the view get as big as it can
        var minHeight = shapeHeight + paddingTop + paddingBottom
        if (displayShapeName){
            minHeight = textYOffset + textPadding
        }
        var height = resolveSizeAndState(minHeight.toInt(),heightMeasureSpec,0)

        // Calling this method determines the measured width and height
        // Retrieve with getMeasuredWidth or getMeasuredHeight methods later
        setMeasuredDimension(width,height)
    }

    // Change the currentShapeIndex whenever the shape is clicked
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event?.action == MotionEvent.ACTION_DOWN){
            Log.i("MasterBlaster","currentShapIndex : $currentShapIndex")
            //currentShapIndex = (current Shape Index ++) % shapes.size
            currentShapIndex = (currentShapIndex +1) % shapes.size
            Log.i("MasterBlaster","currentShapIndex : $currentShapIndex")
            postInvalidate()
            return true
        }
        return super.onTouchEvent(event)
    }

    //provision to provide the selected share
    fun getSelectedShape() : String{
        return shapes[currentShapIndex]
    }

    override fun onSaveInstanceState(): Parcelable {
        //return super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable("SuperState", super.onSaveInstanceState())
        bundle.putInt("currentShapeIndex",this.currentShapIndex)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        //super.onRestoreInstanceState(state)
        var viewState = state

        if(viewState is Bundle){
            this.currentShapIndex = viewState.getInt("currentShapeIndex")
            viewState = viewState.getParcelable("SuperState")
        }
        return super.onRestoreInstanceState(viewState)
    }
}