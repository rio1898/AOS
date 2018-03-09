package com.lwh.rioresearch.Rx

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.ThemedSpinnerAdapter
import com.jakewharton.rxbinding2.view.RxView
import com.lwh.rioresearch.R
import com.lwh.rioresearch.dummy.DummyContent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_subject_detail.*
import kotlinx.android.synthetic.main.frag_rxtest.*
import kotlinx.android.synthetic.main.frag_rxtest.view.*
import kotlinx.android.synthetic.main.progressroundbutton.*
import kotlinx.android.synthetic.main.subject_detail.view.*
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.Thread.sleep
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread
import java.net.URL

/**
 * A fragment representing a single Subject detail screen.
 * This fragment is either contained in a [SubjectListActivity]
 * in two-pane mode (on tablets) or a [SubjectDetailActivity]
 * on handsets.
 */
class RxTestFrag : Fragment() {
    /**
     * The dummy content this fragment is presenting.
     */
    private var mItem: DummyContent.DummyItem? = null
    private val changTv = PublishSubject.create<String>()
    private var disposable:Disposable? = null
    private var compositeDisposable = CompositeDisposable()


    override fun onDestroy() {
        disposable?.dispose()
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                mItem = DummyContent.ITEM_MAP[it.getString(ARG_ITEM_ID)]
                activity?.toolbar_layout?.title = mItem?.content + " : RxTestFrag"
//                activity?.toolbar_layout?.contentScrim = ContextCompat.getDrawable(activity, R.mipmap.img1)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.frag_rxtest, container, false)

        // Show the dummy content as text in a TextView.
        mItem?.let {
//            rootView.tv_rxtest.text = it.details
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val printArticle = { art: String ->
//            println("--- Article ---\n${art.substring(0, 125)}")
            tv_rxtest.text = tv_rxtest.text.run {
                this.toString() + "--- Article ---\n${art.substring(0, 125)}"
            }
        }

        @Suppress("ConvertLambdaToReference")
        val printIt = { it: String -> println(it) }

        changTv.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    tv_rxtest.text = tv_rxtest.text.run {
                        this.toString() + it+"\n"
                    }
                }

        compositeDisposable.add(RxView.clicks(btn_rxtext_test1)
                .throttleFirst(1 , TimeUnit.SECONDS)
                .subscribe {
                    tv_rxtest.text = ""
                    disposable?.dispose()
                    disposable = rx3Interval()
                })

        compositeDisposable.add(RxView.clicks(btn_rxtext_test2)
                .throttleFirst(1 , TimeUnit.SECONDS)
                .subscribe {
//                    asyncWikiWithErrorHandling("Tiger", "Elephant").subscribe(printArticle) { e ->
//                        println("--- Error ---\n${e.message}")
//                    }
                    simpleObservable().subscribeBy(
                            onError = {
                                println("error::${it.localizedMessage}")
                            }
                            ,onNext = {s: String ->
                                changTv.onNext(s)
                            }
                            andThen {
                                changTv.onNext(it)
                            }
                            ,onComplete = {
                                println("onComplete")
                            }
                    )
                })

        compositeDisposable.add(RxView.clicks(btn_rxtext_test3)
                .throttleFirst(1 , TimeUnit.SECONDS)
                .subscribe {
                    tv_rxtest.text = ""
                    syncObservable().subscribe {
                        changTv.onNext(it)
                    }
                })

        compositeDisposable.add(RxView.clicks(btn_rxtext_test4)
                .throttleFirst(1 , TimeUnit.SECONDS)
                .subscribe {
                    tv_rxtest.text = ""
                    simpleComposition()
                })

        combineLatest(listOfObservables())

        zip(listOfObservables())

        addToCompositeSubscription()
    }

    data class SearchResultEntry(val id : String, val latestVersion : String)
    data class SearchResults(val docs : List<SearchResultEntry>)
    data class MavenSearchResponse(val response : SearchResults)

    interface MavenSearchService {
        @GET("/solrsearch/select?wt=json")
        fun search(@Query("q") s : String, @Query("rows") rows : Int = 20) : Observable<MavenSearchResponse>
    }

