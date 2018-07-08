package com.example.a2piano

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PianoView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    companion object;
    //14 white keys and 10 black keys
    private val blackKeysNo = 10
    private val whiteKeysNo = 14
    private var keyWidth: Int = 0
    private var keyHeight: Int = 0
    private var blackPaint = Paint()
    private var whitePaint = Paint()
    private var yellowPaint = Paint()
    private var whiteKeys: ArrayList<Key> = ArrayList()
    private var blackKeys: ArrayList<Key> = ArrayList()
    private var soundPalyer = AudioSoundPlayer(context)

    init {
        setupPaint()
    }

    private fun setupPaint() {

        blackPaint.isAntiAlias = true
        blackPaint.color = Color.BLACK

        whitePaint.isAntiAlias = true
        whitePaint.color = Color.WHITE
        whitePaint.style = Paint.Style.FILL

        yellowPaint.color = Color.YELLOW
        yellowPaint.style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        keyWidth = w / whiteKeysNo
        keyHeight = h
        var count = 15

        for (value in 0..count - 1) {
            var left = value * keyWidth
            var right = left + keyWidth

            if (value == whiteKeysNo - 1) {
                right = w
            }

            var rectF: RectF = RectF(left.toFloat(), 0f, right.toFloat(), h.toFloat())
            whiteKeys.add(Key(value + 1, rectF))

            if (value != 0 && value != 3 && value != 7 && value != 10 && value != 14) {

                var rectF: RectF = RectF(((value - 1) * keyWidth + 0.5f * keyWidth + 0.25f * keyWidth), 0f,
                        (value * keyWidth + 0.25 * keyWidth).toFloat(), (0.67 * keyHeight).toFloat())

                blackKeys.add(Key(count, rectF))
                count++
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (keys in whiteKeys) {
            canvas.drawRect(keys.rect, (if (keys.isPressed) yellowPaint else whitePaint))
        }

        for (lines in 0..whiteKeysNo) {
            canvas.drawLine((lines * keyWidth).toFloat(), 0f, (lines * keyWidth).toFloat(), keyHeight.toFloat(), blackPaint)
        }

        for (keys in blackKeys) {
            canvas.drawRect(keys.rect, (if (keys.isPressed) yellowPaint else blackPaint))
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //return super.onTouchEvent(event)

        var action: Int = event.action
        var isDownAction = action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE
        var touchIndex: Int = event.pointerCount

        for (touch in 0..touchIndex - 1) {
            var x: Float = event.getX(touch)
            var y: Float = event.getY(touch)

            var key: Key? = keyForCoords(x, y)

            if (key != null) {
                key.isPressed = isDownAction
            }

            var temp: ArrayList<Key> = ArrayList(whiteKeys)
            temp.addAll(blackKeys)

            for (key in temp) {
                if (key.isPressed) {
                    if (!soundPalyer.isNotePlaying(key.sound)) {
                        soundPalyer.playNote(key.sound)
                        invalidate()
                    } else {
                        releaseKey(key)
                    }
                } else {
                    soundPalyer.stopNote(key.sound)
                    releaseKey(key)
                }
            }
        }
        return true
    }

    private fun keyForCoords(x: Float, y: Float): Key? {

        for (blackKey in blackKeys) {
            if (blackKey.rect.contains(x, y)) {
                return blackKey
            }
        }

        for (whiteKey in whiteKeys) {
            if (whiteKey.rect.contains(x, y)) {
                return whiteKey
            }
        }
        return null
    }

    private fun releaseKey(key: Key) {
        handler.postDelayed({
            key.isPressed = false
            handler.sendEmptyMessage(0)
        }, 100)
    }

    private val handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            invalidate()
        }
    }
}