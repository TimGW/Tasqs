//package com.timgortworst.roomy.ui.customview.calendar
//
//import android.content.Context
//
//import android.graphics.Color
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.TextView
//
//import com.timgortworst.roomy.R
//import com.timgortworst.roomy.model.Event
//
//import java.util.Calendar
//import java.util.Date
//
//class GridAdapter(context: Context,
//                  private val monthlyDates: List<Date>,
//                  private val currentDate: Calendar,
//                  private val allEvents: MutableList<Event>) : ArrayAdapter<*>(context, R.layout.custom_single_cell_layout) {
//    private val mInflater: LayoutInflater
//
//    init {
//        mInflater = LayoutInflater.from(context)
//    }
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val mDate = monthlyDates[position]
//        val dateCal = Calendar.getInstance()
//        dateCal.time = mDate
//        val dayValue = dateCal.get(Calendar.DAY_OF_MONTH)
//        val displayMonth = dateCal.get(Calendar.MONTH) + 1
//        val displayYear = dateCal.get(Calendar.YEAR)
//        val currentMonth = currentDate.get(Calendar.MONTH) + 1
//        val currentYear = currentDate.get(Calendar.YEAR)
//        var view = convertView
//        if (view == null) {
//            view = mInflater.inflate(R.layout.custom_single_cell_layout, parent, false)
//        }
//        if (displayMonth == currentMonth && displayYear == currentYear) {
//            view!!.setBackgroundColor(Color.parseColor("#FF5733"))
//        } else {
//            view!!.setBackgroundColor(Color.parseColor("#cccccc"))
//        }
//        //Add day to calendar
//        val cellNumber = view.findViewById<View>(R.id.calendar_date_id) as TextView
//        cellNumber.text = dayValue.toString()
//        //Add events to the calendar
//        val eventIndicator = view.findViewById<View>(R.id.event_id) as TextView
//        val eventCalendar = Calendar.getInstance()
//        for (i in allEvents.indices) {
//            eventCalendar.timeInMillis = allEvents[i].eventMetaData.repeatStartDate
//            if (dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
//                    && displayYear == eventCalendar.get(Calendar.YEAR)) {
//                eventIndicator.setBackgroundColor(Color.parseColor("#FF4081"))
//            }
//        }
//        return view
//    }
//
//    override fun getCount(): Int {
//        return monthlyDates.size
//    }
//
//    override fun getItem(position: Int): Any? {
//        return monthlyDates[position]
//    }
//
//    override fun getPosition(item: Nothing?): Int {
//        return monthlyDates.indexOf(item)
//    }
//
//    fun setEvents(events: List<Event>) {
//        allEvents.clear()
//        allEvents.addAll(events)
//    }
//
//
//    companion object {
//        private val TAG = GridAdapter::class.java.simpleName
//    }
//}