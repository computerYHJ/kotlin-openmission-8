package com.example.kotlin_openmission_8

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.min
import androidx.core.graphics.toColorInt


class CircularProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var backgroundPaint: Paint? = null
    private var progressPaint: Paint? = null
    private var textPaint: Paint? = null
    private var progress = 0
    private var max = 100

    init {
        init()
    }

    fun setProgressWithAnimation(targetProgress: Int, duration: Long) {
        post(Runnable {
            val animator = ValueAnimator.ofInt(progress, targetProgress)
            animator.duration = duration
            animator.addUpdateListener(AnimatorUpdateListener { animation: ValueAnimator? ->
                setProgress(animation!!.animatedValue as Int)
            })
            animator.start()
        })
    }

    private fun init() {
        backgroundPaint = Paint()
        backgroundPaint!!.color = -0x1f1f20 // 백그라운드 색 (회색)
        backgroundPaint!!.style = Paint.Style.STROKE
        backgroundPaint!!.strokeWidth = 25f
        backgroundPaint!!.isAntiAlias = true
        backgroundPaint!!.strokeCap = Paint.Cap.ROUND

        progressPaint = Paint()
        progressPaint!!.color = "#00AFF0".toColorInt() // 프로그레스 색 (보라색)
        progressPaint!!.style = Paint.Style.STROKE
        progressPaint!!.strokeWidth = 25f
        progressPaint!!.isAntiAlias = true
        progressPaint!!.strokeCap = Paint.Cap.ROUND

        textPaint = Paint()
        textPaint!!.color = -0xcccccd // 텍스트 색
        textPaint!!.textSize = 80f
        textPaint!!.textAlign = Paint.Align.CENTER
        textPaint!!.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = getWidth()
        val height = getHeight()
        val radius = min(width, height) / 2 - 20

        val rectF = RectF(
            (width / 2 - radius).toFloat(),
            (height / 2 - radius).toFloat(),
            (width / 2 + radius).toFloat(),
            (height / 2 + radius).toFloat()
        )

        // 각도 계산 확인
        val angle = 233f * progress / max

        // 백그라운드 원
        canvas.drawArc(rectF, 23f, -230f, false, backgroundPaint!!)

        // 프로그레스 원
        canvas.drawArc(rectF, 153f, angle, false, progressPaint!!)

        // 텍스트 그리기
        canvas.drawText("현재 이용률", (width / 2).toFloat(), (height / 2 - 60).toFloat(), textPaint!!)
        canvas.drawText(
            "$progress%",
            (width / 2).toFloat(),
            (height / 2 + 40).toFloat(),
            textPaint!!
        )
    }

    fun setProgress(progress: Int) {
        this.progress = progress
        postInvalidate() // 다시 그리기
    }

    fun setMax(max: Int) {
        this.max = max
    }
}
