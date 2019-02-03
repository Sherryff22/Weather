package ru.sherry.test.weather

import android.os.Bundle
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
//import android.R
import android.annotation.SuppressLint
import android.location.LocationListener
import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log


class MyLocationActivity : Activity(), LocationListener {

    @SuppressLint("MissingPermission")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            Log.d(TAG, "Широта=" + location!!.getLatitude())
            Log.d(TAG, "Долгота=" + location!!.getLongitude())
        }
    }

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    companion object {
        private val TAG = MyLocationActivity::class.java.name
    }
}