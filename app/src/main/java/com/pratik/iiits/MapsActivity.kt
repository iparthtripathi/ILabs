package com.pratik.iiits

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.pratik.iiits.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val mapTypeButton: ImageButton = findViewById(R.id.map_type)
        val popupMenu = PopupMenu(this, mapTypeButton)
        popupMenu.menuInflater.inflate(R.menu.map_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }
        mapTypeButton.setOnClickListener {
            popupMenu.show()
        }
    }

    private fun changeMap(itemId: Int) {
        when (itemId) {
            R.id.normal_map -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.setellite_map -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Define the coordinates for IIIT
        val iiitCoordinates = LatLng(13.5553, 80.0267)

        // Add a custom marker in IIIT with a title and icon
        mMap.addMarker(
            MarkerOptions()
                .position(iiitCoordinates)
                .title("IIIT Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
        )

        // Set the default map type to satellite
        mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE

        // Move the camera to the IIIT coordinates
        mMap.moveCamera(CameraUpdateFactory.newLatLng(iiitCoordinates))

        // Create a CameraPosition with the specified parameters
        val cameraPosition = CameraPosition.Builder()
            .target(iiitCoordinates) // Sets the center of the map to IIIT
            .zoom(17f) // Sets the zoom level
            .bearing(90f) // Sets the orientation of the camera to east
            .tilt(30f) // Sets the tilt of the camera to 30 degrees
            .build() // Creates a CameraPosition from the builder

        // Use a Handler to delay the animation slightly
        Handler(Looper.getMainLooper()).postDelayed({
            // Animate the camera to the specified camera position
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }, 1000) // Adjust the delay as needed
    }
}
