package kr.eunice.custume

import android.view.View
import androidx.databinding.BindingAdapter

@BindingAdapter("onTouch")
fun setOnTouchListener(view: View, listener: View.OnTouchListener?) {
    view.setOnTouchListener(listener)
}