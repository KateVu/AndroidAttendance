package com.katevu.attendance.ui.classes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.katevu.attendance.R
import com.katevu.attendance.data.model.MyClass

/**
 * A fragment representing a list of Items.
 */
class TodayClassFragment : Fragment() {

    private var columnCount = 1
    private val todayClassViewModel: TodayClassViewModel by viewModels()
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

        todayClassViewModel.getClasses()
        todayClassViewModel.todayclasses.observe(
                viewLifecycleOwner,
                {listclasses -> updateUI(listclasses)}
        )

//        bookListViewModel.allBooks()
//        bookListViewModel.listBooks.observe(
//                viewLifecycleOwner,
//                { listBooks -> updateUI(listBooks) })
    }


    private fun updateUI(classes: List<MyClass>) {

        adapter = TodayClassRecyclerViewAdapter(classes)
        todayClassRecyclerView.adapter = adapter
//        Log.d(TAG, ".updateUI called")
//        adapter = BookListAdapter(books)
//        bookRecyclerView.adapter = adapter
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