    fun listOfObservables(): List<Observable<String>> = listOf(syncObservable(), syncObservable())

    infix inline fun <T : Any> ((T) -> Unit).andThen(crossinline block: (T) -> Unit): (T) -> Unit = { this(it); block(it) }

    fun simpleObservable(): Observable<String> {
        return (0..17).toObservable().map { "Simple $it" }
    }

//    fun simpleObservable(): Observable<String> = (0..17).toObservable().map { "Simple $it" }

    fun combineLatest(observables: List<Observable<String>>) {
        observables.combineLatest { it.reduce { one, two -> one + two } }.subscribe(::println)
    }

    fun zip(observables: List<Observable<String>>) {
        observables.zip { it.reduce { one, two -> one + two } }.subscribe(::println)
    }

    fun addToCompositeSubscription() {
        val compositeSubscription = CompositeDisposable()

        Observable.just("test")
                .delay(100, TimeUnit.MILLISECONDS)
                .subscribe()
                .addTo(compositeSubscription)

        compositeSubscription.dispose()
    }

    fun simpleComposition() {
        asyncObservable()
                .skip(10)
                .take(5)
                .map { "${it}_xform" }
                .subscribe { changTv.onNext(it) }
    }

    fun syncObservable(): Observable<String> = Observable.create { subscriber ->
        (0..75).toObservable()
                .map { "Sync value_$it" }
                .subscribe { subscriber.onNext(it) }
    }

    fun asyncObservable(): Observable<String> = Observable.create { subscriber ->
        thread {
            (0..75).toObservable()
                    .map { "Async value_$it" }
                    .subscribe { subscriber.onNext(it) }
        }
    }

    private fun asyncWiki(vararg articleNames: String): Observable<String> = Observable.create { subscriber ->
        thread {
            println("LWH get wiki")
            articleNames.toObservable()
                    .flatMapMaybe { name -> URL("http://en.wikipedia.org/wiki/$name").toScannerObservable().firstElement() }
                    .subscribe { subscriber.onNext(it) }
        }
    }

//    http://en.wikipedia.org/wiki/Tiger
//    https://namu.wiki/search/
    fun asyncWikiWithErrorHandling(vararg articleNames: String): Observable<String> = Observable.create { subscriber ->
        thread {
            articleNames.toObservable()
                    .flatMapMaybe { name -> URL("https://www.google.co.kr/search?q=$name").toScannerObservable().firstElement() }
                    .subscribe({ subscriber.onNext(it) }, { subscriber.onError(it) })
        }
    }

    private fun URL.toScannerObservable() = Observable.create<String> { s ->
        this.openStream().use { stream ->
            Scanner(stream).useDelimiter("\\A")
                    .toObservable()
                    .subscribe { s.onNext(it) }
        }
    }

    private fun rx4Defer() {
        var count = 5
        val source = Observable.defer { Observable.range(1, count) }.map { it }
        source.subscribe { changTv.onNext("${"Observer 1: " + it}\n")}

        count = 10
        source.subscribe { changTv.onNext("${"Observer 2: " + it}\n")}
    }

    private fun rx3Interval():Disposable{
        val secondObs = Observable.interval(1, TimeUnit.SECONDS)
        return secondObs.subscribe { t ->
            changTv.onNext("$t ")
        }
    }

    private fun rx2Just(){
        val strObs = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        strObs.publish()

        var str = ""
        strObs.map ( String::length )
                .filter { it >= 5 }
                .subscribe { t ->
                    println(t)
                    str += "${t}\n"
                    changTv.onNext(str)
                }

        strObs.subscribe { t ->
            println(t)
            str += "${t}\n"
            changTv.onNext(str)
        }

        strObs.retry()
    }

    private fun rx1_just(){
        val strObs = Observable.just("Alpha", "Beta", "Gamma", "Delta", "Epsilon")
        var str = ""
        strObs.subscribe { t ->
            println(t)
            str += t + "\n"
            changTv.onNext(str)
        }

    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
