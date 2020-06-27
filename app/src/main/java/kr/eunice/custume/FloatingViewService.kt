package kr.eunice.custume

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableBoolean
import kr.eunice.custume.databinding.LayoutFloatingWidgetBinding


class FloatingViewService : Service() {
    lateinit var windowManager: WindowManager

    lateinit var binding: LayoutFloatingWidgetBinding

    val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.WRAP_CONTENT,
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
        PixelFormat.TRANSLUCENT
    )

    val showExpandedView = ObservableBoolean(false)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        binding = DataBindingUtil.inflate<LayoutFloatingWidgetBinding>(
            LayoutInflater.from(this@FloatingViewService),
            R.layout.layout_floating_widget,
            null,
            false
        )
        binding.service = this@FloatingViewService
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.addView(binding.root, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(binding.root)
    }

    val onClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.layoutExpanded -> {
                collapseView()
            }
            R.id.buttonClose ->                 //closing the widget
                stopSelf()
        }
    }

    fun expandView() {
        showExpandedView.set(true)
    }

    fun collapseView() {
        showExpandedView.set(false)
    }

    val onTouchListener = object : OnTouchListener {
        private var initialX = 0
        private var initialY = 0
        private var initialTouchX = 0f
        private var initialTouchY = 0f

        override fun onTouch(
            v: View,
            event: MotionEvent
        ): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    expandView()
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(binding.root, params)
                    return true
                }
            }
            return false
        }
    }
}