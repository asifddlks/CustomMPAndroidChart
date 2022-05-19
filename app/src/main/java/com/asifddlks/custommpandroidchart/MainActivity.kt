package com.asifddlks.custommpandroidchart

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.buttonLineChart).setOnClickListener {
            startActivity(Intent(this,LineChartActivity::class.java))
        }
        findViewById<Button>(R.id.buttonCandleStick).setOnClickListener {
            startActivity(Intent(this,CandleStickChartActivity::class.java))
        }

    }
}