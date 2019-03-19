package com.example.euphoria.Models

class Section {
    private var mitem: Any?
    private var msection: Int
    private var mrow: Int
    private var mtitle: String?
    private var mIsHeader: Boolean

    constructor(item: Any?, section: Int, row: Int, title: String? = null, isHeader: Boolean = false) {
        mitem = item
        msection = section
        mrow = row
        mtitle = title
        mIsHeader = isHeader
    }

    constructor(section: Int, row: Int, title: String? = null, isHeader: Boolean = false) {
        mitem = null
        msection = section
        mrow = row
        mtitle = title
        mIsHeader = isHeader
    }

    constructor(item: Any?, section: Int, row: Int, isHeader: Boolean = false) {
        mitem = item
        msection = section
        mrow = row
        mtitle = null
        mIsHeader = isHeader
    }


    val item: Any?
        get() {
            return mitem
        }

    val section: Int
        get() {
            return msection
        }

    val row: Int
        get() {
            return mrow
        }

    val title: String?
        get() {
            return mtitle
        }

    val isHeader: Boolean
        get() {
            return mIsHeader
        }
}