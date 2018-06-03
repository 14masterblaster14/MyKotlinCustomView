package com.example.a1smile

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View

//https://www.raywenderlich.com/175645/android-custom-view-tutorial

class EmotionalFaceView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    /*
        constructor(context: Context)
                To create a new View instance from Kotlin code, it needs the Activity context.

        constructor(context: Context, attrs: AttributeSet)
                To create a new View instance from XML.

        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int)
                To create a new view instance from XML with a style from theme attribute.

        constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
                To create a new view instance from XML with a style from theme attribute and/or style resource.
     */

   companion object {
       private const val DEFAULT_FACE_COLOR =   Color.YELLOW
       private const val DEFAULT_EYE_COLOR = Color.BLACK
       private const val DEFAULT_MOUTH_COLOR = Color.BLACK
       private const val DEFAULT_BORDER_COLOR = Color.BLACK
       private const val DEFAULT_BORDER_WIDTH = 7.0f

       const val HAPPY = 0L
       const val SAD = 1L
   }

    private var faceColor = DEFAULT_FACE_COLOR
    private var eyeColor = DEFAULT_EYE_COLOR
    private var mouthColor = DEFAULT_MOUTH_COLOR
    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH

    private val paint = Paint()
    private val mouthPath = Path()
    private var size = 0

    var happinessState  = HAPPY
    set(state){
        field = state
        invalidate()  //makes Android redraw the view by calling onDraw().
    }

    init {
        paint.isAntiAlias = true  // make lines/ corners smooth and not distorted
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {

        //Obtain a typedArray of attributes
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.EmotionalFaceView,0,0)

        // Extract custom attributes into member variables
        happinessState = typedArray.getInt(R.styleable.EmotionalFaceView_state, HAPPY.toInt()).toLong()
        faceColor = typedArray.getColor(R.styleable.EmotionalFaceView_faceColor, DEFAULT_FACE_COLOR)
        eyeColor = typedArray.getColor(R.styleable.EmotionalFaceView_eyesColor, DEFAULT_EYE_COLOR)
        mouthColor = typedArray.getColor(R.styleable.EmotionalFaceView_mouthColor, DEFAULT_MOUTH_COLOR)
        borderColor = typedArray.getColor(R.styleable.EmotionalFaceView_borderColor, DEFAULT_BORDER_COLOR)
        borderWidth = typedArray.getDimension(R.styleable.EmotionalFaceView_borderWidth, DEFAULT_BORDER_WIDTH)

        // TypedArray objects are shared and must be recycled.
        typedArray.recycle()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)  //keep any drawing from the parent side.
        drawFaceBackground(canvas)
        drawEyes(canvas)
        drawMouth(canvas)
    }

    private fun drawFaceBackground(canvas: Canvas?) {

        //Drawing Main circle
        paint.color = faceColor
        paint.style = Paint.Style.FILL

        val radius = size/2f
        canvas?.drawCircle(size/2f, size/2f, radius,paint)

        //Drawing Border circle
        paint.color = borderColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = borderWidth
        canvas?.drawCircle(size/2f,size/2f,radius-borderWidth/2f,paint)

    }

    private fun drawEyes(canvas: Canvas?) {
        paint.color = eyeColor
        paint.style = Paint.Style.FILL

        val leftEyeRect = RectF(size * 0.32f, size * 0.23f, size * 0.43f, size * 0.50f )
        canvas?.drawOval(leftEyeRect,paint)

        val rightEyeRect = RectF(size * 0.57f, size * 0.23f, size * 0.68f, size * 0.50f)
        canvas?.drawOval(rightEyeRect,paint)
    }

    private fun drawMouth(canvas: Canvas?) {
        //Clear path
        mouthPath.reset()

        mouthPath.moveTo(size * 0.22f, size * 0.7f)     //Start point location, if not specified then by default 0,0

        if(happinessState ==  HAPPY){
            // Happy Mouth Path
            mouthPath.quadTo(size * 0.5f, size * 0.80f, size * 0.78f, size * 0.7f)
            //x0,y0 --> x1,y1 --> x2,y2
            mouthPath.quadTo(size * 0.5f, size * 0.90f, size * 0.22f, size * 0.7f)
            //x2,y2 --> x3,y3 --> x0,y0
        }else{
            // Sad Mouth Path
            mouthPath.quadTo(size * 0.5f, size * 0.50f, size * 0.78f, size * 0.7f)
            //x0,y0 --> x1,y1 --> x2,y2
            mouthPath.quadTo(size * 0.5f, size * 0.60f, size * 0.22f, size * 0.7f)
            //x2,y2 --> x3,y3 --> x0,y0
        }

        paint.color = mouthColor
        paint.style = Paint.Style.FILL
        canvas?.drawPath(mouthPath,paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        size = Math.min(measuredWidth, measuredHeight)

        setMeasuredDimension(size, size)

    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putLong("happinessState",happinessState)
        bundle.putParcelable("SuperState", super.onSaveInstanceState())
        return bundle
        //return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var viewState = state
        if(viewState is Bundle){
            happinessState = viewState.getLong("happinessState",HAPPY)
            viewState = viewState.getParcelable("SuperState")
        }

        super.onRestoreInstanceState(viewState)
       // super.onRestoreInstanceState(state)
    }

}