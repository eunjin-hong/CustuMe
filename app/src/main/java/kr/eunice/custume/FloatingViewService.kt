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


class FloatingViewService : Service(), View.OnClickListener {
    lateinit var mWindowManager: WindowManager
    lateinit var mFloatingView: View
    lateinit var collapsedView: View
    lateinit var expandedView: View
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()


        //getting the widget layout from xml using layout inflater
        mFloatingView = LayoutInflater.from(this@FloatingViewService).inflate(R.layout.layout_floating_widget, null)

        //setting the layout parameters
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager.addView(mFloatingView, params)

        collapsedView = mFloatingView.findViewById(R.id.layoutCollapsed)
        expandedView = mFloatingView.findViewById(R.id.layoutExpanded)

        //adding click listener to close button and expanded view
        mFloatingView.findViewById<View>(R.id.buttonClose).setOnClickListener(this)
        expandedView.setOnClickListener(this)

        //adding an touchlistener to make drag movement of the floating widget
        mFloatingView.findViewById<View>(R.id.relativeLayoutParent)
            .setOnTouchListener(object : OnTouchListener {
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
                            //when the drag is ended switching the state of the widget
                            collapsedView.setVisibility(View.GONE)
                            expandedView.setVisibility(View.VISIBLE)
                            return true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            //this code is helping the widget to move around the screen with fingers
                            params.x = initialX + (event.rawX - initialTouchX).toInt()
                            params.y = initialY + (event.rawY - initialTouchY).toInt()
                            mWindowManager.updateViewLayout(mFloatingView, params)
                            return true
                        }
                    }
                    return false
                }
            })
    }

    override fun onDestroy() {
        super.onDestroy()
        mWindowManager.removeView(mFloatingView)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.layoutExpanded -> {
                //switching views
                collapsedView.visibility = View.VISIBLE
                expandedView.visibility = View.GONE
            }
            R.id.buttonClose ->                 //closing the widget
                stopSelf()
        }
    }
}