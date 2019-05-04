package com.sparta.euphoria.Activities.Diet

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import com.sparta.euphoria.DataBase.Diet
import com.sparta.euphoria.Enums.DietType
import com.sparta.euphoria.R

class DietDetailsActivity: AppCompatActivity() {

    private var mTitleTextView: TextView? = null
    private var mNameTextView: TextView? = null
    private var mOrganTextView: TextView? = null
    private var mChannelsTextView: TextView? = null
    private var mEffectTextView: TextView? = null
    private var mFlavourTextView: TextView? = null
    private var mNatureTextView: TextView? = null
    private var mElementTextView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dietdetails)

        mTitleTextView = findViewById(R.id.titleView)
        mNameTextView = findViewById(R.id.nameTextView)
        mOrganTextView = findViewById(R.id.organTextView)
        mChannelsTextView = findViewById(R.id.channelsTextView)
        mEffectTextView = findViewById(R.id.effectTextView)
        mFlavourTextView = findViewById(R.id.flavourTextView)
        mNatureTextView = findViewById(R.id.natureTextView)
        mElementTextView = findViewById(R.id.elementTextView)

        val bundle = intent.extras.getBundle("bundle")
        if (bundle != null) {
            val diet = bundle.get("diet") as Diet
            val dietString = diet.diet

            if (dietString is String) {
                val dietType = DietType.getDietType(dietString)
                mTitleTextView?.text = dietType?.title
            }

            mNameTextView?.text = diet.name
            mOrganTextView?.text = diet.organ
            mChannelsTextView?.text = diet.channels
            mEffectTextView?.text = diet.effect
            mFlavourTextView?.text = diet.flavour
            mNatureTextView?.text = diet.nature
            mElementTextView?.text = diet.element
        }
    }
}