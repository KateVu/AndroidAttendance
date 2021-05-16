package com.katevu.attendance.ui.classes

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.katevu.attendance.R
import com.katevu.attendance.data.model.StudentActivity
import com.katevu.attendance.dummy.DummyContent.DummyItem
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * [RecyclerView.Adapter] that can display a [DummyItem].
 */
class TodayClassRecyclerViewAdapter(
    private val values: List<StudentActivity>
) : RecyclerView.Adapter<TodayClassRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_class, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

//        val date: LocalDate = LocalDate.now()
//        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy MM dd")
//        val text: String = date.format(formatter)
//        val parsedDate: LocalDate = LocalDate.parse(text, formatter)

//        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy MM dd")
//
//        val date = LocalDate.now()
//        val text = date.format(formatter)
//        val parsedDate = LocalDate.parse(text, formatter)

        val formatter1: DateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        val formatter2: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val parsedDate = LocalDate.parse(item.startTime.substring(0,10), formatter2)
        val text = parsedDate.format(formatter1)

        holder.date.text = "Date: " + text

        holder.time.text = "Time: " + item.startTime.substring(11,16) + " - " + item.endTime.substring(11,16)

        holder.time.text = "Time: " + item.startTime.substring(11,16) + " - " + item.endTime.substring(11,16)
        holder.subject.text = "Unit: " + item.unitID
        holder.location.text = "Location: " + item.roomId

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.date)
        val time: TextView = view.findViewById(R.id.time)
        val subject: TextView = view.findViewById(R.id.subject)
        val location: TextView = view.findViewById(R.id.location)


        override fun toString(): String {
            return super.toString() + " '" + subject.text + "'"
        }
    }
}