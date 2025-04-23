package com.mobirecord.utils

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.view.animation.AnimationUtils
import android.widget.ScrollView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import androidx.annotation.Px
import androidx.core.animation.doOnEnd
import androidx.core.content.res.use
import androidx.core.graphics.applyCanvas
import androidx.core.view.ViewCompat
import androidx.core.view.drawToBitmap
import androidx.core.view.forEach
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


/**
 * Created by Durga prasad on 06,August,2021
 * Copyright © 2021 Norm Android Developer. All rights reserved.
 */


fun View.visibleView() {
    this.visibility = View.VISIBLE
}

fun View.goneView() {
    this.visibility = View.GONE
}


fun View.invisibleView() {
    this.visibility = View.INVISIBLE
}

private var lastClickMs: Long = 0
private const val TOO_SOON_DURATION_MS: Long = 1200

fun View.setOnSafeClickListener(onClick: (View) -> Unit) {
    this.setOnClickListener {
        val nowMs = System.currentTimeMillis()
        if (lastClickMs != 0L && nowMs - lastClickMs < TOO_SOON_DURATION_MS) {
            return@setOnClickListener
        }
        lastClickMs = nowMs

        onClick(this)
    }
}

/*fun View.loginCheckClickListener(context:Context,onClick: (View) -> Unit) {
     var sm = SessionManager(context)

    this.setOnClickListener {
        if (sm.isUserLoggedIn()) {
            AppMethods.checkForPlanValidity(sm, context) { isValid ->
                if (isValid) {
                    onClick(this)
                } else {
                    DialogUtils.alertDialog(
                        activity = activity,
                        null,
                        "Please upgrade your subscription"
                    )
                }
            }
        } else {
              AppMethods.guestUserPopup(context)
        }
    }
}*/
/*fun View.loginCheckClickListener(context: Any, onClick: (View) -> Unit) {
    var sm = SessionManager(this.context)
    var isClickable = true // Flag to track if the view is clickable
    this.setOnClickListener {
        if (isClickable) {
            isClickable = false // Set the flag to false to prevent further clicks
            Handler(Looper.getMainLooper()).postDelayed({
                isClickable = true // Reset the flag after the delay
            }, 1000) // Delay in milliseconds (e.g., 1000ms = 1 second)
            if (sm.isUserLoggedIn()) {
                if (sm.getData(AppStrings.SessionValues.profileStatus, 0) == 1) {
                    AppMethods.checkForPlanValidity(sm, this.context) { isValid ->
                        if (isValid) {
                            onClick(this)
                        } else {
                            when (context) {
                                is Activity -> {
                                    // Log to check if context is correctly recognized as Activity
                                    Log.d("LoginCheck", "Context is Activity")
                                    DialogUtils.alertDialog(
                                        activity = context,
                                        fragment = null,
                                        message = "Please upgrade your subscription"
                                    )
                                }

                                is Fragment -> {
                                    // Log to check if context is correctly recognized as Fragment
                                    Log.e("LoginCheck", "Context is Fragment")
                                    DialogUtils.alertDialog(
                                        activity = null, // Get the Activity from the Fragment
                                        fragment = context,
                                        message = "Please upgrade your subscription"
                                    )
                                }

                                else -> {
                                    // Handle case where context is neither Activity nor Fragment
                                    Log.e("LoginCheck", "Context is neither Activity nor Fragment")
                                    // Optionally, provide user feedback or handle this case
                                }
                            }
                        }
                    }
                } else {
                    when (context) {
                        is Activity -> {
                            val intent = Intent(context, AddDetailsActivity::class.java)
                            context.startActivity(intent)
                        }

                        is Fragment -> {
                            val intent =
                                Intent(context.requireActivity(), AddDetailsActivity::class.java)
                            context.startActivity(intent)
                        }

                        else -> {
                            Log.e("LoginCheck", "Context is neither Activity nor Fragment")
                        }
                    }
                }

            }
        } else {
            AppMethods.guestUserPopup(this.context)
        }
    }
}*/


fun applyDim(parent: ViewGroup, dimAmount: Float) {
    val dim = ColorDrawable(Color.BLACK)
    dim.setBounds(0, 0, parent.width, parent.height)
    dim.alpha = (255 * dimAmount).toInt()

    val overlay = parent.overlay
    overlay.add(dim)
}

fun clearDim(parent: ViewGroup) {
    val overlay = parent.overlay
    overlay.clear()
}

