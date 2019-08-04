package ru.skillbranch.devintensive.ui.custom

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff.Mode
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import ru.skillbranch.devintensive.App
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.extensions.dpToPx
import ru.skillbranch.devintensive.extensions.spToPx
import kotlin.math.min


class CircleImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {

    private val defaultBorderColor: Int = Color.WHITE
    private var borderColor: Int = defaultBorderColor
    private var borderWidth: Int = context.dpToPx(2f)
    private var text: String? = null

    init {
        if (attrs != null) {
            val attrVal = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView)
            borderColor = attrVal.getColor(R.styleable.CircleImageView_cv_borderColor, defaultBorderColor)
            borderWidth = attrVal.getDimensionPixelSize(R.styleable.CircleImageView_cv_borderWidth, borderWidth)
            attrVal.recycle()
        }
    }

    @Dimension
    fun getBorderWidth(): Int = borderWidth

    fun setBorderWidth(@Dimension dp: Int) {
        borderWidth = context.dpToPx(dp.toFloat())

        invalidate()
    }

    fun getBorderColor(): Int = borderColor

    fun setBorderColor(hex: String) {
        borderColor = Color.parseColor(hex)

        invalidate()
    }

    fun setBorderColor(@ColorRes colorId: Int) {
        borderColor = ContextCompat.getColor(App.applicationContext(), colorId)

        invalidate()
    }

    fun generateAvatar(text: String?, sizeSp: Float, theme: Resources.Theme) {
        /* don't render if initials haven't changed */
        if (text != this.text) {
            val image =
                    if (text == null) {
                        generateDefAvatar(theme)
                    } else {
                        generateLetterAvatar(text, sizeSp, theme)
                    }

            this.text = text
            setImageBitmap(image)
        }
    }

    override fun onDraw(canvas: Canvas) {
        var bitmap = getBitmapFromDrawable() ?: return
        if (width == 0 || height == 0) return

        bitmap = getScaledBitmap(bitmap, width)
        bitmap = getCenterCroppedBitmap(bitmap, width)
        bitmap = getCircleBitmap(bitmap)

        if (borderWidth > 0) {
            bitmap = getStrokedBitmap(bitmap, borderWidth, borderColor)
        }

        canvas.drawBitmap(bitmap, 0F, 0F, null)
    }

    private fun generateDefAvatar(theme: Resources.Theme): Bitmap {
        val image = Bitmap.createBitmap(layoutParams.height, layoutParams.height, Config.ARGB_8888)
        val color = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, color, true)


        val canvas = Canvas(image)
        canvas.drawColor(color.data)

        return image
    }

    private fun generateLetterAvatar(text: String, sizeSp: Float, theme: Resources.Theme): Bitmap {
        val image = generateDefAvatar(theme)

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.textSize = context.spToPx(sizeSp).toFloat()
        paint.color = Color.WHITE
        paint.textAlign = Paint.Align.CENTER

        val textBounds = Rect()
        paint.getTextBounds(text, 0, text.length, textBounds)

        val backgroundBounds = RectF()
        backgroundBounds.set(0f, 0f, layoutParams.height.toFloat(), layoutParams.height.toFloat())

        val textBottom = backgroundBounds.centerY() - textBounds.exactCenterY()
        val canvas = Canvas(image)
        canvas.drawText(text, backgroundBounds.centerX(), textBottom, paint)

        return image
    }

    private fun getStrokedBitmap(squareBmp: Bitmap, strokeWidth: Int, color: Int): Bitmap {
        val inCircle = RectF()
        val strokeStart = strokeWidth / 2F
        val strokeEnd = squareBmp.width - strokeWidth / 2F

        inCircle.set(strokeStart, strokeStart, strokeEnd, strokeEnd)

        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        strokePaint.color = color
        strokePaint.style = Paint.Style.STROKE
        strokePaint.strokeWidth = strokeWidth.toFloat()

        val canvas = Canvas(squareBmp)
        canvas.drawOval(inCircle, strokePaint)

        return squareBmp
    }

    private fun getCenterCroppedBitmap(bitmap: Bitmap, size: Int): Bitmap {
        val cropStartX = (bitmap.width - size) / 2
        val cropStartY = (bitmap.height - size) / 2

        return Bitmap.createBitmap(bitmap, cropStartX, cropStartY, size, size)
    }

    private fun getScaledBitmap(bitmap: Bitmap, minSide: Int): Bitmap {
        return if (bitmap.width != minSide || bitmap.height != minSide) {
            val smallest = min(bitmap.width, bitmap.height).toFloat()
            val factor = smallest / minSide
            Bitmap.createScaledBitmap(bitmap, (bitmap.width / factor).toInt(), (bitmap.height / factor).toInt(), false)
        } else bitmap
    }

    private fun getBitmapFromDrawable(): Bitmap? {
        if (drawable == null)
            return null

        if (drawable is BitmapDrawable)
            return (drawable as BitmapDrawable).bitmap

        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    private fun getCircleBitmap(bitmap: Bitmap): Bitmap {
        val smallest = min(bitmap.width, bitmap.height)
        val outputBmp = Bitmap.createBitmap(smallest, smallest, Config.ARGB_8888)
        val canvas = Canvas(outputBmp)

        val paint = Paint()
        val rect = Rect(0, 0, smallest, smallest)

        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        canvas.drawARGB(0, 0, 0, 0)
        canvas.drawCircle(smallest / 2F, smallest / 2F, smallest / 2F, paint)

        paint.xfermode = PorterDuffXfermode(Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return outputBmp
    }
}

