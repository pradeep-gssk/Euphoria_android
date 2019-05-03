package com.sparta.euphoria.Generic

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

abstract class BaseViewHolder<T>(parent: ViewGroup,
                                 layoutID: Int) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(layoutID, parent, false)
) {
    abstract fun bindData(model: T)
}