package com.lwh.rioresearch

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.lwh.rioresearch.dummy.DummyContent
import kotlinx.android.synthetic.main.activity_subject_detail.*
import kotlinx.android.synthetic.main.progressroundbutton.*
import kotlinx.android.synthetic.main.subject_detail.view.*

/**
 * A fragment representing a single Subject detail screen.
 * This fragment is either contained in a [SubjectListActivity]
 * in two-pane mode (on tablets) or a [SubjectDetailActivity]
 * on handsets.
 */
class SubjectDetailFragment : Fragment() {
    /**
     * The dummy content this fragment is presenting.
     */
    private var mItem: DummyContent.DummyItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                mItem = DummyContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
                activity?.toolbar_layout?.title = mItem?.content
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.subject_detail, container, false)

        // Show the dummy content as text in a TextView.
        mItem?.let {
            rootView.subject_detail.text = it.details
        }
        
        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
