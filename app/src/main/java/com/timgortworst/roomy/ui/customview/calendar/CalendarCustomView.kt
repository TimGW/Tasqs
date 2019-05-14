//package com.timgortworst.roomy.ui.customview.calendar
//
//import android.content.Context
//import android.util.AttributeSet
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.AdapterView
//import android.widget.Button
//import android.widget.GridView
//import android.widget.ImageView
//import android.widget.LinearLayout
//import android.widget.TextView
//import android.widget.Toast
//import com.timgortworst.roomy.R
//import com.timgortworst.roomy.model.Event
//
//import java.text.SimpleDateFormat
//import java.util.ArrayList
//import java.util.Calendar
//import java.util.Date
//import java.util.EventObject
//import java.util.Locale
//
//class CalendarCustomView : LinearLayout {
//    private var previousButton: ImageView? = null
//    private var nextButton: ImageView? = null
//    private var currentDate: TextView? = null
//    private var calendarGridView: GridView? = null
//    private val month: Int = 0
//    private val year: Int = 0
//    private val formatter = SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)
//    private val cal = Calendar.getInstance(Locale.ENGLISH)
//    private val context: Context
//    private var mAdapter: GridAdapter? = null
//
//    constructor(context: Context) : super(context) {}
//    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
//        this.context = context
//        initializeUILayout()
//        setUpCalendarAdapter()
//        setPreviousButtonClickEvent()
//        setNextButtonClickEvent()
//        setGridCellClickEvents()
//        Log.d(TAG, "I need to call this method")
//    }
//
//    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}
//
//    private fun initializeUILayout() {
//        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val view = inflater.inflate(R.layout.custom_calendar_layout, this)
//        previousButton = view.findViewById<View>(R.id.previous_month) as ImageView
//        nextButton = view.findViewById<View>(R.id.next_month) as ImageView
//        currentDate = view.findViewById<View>(R.id.display_current_date) as TextView
//        calendarGridView = view.findViewById<View>(R.id.calendar_grid) as GridView
//    }
//
//    private fun setPreviousButtonClickEvent() {
//        previousButton!!.setOnClickListener {
//            cal.add(Calendar.MONTH, -1)
//            setUpCalendarAdapter()
//        }
//    }
//
//    private fun setNextButtonClickEvent() {
//        nextButton!!.setOnClickListener {
//            cal.add(Calendar.MONTH, 1)
//            setUpCalendarAdapter()
//        }
//    }
//
//    private fun setGridCellClickEvents() {
//        calendarGridView!!.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id -> Toast.makeText(context, "Clicked $position", Toast.LENGTH_LONG).show() }
//    }
//
//    private fun setUpCalendarAdapter() {
//        val dayValueInCells = ArrayList<Date>()
//        val mCal = cal.clone() as Calendar
//        mCal.set(Calendar.DAY_OF_MONTH, 1)
//        val firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1
//        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth)
//        while (dayValueInCells.size < MAX_CALENDAR_COLUMN) {
//            dayValueInCells.add(mCal.time)
//            mCal.add(Calendar.DAY_OF_MONTH, 1)
//        }
//        Log.d(TAG, "Number of date " + dayValueInCells.size)
//        val sDate = formatter.format(cal.time)
//        currentDate!!.text = sDate
//        mAdapter = GridAdapter(context, dayValueInCells, cal, emptyList())
//        calendarGridView!!.adapter = mAdapter
//    }
//
//    fun setEvent(mEvents: List<Event>) {
//        mAdapter.setEvents(mEvents)
//        mAdapter?.notifyDataSetChanged()
//    }
//
//    companion object {
//        private val TAG = CalendarCustomView::class.java.simpleName
//        private val MAX_CALENDAR_COLUMN = 42
//    }
//}