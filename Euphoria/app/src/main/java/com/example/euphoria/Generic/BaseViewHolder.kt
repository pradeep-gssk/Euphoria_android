package com.example.euphoria.Generic

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.grid_adapter.view.*

abstract class BaseViewHolder<T>(parent: ViewGroup,
                                 layoutID: Int) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutID, parent, false)
) {
    abstract fun bindData(model: T)
}