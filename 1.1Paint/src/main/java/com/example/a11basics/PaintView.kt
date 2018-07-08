package com.example.a11basics

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View

//https://www.ssaurel.com/blog/learn-to-create-a-paint-application-for-android/


class PaintView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        const val DEFAULT_BRUSH_SIZE = 20
        const val DEFAULT_BRUSH_COLOR = Color.RED
        const val DEFAULT_BACKGROUND_COLOR = Color.WHITE
        const val TOUCH_TOLERENCE = 4
    }

    private val mPaint = Paint()
    private var mPath = Path()
    private var mX: Float? = null
    private var mY: Float? = null
    private var paths: ArrayList<FingerPath>? = null
    private var mCurrentColor = DEFAULT_BRUSH_COLOR
    private var mStrokeWidth = DEFAULT_BRUSH_SIZE
    private var isEmboss: Boolean? = null
    private var isBlur: Boolean? = null
    private var currentColor = DEFAULT_BRUSH_COLOR
    private var mBackgroundColor = DEFAULT_BACKGROUND_COLOR
    private var mEmboss: MaskFilter? = null
    private var mBlurr: MaskFilter? = null
    private var mCanvas: Canvas? = null
    private var mBitmap: Bitmap? = null
    private var mBitmapPaint: Paint = Paint(Paint.DITHER_FLAG)

    init {
        setupPaint()
    }

    private fun setupPaint() {
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.color = DEFAULT_BRUSH_COLOR
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.xfermode = null
        mPaint.alpha = 0xff

        mEmboss = EmbossMaskFilter(floatArrayOf(1f, 1f, 1f), 0.4f, 6f, 3.5f)
        mBlurr = BlurMaskFilter(5f, BlurMaskFilter.Blur.NORMAL)
    }

    fun initialize(displayMetrics: DisplayMetrics): Unit {
        var height = displayMetrics.heightPixels
        var width = displayMetrics.widthPixels

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(mBitmap)


    }

    fun normal() {
        isEmboss = false
        isBlur = false
    }

    fun emboss() {
        isEmboss = true
        isBlur = false
    }

    fun blur() {
        isEmboss = false
        isBlur = true
    }

    fun clear() {
        mPath.reset()
        normal()
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mCanvas?.drawColor(mBackgroundColor)

        /*for(fingerpath in paths){
            mPaint.color = fingerpath.color
            mPaint.strokeWidth = fingerpath.strokeWidth.toFloat()
            mPaint.maskFilter = null

            if (fingerpath.isEmboss){
                mPaint.maskFilter = mEmboss}
            else if (fingerpath.isBlurr){
                mPaint.maskFilter = mBlurr
            }

            mCanvas?.drawPath(fingerpath.path,mPaint)
        }*/

        canvas?.drawBitmap(mBitmap, 0f, 0f, mBitmapPaint)
        canvas?.restore()
    }

    fun touchStart(x: Float, y: Float) {
        mPath = Path()
        var fingerPath: FingerPath = FingerPath(currentColor, isEmboss!!, isBlur!!, mStrokeWidth, mPath)
        paths?.add(fingerPath)

        mPath.reset()
        mPath.moveTo(x, y)
        mX = x
    }

    fun touchMove(x: Float, y: Float) {

        var dx = Math.abs(x - mX!!)
        var dy = Math.abs(x - mY!!)

        if (dx >= TOUCH_TOLERENCE || dy >= TOUCH_TOLERENCE) {

            // mPath.quadTo(mX,mY,(x+mX)/2,(y+mY)/2)
            mX = x
            mY = y
        }
    }

}