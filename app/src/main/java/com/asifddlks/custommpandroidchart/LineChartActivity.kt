package com.asifddlks.custommpandroidchart

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.asifddlks.custommpandroidchart.model.PriceDataModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend.LegendForm
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.Utils
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//
// Created by Asif Ahmed on 18/5/22.
//
class LineChartActivity:AppCompatActivity() {

    private var upperLimit: Float = 0f
    private var lowerLimit: Float = 1200f
    private lateinit var chart: LineChart
    private lateinit var seekBarX: SeekBar
    private lateinit var seekBarY:SeekBar
    private lateinit var tvX: TextView
    private lateinit var tvY:TextView

    //
    private val chartValueList = ArrayList<Entry>()
    // // X-Axis Style // //
    private lateinit var xAxis: XAxis
    // // Y-Axis Style // //
    private lateinit var yAxis: YAxis
    //

    private val onSeekBarChangeListener = object : SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            tvX.text = seekBarX.progress.toString()
            tvY.text = seekBarY.progress.toString()
            setData(seekBarX.progress, seekBarY.progress.toFloat())
            chart.invalidate()
            chart.animateX(1500)
        }
        override fun onStartTrackingTouch(seekBar: SeekBar?) {

        }
        override fun onStopTrackingTouch(seekBar: SeekBar?) {

        }
    }

    private val onChartValueSelectedListener: OnChartValueSelectedListener = object : OnChartValueSelectedListener{
        override fun onValueSelected(e: Entry?, h: Highlight?) {
            Log.i("Entry selected", e.toString())
            Log.i("LOW HIGH", "low: " + chart.lowestVisibleX + ", high: " + chart.highestVisibleX)
            Log.i("MIN MAX", "xMin: " + chart.xChartMin + ", xMax: " + chart.xChartMax + ", yMin: " + chart.yChartMin + ", yMax: " + chart.yChartMax)
        }
        override fun onNothingSelected() {
            Log.i("Nothing selected", "Nothing selected.")
        }
    }

    var dataCount:Int = 5
    private var countDownTimer = object : CountDownTimer(20000,1000){
        override fun onTick(millisUntilFinished: Long) {
            //setData(dataCount++, 120f)
            updateData(120f)
            chart.invalidate()
            chart.animateX(1500)
            Log.d(this@LineChartActivity.localClassName, "countDownTimer: onTick()$millisUntilFinished")
        }

        override fun onFinish() {
            Log.d(this@LineChartActivity.localClassName, "countDownTimer: onFinish()")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.activity_line_chart)
        title = "Line Chart"

        tvX = findViewById(R.id.tvXMax)
        tvY = findViewById(R.id.tvYMax)

        chart = findViewById(R.id.chart1)

        seekBarX = findViewById(R.id.seekBar1)
        //seekBarX.setOnSeekBarChangeListener(onSeekBarChangeListener)

        seekBarY = findViewById(R.id.seekBar2)
        //seekBarY.setOnSeekBarChangeListener(onSeekBarChangeListener)

        //seekBarY.max = 180

        // // Chart Style // //
        // background color
        chart.setBackgroundColor(Color.WHITE)

        // disable description text
        chart.description.isEnabled = true

        // enable touch gestures
        chart.setTouchEnabled(true)

        // set listeners
        chart.setOnChartValueSelectedListener(onChartValueSelectedListener)
        chart.setDrawGridBackground(false)

        // create marker to display box when values are selected
        val mv = MyMarkerView(this, R.layout.custom_marker_view)

        // Set the marker to the chart
        mv.chartView = chart
        chart.marker = mv

        // enable scaling and dragging
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        // chart.setScaleXEnabled(true);
        // chart.setScaleYEnabled(true);

        // force pinch zoom along both axis
        chart.setPinchZoom(false)

        // // X-Axis Style // //
        xAxis = chart.xAxis
        // // Y-Axis Style // //
        yAxis = chart.axisLeft

        // vertical grid lines
        xAxis.enableGridDashedLine(10f, 10f, 0f)

        // disable dual axis (only use LEFT axis)
        chart.axisRight.isEnabled = true

        // horizontal grid lines
        yAxis.enableGridDashedLine(10f, 10f, 0f)

        setYAxisMaximumAndMinimumRange()

        setUpperAndLowerLimit()

        // add data
        seekBarX.progress = 5
        seekBarY.progress = 180
        //setData(5, 180f)

        //prepareCustomData()

        //set custom stock data
        setCustomData(prepareCustomData())

        // draw points over time
        chart.animateX(1500)

        // get the legend (only possible after setting data)
        val l = chart.legend

        // draw legend entries as lines
        l.form = LegendForm.LINE

        //countDownTimer.start()
    }

    private fun setUpperAndLowerLimit() {
        // // Create Limit Lines // //
        val llXAxis = LimitLine(90f, "Index 10")
        llXAxis.lineWidth = 4f
        llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        llXAxis.textSize = 10f
        //llXAxis.typeface = tfRegular
        val ll1 = LimitLine(upperLimit, "Upper Limit")
        ll1.lineWidth = 4f
        ll1.enableDashedLine(10f, 10f, 0f)
        ll1.labelPosition = LimitLabelPosition.RIGHT_TOP
        ll1.textSize = 10f
        //ll1.typeface = tfRegular
        val ll2 = LimitLine(lowerLimit, "Lower Limit")
        ll2.lineWidth = 4f
        ll2.enableDashedLine(10f, 10f, 0f)
        ll2.labelPosition = LimitLabelPosition.RIGHT_BOTTOM
        ll2.textSize = 10f
        //ll2.typeface = tfRegular

        // draw limit lines behind data instead of on top
        yAxis.setDrawLimitLinesBehindData(true)
        xAxis.setDrawLimitLinesBehindData(true)

        // add limit lines
        yAxis.addLimitLine(ll1)
        yAxis.addLimitLine(ll2)

        setYAxisMaximumAndMinimumRange()
    }

    private fun setYAxisMaximumAndMinimumRange() {
        // axis range
        yAxis.axisMaximum = upperLimit+10
        yAxis.axisMinimum = lowerLimit-10
    }

    private fun prepareCustomData(): ArrayList<PriceDataModel>{
        val list: ArrayList<PriceDataModel> = ArrayList()
        val jsonString:String? = FileHelper().readJSONFile(this,"stockx_dummy_data_day")
        val jsonObject = jsonString?.let { JSONObject(it) }
        val dataJSONArray = jsonObject?.getJSONArray("data")

        for (i in 0 until (dataJSONArray?.length() ?: 0)) {
            val jsonObjectDataItem = dataJSONArray!!.getJSONObject(i)

            val time:String = jsonObjectDataItem.getString("time")
            val closePrice:Double = jsonObjectDataItem.getDouble("close")

            //convertDateToMilliSeconds(time)

            if (upperLimit<closePrice){
                upperLimit = closePrice.toFloat()
            }
            if (lowerLimit>closePrice){
                lowerLimit = closePrice.toFloat()
            }

            list.add(PriceDataModel(convertDateToMilliSeconds(time), closePrice.toFloat()))
        }
        setUpperAndLowerLimit()
        return list

    }

    private fun convertDateToMilliSeconds(time: String): Long? {
        //10/03/2022 10:01:00
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        var convertedDate: Date? = null
        var formattedDate: String? = null
        try {
            convertedDate = sdf.parse(time)
            //convertedDate.time
            //formattedDate = SimpleDateFormat("dd MM,yyyy").format(convertedDate)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return convertedDate?.time
    }

    private fun setCustomData(list: ArrayList<PriceDataModel>) {
        //val values = ArrayList<Entry>()
        chartValueList.clear()
        /*for (i in 0 until count) {
            val `val` = (Math.random() * range).toFloat() - 30
            chartValueList.add(Entry(range * i.toFloat(), `val`, resources.getDrawable(R.drawable.star)))
        }
        */
        var i:Int = 1
        for(chartDataItem in list){
            chartValueList.add(Entry(i++.toFloat(),
                chartDataItem.price?.toFloat() ?: 0f
            ))

        }
        val set1: LineDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = chartValueList
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(chartValueList, "DataSet 1")
            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f)

            // black lines and points
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.BLACK
            }
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            chart.data = data
        }
    }

    private fun setData(count: Int, range: Float) {
        //val values = ArrayList<Entry>()
        chartValueList.clear()
        for (i in 0 until count) {
            val `val` = (Math.random() * range).toFloat() - 30
            chartValueList.add(Entry(range * i.toFloat(), `val`, resources.getDrawable(R.drawable.star)))
        }
        val set1: LineDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = chartValueList
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(chartValueList, "DataSet 1")
            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f)

            // black lines and points
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.BLACK
            }
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            chart.data = data
        }
    }

    private fun updateData(range: Float) {
        //val values = ArrayList<Entry>()
        //chartValueList.clear()

        Log.d(this.localClassName,"updateData: chartValueList.size.toFloat()*100: "+chartValueList.size.toFloat()*100)

        val `val` = (Math.random() * range).toFloat() - 30
        chartValueList.add(Entry(chartValueList.size.toFloat()*100, `val`, resources.getDrawable(R.drawable.star)))
        val set1: LineDataSet
        if (chart.data != null && chart.data.dataSetCount > 0) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = chartValueList
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(chartValueList, "DataSet 1")
            set1.setDrawIcons(false)

            // draw dashed line
            set1.enableDashedLine(10f, 5f, 0f)

            // black lines and points
            set1.color = Color.BLACK
            set1.setCircleColor(Color.BLACK)

            // line thickness and point size
            set1.lineWidth = 1f
            set1.circleRadius = 3f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // customize legend entry
            set1.formLineWidth = 1f
            set1.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            set1.formSize = 15f

            // text size of values
            set1.valueTextSize = 9f

            // draw selection line as dashed
            set1.enableDashedHighlightLine(10f, 5f, 0f)

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter = IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.BLACK
            }
            val dataSets = ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            chart.data = data
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.line, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.actionToggleValues -> {
                val sets = chart.data
                    .dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.setDrawValues(!set.isDrawValuesEnabled)
                }
                chart.invalidate()
            }
            R.id.actionToggleIcons -> {
                val sets = chart.data
                    .dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.setDrawIcons(!set.isDrawIconsEnabled)
                }
                chart.invalidate()
            }
            R.id.actionToggleHighlight -> {
                if (chart.data != null) {
                    chart.data.isHighlightEnabled = !chart.data.isHighlightEnabled
                    chart.invalidate()
                }
            }
            R.id.actionToggleFilled -> {
                val sets = chart.data.dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    if (set.isDrawFilledEnabled) set.setDrawFilled(false) else set.setDrawFilled(
                        true
                    )
                }
                chart.invalidate()
            }
            R.id.actionToggleCircles -> {
                val sets = chart.data.dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    if (set.isDrawCirclesEnabled) set.setDrawCircles(false) else set.setDrawCircles(
                        true
                    )
                }
                chart.invalidate()
            }
            R.id.actionToggleCubic -> {
                val sets = chart.data.dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.mode =
                        if (set.mode == LineDataSet.Mode.CUBIC_BEZIER) LineDataSet.Mode.LINEAR else LineDataSet.Mode.CUBIC_BEZIER
                }
                chart.invalidate()
            }
            R.id.actionToggleStepped -> {
                val sets = chart.data
                    .dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.mode =
                        if (set.mode == LineDataSet.Mode.STEPPED) LineDataSet.Mode.LINEAR else LineDataSet.Mode.STEPPED
                }
                chart.invalidate()
            }
            R.id.actionToggleHorizontalCubic -> {
                val sets = chart.data
                    .dataSets
                for (iSet in sets) {
                    val set = iSet as LineDataSet
                    set.mode =
                        if (set.mode == LineDataSet.Mode.HORIZONTAL_BEZIER) LineDataSet.Mode.LINEAR else LineDataSet.Mode.HORIZONTAL_BEZIER
                }
                chart.invalidate()
            }
            R.id.actionTogglePinch -> {
                if (chart.isPinchZoomEnabled) chart.setPinchZoom(false) else chart.setPinchZoom(
                    true
                )
                chart.invalidate()
            }
            R.id.actionToggleAutoScaleMinMax -> {
                chart.isAutoScaleMinMaxEnabled = !chart.isAutoScaleMinMaxEnabled
                chart.notifyDataSetChanged()
            }
            R.id.animateX -> {
                chart.animateX(2000)
            }
            R.id.animateY -> {
                chart.animateY(2000, Easing.EaseInCubic)
            }
            R.id.animateXY -> {
                chart.animateXY(2000, 2000)
            }

        }
        return true
    }

}