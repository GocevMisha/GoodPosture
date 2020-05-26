package ru.gocev.goodposture.ui.histories.history

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import ru.gocev.goodposture.R
import ru.gocev.goodposture.models.HistoryAdapter
import ru.gocev.goodposture.models.HistoryItem


class HistoryFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var historyRecycler: RecyclerView
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var api_ulr: String
    var data = MutableLiveData<List<HistoryItem>>()
    lateinit  var da : List<HistoryItem>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val pref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        api_ulr = pref.getString("api_url", "") as String
        historyViewModel =
            ViewModelProviders.of(this).get(HistoryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_history, container, false)
        historyRecycler = root.findViewById(R.id.rv_history)
        val progressBar: ProgressBar = root.findViewById(R.id.progress)
        historyRecycler.layoutManager = LinearLayoutManager(context)
        historyViewModel.getPhotoList(api_ulr).observe(viewLifecycleOwner,  Observer<List<HistoryItem>>{ fruitList ->
            // update UI
            data.value = fruitList.asReversed()
            Log.d("MainActivity ","Data Send"+data.value!!.size.toString())
            historyRecycler.adapter = HistoryAdapter(context,data)
            progressBar.visibility=View.GONE
        })
        mSwipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout) as SwipeRefreshLayout
        mSwipeRefreshLayout.setOnRefreshListener(this)



        return root
    }

    override fun onRefresh() {
        val pref = requireActivity().getPreferences(Context.MODE_PRIVATE)
        val baseUrl = pref.getString("api_url", "") as String + "/result"
        historyViewModel.load(api_ulr)
        mSwipeRefreshLayout.isRefreshing = false
    }
}