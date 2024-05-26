package com.techyourchance.coroutines.exercises.exercise8

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.techyourchance.coroutines.R
import com.techyourchance.coroutines.common.BaseFragment
import com.techyourchance.coroutines.common.ThreadInfoLogger.logThreadInfo
import com.techyourchance.coroutines.home.ScreenReachableFromHome
import kotlinx.coroutines.*

class Exercise8Fragment : BaseFragment() {

    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

    override val screenTitle get() = ScreenReachableFromHome.EXERCISE_8.description

    private lateinit var fetchAndCacheUsersUseCase: FetchAndCacheUsersUseCase

    private lateinit var btnFetch: Button
    private lateinit var txtElapsedTime: TextView

    private val userIds = listOf<String>("bmq81", "gfn12", "gla34")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fetchAndCacheUsersUseCase = compositionRoot.fetchAndCacheUserUseCase

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_exercise_8, container, false)

        view.apply {
            txtElapsedTime = findViewById(R.id.txt_elapsed_time)
            btnFetch = findViewById(R.id.btn_fetch_users)
//            this didn't work
//            txtElapsedTime.text = ""
        }

        btnFetch.setOnClickListener {
            logThreadInfo("button callback")

            val updateElapsedTimeJob = coroutineScope.launch {
                try {
                    updateElapsedTime()
                }catch (cancellationException: CancellationException) {
                    Log.d("Exercise8Fragment", "updateElapsedTimeJob - CancellationException")

                }
            }

            coroutineScope.launch {
                try {
                    btnFetch.isEnabled = false
                    fetchAndCacheUsersUseCase.fetchAndCacheUsers(userIds)
                    Log.d("Exercise8Fragment", "updateElapsedTimeJob - Cancelling...")
                    updateElapsedTimeJob.cancel()
                    Log.d("Exercise8Fragment", "updateElapsedTimeJob - Cancelled")

                } catch (e: CancellationException) {
                    Log.d("Exercise8Fragment", "fetchingUsers - CancellationException")
                    withContext(NonCancellable) {
                        updateElapsedTimeJob.cancelAndJoin()
                    }
                    Log.d("Exercise8Fragment", "fetchingUsers - making elapsed time text empty")
                    txtElapsedTime.text = ""
                } finally {

                    Log.d("Exercise8Fragment", "fetchingUsers - finally - enabling button")
                    withContext(NonCancellable){
                        btnFetch.isEnabled = true
                    }
                }
            }
        }

        return view
    }

    override fun onStop() {
        logThreadInfo("onStop()")
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
//        this solution is too easy
//        txtElapsedTime.text = ""
    }


    private suspend fun updateElapsedTime() {
        val startTimeNano = System.nanoTime()
        while (true) {
            delay(100)
            val elapsedTimeNano = System.nanoTime() - startTimeNano
            val elapsedTimeMs = elapsedTimeNano / 1000000
            txtElapsedTime.text = "Elapsed time: $elapsedTimeMs ms"
        }
    }

    companion object {
        fun newInstance(): Fragment {
            return Exercise8Fragment()
        }
    }
}