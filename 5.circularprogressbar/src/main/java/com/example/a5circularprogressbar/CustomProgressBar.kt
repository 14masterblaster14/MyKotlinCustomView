package com.example.a5circularprogressbar

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.View

//class CustomProgressBar (context: Context?): View(context){
class CustomProgressBar(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object {

        private const val DEFAULT_DISABLED_COLOR = Color.LTGRAY
        private const val DEFAULT_BACKGROUND_COLOR = Color.LTGRAY
        private const val DEFAULT_OUTERCIRCLE_COLOR = Color.DKGRAY
        private const val DEFAULT_PROGRESS_COLOR = Color.GREEN
        private const val DEFAULT_BACKGROUND_STROKE = 12f
        private const val DEFAULT_PROGRESS_STROKE = 20f
        private const val DEFAULT_TITLE = "Title"
        private const val DEFAULT_TITLE_TEXT_SIZE = 60f
        private const val DEFAULT_UNIT_TEXT_SIZE = 40f
        private const val DEFAULT_UNIT = "%"
        private const val DEFAULT_SPACE_AFTER_TITLE = 4f
        private const val DEFAULT_ANIMATION_DURATION = 4000L

    }


    /*private var attrs : AttributeSet? = null
    private var defStyleAttr : Int = 0
    private var defStyleRes : Int? = null
*/


    private var animationDuration: Long = DEFAULT_ANIMATION_DURATION    //4 second default.  this is the time it takes to traverse the entire bar
    private var percentage = 100
    private var percentage1: Int = 0
    private var startAngle = 0f
    private var sweepAngle = 0f

    //animation attributes
    private var ifAnimated = false
    private var arcProgressAnimator: ValueAnimator? = null
    private var mHandler = Handler()
    private var repeat_interval_ms: Long = animationDuration

    // Circle Boundry set up
    private val circleBound = RectF()

    //instance variables for storing xml attributes
    private var backGroundColor = DEFAULT_BACKGROUND_COLOR
    private var progressColor = DEFAULT_PROGRESS_COLOR
    private var title = DEFAULT_TITLE
    private var titleColor = DEFAULT_PROGRESS_COLOR
    private var titleTextSize = DEFAULT_TITLE_TEXT_SIZE
    private var unit = DEFAULT_UNIT
    private var unitColor = DEFAULT_PROGRESS_COLOR
    private var unitTextSize = DEFAULT_UNIT_TEXT_SIZE
    private var backgroundStrokeWidth = DEFAULT_BACKGROUND_STROKE
    private var progressStrokeWidth = DEFAULT_PROGRESS_STROKE
    private var spaceAfterTitle = DEFAULT_SPACE_AFTER_TITLE


    //objects used for drawing
    private val backgroundColorPaint = Paint()
    private val outerCirclePaint = Paint()
    private val progressColorPaint = Paint()
    private val titlePaint = Paint()
    private val unitPaint = Paint()


    /*  constructor(context: Context?, attributeSet: AttributeSet?): this(context){
          attrs = attributeSet
      }

      constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int): this(context){
          attrs = attributeSet
          this.defStyleAttr = 0
      }

      constructor(context: Context?, attributeSet: AttributeSet?, defStyleAttr: Int, defStyleRes : Int): this(context){
          attrs = attributeSet
          this.defStyleAttr = defStyleAttr
          this.defStyleRes = defStyleRes
      }*/

    init {
        attrs?.let { setupAttributes(attrs) }
        setupPaint()
    }

    private fun setupAttributes(attrs: AttributeSet?) {

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomProgressBar, 0, 0)
        backGroundColor = typedArray.getColor(R.styleable.CustomProgressBar_backgroundColor, DEFAULT_BACKGROUND_COLOR)
        progressColor = typedArray.getColor(R.styleable.CustomProgressBar_progressColor, DEFAULT_PROGRESS_COLOR)
        title = typedArray.getString(R.styleable.CustomProgressBar_title)
        titleColor = typedArray.getColor(R.styleable.CustomProgressBar_titleColor, DEFAULT_PROGRESS_COLOR)
        titleTextSize = typedArray.getDimension(R.styleable.CustomProgressBar_titleTextSize, DEFAULT_TITLE_TEXT_SIZE)
        unit = typedArray.getString(R.styleable.CustomProgressBar_unit)
        unitColor = typedArray.getColor(R.styleable.CustomProgressBar_unitColor, DEFAULT_PROGRESS_COLOR)
        unitTextSize = typedArray.getDimension(R.styleable.CustomProgressBar_unitTextSize, DEFAULT_UNIT_TEXT_SIZE)
        backgroundStrokeWidth = typedArray.getDimension(R.styleable.CustomProgressBar_backgroundStrokeWidth, DEFAULT_BACKGROUND_STROKE)
        progressStrokeWidth = typedArray.getDimension(R.styleable.CustomProgressBar_progressStrokeWidth, DEFAULT_PROGRESS_STROKE)
        spaceAfterTitle = typedArray.getDimension(R.styleable.CustomProgressBar_spaceAfterTitle, DEFAULT_SPACE_AFTER_TITLE)

        typedArray.recycle()
    }

    private fun setupPaint() {

        backgroundColorPaint.isAntiAlias = true
        backgroundColorPaint.color = DEFAULT_BACKGROUND_COLOR
        backgroundColorPaint.style = Paint.Style.STROKE
        backgroundColorPaint.strokeWidth = backgroundStrokeWidth

        outerCirclePaint.isAntiAlias = true
        outerCirclePaint.color = DEFAULT_OUTERCIRCLE_COLOR
        outerCirclePaint.style = Paint.Style.STROKE
        outerCirclePaint.strokeWidth = backgroundStrokeWidth

        progressColorPaint.isAntiAlias = true
        progressColorPaint.color = DEFAULT_PROGRESS_COLOR
        progressColorPaint.style = Paint.Style.STROKE
        progressColorPaint.strokeWidth = progressStrokeWidth
        progressColorPaint.strokeCap = Paint.Cap.ROUND

        titlePaint.isAntiAlias = true
        titlePaint.color = titleColor
        titlePaint.textSize = titleTextSize
        titlePaint.style = Paint.Style.FILL
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        unitPaint.isAntiAlias = true
        unitPaint.color = unitColor
        unitPaint.textSize = unitTextSize
        unitPaint.style = Paint.Style.FILL
        unitPaint.typeface = Typeface.DEFAULT_BOLD

        //setMax(100)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //var widthAndHeight = maxOf(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
        var widthAndHeight = Math.round(maxOf(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
                + backgroundStrokeWidth + progressStrokeWidth)
        setMeasuredDimension(widthAndHeight, widthAndHeight)

        //Required to draw the Arc
        circleBound.set(backgroundStrokeWidth, backgroundStrokeWidth, Math.round(0.9 * widthAndHeight) + backgroundStrokeWidth, Math.round(0.9 * widthAndHeight) + backgroundStrokeWidth)
    }

    private fun measureWidth(widthMeasureSpec: Int): Int {


        var size = paddingLeft + paddingRight

        var titleBound = Rect()
        titlePaint.getTextBounds(title, 0, title.length, titleBound)
        size += titleBound.width()

        size += Math.round(spaceAfterTitle)

        var unitBound = Rect()
        unitPaint.getTextBounds(unit, 0, unit.length, unitBound)
        size += unitBound.width()

        return resolveSizeAndState(size, widthMeasureSpec, 0)

    }

    private fun measureHeight(heightMeasureSpec: Int): Int {

        var size = paddingTop + paddingBottom

        var titleTextSpacing = titlePaint.fontSpacing
        var unitTextSpacing = unitPaint.fontSpacing

        size += Math.round(maxOf(titleTextSpacing, unitTextSpacing))

        return resolveSizeAndState(size, heightMeasureSpec, 0)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawArc(canvas)
        drawTitleAndUnit(canvas)
    }

    private fun drawArc(canvas: Canvas?) {

        canvas?.let {

            //Drawing Background Circle
            canvas.drawArc(circleBound, 0f, 360f, false, outerCirclePaint)

            //Filling circle background
            backgroundColorPaint.style = Paint.Style.FILL
            canvas.drawArc(circleBound, 0f, 360f, false, backgroundColorPaint)

            //Drawing the progress Arc
            canvas.drawArc(circleBound, startAngle, sweepAngle, false, progressColorPaint)
        }
    }

    private fun drawTitleAndUnit(canvas: Canvas?) {

        canvas?.let {

            var xPosition = ((width / 2) - ((titlePaint.measureText(title) + unitPaint.measureText(unit) + spaceAfterTitle) / 2))
            var yPosition = (height / 2).toFloat()

            // Drawing Title Text
            canvas.drawText(title, xPosition, yPosition, titlePaint)

            // Drawing Unit Text
            canvas.drawText(unit, (xPosition + (titlePaint.measureText(title)) + spaceAfterTitle), yPosition, unitPaint)
        }
    }


    fun setUpProgressBar(isAnimated: Boolean, titleSymbol: String, unitSymbol: String, percent: Int, startPoint: Float, endPoint: Float) {

        ifAnimated = isAnimated
        unit = unitSymbol
        startAngle = startPoint + 270
        sweepAngle = endPoint
        percentage1 = percent

        //Checking and Setting up Empty progress
        if (percentage1 == 0 || sweepAngle == 0f) {

            title = percent.toString()

        } else {

            //Adjusting the start point of the progress
            Log.i("MasterBlaster", "prev startAngle : ${startAngle}")
            if (startAngle >= 360) {
                startAngle = (startAngle - 360)
                Log.i("MasterBalster", "revised startAngle : ${startAngle}")
            }

            if (ifAnimated) {

                if (arcProgressAnimator != null) {
                    arcProgressAnimator?.cancel()
                }

                arcProgressAnimator = ValueAnimator.ofFloat(sweepAngle)
                arcProgressAnimator?.duration = animationDuration

                arcProgressAnimator?.addUpdateListener {
                    sweepAngle = it.animatedValue as Float
                    this.invalidate()
                }

                arcProgressAnimator?.start()

                //Incrementing the percentage value
                repeat_interval_ms = animationDuration / percent
                percentage = 0
                mHandler.post(AutoIncrementor())

            } else {
                title = percent.toString()
            }
        }
        invalidate()
    }

    override fun onSaveInstanceState(): Parcelable {

        val bundle = Bundle()
        bundle.putFloat("StartAngle", startAngle)
        bundle.putFloat("SweepAngle", sweepAngle)
        bundle.putInt("Percentage", percentage1)
        bundle.putParcelable("SuperState", super.onSaveInstanceState())
        return bundle
        //return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {

        var viewState = state
        if (viewState is Bundle) {
            startAngle = viewState.getFloat("StartAngle", 0F)
            sweepAngle = viewState.getFloat("SweepAngle", 0F)
            percentage = viewState.getInt("Percentage", 0)
            title = percentage.toString()
            viewState = viewState.getParcelable("SuperState")
        }

        super.onRestoreInstanceState(viewState)
        //super.onRestoreInstanceState(state)
    }


    private inner class AutoIncrementor : Runnable {
        override fun run() {
            Log.i("MasterBlaster", "percentage : ${percentage}")

            if (percentage < percentage1) {
                percentage++
                title = percentage.toString()
                mHandler.postDelayed(AutoIncrementor(), repeat_interval_ms)
            }
        }
    }
}