fun scrollToView(scrollViewParent: NestedScrollView, view: View) {
    // Get deepChild Offset
    val childOffset = Point()
    getDeepChildOffset(scrollViewParent, view.parent, view, childOffset)
    // Scroll to child.
    val x = 0
    val y = childOffset.y
    val xTranslate: ObjectAnimator = ObjectAnimator.ofInt(scrollViewParent, "scrollX", x)
    val yTranslate: ObjectAnimator = ObjectAnimator.ofInt(scrollViewParent, "scrollY", y)
    val animators = AnimatorSet()
    animators.duration = 1000L
    animators.playTogether(xTranslate, yTranslate)
    animators.start()
}

fun scrollToViewBottom(scrollViewParent: NestedScrollView, view: View, endAnimation: () -> View?) {
    // Get deepChild Offset
    val childOffset = Point()
    getDeepChildOffset(scrollViewParent, view.parent, view, childOffset)
    // Scroll to child.
    val x = childOffset.y
    val y = 0
    val xTranslate: ObjectAnimator = ObjectAnimator.ofInt(scrollViewParent, "scrollX", x)
    val yTranslate: ObjectAnimator = ObjectAnimator.ofInt(scrollViewParent, "scrollY", y)
    val animators = AnimatorSet()
    animators.duration = 1000L
    animators.playTogether(xTranslate, yTranslate)
    animators.start()
    animators.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator) {
        }

        override fun onAnimationEnd(p0: Animator) {
            endAnimation()?.goneView()
        }

        override fun onAnimationCancel(p0: Animator) {
        }

        override fun onAnimationRepeat(p0: Animator) {
        }

    })
}

fun getDeepChildOffset(
    mainParent: ViewGroup, parent: ViewParent, child: View, accumulatedOffset: Point
) {
    val parentGroup = parent as (ViewGroup)
    accumulatedOffset.x += child.left
    accumulatedOffset.y += child.top
    if (parentGroup == mainParent) {
        return
    }
    getDeepChildOffset(mainParent, parentGroup.parent, parentGroup, accumulatedOffset)
}

fun scrollToTop(mScrollView: ScrollView) {
    val x = 0
    val y = 0
    val xTranslate: ObjectAnimator = ObjectAnimator.ofInt(mScrollView, "scrollX", x)
    val yTranslate: ObjectAnimator = ObjectAnimator.ofInt(mScrollView, "scrollY", y)
    val animators = AnimatorSet()
    animators.duration = 1000L
    animators.playTogether(xTranslate, yTranslate)
    animators.start()
}

/**
 * An extension function which creates/retrieves a [SpringAnimation] and stores it in the [View]s
 * tag.
 *//*fun View.spring(
    property: ViewProperty,
    stiffness: Float = 200f,
    damping: Float = 0.3f,
    startVelocity: Float? = null
): SpringAnimation {
    val key = getKey(property)
    var springAnim = getTag(key) as? SpringAnimation?
    if (springAnim == null) {
        springAnim = SpringAnimation(this, property).apply {
            spring = SpringForce().apply {
                this.dampingRatio = damping
                this.stiffness = stiffness
                startVelocity?.let { setStartVelocity(it) }
            }
        }
        setTag(key, springAnim)
    }
    return springAnim
}*/

/**
 * Map from a [ViewProperty] to an `id` suitable to use as a [View] tag.
 *//*@IdRes
private fun getKey(property: ViewProperty): Int {
    return when (property) {
        SpringAnimation.TRANSLATION_X -> R.id.translation_x
        SpringAnimation.TRANSLATION_Y -> R.id.translation_y
        SpringAnimation.TRANSLATION_Z -> R.id.translation_z
        SpringAnimation.SCALE_X -> R.id.scale_x
        SpringAnimation.SCALE_Y -> R.id.scale_y
        SpringAnimation.ROTATION -> R.id.rotation
        SpringAnimation.ROTATION_X -> R.id.rotation_x
        SpringAnimation.ROTATION_Y -> R.id.rotation_y
        SpringAnimation.X -> R.id.x
        SpringAnimation.Y -> R.id.y
        SpringAnimation.Z -> R.id.z
        SpringAnimation.ALPHA -> R.id.alpha
        SpringAnimation.SCROLL_X -> R.id.scroll_x
        SpringAnimation.SCROLL_Y -> R.id.scroll_y
        else -> throw IllegalAccessException("Unknown ViewProperty: $property")
    }
}*/

/**
 * Retrieve a color from the current [android.content.res.Resources.Theme].
 */
@ColorInt
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}

/**
 * Search this view and any children for a [ColorDrawable] `background` and return it's `color`,
 * else return `colorSurface`.
 */
