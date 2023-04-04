package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var mBackgroundColor = 0
    private var mLoadingColor = 0
    private var mTextColor = 0
    private var mTextSize = 0.0f
    private var mTextString = ""

    private var mTextPaint = Paint()

    private var mButtonPaint = Paint()

    private var mLoadingPaint = Paint()

    private var mProgressPaint = Paint()

    private var mProgress = 0f

    private val valueAnimator = ValueAnimator.ofFloat(0f, 360f)

    private lateinit var onAnimationListener: OnAnimationListener

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, buttonState ->
        when (buttonState) {
            ButtonState.Completed -> {
                updateTextPaint(false)
                valueAnimator.cancel()
                mProgress = 0f
            }
            ButtonState.Loading -> {
                updateTextPaint(true)
                valueAnimator.start()
            }
            ButtonState.Clicked -> {
            }
        }

        invalidate()
    }


    init {
        initAttributeSet(attrs!!)
        initAnimation()
        initButtonPaint()
        initLoadingPaint()
        initProgressPaint()
        updateTextPaint(false)
    }

    fun setOnAnimationListener (onAnimationListener: OnAnimationListener) {
        this.onAnimationListener = onAnimationListener
    }
    private fun initAttributeSet(attrs: AttributeSet) {
        val typeArray =
            context.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0)
        try {
            mBackgroundColor = typeArray.getColor(
                R.styleable.LoadingButton_backgroundColor,
                ContextCompat.getColor(context, R.color.colorPrimary)
            )
            mLoadingColor = typeArray.getColor(
                R.styleable.LoadingButton_loadingColor,
                ContextCompat.getColor(context, R.color.colorPrimaryDark)
            )
            mTextColor = typeArray.getColor(
                R.styleable.LoadingButton_textColor,
                ContextCompat.getColor(context, R.color.white)
            )
            mTextSize = typeArray.getDimension(
                R.styleable.LoadingButton_textSize,
                resources.getDimension(R.dimen.text_size_28)
            )
        } finally {
            typeArray.recycle()
        }
    }

    private fun initAnimation() {
        valueAnimator.apply {
            addUpdateListener {
                mProgress = it.animatedValue as Float
                invalidate()
                // Mock download completed
                if (mProgress == 360f) {
                    onAnimationListener.onFinish()
                }
            }
            interpolator = LinearInterpolator()
            duration = 2000
        }
    }

    private fun initButtonPaint() {
        mButtonPaint.apply {
            color = mBackgroundColor
            style = Paint.Style.FILL
        }
    }

    private fun initLoadingPaint() {
        mLoadingPaint.apply {
            color = mLoadingColor
            style = Paint.Style.FILL
        }
    }

    private fun initProgressPaint() {
        mProgressPaint.apply {
            color = resources.getColor(R.color.colorAccent, null)
            style = Paint.Style.FILL
        }
    }

    private fun updateTextPaint(isLoading: Boolean) {
        mTextColor = if (isLoading) {
            ContextCompat.getColor(context, R.color.grey)
        } else {
            ContextCompat.getColor(context, R.color.white)
        }
        mTextString = if (isLoading) {
            resources.getString(R.string.button_loading)
        } else {
            resources.getString(R.string.button_name)
        }
        mTextPaint.apply {
            textAlign = Paint.Align.CENTER
            color = mTextColor
            textSize = mTextSize
            typeface = Typeface.create("", Typeface.BOLD)
        }
    }

    override fun onDraw(canvas: Canvas?) {

        Log.d("LoadingButton", "Progress change $mProgress")
        super.onDraw(canvas)
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), mButtonPaint)
        canvas?.drawRect(0f, 0f, widthSize * mProgress / 360f, heightSize.toFloat(), mLoadingPaint)
        canvas?.drawText(mTextString, widthSize / 2.0f, heightSize / 2.0f + 30.0f, mTextPaint)
        canvas?.drawArc(
            widthSize - 150f,
            35f,
            widthSize - 50f,
            135f,
            0f,
            mProgress,
            true,
            mProgressPaint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    interface OnAnimationListener {
        fun onFinish()
    }
}