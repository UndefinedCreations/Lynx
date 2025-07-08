@file:Suppress("UNCHECKED_CAST")
package com.undefined.lynx.util

import java.lang.reflect.Method

object ReflectionUtil {
    fun <T : Any> getPrivateField(any: Class<*>, name: String): T =
        any::class.java.getDeclaredField(name).apply { isAccessible = true }[this] as T

    inline fun <reified T : Any, R> executePrivateMethod(name: String, vararg args: Any?): R =
        T::class.java.getDeclaredMethod(name).apply { isAccessible = true }(null, args) as R
}

fun <T : Any> Any.getPrivateField(clazz: Class<*>, name: String): T =
    clazz.getDeclaredField(name).apply { isAccessible = true }[this] as T

fun <T : Any> Any.getPrivateField(name: String): T =
    javaClass.getDeclaredField(name).apply { isAccessible = true }[this] as T

fun Any.setPrivateField(name: String, data: Any) {
    this::class.java.getDeclaredField(name).run {
        isAccessible = true
        set(this@setPrivateField, data)
    }
}

fun <T : Any> Any.executePrivateMethod(name: String, vararg args: Any?): T =
    javaClass.getDeclaredMethod(name).apply { isAccessible = true }(this, args) as T

fun Class<*>.getPrivateMethod(name: String, vararg args: Class<*>): Method =
    getDeclaredMethod(name, *args).apply { this.isAccessible = true}

fun Method.execute(instance: Any, vararg parameters: Any): Any = this(instance, *parameters)