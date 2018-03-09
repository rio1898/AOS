package com.lwh.rioresearch

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lwh.rioresearch.Rx.RxTestFrag
import com.lwh.rioresearch.UICategory.ProgressRoundButton

import com.lwh.rioresearch.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_subject_list.*
import kotlinx.android.synthetic.main.subject_list_content.view.*

import kotlinx.android.synthetic.main.subject_list.*

/**
 * An activity representing a list of Pings. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a [SubjectDetailActivity] representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
class SubjectListActivity : AppCompatActivity() {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private var mTwoPane: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_list)

        setSupportActionBar(toolbar)
        toolbar.title = title

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        if (subject_detail_container != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true
        }

        setupRecyclerView(subject_list)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane)
    }

    class SimpleItemRecyclerViewAdapter(private val mParentActivity: SubjectListActivity,
                                        private val mValues: List<DummyContent.DummyItem>,
                                        private val mTwoPane: Boolean) :
            RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder>() {

        private val mOnClickListener: View.OnClickListener

        init {
            mOnClickListener = View.OnClickListener { v ->
                val item = v.tag as DummyContent.DummyItem
                var fragment: Fragment? = null
                when (item.id.toInt()){
                    1 -> {
                        fragment = RxTestFrag().apply {
                            arguments = Bundle().apply {
                                putString(RxTestFrag.ARG_ITEM_ID, item.id)
                            }
                        }
                    }
                    2 -> {
                        fragment = ProgressRoundButton().apply {
                            arguments = Bundle().apply {
                                putString(ProgressRoundButton.ARG_ITEM_ID, item.id)
                            }
                        }
                    }
                    3 -> {
                        fragment = SubjectDetailFragment().apply {
                            arguments = Bundle().apply {
                                putString(SubjectDetailFragment.ARG_ITEM_ID, item.id)
                            }
                        }
                    }
                    4 -> {

                    }
                    5 -> {

                    }
                }
                if (mTwoPane) {
                    mParentActivity.supportFragmentManager
                            .beginTransaction()
                            .replace(R.id.subject_detail_container, fragment)
                            .commit()
                } else {
                    val intent = Intent(v.context, SubjectDetailActivity::class.java).apply {
                        when (item.id.toInt()){
                            1 -> {
                                putExtra(RxTestFrag.ARG_ITEM_ID, item.id)
                            }
                            2 -> {
                                putExtra(ProgressRoundButton.ARG_ITEM_ID, item.id)
                            }
                            3 -> {
                                putExtra(SubjectDetailFragment.ARG_ITEM_ID, item.id)
                            }
                            4 -> {
                                putExtra(SubjectDetailFragment.ARG_ITEM_ID, item.id)
                            }
                            5 -> {
                                putExtra(SubjectDetailFragment.ARG_ITEM_ID, item.id)
                            }
                        }
                    }
                    v.context.startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.subject_list_content, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = mValues[position]
            holder.mIdView.text = item.id
            holder.mContentView.text = item.content

            with(holder.itemView) {
                tag = item
                setOnClickListener(mOnClickListener)
            }
        }

        override fun getItemCount(): Int {
            return mValues.size
        }

        inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
            val mIdView: TextView = mView.id_text
            val mContentView: TextView = mView.content
        }
    }
}
