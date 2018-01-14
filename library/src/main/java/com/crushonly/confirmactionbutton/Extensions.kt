package com.crushonly.confirmactionbutton

import android.content.res.Resources
import android.os.Build

/**
 * Created by tom.stoepker on 1/13/18.
 */

fun Resources.colorFromResId(resId: Int, theme: Resources.Theme? = null) =
        if (Build.VERSION.SDK_INT < 23) getColor(resId) else getColor(resId, theme)

fun Resources.dpToPx(dp: Float): Int {
    val scale = displayMetrics.density
    return (dp * scale + 0.5F).toInt()
}