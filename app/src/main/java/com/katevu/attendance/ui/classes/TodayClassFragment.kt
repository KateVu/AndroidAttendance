package com.katevu.attendance.ui.classes

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katevu.attendance.CheckinActivityViewModel
import com.katevu.attendance.R
import com.katevu.attendance.data.model.StudentActivity

/**
 * A fragment representing a list of Items.
 */
class TodayClassFragment : Fragment() {

    private var columnCount = 1
    private val todayClassViewModel: CheckinActivityViewModel by activityViewModels()
    private lateinit var todayClassRecyclerView: RecyclerView
    private var adapter: TodayClassRecyclerViewAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_class_list, container, false)
        todayClassRecyclerView =
                view.findViewById(R.id.list) as RecyclerView
        todayClassRecyclerView.layoutManager = when {
            columnCount <= 1 -> LinearLayoutManager(context)
            else -> GridLayoutManager(context, columnCount)
        }

        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true

        if (isConnected) {
            todayClassViewModel.getActivities();
        } else {
            showToast("No internet access")
        }

        // Set the adapter
//        if (view is RecyclerView) {
//            with(view) {
//                layoutManager = when {
//                    columnCount <= 1 -> LinearLayoutManager(context)
//                    else -> GridLayoutManager(context, columnCount)
//                }
////                adapter = TodayClassRecyclerViewAdapter(DummyContent.ITEMS)
//            }
//        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        todayClassViewModel.getActivitiesResult.observe(
                viewLifecycleOwner,
                {getActivityResult ->
                    getActivityResult.error?.let {
                        showLoginFailed(it.toString())
                    }
                    getActivityResult.success?.let {
                        if (it.data.isEmpty()) {
                            showToast("You do not have any class")
                        } else {
                            updateUI(it.data)
                        }
                    }
                }
        )

//        bookListViewModel.allBooks()
//        bookListViewModel.listBooks.observe(
//                viewLifecycleOwner,
//                { listBooks -> updateUI(listBooks) })
    }

    private fun showLoginFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }


    private fun updateUI(studentActivities: List<StudentActivity>) {

//        list_view.setAdapter(adapter);
//        if (adapter.getCount() > 0) {
//
//        } else {
//            Toast.makeText(getApplicationContext(), "NO Data Available..", Toast.LENGTH_SHORT).show();
//        }

        adapter = TodayClassRecyclerViewAdapter(studentActivities)
        todayClassRecyclerView.adapter = adapter
//        Log.d(TAG, ".updateUI called")
//        adapter = BookListAdapter(books)
//        bookRecyclerView.adapter = adapter
    }

    fun showToast(message: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TodayClassFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}