package com.ash.thetracker.shared


import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.ash.thetracker.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Context?.showToast(message: Any?) = CoroutineScope(Dispatchers.Main).launch {
    this@showToast?.let { Toast.makeText(it, message.toString(), Toast.LENGTH_SHORT).show() }
}

fun Fragment?.showToast(message: Any?) = this?.context.showToast(message)
fun Activity?.showToast(message: Any?) = (this as Context?).showToast(message)
fun showToast(message: Any?) = App.applicationContext().showToast(message)

fun Context.string(@StringRes stringRes: Int): String = getString(stringRes)
fun Context.string(@StringRes stringRes: Int, vararg args: Any): String = getString(stringRes).format(*args)

fun View.string(@StringRes stringRes: Int): String = context.getString(stringRes)
fun View.string(@StringRes stringRes: Int, vararg args: Any): String =
    context.getString(stringRes).format(*args)

fun Fragment.string(@StringRes stringRes: Int, vararg args: Any): String =
    context?.string(stringRes, *args) ?: ""

fun Context.integer(@IntegerRes integerRes: Int): Int = resources.getInteger(integerRes)
fun View.integer(@IntegerRes integerRes: Int): Int = context.integer(integerRes)

fun RecyclerView.ViewHolder.color(resColor: Int) =
    itemView.context.color(resColor)

fun RecyclerView.ViewHolder.getColorStateList(resColor: Int) =
    itemView.context.colorStateList(resColor)

fun RecyclerView.ViewHolder.string(@StringRes stringRes: Int): String =
    itemView.context.getString(stringRes)

fun RecyclerView.ViewHolder.string(@StringRes stringRes: Int, vararg args: Any): String =
    itemView.context.getString(stringRes).format(*args)

fun Context.color(resColor: Int) = ContextCompat.getColor(this, resColor)
fun View.color(resColor: Int) = ContextCompat.getColor(context, resColor)
fun Context.colorStateList(resColor: Int) = ContextCompat.getColorStateList(this, resColor)
fun View.colorStateList(resColor: Int) = ContextCompat.getColorStateList(context, resColor)

fun Fragment.getColor(resColor: Int) = requireContext().color(resColor)
fun Fragment.getColorStateList(resColor: Int) = requireContext().colorStateList(resColor)


fun Fragment.drawable(@DrawableRes drawableRes: Int): Drawable? =
    ContextCompat.getDrawable(requireContext(), drawableRes)

fun Context.drawable(@DrawableRes drawableRes: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableRes)

fun View.drawable(@DrawableRes drawableRes: Int): Drawable? =
    ContextCompat.getDrawable(context, drawableRes)

fun Fragment.tintDrawable(@DrawableRes drawableRes: Int, @ColorRes color: Int): Drawable? =
    drawable(drawableRes)?.tinted(getColor(color))

fun Context.tintDrawable(@DrawableRes drawableRes: Int, @ColorInt color: Int): Drawable? =
    drawable(drawableRes)?.tinted(color)

fun View.tintDrawable(@DrawableRes drawableRes: Int, @ColorInt color: Int): Drawable? =
    drawable(drawableRes)?.tinted(color)

fun Drawable.tinted(@ColorInt color: Int): Drawable {
    val wrapDrawable = DrawableCompat.wrap(this).mutate()
    DrawableCompat.setTint(wrapDrawable, color)
    return wrapDrawable
}

val Fragment.displayMetrics: DisplayMetrics
    get() {
        val metrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getRealMetrics(metrics)
        return metrics
    }

val Fragment.screenHeight get() = displayMetrics.heightPixels