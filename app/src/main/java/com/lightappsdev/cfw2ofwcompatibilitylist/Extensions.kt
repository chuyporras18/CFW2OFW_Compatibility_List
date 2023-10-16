package com.lightappsdev.cfw2ofwcompatibilitylist

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.util.TypedValue
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import kotlin.properties.Delegates

val cfw2OfwApp: Cfw2OfwApp by lazy { Cfw2OfwApp.instance }

val gson: Gson = Gson()

private var toast: Toast? = null

fun <VH : RecyclerView.ViewHolder, T> RecyclerView.Adapter<VH>.basicDiffUtil(
    initialList: List<T> = emptyList(),
    areItemsTheSame: (T, T) -> Boolean = { old, new -> old == new },
    areContentsTheSame: (T, T) -> Boolean = { old, new -> old == new }
) =
    Delegates.observable(initialList) { _, old, new ->
        DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = old.size

            override fun getNewListSize(): Int = new.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                areItemsTheSame(old[oldItemPosition], new[newItemPosition])

            override fun areContentsTheSame(
                oldItemPosition: Int,
                newItemPosition: Int
            ): Boolean =
                areContentsTheSame(old[oldItemPosition], new[newItemPosition])

        }).dispatchUpdatesTo(this)
    }

fun View.addBackgroundRipple() {
    with(TypedValue()) {
        isClickable = true
        isFocusable = true
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
        setBackgroundResource(resourceId)
    }
}

fun View.addBackgroundCircleRipple() {
    with(TypedValue()) {
        isClickable = true
        isFocusable = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            context.theme.resolveAttribute(
                android.R.attr.selectableItemBackgroundBorderless,
                this,
                true
            )
        }
        setBackgroundResource(resourceId)
    }
}

fun View.addForegroundRipple() {
    with(TypedValue()) {
        isClickable = true
        isFocusable = true
        context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            foreground = ContextCompat.getDrawable(context, resourceId)
        }
    }
}

fun ImageView.load(any: Any?) {
    Glide.with(this).load(any).into(this)
}

fun toast(message: String?, duration: Int = Toast.LENGTH_SHORT) {
    Handler(Looper.getMainLooper()).post {
        toast?.cancel()
        toast = Toast.makeText(cfw2OfwApp.applicationContext, message, duration)
        toast?.show()
    }
}

fun RecyclerView.saveState(outState: Bundle?) {
    outState?.putParcelable("rvState_${this.id}", this.layoutManager?.onSaveInstanceState())
}

@Suppress("DEPRECATION")
fun RecyclerView.restoreState(savedInstanceState: Bundle?) {
    val state = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        savedInstanceState?.getParcelable("rvState_${this.id}", Parcelable::class.java)
    } else {
        savedInstanceState?.getParcelable("rvState_${this.id}")
    }
    layoutManager?.onRestoreInstanceState(state)
}

inline fun <reified T> String.fromJson(): T {
    return gson.fromJson(this, T::class.java)
}

fun Any.toJson(): String {
    return gson.toJson(this)
}

fun ProgressBar.setProgressSmoothly(progress: Int, duration: Long = 1000) {
    ObjectAnimator.ofInt(this, "progress", progress).apply {
        this.duration = duration; interpolator = DecelerateInterpolator(); start()
    }!!
}