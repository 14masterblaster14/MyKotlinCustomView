package com.example.a4valueselector

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View

/*
* View that displays a colored bar as a ratio of an integer value to a maximum value.  There is also a circle
* indicator and text that displays at the position of the current value, as well as a label (positioned above the bar).
* The maximum value is displayed to the right of the bar.
*
* <p>See {@link R.styleable#ValueBar ValueBar Attributes}</p>
*
* */

//It will crash for Kitkat and lower versions
//class ValueBar(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : View(context, attrs, defStyleAttr, defStyleRes) {

/* class ValueBar : View {

    @JvmOverloads
    constructor(context: Context?,
                attrs: AttributeSet?,
                defStyleAttr: Int = 0)
                : super(context,attrs,defStyleAttr)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?,
                attrs: AttributeSet?,
                defStyleAttr: Int,
                defStyleRes: Int)
                : super(context, attrs,defStyleAttr, defStyleRes)*/


class ValueBar(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    companion object {
        // Values
        private const val DEFAULT_MIN_VALUE = 0
        private const val DEFAULT_MAX_VALUE = 100
        private const val DEFAULT_CURRENT_VALUE = 0
        private const val DEFAULT_COLOR = Color.BLACK
        private const val DEFAULT_LABEL_TEXT = "Hi"

    }

    private var minValue: Int = DEFAULT_MIN_VALUE
    private var maxValue: Int = DEFAULT_MAX_VALUE
    private var currentValue: Int = DEFAULT_CURRENT_VALUE

    //animation attributes
    private var animated: Boolean = true
    private var valueToDraw: Float = 0f //bar filling , for use during an animation
    private var animationDuration: Long = 4000L    //4 second default.  this is the time it takes to traverse the entire bar
    private var animator: ValueAnimator? = null

    //instance variables for storing xml attributes
    private var barHeight = 0f
    private var circleRadius = 0f
    private var spaceAfterBar = 0f
    private var circleTextSize = 0f
    private var maxValueTextSize = 0f
    private var labelTextSize = 0f
    private var labelTextColor = DEFAULT_COLOR
    private var currentValueTextColor = DEFAULT_COLOR
    private var circleTextColor = DEFAULT_COLOR
    private var baseColor = DEFAULT_COLOR
    private var fillColor = DEFAULT_COLOR
    private var labelText = DEFAULT_LABEL_TEXT

    //objects used for drawing
    private val labelPaint = Paint()
    private val maxValuePaint = Paint()
    private val barBasePaint = Paint()
    private val barFillPaint = Paint()
    private val circlePaint = Paint()
    private val currentValuePaint = Paint()

    init {
        attrs?.let { setupAttributes(attrs) }
        setupPaint()

    }

    private fun setupAttributes(attrs: AttributeSet?) {

        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ValueBar, 0, 0)
        barHeight = typedArray.getDimension(R.styleable.ValueBar_barHeight, 0f)
        circleRadius = typedArray.getDimension(R.styleable.ValueBar_circleRadius, 0f)
        spaceAfterBar = typedArray.getDimension(R.styleable.ValueBar_spaceAfterBar, 0f)
        circleTextSize = typedArray.getDimension(R.styleable.ValueBar_circleTextSize, 0f)
        maxValueTextSize = typedArray.getDimension(R.styleable.ValueBar_maxValueTextSize, 0f)
        labelTextSize = typedArray.getDimension(R.styleable.ValueBar_labelTextSize, 0f)
        labelTextColor = typedArray.getColor(R.styleable.ValueBar_labelTextColor, DEFAULT_COLOR)
        currentValueTextColor = typedArray.getColor(R.styleable.ValueBar_maxValueTextColor, DEFAULT_COLOR)
        circleTextColor = typedArray.getColor(R.styleable.ValueBar_circleTextColor, DEFAULT_COLOR)
        baseColor = typedArray.getColor(R.styleable.ValueBar_baseColor, DEFAULT_COLOR)
        fillColor = typedArray.getColor(R.styleable.ValueBar_fillColor, DEFAULT_COLOR)
        labelText = typedArray.getString(R.styleable.ValueBar_labelText)

        typedArray.recycle()

    }


    private fun setupPaint() {

        labelPaint.isAntiAlias = true
        labelPaint.textSize = labelTextSize
        labelPaint.color = labelTextColor
        labelPaint.textAlign = Paint.Align.LEFT
        labelPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        maxValuePaint.isAntiAlias = true
        maxValuePaint.textSize = maxValueTextSize
        maxValuePaint.color = currentValueTextColor
        maxValuePaint.textAlign = Paint.Align.RIGHT
        maxValuePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

        barBasePaint.isAntiAlias = true
        barBasePaint.color = baseColor

        barFillPaint.isAntiAlias = true
        barFillPaint.color = fillColor

        circlePaint.isAntiAlias = true
        circlePaint.color = fillColor

        currentValuePaint.isAntiAlias = true
        currentValuePaint.textSize = circleTextSize
        currentValuePaint.color = currentValueTextColor
        currentValuePaint.textAlign = Paint.Align.CENTER
        currentValuePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)

    }

    fun setMinValue(minValue: Int) {
        this.minValue = minValue
        invalidate()        // state of the view has changed and needs to be redrawn.
        requestLayout()     // the size of the view may have changed and needs to be remeasured,
        // which could impact the entire layout
    }

    fun setMaxValue(maxValue: Int) {
        this.maxValue = maxValue
        invalidate()        //state of the view has changed and needs to be redrawn.
        requestLayout()     // the size of the view may have changed and needs to be remeasured,
        // which could impact the entire layout
    }

    fun setValue(newValue: Int) {

        var previousValue = currentValue

        if (newValue < 0) {
            currentValue = 0
        } else if (newValue > maxValue) {
            currentValue = maxValue
        } else {
            currentValue = newValue
        }

        // In case an animation is still running when we need to start a new one,
        // i.e. the bar’s value is updated while an animation for a previous update is still in progress.
        // In that scenario, we need to cancel the running animation before starting a new one.
        if (animator != null) {
            animator?.cancel()
        }

        if (animated) {

            animator = ValueAnimator.ofFloat(previousValue.toFloat(), currentValue.toFloat())
            //animationDuration specifies how long it should take to animate the entire graph, so the
            //actual value to use depends on how much the value needs to change
            var changeInValue: Int = Math.abs(currentValue - previousValue)
            var durationToUse: Long = (animationDuration * (changeInValue.toFloat() / maxValue.toFloat())).toLong()
            animator?.duration = durationToUse

            animator?.addUpdateListener {
                valueToDraw = it.animatedValue as Float
                this.invalidate()
            }

            animator?.start()

        } else {

            valueToDraw = currentValue.toFloat()
        }

        invalidate()

    }

    fun getValue(): Int {
        return currentValue
    }

    fun setAnimation(animated: Boolean) {
        this.animated = animated
    }

    fun setAnimationDuration(animationDuration: Long) {
        this.animationDuration = animationDuration
    }

    //The onMeasure method is where a view determines it’s height and width.
    // It does this by calling setMeasuredDimension, passing in the height and width.

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec))
    }

    //Determine Width
    //left and right padding + label width + max value label width
    private fun measureWidth(widthMeasureSpec: Int): Int {

        var size: Int = paddingLeft + paddingRight

        var bounds: Rect = Rect()
        labelPaint.getTextBounds(labelText, 0, labelText.length, bounds)
        size += bounds.width()

        var bounds1 = Rect()
        var maxValueText = maxValue.toString()
        maxValuePaint.getTextBounds(maxValueText, 0, maxValueText.length, bounds1)
        size += bounds1.width()

        return resolveSizeAndState(size, widthMeasureSpec, 0) // Explanation at bottom
    }

    //Determine Height
    //top and bottom padding + label height + maximum height of bar, circle indicator, and max value label

    private fun measureHeight(heightMeasureSpec: Int): Int {

        var size: Int = paddingTop + paddingBottom
        size += Math.round(labelPaint.fontSpacing)     //Return the recommended line spacing based on the current typeface and text size

        var maxValueTextSpacing: Float = maxValuePaint.fontSpacing     //Return the recommended line spacing based on the current typeface and text size

        size += Math.round(Math.max(maxValueTextSpacing, Math.max(barHeight, circleRadius * 2)))

        return resolveSizeAndState(size, heightMeasureSpec, 0)  // Explanation at bottom
    }


    // Drawing Views

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)        //keep any drawing from the parent side.
        drawLabel(canvas)
        drawBar(canvas)
        drawMaxValue(canvas)
    }

    private fun drawLabel(canvas: Canvas?) {
        canvas?.let {

            var x: Float = paddingLeft.toFloat()
            //the y coordinate marks the bottom of the text, so we need to factor in the height
            var bound: Rect = Rect()
            labelPaint.getTextBounds(labelText, 0, labelText.length, bound)

            var y: Float = paddingTop.toFloat() + bound.height()
            canvas.drawText(labelText, x, y, labelPaint)
        }
    }

    private fun drawBar(canvas: Canvas?) {
        canvas?.let {

            //Drawing Bar

            var maxValueString: String = maxValue.toString()
            var maxValueRect: Rect = Rect()
            maxValuePaint.getTextBounds(maxValueString, 0, maxValueString.length, maxValueRect)

            var barLength: Float = width - paddingRight - paddingLeft - circleRadius - maxValueRect.width() - spaceAfterBar
            var barCenter: Float = getBarCenter()

            var halfBarHeight = barHeight / 2
            var top = barCenter - halfBarHeight
            var bottom = barCenter + halfBarHeight
            var left = paddingLeft.toFloat()
            //var left = paddingLeft.toFloat() + circleRadius
            var right = left + barLength
            var rect: RectF = RectF(left, top, right, bottom)
            canvas.drawRoundRect(rect, halfBarHeight, halfBarHeight, barBasePaint)

            // Filling Bar

            var percentFilled: Float = valueToDraw / maxValue
            var fillLength: Float = barLength * percentFilled
            var fillPosition: Float = left + fillLength
            var fillRect: RectF = RectF(left, top, fillPosition, bottom)
            canvas.drawRoundRect(fillRect, halfBarHeight, halfBarHeight, barFillPaint)

            //Drawing Circle

            canvas.drawCircle(fillPosition, barCenter, circleRadius, circlePaint)

            var currentValueRect: Rect = Rect()
            var valueString: String = Math.round(valueToDraw).toString()
            currentValuePaint.getTextBounds(valueString, 0, valueString.length, currentValueRect)
            var y: Float = barCenter + (currentValueRect.height() / 2)
            canvas.drawText(valueString, fillPosition, y, currentValuePaint)


        }
    }

    //vertical bar center
    private fun getBarCenter(): Float {
        //position the bar slightly below the middle of the drawable area
        var barCenter: Float = (height.toFloat() - paddingTop - paddingBottom) / 2     // This is center
        barCenter += paddingTop + 0.1F * height     //moving it down a  i.e. 60% vertically
        return barCenter
    }

    private fun drawMaxValue(canvas: Canvas?) {

        canvas?.let {
            var maxValueString: String = maxValue.toString()
            var maxValueRect: Rect = Rect()
            maxValuePaint.getTextBounds(maxValueString, 0, maxValueString.length, maxValueRect)

            var x: Float = width - paddingRight.toFloat()
            var y: Float = getBarCenter() + maxValueRect.height() / 2
            canvas.drawText(maxValueString, x, y, maxValuePaint)
        }
    }


    override fun onSaveInstanceState(): Parcelable {

        val bundle = Bundle()
        bundle.putInt("CurrentValue", currentValue)
        bundle.putParcelable("SuperState", super.onSaveInstanceState())
        return bundle
        //return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {

        var viewState = state
        if (viewState is Bundle) {
            currentValue = viewState.getInt("CurrentValue", 0)
            valueToDraw = currentValue.toFloat() //set valueToDraw directly to prevent re-animation
            viewState = viewState.getParcelable("SuperState")
        }

        super.onRestoreInstanceState(viewState)
        //super.onRestoreInstanceState(state)
    }

}

/*
     <-----      resolveSizeAndState     ----->

    We need to consider the size restrictions that are imposed by the parent view.
    This involves two pieces of information: the mode and the size.  The mode can be one of three possible values:
    EXACTLY, AT_MOST, and UNSPECIFIED.  They are defined in View.MeasureSpec.
    EXACTLY means just that: the parent view is telling us exactly what size our view needs to be.
    AT_MOST is also self explanatory.  UNSPECIFIED means that there are no restrictions.

    MeasureSpec provides methods to decode both the mode and the size:


    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);


    Now we can go about measuring our view, while respecting the constraints set by the parent.  The pattern looks like this:


    if mode is EXACTLY:
    we are being told what size we need to be, so just return the specSize (size from the measure spec)
    else:
    compute the desired size
    if mode is AT_MOST:
    return the lesser of desired size and specSize
    else:
    return the desired size


    If this seems like too much work, you can instead use a utility method provided by the View class: resolveSizeAndState.
    This method takes your view’s desired size and the measure spec, and returns the correct size after factoring in the constraints.
    This means that you don’t have to implement the above pattern, you just compute the desired size and call resolveSizeAndState.
    We will use this method when determining the dimensions of our view.
    */