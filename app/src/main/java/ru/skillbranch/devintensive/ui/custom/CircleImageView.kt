package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.appcompat.widget.AppCompatImageView
import ru.skillbranch.devintensive.R
import kotlin.math.min
import kotlin.math.round

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    companion object {
        private const val DEFAULT_BORDER_COLOR = Color.WHITE
        private const val DEFAULT_BORDER_WIDTH = 2
    }

    private val paint: Paint = Paint().apply { isAntiAlias = true }
    private val paintBorder: Paint = Paint().apply { isAntiAlias = true }
    private val paintBackground: Paint = Paint().apply { isAntiAlias = true }
    private var circleCenter = 0f
    private var heightCircle: Int = 0
    private var circleColor: Int = Color.DKGRAY
    private var borderWidth: Float = DEFAULT_BORDER_WIDTH * context.resources.displayMetrics.density
    private var borderColor: Int = DEFAULT_BORDER_COLOR

    private var civImage: Bitmap? = null

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            val attributes =
                context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)

            val defaultBorderSize = DEFAULT_BORDER_WIDTH * context.resources.displayMetrics.density
            borderWidth = attributes.getDimension(
                R.styleable.CircleImageView_cv_borderWidth,
                defaultBorderSize
            )
            borderColor =
                attributes.getColor(R.styleable.CircleImageView_cv_borderColor, Color.WHITE)
            circleColor = borderColor

            attributes.recycle()

            updateBitmap()
        }
    }

    private fun updateBitmap() {
        civImage = drawableToBitmap(drawable)
        if (civImage == null || paintBorder == null || paint == null) return
        civImage?.also {
            val shader = BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            val usableWidth = width - (paddingLeft + paddingRight)
            val usableHeight = height - (paddingTop + paddingBottom)

            heightCircle = min(usableWidth, usableHeight)

            circleCenter = (heightCircle - borderWidth * 2) / 2
            paintBorder.color = if (borderWidth == 0f) circleColor else borderColor

            // Center Image in Shader
            val scale: Float
            val dx: Float
            val dy: Float

            when (scaleType) {
                ScaleType.CENTER_CROP -> if (it.width * height > width * it.height) {
                    scale = height / it.height.toFloat()
                    dx = (width - it.width * scale) * 0.5f
                    dy = 0f
                } else {
                    scale = width / it.width.toFloat()
                    dx = 0f
                    dy = (height - it.height * scale) * 0.5f
                }
                ScaleType.CENTER_INSIDE -> if (it.width * height < width * it.height) {
                    scale = height / it.height.toFloat()
                    dx = (width - it.width * scale) * 0.5f
                    dy = 0f
                } else {
                    scale = width / it.width.toFloat()
                    dx = 0f
                    dy = (height - it.height * scale) * 0.5f
                }
                else -> {
                    scale = 0f
                    dx = 0f
                    dy = 0f
                }
            }

            shader.setLocalMatrix(Matrix().apply {
                setScale(scale, scale)
                postTranslate(dx, dy)
            })

            paint.shader = shader
        }

        invalidate()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        updateBitmap()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        updateBitmap()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        updateBitmap()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        updateBitmap()
    }

    override fun getScaleType(): ScaleType =
        super.getScaleType().let { if (it == null || it != ScaleType.CENTER_INSIDE) ScaleType.CENTER_CROP else it }

    override fun setScaleType(scaleType: ScaleType) {
        if (scaleType != ScaleType.CENTER_CROP && scaleType != ScaleType.CENTER_INSIDE) {
            throw IllegalArgumentException(
                String.format(
                    "ScaleType %s not supported. " + "Just ScaleType.CENTER_CROP & ScaleType.CENTER_INSIDE are available for this library.",
                    scaleType
                )
            )
        } else {
            super.setScaleType(scaleType)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (civImage == null) return

        val circleCenterWithBorder = circleCenter + borderWidth

        canvas.drawCircle(
            circleCenterWithBorder,
            circleCenterWithBorder,
            circleCenterWithBorder,
            paintBorder
        )

        canvas.drawCircle(
            circleCenterWithBorder,
            circleCenterWithBorder,
            circleCenter,
            paintBackground
        )

        canvas.drawCircle(circleCenterWithBorder, circleCenterWithBorder, circleCenter, paint)
    }

    private fun drawableToBitmap(drawable: Drawable?): Bitmap? =
        when (drawable) {
            null -> null
            is BitmapDrawable -> drawable.bitmap
            else -> try {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = measure(widthMeasureSpec)
        val height = measure(heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    private fun measure(measureSpec: Int): Int {
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        return when (specMode) {
            MeasureSpec.EXACTLY -> specSize // The parent has determined an exact size for the child.
            MeasureSpec.AT_MOST -> specSize // The child can be as large as it wants up to the specified size.
            else -> heightCircle // The parent has not imposed any constraint on the child.
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateBitmap()
    }

    fun getBorderWidth(): Int = (borderWidth / context.resources.displayMetrics.density).toInt()

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = dp * context.resources.displayMetrics.density
        updateBitmap()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)
        circleColor = borderColor
        updateBitmap()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = resources.getColor(colorId, context.theme)
        circleColor = borderColor
        updateBitmap()
    }

    fun setInitialsAvatar(initials: String) {
        if (initials.isEmpty()) return

        val textSizeCoef = 0.43f
        val imageSize = context.resources.getDimensionPixelSize(R.dimen.avatar_round_size)
        val tv = TypedValue()
        val a = context.obtainStyledAttributes(tv.data, intArrayOf(R.attr.colorAccent))

        val rectF = RectF()
        with(rectF) {
            bottom = imageSize.toFloat()
            right = imageSize.toFloat()
        }
        val paint = Paint(ANTI_ALIAS_FLAG)
        paint.color = a.getColor(0, 0)

        val textWidth: Float
        val textBaseLine: Float
        val textPaint = TextPaint(ANTI_ALIAS_FLAG)

        with(textPaint) {
            textSize = imageSize * textSizeCoef
            color = Color.WHITE
            textWidth = measureText(initials) * 0.5f
            textBaseLine = fontMetrics.ascent * -0.4f
        }

        val centerX = round(imageSize * 0.5f)
        val centerY = round(imageSize * 0.5f)

        val bitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        with(canvas) {
            drawRect(rectF, paint)
            drawText(initials, centerX - textWidth, centerY + textBaseLine, textPaint)
        }

        a.recycle()

        setImageBitmap(bitmap)
    }
}