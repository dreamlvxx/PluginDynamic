package com.example.plugindynamic

import android.app.Instrumentation
import android.content.Context

class HookHelper {
    companion object{
        @JvmField
        val TARGET_INTENT = "target_intent"

        @JvmStatic
        @Throws(Exception::class)
        fun hookInstrumentation(context: Context) {
            val contextImplClass = Class.forName("android.app.ContextImpl")
            val activityThread = ReflectUtil.getField(contextImplClass, context, "mMainThread")
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val mInstrumentation = ReflectUtil.getField(activityThreadClass, activityThread, "mInstrumentation")

            ReflectUtil.setField(
                activityThreadClass,
                activityThread,
                "mInstrumentation",
                InstrumentationProxy(mInstrumentation as Instrumentation, context.getPackageManager())
            )
        }
    }
}