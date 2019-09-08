package ru.skillbranch.devintensive.ui.adapters

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * @author mmikhailov on 2019-09-01.
 */
class ChatItemTouchHelperCallback(
    private val adapter: ChatAdapter,
    val swipeListener: (ChatItem) -> Unit
) : ItemTouchHelper.Callback() {

    private val bgRect = RectF()
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val iconBounds = Rect()

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder is ItemTouchViewHolder) {
            makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.START)
        } else {
            makeFlag(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START)
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        swipeListener(adapter.items[viewHolder.adapterPosition])
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE && viewHolder is ItemTouchViewHolder) {
            viewHolder.onItemSelected()
        }

        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder is ItemTouchViewHolder) {
            viewHolder.onItemCleared()
        }

        super.clearView(recyclerView, viewHolder)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            drawBackbround(c, viewHolder.itemView, dX)
            drawIcon(c, viewHolder.itemView, dX)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawIcon(canvas: Canvas, itemView: View, dX: Float) {
        val icon =
            itemView.resources.getDrawable(R.drawable.ic_archive_black_24dp, itemView.context.theme)
        val iconSize = itemView.resources.getDimensionPixelSize(R.dimen.icon_size)
        val space = itemView.resources.getDimensionPixelSize(R.dimen.spacing_normal_16)
        val margin = itemView.bottom - itemView.top - iconSize / 2

        with(iconBounds) {
            left = itemView.left + dX.toInt() + space
            top = itemView.top + margin
            right = itemView.right + dX.toInt() + space + iconSize
            bottom = itemView.bottom - margin
        }

        icon.bounds = iconBounds
        icon.draw(canvas)
    }

    private fun drawBackbround(canvas: Canvas, itemView: View, dX: Float) {
        with(bgRect) {
            left = itemView.right + dX
            top = itemView.top.toFloat()
            right = itemView.right.toFloat()
            bottom = itemView.bottom.toFloat()
        }

        with(bgPaint) {
            val attrs = intArrayOf(android.R.attr.background)
            val ta =
                itemView.context.obtainStyledAttributes(R.style.ChatItem_SwipeBackground, attrs)
            color = ta.getColor(
                0,
                itemView.resources.getColor(R.color.color_primary, itemView.context.theme)
            )
            alpha = if (itemView.width == 0) 0 else (abs(dX * 255) / itemView.width).roundToInt()

            ta.recycle()
        }

        canvas.drawRect(bgRect, bgPaint)
    }

    interface ItemTouchViewHolder {
        fun onItemSelected()
        fun onItemCleared()
    }
}