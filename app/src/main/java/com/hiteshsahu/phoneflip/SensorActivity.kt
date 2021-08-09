package com.hiteshsahu.phoneflip

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.hiteshsahu.phoneflip.databinding.ActivityScrollingBinding

class SensorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScrollingBinding
    private lateinit var sensorViewModel: SensorViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScrollingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        sensorViewModel = SensorViewModel()
        sensorViewModel.initSensor(this)
        binding.viewModel = sensorViewModel
        binding.lifecycleOwner = this
    }

    override fun onResume() {
        super.onResume()
        sensorViewModel.subscribeSensorData()
    }

    override fun onPause() {
        super.onPause()
        sensorViewModel.unsubscribeSensorData()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}