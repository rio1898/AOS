package com.lwh.rioresearch.UICategory

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.lwh.rioresearch.R
import com.lwh.rioresearch.dummy.DummyContent
import com.xiaochen.progressroundbutton.AnimDownloadProgressButton
import kotlinx.android.synthetic.main.activity_subject_detail.*
import kotlinx.android.synthetic.main.progressroundbutton.*
import java.util.concurrent.TimeUnit

/**
 * A fragment representing a single Subject detail screen.
 * This fragment is either contained in a [SubjectListActivity]
 * in two-pane mode (on tablets) or a [SubjectDetailActivity]
 * on handsets.
 */
class ProgressRoundButton : Fragment() {

    private val TAG = ProgressRoundButton::class.java.getSimpleName()
    /**
     * The dummy content this fragment is presenting.
     */
    private var mItem: DummyContent.DummyItem? = null
    private var timer: CountDownTimer? = null
    private var init_timer_value: Long = 4096
    private var totalDuration :Long = 0
    private var currentDuration : Long = 0
    private var state = 0
    private val _RECORD_READY = 0
    private val _RECORD_ING = 1
    private val _RECORD_PAUSE = 2
    private val _RECORD_FINISH = 3

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
        val rootView = inflater.inflate(R.layout.progressroundbutton, container, false)

        // Show the dummy content as text in a TextView.
//        mItem?.let {
//            rootView.subject_detail.text = it.details
//        }

        return rootView
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
        fun newInstance(id : String): ProgressRoundButton {
            val args = Bundle()
            args.putString(ARG_ITEM_ID, id)
            val fragment = ProgressRoundButton()
            fragment.arguments = args

            return fragment
        }
    }

//    val anim_btn = R.id.anim_btn as AnimDownloadProgressButton
//    val anim_btn2 = R.id.anim_btn2 as AnimDownloadProgressButton
//    val anim_btn3 = R.id.anim_btn3 as AnimButtonLayout

    private fun initView(){
        anim_btn.setCurrentText("安装")
//        anim_btn.setTextSize(60f);
        anim_btn.setOnClickListener(View.OnClickListener { showTheButton(R.id.anim_btn) })

        anim_btn.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            v.tag
            if (hasFocus) {
                anim_btn.setBackgroundColor(Color.parseColor("#0000ff"))
//                anim_btn.setProgressBtnBackgroundColor(Color.parseColor("#0000ff"))
            } else {
                anim_btn.setBackgroundColor(Color.parseColor("#00ff00"))
//                anim_btn.setProgressBtnBackgroundColor(Color.parseColor("#00ff00"))
            }
        })

        anim_btn2.setCurrentText("安装")
        anim_btn2.setTextSize(60f)
        anim_btn2.setOnClickListener(View.OnClickListener { showTheButton(R.id.anim_btn2) })


        anim_btn3.setCurrentText("安装")
        anim_btn3.setTextSize(60f)
        anim_btn3.setOnClickListener(View.OnClickListener { showTheButton(R.id.anim_btn3) })

        reset.setOnClickListener(View.OnClickListener {
            anim_btn.setState(AnimDownloadProgressButton.NORMAL)
            anim_btn.setCurrentText("安装")
            anim_btn.setProgress(0f)
        })

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                anim_btn.setButtonRadius(progress / 100.0f * anim_btn.getHeight() / 2)
                anim_btn.postInvalidate()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })

        description.setText(" This is a DownloadProgressButton library with Animation," +
                "you can change radius,textColor,coveredTextColor,BackgroudColor,etc in" +
                " your code or just in xml.\n\n" +
                "The library is open source in github https://github.com/cctanfujun/ProgressRoundButton .\n" +
                "Hope you like it ")

    }

    private fun setTimer(){
        timer = object : CountDownTimer(init_timer_value, 25) {
            override fun onFinish() {

            }

            override fun onTick(millisUntilFinished: Long) {
                if(state == _RECORD_PAUSE){
                    totalDuration = currentDuration
                    currentDuration = 0
                    cancel()
                }else {
//                    currentDuration = totalDuration + init_timer_value - millisUntilFinished
                    currentDuration = init_timer_value - millisUntilFinished
                    Log.e("TAG", "currentDuration::"+currentDuration.toFloat()/init_timer_value.toFloat() * 100 *2.5)
//                    val min = TimeUnit.MILLISECONDS.toMinutes(currentDuration)
//                    val sec = TimeUnit.MILLISECONDS.toSeconds(currentDuration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentDuration))

                    common_progress.setProgressWithAnima(currentDuration.toFloat()/init_timer_value.toFloat() * 400)

                    anim_btn.setProgressText("", currentDuration.toFloat()/100)
                    if (anim_btn.progress + 10 > init_timer_value/100) {
                        anim_btn.setProgressText("", 0F)
//                        anim_btn.setState(AnimDownloadProgressButton.INSTALLING)
//                        anim_btn.setCurrentText("")
                        currentDuration = 0
                        totalDuration = 0

                        common_progress.setProgressWithAnima(0F)

                        timer?.cancel()
//                        Handler().postDelayed({
//                            anim_btn.setState(AnimDownloadProgressButton.NORMAL)
//                            anim_btn.setCurrentText("재생완료")
//                        }, 2000)   //2秒
                    }
                }
            }
        }
        timer?.start()
    }

    private fun showTheButton(id: Int) {
        when (id) {
            R.id.anim_btn -> {
                anim_btn.setState(AnimDownloadProgressButton.DOWNLOADING)
//                anim_btn.setProgressText("재생중", 0f)
                setTimer()
//                anim_btn.setProgressText("下载中", anim_btn.getProgress() + 8)
                Log.d(TAG, "showTheButton: " + anim_btn.getProgress())
//                if (anim_btn.getProgress() + 10 > 100) {
//                    anim_btn.setState(AnimDownloadProgressButton.INSTALLING)
//                    anim_btn.setCurrentText("安装中")
//                    Handler().postDelayed({
//                        anim_btn.setState(AnimDownloadProgressButton.NORMAL)
//                        anim_btn.setCurrentText("打开")
//                    }, 2000)   //2秒
//                }
            }
            R.id.anim_btn2 -> {
                anim_btn2.setState(AnimDownloadProgressButton.DOWNLOADING)
                anim_btn2.setProgressText("下载中", anim_btn2.getProgress() + 8)
                Log.d(TAG, "showTheButton: " + anim_btn2.getProgress())
                if (anim_btn2.getProgress() + 10 > 100) {
                    anim_btn2.setState(AnimDownloadProgressButton.INSTALLING)
                    anim_btn2.setCurrentText("安装中")
                    Handler().postDelayed({
                        anim_btn2.setState(AnimDownloadProgressButton.NORMAL)
                        anim_btn2.setCurrentText("打开")
                    }, 2000)   //2秒
                }
            }
            R.id.anim_btn3 -> {
                anim_btn3.setState(AnimDownloadProgressButton.DOWNLOADING)
                anim_btn3.setProgressText("下载中", anim_btn3.getProgress() + 8)
                Log.d(TAG, "showTheButton: " + anim_btn3.getProgress())
                if (anim_btn3.getProgress() + 10 > 100) {
                    anim_btn3.setState(AnimDownloadProgressButton.INSTALLING)
                    anim_btn3.setCurrentText("安装中")
                    Handler().postDelayed({
                        anim_btn3.setState(AnimDownloadProgressButton.NORMAL)
                        anim_btn3.setCurrentText("打开")
                    }, 2000)   //2秒
                }
            }
        }


    }
}
