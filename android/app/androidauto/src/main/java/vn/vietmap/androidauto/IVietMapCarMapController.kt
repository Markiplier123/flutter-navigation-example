package vn.vietmap.androidauto

import com.mapbox.api.directions.v5.models.DirectionsRoute

interface IVietMapCarMapController {
    fun overviewRoute()
    fun zoomIn()
    fun zoomOut()
    fun recenter()
    fun startNavigation()
    fun stopNavigation()
    fun setRoute(route: DirectionsRoute)
}