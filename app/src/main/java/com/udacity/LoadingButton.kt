package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = "Download"
    private var loadingProgress = 0f
    private var textColor = 0
    private var loadingColor = 0
    private var loadingCircleColor = 0
    private var buttonBackgroundColor = 0
    private val textPaint = Paint()
    private val buttonPaint = Paint()
    private val circlePaint = Paint()
    private lateinit var rectF : RectF


    private val valueAnimator = ValueAnimator.ofFloat(0f, width.toFloat())

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new){
            ButtonState.Clicked -> loadingStarted()
            ButtonState.Loading -> loadingProcessing()
            ButtonState.Completed -> loadingCompleted()
        }
    }


    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton){
            textColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
            loadingColor = getColor(R.styleable.LoadingButton_loadingColor, resources.getColor(R.color.colorPrimaryDark, null))
            loadingCircleColor = getColor(R.styleable.LoadingButton_loadingCircleColor, Color.YELLOW)
            buttonBackgroundColor = getColor(R.styleable.LoadingButton_buttonBackgroundColor, resources.getColor(R.color.colorPrimary, null))
        }
        textPaint.apply {
            textSize = 60f
            color = textColor
        }
        buttonPaint.apply {
            isAntiAlias = true
            color = buttonBackgroundColor
            style = Paint.Style.FILL
        }
        circlePaint.apply{
            isAntiAlias = true
            color = loadingCircleColor
            style = Paint.Style.FILL
        }

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
        rectF = RectF(0f, 0f, (heightSize/2).toFloat(), (heightSize/2).toFloat())
        super.onSizeChanged(w, h, oldw, oldh)
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
        canvas.translate((widthSize-widthSize/10).toFloat(),
            height / 2 - 30f)
        canvas.drawArc(
            rectF,
            -90f,
            (loadingProgress*3.6).toFloat(),
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
                loadingProgress += it.animatedValue as Float
                //loadingProgress = loadingProgress + width.toFloat()/360f
                invalidate()
            }
            duration = 5000L
            start()
        }
    }

    private fun loadingCompleted(){
        buttonText = "Download"
        loadingProgress = 0f
        valueAnimator.cancel()
        invalidate()
    }

}