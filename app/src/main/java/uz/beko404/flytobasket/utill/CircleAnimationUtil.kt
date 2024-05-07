package uz.beko404.flytobasket.utill

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import java.lang.ref.WeakReference
import kotlin.math.max
import kotlin.math.pow

class CircleAnimationUtil {
    private val DEFAULT_DURATION = 700
    private val DEFAULT_DURATION_DISAPPEAR = 100
    private var mTarget: View? = null
    private var mDest: View? = null

    private var originX = 0f
    private var originY = 0f
    private var destX = 0f
    private var destY = 0f

    private var mCircleDuration = 100
    private var mMoveDuration = DEFAULT_DURATION
    private val mDisappearDuration = DEFAULT_DURATION_DISAPPEAR

    private var mContextReference: WeakReference<Activity?>? = null
    private var mBorderWidth = 4
    private var mBorderColor = Color.BLACK

    //    private CircleLayout mCircleLayout;
    private var mBitmap: Bitmap? = null
    private var mImageView: CircleImageView? = null
    private var mAnimationListener: Animator.AnimatorListener? = null

    fun CircleAnimationUtil() {}

    fun attachActivity(activity: Activity?): CircleAnimationUtil {
        mContextReference = WeakReference(activity)
        return this
    }

    fun setTargetView(view: View?): CircleAnimationUtil {
        mTarget = view
        setOriginRect(mTarget!!.width.toFloat(), mTarget!!.height.toFloat())
        return this
    }

    private fun setOriginRect(x: Float, y: Float): CircleAnimationUtil {
        originX = x
        originY = y
        return this
    }

    private fun setDestRect(x: Float, y: Float): CircleAnimationUtil {
        destX = x
        destY = y
        return this
    }

    fun setDestView(view: View?): CircleAnimationUtil {
        mDest = view
        setDestRect(mDest!!.width.toFloat(), mDest!!.width.toFloat())
        return this
    }

    fun setBorderWidth(width: Int): CircleAnimationUtil {
        mBorderWidth = width
        return this
    }

    fun setBorderColor(color: Int): CircleAnimationUtil {
        mBorderColor = color
        return this
    }

    fun setCircleDuration(duration: Int): CircleAnimationUtil {
        mCircleDuration = duration
        return this
    }

    fun setMoveDuration(duration: Int): CircleAnimationUtil {
        mMoveDuration = duration
        return this
    }

    private fun prepare(): Boolean {
        if (mContextReference!!.get() != null) {
            val decoreView = mContextReference!!.get()!!.window.decorView as ViewGroup
            mBitmap = drawViewToBitmap(mTarget, mTarget!!.width, mTarget!!.height)
            if (mImageView == null) mImageView = CircleImageView(mContextReference!!.get())
            mImageView!!.setImageBitmap(mBitmap!!)
//            mImageView!!.borderWidth = mBorderWidth
//            mImageView!!.borderColor = mBorderColor
            val src = IntArray(2)
            mTarget!!.getLocationOnScreen(src)
            val params = FrameLayout.LayoutParams(mTarget!!.width, mTarget!!.height)
            params.setMargins(src[0], src[1], 0, 0)
            if (mImageView!!.parent == null) decoreView.addView(mImageView, params)
        }
        return true
    }

    fun startAnimation() {
        if (prepare()) {
            mTarget!!.visibility = View.INVISIBLE
            getAvatarRevealAnimator().start()
        }
    }

