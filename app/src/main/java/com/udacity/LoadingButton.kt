package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText = context.getString(R.string.download)
    private var loadingProgress = 0f
    private var textColor = 0
    private var loadingColor = 0
    private var loadingCircleColor = 0
    private var buttonBackgroundColor = 0
    private val textPaint = Paint()
    private val buttonPaint = Paint()
    private val circlePaint = Paint()
    private val loadingPaint = Paint()
    private lateinit var rectF: RectF
    private var isAnimationAllowed = false
    private var animationDurationMillis: Int = 1000


    private var valueAnimator = ValueAnimator.ofFloat(0f, 1f)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Clicked -> loadingStarted()
            ButtonState.Loading -> loadingProcessing()
            ButtonState.Completed -> {
                //isAnimationAllowed = false
                loadingCompleted()
            }
        }
    }


    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            textColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
            loadingColor = getColor(
                R.styleable.LoadingButton_loadingColor,
                resources.getColor(R.color.colorPrimaryDark, null)
            )
            loadingCircleColor =
                getColor(R.styleable.LoadingButton_loadingCircleColor, Color.YELLOW)
            buttonBackgroundColor = getColor(
                R.styleable.LoadingButton_buttonBackgroundColor,
                resources.getColor(R.color.colorPrimary, null)
            )
            animationDurationMillis =
                getInteger(R.styleable.LoadingButton_animationDurationMillis, 1000)
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
        circlePaint.apply {
            isAntiAlias = true
            color = loadingCircleColor
            style = Paint.Style.FILL
        }

        loadingPaint.apply {
            isAntiAlias = true
            color = loadingColor
            style = Paint.Style.FILL
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
            drawButtonShape(it)
            drawLoadingProgress(it)
            drawButtonText(it)
            drawLoadingCircle(it)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
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
        rectF = RectF(0f, 0f, (heightSize / 2).toFloat(), (heightSize / 2).toFloat())
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun performClick(): Boolean {
        super.performClick()
        if (buttonState == ButtonState.Completed && isAnimationAllowed) buttonState =
            ButtonState.Clicked
        return true
    }

    private fun drawButtonText(canvas: Canvas) {
        canvas.drawText(
            buttonText,
            (width - textPaint.measureText(buttonText)) / 2,
            (height / 2) - ((textPaint.descent() + textPaint.ascent()) / 2),
            textPaint
        )
    }

    private fun drawButtonShape(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), buttonPaint)
    }

    private fun drawLoadingCircle(canvas: Canvas) {
        canvas.translate(
            (widthSize - widthSize / 10).toFloat(),
            height / 2 - 30f
        )
        canvas.drawArc(
            rectF,
            -90f,
            loadingProgress * 360,
            true,
            circlePaint
        )
    }

    private fun drawLoadingProgress(canvas: Canvas) {
        canvas.drawRect(0f, 0f, width.toFloat() * loadingProgress, height.toFloat(), loadingPaint)
    }

    private fun loadingStarted() {
        buttonText = context.getString(R.string.loading_in_process)
        loadingProgress = 0f
        isClickable = false
        invalidate()
        buttonState = ButtonState.Loading
    }

    private fun loadingProcessing() {
        valueAnimator.apply {
            duration = animationDurationMillis.toLong()
            addUpdateListener {
                loadingProgress = it.animatedValue as Float
                if (loadingProgress == 1f) {
                    loadingProgress = 0f
                }
                invalidate()
            }
            start()
            // allows the animation to be repeated while download progress is not completed
            doOnEnd {
                if (isAnimationAllowed) {
                    it.start()
                }
            }
        }

    }

    private fun loadingCompleted() {
        isAnimationAllowed = false
        buttonText = context.getString(R.string.download)
        loadingProgress = 0f
        valueAnimator.cancel()
        invalidate()
        isClickable = true
    }

    fun allowAnimation() {
        isAnimationAllowed = true
    }
}