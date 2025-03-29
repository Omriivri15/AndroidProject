package com.example.myapplication.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.model.Recipe
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.io.Serializable

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map_fragment_container) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        loadRecipesAndDisplayMarkers()
    }

    private fun openRecipeDetails(recipe: Recipe) {
        val bundle = Bundle().apply {
            putParcelable("recipe", recipe)  // ודא ש-Recipe מממש Serializable
        }
        findNavController().navigate(R.id.action_mapFragment_to_recipeDetailsFragment, bundle)
    }

    private fun loadRecipesAndDisplayMarkers() {
        val db = FirebaseFirestore.getInstance()

        db.collection("recipes").get().addOnSuccessListener { documents ->
            for (document in documents) {
                val recipe = document.toObject(Recipe::class.java)
                val lat = recipe.latitude
                val lng = recipe.longitude

                if (lat != null && lng != null) {
                    val position = LatLng(lat, lng)
                    val marker = googleMap.addMarker(
                        MarkerOptions()
                            .position(position)
                            .title(recipe.name)
                            .snippet(recipe.description)
                    )
                    marker?.tag = recipe
                }
            }

            // מעבר למתכון הראשון במפה
            documents.firstOrNull()?.toObject(Recipe::class.java)?.let {
                if (it.latitude != null && it.longitude != null) {
                    val center = LatLng(it.latitude!!, it.longitude!!)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 10f))
                }
            }

            // מאזין ללחיצה על Tooltip
            googleMap.setOnInfoWindowClickListener { marker ->
                val recipe = marker.tag as? Recipe
                recipe?.let {
                    openRecipeDetails(it)
                }
            }
        }
    }
}
