package com.example.plugindynamic

import java.lang.reflect.Field


class ReflectUtil {

    companion object{
        @JvmStatic
        @Throws(NoSuchFieldException::class, IllegalAccessException::class)
        fun getField(clazz: Class<*>, target: Any?, name: String): Any {
            val field = clazz.getDeclaredField(name)
            field.isAccessible = true
            return field.get(target)
        }

        @JvmStatic
        @Throws(NoSuchFieldException::class)
        fun getField(clazz: Class<*>, name: String): Field {
            val field = clazz.getDeclaredField(name)
            field.isAccessible = true
            return field
        }

        @JvmStatic
        @Throws(NoSuchFieldException::class, IllegalAccessException::class)
        fun setField(clazz: Class<*>, target: Any, name: String, value: Any) {
            val field = clazz.getDeclaredField(name)
            field.isAccessible = true
            field.set(target, value)
        }
    }

}