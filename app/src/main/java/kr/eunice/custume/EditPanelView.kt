package kr.eunice.custume

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.databinding.BindingAdapter


class EditPanelView(context: Context?, attrs: AttributeSet?) :
    View(context, attrs) {
    private val drawPath: Path = Path()

    private var drawPaint: Paint = Paint().apply {
        color = paintColor
        isAntiAlias = true
        strokeWidth = 15.0f
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private val canvasPaint: Paint = Paint(Paint.DITHER_FLAG)

    private var paintColor = Color.BLACK

    private lateinit var canvasBitmap: Bitmap

    private val drawCanvas: Canvas by lazy { Canvas(canvasBitmap) }

    lateinit var bitmap: Bitmap

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

    }

    override fun onDraw(canvas: Canvas) {
        if (this::bitmap.isInitialized) {
            canvas.drawBitmap(
                bitmap,
                0f,
                0f,
                canvasPaint
            )
        }
        canvas.drawBitmap(canvasBitmap, 0f, 0f, canvasPaint)
        canvas.drawPath(drawPath, drawPaint)
    }

    //register user touches as drawing action
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val touchX = event.x
        val touchY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> drawPath.moveTo(touchX, touchY)
            MotionEvent.ACTION_MOVE -> drawPath.lineTo(touchX, touchY)
            MotionEvent.ACTION_UP -> {
                drawPath.lineTo(touchX, touchY)
                drawCanvas.drawPath(drawPath, drawPaint)
                drawPath.reset()
            }
            else -> return false
        }
        //redraw
        invalidate()
        return true
    }

    //update color
    fun setColor(newColor: String?) {
        invalidate()
        paintColor = Color.parseColor(newColor)
        drawPaint.color = paintColor
    }

    //start new drawing
    fun startNew() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR)
        invalidate()
    }

}

@BindingAdapter("imageUri")
fun setImageUri(view: EditPanelView, uri: Uri?) {
    try {
        view.bitmap = if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(
                view.context.contentResolver,
                uri
            )
        } else {
            val source = ImageDecoder.createSource(view.context.contentResolver, uri ?: return)
            ImageDecoder.decodeBitmap(source)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}