    private fun getAvatarRevealAnimator(): AnimatorSet {
        val endRadius = (max(destX.toDouble(), destY.toDouble()) / 2).toFloat()
        val startRadius = max(originX.toDouble(), originY.toDouble()).toFloat()
        val mRevealAnimator: Animator = ObjectAnimator.ofFloat(
            mImageView,
            "drawableRadius",
            startRadius,
//            endRadius * 1.05f,
//            endRadius * 0.9f,
            startRadius
        )
        mRevealAnimator.interpolator = AccelerateInterpolator()

//        float scaleFactor = Math.max(2f * destY / originY, 2f * destX / originX);
        val scaleFactor = 0.3f
        val scaleAnimatorY: Animator =
            ObjectAnimator.ofFloat(mImageView, View.SCALE_Y, 0.3f, 0.3f, scaleFactor, scaleFactor)
        val scaleAnimatorX: Animator =
            ObjectAnimator.ofFloat(mImageView, View.SCALE_X, 0.3f, 0.3f, scaleFactor, scaleFactor)
        val animatorCircleSet = AnimatorSet()
        animatorCircleSet.setDuration(mCircleDuration.toLong())
        animatorCircleSet.playTogether(scaleAnimatorX, scaleAnimatorY, mRevealAnimator)
        animatorCircleSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                if (mAnimationListener != null) mAnimationListener!!.onAnimationStart(animation)
            }

            override fun onAnimationEnd(animation: Animator) {
                val src = IntArray(2)
                val dest = IntArray(2)
                mImageView!!.getLocationOnScreen(src)
                mDest!!.getLocationOnScreen(dest)
                val y: Float = mImageView!!.getY()
                val x: Float = mImageView!!.getX()
                val translatorX: Animator = ObjectAnimator.ofFloat<View>(
                    mImageView,
                    View.X,
                    x,
                    x + dest[0] - (src[0] + (originX * scaleFactor - 2 * endRadius * scaleFactor) / 2) + (0.5f * destX - scaleFactor * endRadius)
                )
                translatorX.interpolator =
                    TimeInterpolator { input -> //                        return (float) (Math.sin((0.5f * input) * Math.PI));
                        //-(1-x)^2+1
                        (-(input - 1).pow(2) + 1f).toFloat()
                    }
                val translatorY: Animator = ObjectAnimator.ofFloat<View>(
                    mImageView,
                    View.Y,
                    y,
                    y + dest[1] - (src[1] + (originY * scaleFactor - 2 * endRadius * scaleFactor) / 2) + (0.5f * destY - scaleFactor * endRadius)
                )
                translatorY.interpolator = LinearInterpolator()
                val animatorMoveSet = AnimatorSet()
                animatorMoveSet.playTogether(translatorX, translatorY)
                animatorMoveSet.setDuration(mMoveDuration.toLong())
                val animatorDisappearSet = AnimatorSet()
                val disappearAnimatorY: Animator =
                    ObjectAnimator.ofFloat<View>(mImageView, View.SCALE_Y, scaleFactor, 0f)
                val disappearAnimatorX: Animator =
                    ObjectAnimator.ofFloat<View>(mImageView, View.SCALE_X, scaleFactor, 0f)
                animatorDisappearSet.setDuration(mDisappearDuration.toLong())
                animatorDisappearSet.playTogether(disappearAnimatorX, disappearAnimatorY)
                val total = AnimatorSet()
                total.playSequentially(animatorMoveSet, animatorDisappearSet)
                total.addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationEnd(animation: Animator) {
                        if (mAnimationListener != null) mAnimationListener!!.onAnimationEnd(
                            animation
                        )
                        reset()
                    }

                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {}
                })
                total.start()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        return animatorCircleSet
    }

    private fun drawViewToBitmap(view: View?, width: Int, height: Int): Bitmap {
        val drawable: Drawable = BitmapDrawable()
        //        view.layout(0, 0, width, height);
        val dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val c = Canvas(dest)
        drawable.bounds = Rect(0, 0, width, height)
        drawable.draw(c)
        view!!.draw(c)
        //        view.layout(0, 0, width, height);
        return dest
    }

    private fun reset() {
        mBitmap!!.recycle()
        mBitmap = null
        if (mImageView!!.getParent() != null) (mImageView!!.getParent() as ViewGroup).removeView(
            mImageView
        )
        mImageView = null
//        mTarget.setVisibility(View.VISIBLE);
    }

    fun setAnimationListener(listener: Animator.AnimatorListener?): CircleAnimationUtil {
        mAnimationListener = listener
        return this
    }
}