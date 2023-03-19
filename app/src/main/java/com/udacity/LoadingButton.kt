package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = "Download"
    private var loadingProgress = 50f
    private var loadingProgressCircle = 50f
    private val textPaint = Paint().apply {
            textSize = 60f
            color = Color.WHITE
        }

    private val buttonPaint = Paint().apply {
            isAntiAlias = true
            color = resources.getColor(R.color.colorPrimary, null)
            style = Paint.Style.FILL
        }
    private val circlePaint = Paint().apply{
        isAntiAlias = true
        color = Color.YELLOW
        style = Paint.Style.FILL
    }

    private val valueAnimator = ValueAnimator.ofFloat(0f, width.toFloat())

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Clicked -> {}
            ButtonState.Loading -> {}
            ButtonState.Completed -> {}
        }
    }


    init {

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawButtonShape(canvas)
        drawButtonText(canvas)
        drawLoadingCircle(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    private fun drawButtonText(canvas: Canvas) {
        canvas.drawText(
            buttonText,
            (width - textPaint.measureText(buttonText)) / 2,
            (height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2),
            textPaint
        )
    }

    private fun drawButtonShape(canvas: Canvas){
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), buttonPaint)
    }

    private fun drawLoadingCircle(canvas: Canvas){
        canvas.translate(width / 2 + 30f,
            height / 2 - 30f)
        canvas.drawArc(
            RectF(0f, 0f, 30f, 30f),
            0F,
            loadingProgressCircle * 0.36f,
            true,
            circlePaint)
    }

    private fun loadingStarted(){
        buttonText = "Loading is in the process"
        invalidate()
    }

    private fun loadingProcessing(){
        buttonText = "Loading is in the process"
        valueAnimator.apply {
            addUpdateListener {
                loadingProgress = it.animatedValue as Float
                loadingProgressCircle = loadingProgress + width.toFloat()/360f
                invalidate()
            }
            duration = 5000L
            start()
        }
    }

    private fun loadingCompleted(){
        buttonText = "Download"
        loadingProgress = 0f
        loadingProgressCircle = 0f
        invalidate()
    }

}