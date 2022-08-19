package com.ash.thetracker.presentation.ui.maps

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.location.LocationManagerCompat
import com.ash.thetracker.R
import com.ash.thetracker.data.cache.PreferencesManager
import com.ash.thetracker.databinding.ActivityMapsBinding
import com.ash.thetracker.presentation.model.TrackingModel
import com.ash.thetracker.presentation.viewModels.MapsViewModel
import com.ash.thetracker.shared.DialogUtils
import com.ash.thetracker.shared.permissions.MultiplePermissionChecker
import com.ash.thetracker.shared.string
import com.ash.thetracker.shared.toLog
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {

    private lateinit var binding: ActivityMapsBinding
    private lateinit var gMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val mapsViewModel by viewModel<MapsViewModel>()
    private var polylineOptions = PolylineOptions()

    private var isTracking: Boolean
        get() = PreferencesManager.isTracking
        set(value) {
            PreferencesManager.isTracking = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapsActivity)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        launchPermissionChecking()

        updateButtonStates()

        setObservers()
        setClickListeners()
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            locationResult.locations.forEach {
                val trackingModel = TrackingModel(Calendar.getInstance().timeInMillis, it.latitude, it.longitude)
                mapsViewModel.insertTrackingModel(trackingModel)
            }
        }
    }

    private val locationPermissionListener = object : MultiplePermissionChecker.PermissionListener {
        @SuppressLint("MissingPermission")
        override fun locationGranted() {
            if (this@MapsActivity::gMap.isInitialized) gMap.isMyLocationEnabled = true
            ifLocationEnabled {
                if (isTracking) launchLocationUpdating()
            }
        }

        override fun locationNonGranted() {
            stopTracking()
        }

        override fun activityRecognitionGranted() {
            if (isTracking) launchStepCounterListener()
        }

        override fun activityRecognitionNonGranted() {}
    }


    private val locationPermissionChecker = MultiplePermissionChecker(locationPermissionListener, this)

    @SuppressLint("MissingPermission")
    private fun launchLocationUpdating() {
        val locationRequest = LocationRequest.create().apply {
            interval = 3000L
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    private fun setClickListeners() = with(binding) {
        startButton.setOnClickListener {
            if (!this@MapsActivity::gMap.isInitialized) {
                launchPermissionChecking()
                return@setOnClickListener
            }
            isTracking = true
            gMap.clear()
            updateButtonStates()
            updateAllDisplayedTexts(0, 0f)
            launchPermissionChecking()
        }
        endButton.setOnClickListener {
            DialogUtils.showDialog(this@MapsActivity, title = string(R.string.stop_tracking_message),
                positiveButtonClickListener = {
                    isTracking = false
                    updateButtonStates()
                    stopTracking()
                }, negativeButtonClickListener = { dialog ->
                    dialog.dismiss()
                })
        }
    }

    private fun setObservers() = with(mapsViewModel) {
        allTrackingModelsLiveData.observe(this@MapsActivity) { allTrackingEntities ->
            if (allTrackingEntities.isEmpty()) {
                updateAllDisplayedTexts(0, 0f)
            }
        }

        lastTrackingModelLiveData.observe(this@MapsActivity) { lastTrackingEntity ->
            lastTrackingEntity ?: return@observe
            addLocationToRoute(lastTrackingEntity)
        }

        totalDistanceTravelledLiveData.observe(this@MapsActivity) { distance ->
            distance ?: return@observe
            val stepCount = mapsViewModel.currentNumberOfStepCount.value ?: 0
            updateAllDisplayedTexts(stepCount, distance)
        }

        currentNumberOfStepCount.observe(this@MapsActivity) { stepCount ->
            val totalDistanceTravelled = mapsViewModel.totalDistanceTravelledLiveData.value ?: 0f
            updateAllDisplayedTexts(stepCount, totalDistanceTravelled)
        }

        allTrackingModels.observe(this@MapsActivity) { trackingModels ->
            addLocationListToRoute(trackingModels)
        }
    }

    private fun updateButtonStates() = with(binding) {
        isTracking.toLog()
        startButton.isEnabled = !isTracking
        endButton.isEnabled = isTracking
    }

    private fun updateAllDisplayedTexts(stepCount: Int, totalDistanceTravelled: Float) = with(binding) {
        stepNumberTextView.text = String.format("Step count: %d", stepCount)
        totalDistanceTextView.text = String.format("Total distance: %.2fm", totalDistanceTravelled)
    }

    private fun stopTracking() {
        polylineOptions = PolylineOptions()
        mapsViewModel.deleteAllTrackingModels()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        sensorManager.unregisterListener(this, stepCounterSensor)

        isTracking = false
        updateButtonStates()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.apply {
            if (locationPermissionChecker.isLocationPermissionGranted()) isMyLocationEnabled = true
            setOnMyLocationButtonClickListener {
                launchPermissionChecking()
                return@setOnMyLocationButtonClickListener false
            }
        }

        // Yerevan LatLong
        val latitude = 40.1872
        val longitude = 44.5152
        val yerevanLatLong = LatLng(latitude, longitude)
        val zoomLevel = 10f

        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(yerevanLatLong, zoomLevel))

        if (isTracking) mapsViewModel.getAllTrackingModels()
    }

    private fun addLocationListToRoute(trackingModels: List<TrackingModel>) {
        if (!this::gMap.isInitialized) return
        gMap.clear()
        trackingModels.forEach { trackingModel ->
            val newLatLngInstance = trackingModel.asLatLng()
            polylineOptions.points.add(newLatLngInstance)
        }
        gMap.addPolyline(polylineOptions)
    }

    private fun addLocationToRoute(trackingModel: TrackingModel) {
        if (!this::gMap.isInitialized) {
            launchPermissionChecking()
            return
        }
        gMap.clear()
        val newLatLngInstance = trackingModel.asLatLng()
        polylineOptions.points.add(newLatLngInstance)
        gMap.addPolyline(polylineOptions)
    }

    private fun launchStepCounterListener() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        stepCounterSensor ?: return
        sensorManager.registerListener(this@MapsActivity, stepCounterSensor, SensorManager.SENSOR_DELAY_FASTEST)
    }

    private fun launchPermissionChecking() {
        locationPermissionChecker.launchPermissionChecking()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onSensorChanged(sensorEvent: SensorEvent?) = with(mapsViewModel) {
        sensorEvent ?: return
        val firstSensorEvent = sensorEvent.values.firstOrNull() ?: return
        val isFirstStepCountRecord = currentNumberOfStepCount.value == 0
        if (isFirstStepCountRecord) {
            initialStepCount = firstSensorEvent.toInt()
            currentNumberOfStepCount.value = 1
        } else {
            currentNumberOfStepCount.value = firstSensorEvent.toInt() - initialStepCount
        }
    }

    private fun ifLocationEnabled(enabled: () -> Unit) {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isEnabled = LocationManagerCompat.isLocationEnabled(locationManager)
        if (isEnabled) enabled.invoke()
        else {
            stopTracking()
            DialogUtils.showDialog(this, message = string(R.string.enable_location_message), isCancelable = false,
                positiveButtonClickListener = {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }, negativeButtonClickListener = { dialog ->
                    dialog.dismiss()
                })
        }
    }
}