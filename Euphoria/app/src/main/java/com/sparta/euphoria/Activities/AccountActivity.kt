package com.sparta.euphoria.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SectionIndexer
import android.widget.TextView
import com.sparta.euphoria.Generic.BaseViewHolder
import com.sparta.euphoria.Model.EUUser
import com.sparta.euphoria.Model.Section
import com.sparta.euphoria.R

class AccountActivity: AppCompatActivity() {

    private var profileButton: ImageButton? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var recyclerView: RecyclerView
    private var accountAdapter: AccountAdapter? = null

    private var values = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        setTitle("SETTINGS")

        val user = EUUser.shared(this)
        values.add(user.firstName)
        values.add(user.lastName)
        values.add(user.phone)
        values.add(user.email)

        profileButton = findViewById(R.id.profileButton)
        recyclerView = findViewById(R.id.recycleView)
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        createAdapter()
    }

    fun didClickSignOutButton(view: View) {

    }

    fun didTapProfileButton(view: View) {

    }

    fun createAdapter() {
        if (accountAdapter == null) {
            accountAdapter = AccountAdapter(values)
            recyclerView.adapter = accountAdapter
        }
    }

    private class AccountAdapter(var values: List<String>): RecyclerView.Adapter<BaseViewHolder<String>>() {

        private val titles = listOf<String>("First name:", "Last Name:", "Phone:", "E-mail:")

        override fun onBindViewHolder(p0: BaseViewHolder<String>, p1: Int) {
            p0.bindData(values[p1])
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BaseViewHolder<String> {
            return AccountHolder(p0, R.layout.account_adapter, titles[p1])
        }

        override fun getItemCount(): Int {
            return values.size
        }

        override fun getItemViewType(position: Int): Int {
            return position
        }
    }

    private class AccountHolder(parent: ViewGroup, layoutID: Int, var title: String):
        BaseViewHolder<String>(parent, layoutID) {

        private var mTitleTextView: TextView? = null
        private var mDetailTextView: TextView? = null

        init {
            mTitleTextView = itemView.findViewById(R.id.titleTextView)
            mDetailTextView = itemView.findViewById(R.id.detailTextView)
        }

        override fun bindData(model: String) {
            mTitleTextView?.text = title
            mDetailTextView?.text = model
        }
   }
}