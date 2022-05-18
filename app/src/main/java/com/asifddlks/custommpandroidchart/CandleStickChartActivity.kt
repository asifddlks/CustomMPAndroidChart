package com.asifddlks.custommpandroidchart

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry

//
// Created by Asif Ahmed on 18/5/22.
//
class CandleStickChartActivity:AppCompatActivity() {

    private lateinit var chart: CandleStickChart
    private lateinit var seekBarX: SeekBar
    private lateinit var seekBarY:SeekBar
    private lateinit var tvX: TextView
    private lateinit var tvY:TextView

    private val seekBarChangeListener = object :SeekBar.OnSeekBarChangeListener{
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

            seekBarX.progress = progress
            tvX.text = progress.toString()
            tvY.text = seekBarY.progress.toString()
            chart.resetTracking()
            val values = ArrayList<CandleEntry>()
            for (i in 0..99) {
                val multi: Float = (seekBarY.progress + 1).toFloat()
                val `val` = (Math.random() * 40).toFloat() + multi
                val high = (Math.random() * 9).toFloat() + 8f
                val low = (Math.random() * 9).toFloat() + 8f
                val open = (Math.random() * 6).toFloat() + 1f
                val close = (Math.random() * 6).toFloat() + 1f
                val even = i % 2 == 0
                values.add(
                    CandleEntry(
                        i.toFloat(), `val` + high,
                        `val` - low,
                        if (even) `val` + open else `val` - open,
                        if (even) `val` - close else `val` + close,
                        resources.getDrawable(R.drawable.star)

                    )
                )
            }
            val set1 = CandleDataSet(values, "Data Set")
            set1.setDrawIcons(false)
            set1.axisDependency = YAxis.AxisDependency.LEFT
            //        set1.setColor(Color.rgb(80, 80, 80));
            set1.shadowColor = Color.DKGRAY
            set1.shadowWidth = 0.7f
            set1.decreasingColor = Color.RED
            set1.decreasingPaintStyle = Paint.Style.FILL
            set1.increasingColor = Color.rgb(122, 242, 84)
            set1.increasingPaintStyle = Paint.Style.STROKE
            set1.neutralColor = Color.BLUE
            //set1.setHighlightLineWidth(1f);
            val data = CandleData(set1)
            chart.data = data
            chart.invalidate()
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {

        }

        override fun onStopTrackingTouch(p0: SeekBar?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_candle_chart)
        //title = "CandleStickChartActivity"
        tvX = findViewById(R.id.tvXMax)
        tvY = findViewById(R.id.tvYMax)

        chart = findViewById(R.id.chart1)

        seekBarX = findViewById(R.id.seekBar1)

        seekBarX.setOnSeekBarChangeListener(seekBarChangeListener)
        seekBarY = findViewById(R.id.seekBar2)
        seekBarY.setOnSeekBarChangeListener(seekBarChangeListener)
        chart.xAxis
        chart.setBackgroundColor(Color.WHITE)
        chart.description.isEnabled = false

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60)

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false)
        chart.setDrawGridBackground(false)
        val xAxis = chart.xAxis
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        val leftAxis = chart.axisLeft
        //        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(7, false)
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)
        val rightAxis = chart.getAxisRight()
        rightAxis.isEnabled = false
        //        rightAxis.setStartAtZero(false);

        // setting data
        seekBarX.progress = 40
        seekBarY.progress = 100
        chart.legend.isEnabled = false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.candle, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionToggleValues -> {
                for (set in chart.data.dataSets) set.setDrawValues(!set.isDrawValuesEnabled)
                chart.invalidate()
            }
            R.id.actionToggleIcons -> {
                for (set in chart.data.dataSets) set.setDrawIcons(!set.isDrawIconsEnabled)
                chart.invalidate()
            }
            R.id.actionToggleHighlight -> {
                if (chart.data != null) {
                    chart.data.isHighlightEnabled = !chart.data.isHighlightEnabled
                    chart.invalidate()
                }
            }
            R.id.actionTogglePinch -> {
                chart.setPinchZoom(!chart.isPinchZoomEnabled)
                chart.invalidate()
            }
            R.id.actionToggleAutoScaleMinMax -> {
                chart.isAutoScaleMinMaxEnabled = !chart.isAutoScaleMinMaxEnabled
                chart.notifyDataSetChanged()
            }
            R.id.actionToggleMakeShadowSameColorAsCandle -> {
                for (set in chart.data.dataSets) {
                    (set as CandleDataSet).shadowColorSameAsCandle =
                        !set.getShadowColorSameAsCandle()
                }
                chart.invalidate()
            }
            R.id.animateX -> {
                chart.animateX(2000)
            }
            R.id.animateY -> {
                chart.animateY(2000)
            }
            R.id.animateXY -> {
                chart.animateXY(2000, 2000)
            }
        }
        return true
    }
}