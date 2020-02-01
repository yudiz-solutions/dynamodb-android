package com.yudiz.dynamodbdemo.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.roundToInt

class RvDividerDecoration(
    val context: Context, orientation: Int = RecyclerView.VERTICAL, isGrid: Boolean = false, spanCount: Int = 2,
    private val showInLastItem: Boolean = false
) : RecyclerView.ItemDecoration() {

    private var dividerGap: Pair<Int, Int> = Pair(0, 0)
    private var divider: Drawable? = null
    private var mSpace: Int = -1
    private var isGrid: Boolean
    private var spanCount: Int

    private var mOrientation: Int = 0

    init {
//        val a = context.obtainStyledAttributes(ATTRS)
//        divider = a.getDrawable(0)
//        if (divider == null) {
//            Log.w(
//                TAG,
//                "@android:attr/listDivider was not set in the theme used for this " + "RvDividerDecoration. Please set that attribute all call setDrawable()"
//            )
//        }
//        a.recycle()
        setOrientation(orientation)

        this.spanCount = spanCount
        this.isGrid = isGrid

    }

    private fun setOrientation(orientation: Int) {
        if (orientation != HORIZONTAL && orientation != VERTICAL)
            throw IllegalArgumentException(
                "Invalid orientation. It should be either HORIZONTAL or VERTICAL"
            )
        mOrientation = orientation
    }

    fun setDrawable(resDrawable: Int, mainAxisGap: Pair<Int, Int>? = null): RvDividerDecoration {
        mainAxisGap?.let { dividerGap = it }
        divider = ContextCompat.getDrawable(context, resDrawable)
        return this
    }

    fun setSpace(resSpace: Int): RvDividerDecoration {
        mSpace = context.resources.getDimension(resSpace).toInt()
        return this
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.layoutManager == null || divider == null)
            return

        if (divider != null)
            if (mOrientation == VERTICAL)
                drawVertical(c, parent)
            else
                drawHorizontal(c, parent)

    }

    private fun drawVertical(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val left: Int
        val right: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right, parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }

        val childCount: Int = if (showInLastItem)
            parent.childCount
        else
            parent.childCount - 1

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (child.visibility == View.VISIBLE) {

                val decoratedBottom = parent.layoutManager!!.getDecoratedBottom(child)
                val bottom = decoratedBottom + child.translationY.roundToInt() - mSpace / 2
                val top = bottom - divider!!.intrinsicHeight

                divider!!.setBounds(left + dividerGap.first, top, right - dividerGap.second, bottom)
                divider!!.draw(canvas)
            }
        }
        canvas.restore()
    }

    private fun drawHorizontal(canvas: Canvas, parent: RecyclerView) {
        canvas.save()
        val top: Int
        val bottom: Int
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            canvas.clipRect(
                parent.paddingLeft, top,
                parent.width - parent.paddingRight, bottom
            )
        } else {
            top = 0
            bottom = parent.height
        }

        val childCount: Int
        childCount = if (showInLastItem)
            parent.childCount
        else
            parent.childCount - 1

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (child.visibility == View.VISIBLE) {
                val decoratedRight = parent.layoutManager!!.getDecoratedRight(child)
                val right = decoratedRight + child.translationX.roundToInt() - mSpace / 2
                val left = right - divider!!.intrinsicWidth
                divider!!.setBounds(left, top + dividerGap.first, right, bottom - dividerGap.second)
                divider!!.draw(canvas)
            }
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

        if (divider == null && mSpace == -1) {
            outRect.setEmpty()
            return
        }

        val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
        val column = itemPosition % spanCount // item column
        val itemCount = state.itemCount

        if (isGrid) {

//        if (includeEdge) {
//            outRect.left = mSpace - column * mSpace / spanCount // mSpace - column * ((1f / spanCount) * mSpace)
//            outRect.right = (column + 1) * mSpace / spanCount // (column + 1) * ((1f / spanCount) * mSpace)
//
//            if (position < spanCount) { // top edge
//                outRect.top = mSpace
//            }
//            outRect.bottom = mSpace // item bottom
//        } else {
            outRect.left = column * mSpace / spanCount // column * ((1f / spanCount) * mSpace)
            outRect.right =
                mSpace - (column + 1) * mSpace / spanCount // mSpace - (column + 1) * ((1f /    spanCount) * mSpace)
            if (itemPosition >= spanCount) {
                outRect.top = mSpace // item top
            }
//        }
        } else {
            if (showInLastItem) {
                if (mOrientation == VERTICAL)
                    outRect.set(0, 0, 0, if (mSpace == -1) divider!!.intrinsicHeight else mSpace)
                else
                    outRect.set(0, 0, if (mSpace == -1) divider!!.intrinsicWidth else mSpace, 0)

            } else if (itemPosition == itemCount - 1)
                outRect.setEmpty()
            else {
                if (mOrientation == VERTICAL)
                    outRect.set(0, 0, 0, if (mSpace == -1) divider!!.intrinsicHeight else mSpace)
                else
                    outRect.set(0, 0, if (mSpace == -1) divider!!.intrinsicWidth else mSpace, 0)
            }
        }
    }

    companion object {

        val HORIZONTAL = LinearLayout.HORIZONTAL
        val VERTICAL = LinearLayout.VERTICAL
    }
}