@ColorInt
fun View.descendantBackgroundColor(): Int {
    val bg = backgroundColor()
    if (bg != null) {
        return bg
    } else if (this is ViewGroup) {
        forEach {
            val childBg = descendantBackgroundColorOrNull()
            if (childBg != null) {
                return childBg
            }
        }
    }
    return context.themeColor(android.R.attr.colorBackground)
}

@ColorInt
private fun View.descendantBackgroundColorOrNull(): Int? {
    val bg = backgroundColor()
    if (bg != null) {
        return bg
    } else if (this is ViewGroup) {
        forEach {
            val childBg = backgroundColor()
            if (childBg != null) {
                return childBg
            }
        }
    }
    return null
}

/**
 * Check if this [View]'s `background` is a [ColorDrawable] and if so, return it's `color`,
 * otherwise `null`.
 */
@ColorInt
fun View.backgroundColor(): Int? {
    val bg = background
    if (bg is ColorDrawable) {
        return bg.color
    }
    return null
}

/**
 * Walk up from a [View] looking for an ancestor with a given `id`.
 */
fun View.findAncestorById(@IdRes ancestorId: Int): View {
    return when {
        id == ancestorId -> this
        parent is View -> (parent as View).findAncestorById(ancestorId)
        else -> throw IllegalArgumentException("$ancestorId not a valid ancestor")
    }
}

/**
 * Potentially animate showing a [BottomNavigationView].
 *
 * Abruptly changing the visibility leads to a re-layout of main content, animating
 * `translationY` leaves a gap where the view was that content does not fill.
 *
 * Instead, take a snapshot of the view, and animate this in, only changing the visibility (and
 * thus layout) when the animation completes.
 */
fun BottomNavigationView.show() {
    if (visibility == View.VISIBLE) return

    val parent = parent as ViewGroup
    // View needs to be laid out to create a snapshot & know position to animate. If view isn't
    // laid out yet, need to do this manually.
    if (!isLaidOut) {
        measure(
            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.AT_MOST)
        )
        layout(parent.left, parent.height - measuredHeight, parent.right, parent.height)
    }

    val drawable = BitmapDrawable(context.resources, drawToBitmap())
    drawable.setBounds(left, parent.height, right, parent.height + height)
    parent.overlay.add(drawable)
    ValueAnimator.ofInt(parent.height, top).apply {
        startDelay = 100L
        duration = 300L
        interpolator = AnimationUtils.loadInterpolator(
            context, android.R.interpolator.linear_out_slow_in
        )
        addUpdateListener {
            val newTop = it.animatedValue as Int
            drawable.setBounds(left, newTop, right, newTop + height)
        }
        doOnEnd {
            parent.overlay.remove(drawable)
            visibility = View.VISIBLE
        }
        start()
    }
}

/**
 * Potentially animate hiding a [BottomNavigationView].
 *
 * Abruptly changing the visibility leads to a re-layout of main content, animating
 * `translationY` leaves a gap where the view was that content does not fill.
 *
 * Instead, take a snapshot, instantly hide the view (so content lays out to fill), then animate
 * out the snapshot.
 */
fun BottomNavigationView.hide() {
    if (visibility == View.GONE) return

    val drawable = BitmapDrawable(context.resources, drawToBitmap())
    val parent = parent as ViewGroup
    drawable.setBounds(left, top, right, bottom)
    parent.overlay.add(drawable)
    visibility = View.GONE
    ValueAnimator.ofInt(top, parent.height).apply {
        startDelay = 100L
        duration = 200L
        interpolator = AnimationUtils.loadInterpolator(
            context, android.R.interpolator.fast_out_linear_in
        )
        addUpdateListener {
            val newTop = it.animatedValue as Int
            drawable.setBounds(left, newTop, right, newTop + height)
        }
        doOnEnd {
            parent.overlay.remove(drawable)
        }
        start()
    }
}

/**
 * A copy of the KTX method, adding the ability to add extra padding the bottom of the [Bitmap];
 * useful when it will be used in a [android.graphics.BitmapShader][BitmapShader] with
 * a [android.graphics.Shader.TileMode.CLAMP][CLAMP tile mode].
 */
fun View.drawToBitmap(@Px extraPaddingBottom: Int = 0): Bitmap {
    if (!ViewCompat.isLaidOut(this)) {
        throw IllegalStateException("View needs to be laid out before calling drawToBitmap()")
    }
    return Bitmap.createBitmap(width, height + extraPaddingBottom, Bitmap.Config.ARGB_8888)
        .applyCanvas {
            translate(-scrollX.toFloat(), -scrollY.toFloat())
            draw(this)
        }
}
