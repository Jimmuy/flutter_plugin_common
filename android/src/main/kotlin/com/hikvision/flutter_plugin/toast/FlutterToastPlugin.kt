package com.hikvision.flutter_plugin.toast

import android.content.Context
import android.graphics.PorterDuff
import android.os.Build
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.hikvision.flutter_plugin.R
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.PluginRegistry.Registrar

/** FluttertoastPlugin  */
open class FlutterToastPlugin constructor(private val ctx: Context) : MethodCallHandler {

    companion object {
        /** Plugin registration.  */
        @JvmStatic
        fun registerWith(registrar: Registrar) {
            val channel = MethodChannel(registrar.messenger(), "hikvision/fluttertoast")
            channel.setMethodCallHandler(FlutterToastPlugin(registrar.context()))
        }
    }

    private var toast: Toast? = null
    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "showToast" -> showToast(call, result)
            "cancel" -> {
                toast?.cancel()
                result.success(true)
            }
            "getPlatformVersion" -> {
                result.success("Android ${android.os.Build.VERSION.RELEASE}")
            }
            else -> result.notImplemented()
        }
    }

    private fun showToast(call: MethodCall, result: MethodChannel.Result) {
        val msg = call.argument<Any>("msg").toString()
        val length = call.argument<Any>("length").toString()
        val gravity = call.argument<Any>("gravity").toString()
        val bgColor = call.argument<Number>("bgcolor")?.toInt()
        val textColor = call.argument<Number>("textcolor")?.toInt()
        val textSize = call.argument<Number>("fontSize")?.toFloat()
        toast = Toast.makeText(ctx, msg, Toast.LENGTH_SHORT)
        toast?.apply {
            setText(msg)
            duration = if (length == "long") {
                Toast.LENGTH_LONG
            } else {
                Toast.LENGTH_SHORT
            }
            when (gravity) {
                "top" -> setGravity(Gravity.TOP, 0, 100)
                "center" -> setGravity(Gravity.CENTER, 0, 0)
                else -> setGravity(Gravity.BOTTOM, 0, 100)
            }
            val text = view.findViewById<TextView>(android.R.id.message)
            textSize?.let { text.textSize = it }
            if (bgColor != null) {
                val shapeDrawable = ContextCompat.getDrawable(ctx, R.drawable.toast_bg)
                shapeDrawable?.apply {
                    setColorFilter(bgColor, PorterDuff.Mode.SRC_IN)
                    if (Build.VERSION.SDK_INT <= 27) {
                        view.background = shapeDrawable
                    } else {
                        text.background = shapeDrawable
                    }
                }

            }
            textColor?.let { text.setTextColor(it) }
            show()
            result.success(true)
        }

    }



}