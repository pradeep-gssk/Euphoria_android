package com.example.euphoria.Generic

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.text.TextPaint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.SectionIndexer

class EUSectionIndex: View {

    private var sortedKeys: Array<Any> = emptyArray()
    private val padding: Int
        get() {
            return height - 10
        }

    private var selectionIndexer: SectionIndexer? = null
    private var recyclerView: RecyclerView? = null
    private var paint: TextPaint

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
    }

    init {
        setBackgroundColor(0x44FFFFFF)
        paint = TextPaint()
        paint.color = Color.BLACK
        paint.textSize = 30.0F
        paint.textAlign = Paint.Align.CENTER
        paint.typeface = Typeface.DEFAULT_BOLD
    }

    fun setRecyclerView(_recyclerView: RecyclerView) {
        recyclerView = _recyclerView
        val _selectionIndexer = _recyclerView.adapter as SectionIndexer
        sortedKeys = _selectionIndexer.sections
        selectionIndexer = selectionIndexer
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        if (event is MotionEvent) {
            val y = event.y
            val selectedIndex = (y/padding) * sortedKeys.size

            if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                if (selectionIndexer == null) {
                    selectionIndexer = recyclerView?.adapter as SectionIndexer
                }

                val position = selectionIndexer?.getPositionForSection(selectedIndex.toInt())

                if (position is Int && position >= 0) {
                    recyclerView?.smoothScrollToPosition(position)
                }
            }
        }

        return true
    }

    override fun draw(canvas: Canvas?) {
        var canvasHeight = padding/sortedKeys.size.toFloat()
        var charHeight = (padding - canvasHeight)/sortedKeys.size.toFloat()
        val widthCenter = measuredWidth.toFloat()/2.toFloat()
        for (i in 0..(sortedKeys.size - 1)) {
            canvas?.drawText((sortedKeys[i] as String), widthCenter, charHeight + (i * charHeight), paint)
        }

        super.draw(canvas)
    }
}