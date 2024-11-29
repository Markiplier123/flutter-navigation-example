package vn.vietmap.androidauto

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.SurfaceCallback
import androidx.car.app.SurfaceContainer
import androidx.car.app.model.Template
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.geojson.Point
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.vietmap.androidauto.helper.VietMapCarSurfaceHelper
import vn.vietmap.androidauto.helper.VietMapHelper
import vn.vietmap.services.android.navigation.ui.v5.camera.CameraOverviewCancelableCallback
import vn.vietmap.services.android.navigation.ui.v5.listeners.BannerInstructionsListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.NavigationListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.RouteListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener
import vn.vietmap.services.android.navigation.ui.v5.voice.NavigationSpeechPlayer
import vn.vietmap.services.android.navigation.ui.v5.voice.SpeechAnnouncement
import vn.vietmap.services.android.navigation.ui.v5.voice.SpeechPlayer
import vn.vietmap.services.android.navigation.ui.v5.voice.SpeechPlayerProvider
import vn.vietmap.services.android.navigation.v5.location.engine.LocationEngineProvider
import vn.vietmap.services.android.navigation.v5.location.replay.ReplayRouteLocationEngine
import vn.vietmap.services.android.navigation.v5.milestone.Milestone
import vn.vietmap.services.android.navigation.v5.milestone.MilestoneEventListener
import vn.vietmap.services.android.navigation.v5.milestone.VoiceInstructionMilestone
import vn.vietmap.services.android.navigation.v5.navigation.NavigationConstants
import vn.vietmap.services.android.navigation.v5.navigation.NavigationEventListener
import vn.vietmap.services.android.navigation.v5.navigation.NavigationMapRoute
import vn.vietmap.services.android.navigation.v5.navigation.NavigationRoute
import vn.vietmap.services.android.navigation.v5.navigation.NavigationTimeFormat
import vn.vietmap.services.android.navigation.v5.navigation.VietmapNavigation
import vn.vietmap.services.android.navigation.v5.navigation.VietmapNavigationOptions
import vn.vietmap.services.android.navigation.v5.offroute.OffRouteListener
import vn.vietmap.services.android.navigation.v5.route.FasterRouteListener
import vn.vietmap.services.android.navigation.v5.routeprogress.ProgressChangeListener
import vn.vietmap.services.android.navigation.v5.routeprogress.RouteProgress
import vn.vietmap.services.android.navigation.v5.snap.SnapToRoute
import vn.vietmap.vietmapsdk.camera.CameraPosition
import vn.vietmap.vietmapsdk.camera.CameraUpdate
import vn.vietmap.vietmapsdk.camera.CameraUpdateFactory
import vn.vietmap.vietmapsdk.camera.CameraUpdateFactory.newLatLngBounds
import vn.vietmap.vietmapsdk.geometry.LatLng
import vn.vietmap.vietmapsdk.geometry.LatLngBounds
import vn.vietmap.vietmapsdk.location.LocationComponentActivationOptions
import vn.vietmap.vietmapsdk.location.LocationComponentOptions
import vn.vietmap.vietmapsdk.location.engine.LocationEngine
import vn.vietmap.vietmapsdk.location.modes.CameraMode
import vn.vietmap.vietmapsdk.location.modes.RenderMode
import vn.vietmap.vietmapsdk.maps.Style
import vn.vietmap.vietmapsdk.maps.VietMapGL
import vn.vietmap.vietmapsdk.maps.VietMapGLOptions
import vn.vietmap.vietmapsdk.style.layers.LineLayer
import vn.vietmap.vietmapsdk.style.layers.Property.LINE_CAP_ROUND
import vn.vietmap.vietmapsdk.style.layers.Property.LINE_JOIN_ROUND
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.lineCap
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.lineColor
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.lineJoin
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.lineWidth
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

class VietMapNavigationScreen(
    carContext: CarContext,
    private val mSurfaceRenderer: VietMapAndroidAutoSurface,
) : Screen(carContext), ProgressChangeListener,
    OffRouteListener, MilestoneEventListener, NavigationEventListener, NavigationListener,
    FasterRouteListener, SpeechAnnouncementListener, BannerInstructionsListener, RouteListener, IVietMapCarMapController {


    private var routeClicked: Boolean = false
    private var currentRoute: DirectionsRoute? = null
    private var locationEngine: LocationEngine? = null
    private var navigationMapRoute: NavigationMapRoute? = null
    private var directionsRoutes: List<DirectionsRoute>? = null
    private val navigationOptions =
        VietmapNavigationOptions.builder().maxTurnCompletionOffset(30.0).maneuverZoneRadius(40.0)
            .maximumDistanceOffRoute(50.0).deadReckoningTimeInterval(5.0)
            .maxManipulatedCourseAngle(25.0).userLocationSnapDistance(20.0).secondsBeforeReroute(3)
            .enableOffRouteDetection(true).enableFasterRouteDetection(false).snapToRoute(false)
            .manuallyEndNavigationUponCompletion(false).defaultMilestonesEnabled(true)
            .minimumDistanceBeforeRerouting(10.0).metersRemainingTillArrival(20.0)
            .isFromNavigationUi(false).isDebugLoggingEnabled(false)
            .roundingIncrement(NavigationConstants.ROUNDING_INCREMENT_FIFTY)
            .timeFormatType(NavigationTimeFormat.NONE_SPECIFIED)
            .locationAcceptableAccuracyInMetersThreshold(100).build()
    private var navigation: VietmapNavigation? = null
    private var mapReady = false
    private var isDisposed = false
    private var isRefreshing = false
    private var isBuildingRoute = false
    private var isNavigationInProgress = false
    private var isNavigationCanceled = false
    private var isOverviewing = false
    private var isNextTurnHandling = false
    private val snapEngine = SnapToRoute()
    private var apikey: String? = null
    private var speechPlayer: SpeechPlayer? = null
    private var routeProgress: RouteProgress? = null
    private var primaryRouteIndex = 0

    private var currentCenterPoint: CurrentCenterPoint? = null
    val vietMapCarSurfaceHelper: VietMapCarSurfaceHelper = VietMapCarSurfaceHelper(this,carContext)
    companion object {


        var profile: String = "driving-traffic"
        val wayPoints: MutableList<Point> = mutableListOf()
        var simulateRoute = false
        var zoom = 20.0
        var bearing = 0.0
        var tilt = 45.0
        var distanceRemaining: Double? = null
        var durationRemaining: Double? = null
        var animateBuildRoute = true
        var originPoint: Point? = null
        var destinationPoint: Point? = null
        var isRunning: Boolean = false

        var padding: IntArray = intArrayOf(300, 200, 30, 30)

    }

    private fun playVoiceAnnouncement(milestone: Milestone?) {
        if (milestone is VoiceInstructionMilestone) {
            val announcement = SpeechAnnouncement.builder()
                .voiceInstructionMilestone(milestone as VoiceInstructionMilestone?).build()
            speechPlayer!!.play(announcement)
        }
    }

    private fun configSpeechPlayer() {
        var speechPlayerProvider = SpeechPlayerProvider(carContext, "vi", true)
        this.speechPlayer = NavigationSpeechPlayer(speechPlayerProvider)
    }

    private var vietmapGL: VietMapGL? = null



    private fun clearRoute() {
        if (navigationMapRoute != null) {
            navigationMapRoute?.removeRoute()
        }
        currentRoute = null
    }

    override fun stopNavigation(){
        navigation?.stopNavigation()
        navigationMapRoute?.removeRoute()
        isRunning = false
        currentRoute = null
        isNavigationInProgress = false
        isNavigationCanceled = true
        isOverviewing = false
        isNextTurnHandling = false
    }

    override fun setRoute(route: DirectionsRoute) {
        currentRoute = route
        isNavigationInProgress = true


    }

    override fun startNavigation() {
        tilt = 45.0
        zoom = 19.0
        isOverviewing = false
        isNavigationCanceled = false
        vietmapGL?.locationComponent?.cameraMode = CameraMode.TRACKING_GPS_NORTH

        if (currentRoute != null) {
            if (simulateRoute) {
                val mockLocationEngine = ReplayRouteLocationEngine()
                mockLocationEngine.assign(currentRoute)
                navigation?.locationEngine = mockLocationEngine
            } else {
                locationEngine?.let {
                    navigation?.locationEngine = it
                }
            }
            isRunning = true
            vietmapGL?.locationComponent?.locationEngine = null
            navigation?.addNavigationEventListener(this)
            navigation?.addFasterRouteListener(this)
            navigation?.addMilestoneEventListener(this)
            navigation?.addOffRouteListener(this)
            navigation?.addProgressChangeListener(this)
            navigation?.snapEngine = snapEngine
            currentRoute?.let {
                isNavigationInProgress = true
                navigation?.startNavigation(currentRoute!!)
                vietMapCarSurfaceHelper.updateOnStartNavigationTemplate( )
                invalidate()
                recenter()
            }
        }
    }

    override fun overviewRoute() {
        isOverviewing = true
        if (currentRoute != null) {
            val routePoints: List<Point> =
                currentRoute?.routeOptions()?.coordinates() as List<Point>
            animateVietmapGLForRouteOverview(padding, routePoints)
        }
    }

    override fun zoomIn() {
        vietmapGL?.animateCamera(CameraUpdateFactory.zoomIn())
    }

    override fun zoomOut() {
        vietmapGL?.animateCamera(CameraUpdateFactory.zoomBy(-1.0))
    }

    override fun recenter() {
        isOverviewing = false
        if (currentCenterPoint != null) {
            moveCamera(
                LatLng(currentCenterPoint!!.latitude, currentCenterPoint!!.longitude),
                currentCenterPoint!!.bearing
            )
        }
    }

    private fun finishNavigation(isOffRouted: Boolean = false) {

        zoom = 15.0
        bearing = 0.0
        tilt = 0.0
        isNavigationCanceled = true

        if (!isOffRouted) {
            isNavigationInProgress = false
//            moveCameraToOriginOfRoute()
        }

        if (currentRoute != null) {
            isRunning = false
            navigation?.stopNavigation()
            navigation?.removeFasterRouteListener(this)
            navigation?.removeMilestoneEventListener(this)
            navigation?.removeNavigationEventListener(this)
            navigation?.removeOffRouteListener(this)
            navigation?.removeProgressChangeListener(this)
        }

    }

    private val mSurfaceCallback: SurfaceCallback = object : SurfaceCallback {
        override fun onSurfaceAvailable(container: SurfaceContainer) {
            super.onSurfaceAvailable(container)
        }

        override fun onClick(x: Float, y: Float) {
            super.onClick(x, y)

            val clickedLatLng = vietmapGL?.projection?.fromScreenLocation(PointF(x, y))
            clickedLatLng?.let { navigationMapRoute?.onMapClick(it) }
            Log.d("onSurfaceClick", "data: $clickedLatLng")
            if (isRunning) {
                return
            }
            val currentLatLng = vietmapGL?.projection?.fromScreenLocation(PointF(x, y))
            if (simulateRoute) {
                originPoint = Point.fromLngLat(106.628881, 10.798373)
            }
            vietmapGL?.locationComponent?.lastKnownLocation?.let {
                originPoint = Point.fromLngLat(it.longitude, it.latitude)
            }
            currentLatLng?.let {
                destinationPoint = Point.fromLngLat(it.longitude, it.latitude)
                profile = DirectionsCriteria.PROFILE_DRIVING_TRAFFIC
//                destinationPoint = Point.fromLngLat(106.628881, 10.798373)

                getRoute(false, bearing.toFloat(), profile)
            }

        }

        override fun onFling(velocityX: Float, velocityY: Float) {
            super.onFling(velocityX, velocityY)
        }

        override fun onScroll(distanceX: Float, distanceY: Float) {
            isOverviewing = true
            super.onScroll(distanceX, distanceY)
        }

        override fun onScale(focusX: Float, focusY: Float, scaleFactor: Float) {

            isOverviewing = true
            super.onScale(focusX, focusY, scaleFactor)
        }
    }

    init {
        locationEngine = if (simulateRoute) {
            ReplayRouteLocationEngine()
        } else {
            LocationEngineProvider.getBestLocationEngine(carContext)
        }

        navigation = VietmapNavigation(
            carContext, navigationOptions, locationEngine!!
        )
        mSurfaceRenderer.addOnSurfaceCallbackListener(mSurfaceCallback)
        mSurfaceRenderer.init(
            Style.Builder()
                .fromUri("https://maps.vietmap.vn/api/maps/light/styles.json?apikey=YOUR_API_KEY_HERE"),
            {

                val routeLineLayer = LineLayer("line-layer-id", "source-id")
                routeLineLayer.setProperties(
                    lineWidth(9f),
                    lineColor(Color.RED),
                    lineCap(LINE_CAP_ROUND),
                    lineJoin(LINE_JOIN_ROUND)
                )
                it.addLayer(routeLineLayer)
                enableLocationComponent(it)

                initMapRoute()
            }, {
                vietmapGL = it
            }

        )
    }

    private fun initMapRoute() {
        if (vietmapGL != null) {
            navigationMapRoute =
                NavigationMapRoute(
                    mSurfaceRenderer.getMapView()!!,
                    vietmapGL!!,
                    "vmadmin_province"
                )
        }

        navigationMapRoute?.setOnRouteSelectionChangeListener {
            routeClicked = true

            currentRoute = it

            val routePoints: List<Point> =
                currentRoute?.routeOptions()?.coordinates() as List<Point>
            animateVietmapGLForRouteOverview(padding, routePoints)
            primaryRouteIndex = try {
                it.routeIndex()?.toInt() ?: 0
            } catch (e: Exception) {
                0
            }
            if (isRunning) {
                finishNavigation(isOffRouted = true)
                startNavigation()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        val customLocationComponentOptions =
            LocationComponentOptions.builder(carContext).pulseEnabled(true)
                .maxZoomIconScale(2.5f)
                .minZoomIconScale(2.0f)
                .padding(intArrayOf(300, 0, 0, 0))
//                .backgroundDrawable()
                /// If you want to customize the Location icon, you can set a custom drawable here
                .build()
        vietmapGL?.locationComponent?.let { locationComponent ->
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(carContext, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .locationEngine(locationEngine).build()
            )

            locationComponent.setCameraMode(
                CameraMode.TRACKING_GPS_NORTH,
                750L,
                zoom,
                locationComponent.lastKnownLocation?.bearing?.toDouble() ?: 0.0,
                tilt,
                null
            )
            locationComponent.zoomWhileTracking(18.0)
            locationComponent.renderMode = RenderMode.GPS

            /// set custom icon

            locationComponent.locationEngine = locationEngine

            if (!simulateRoute) {
                locationComponent.isLocationComponentEnabled = true
            }
        }
        invalidate()

    }

    private fun updateRoutingInfo(distanceToNextTurn: Double) {
        vietMapCarSurfaceHelper.updateRoutingInfo(distanceToNextTurn)
        invalidate()
    }

    private fun updateStep(cueGuide: String) {
        vietMapCarSurfaceHelper.updateStep(cueGuide)
        invalidate()
    }


    // Update turn icon guide for next turn
    private fun updateManeuver() {
        vietMapCarSurfaceHelper.updateManeuver(routeProgress)
        invalidate()
    }

    // DistanceEstimate: meters
    // DurationEstimate: minutes
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateTravelEstimate(
        distanceEstimate: Double,
        durationEstimate: Long,
        descriptionText: String,
    ) {
        vietMapCarSurfaceHelper.updateTravelEstimate(distanceEstimate, durationEstimate, descriptionText)
        invalidate()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    override fun onGetTemplate(): Template {
        return vietMapCarSurfaceHelper.navigationTemplateBuilder.build()
    }

    private fun getRoute(
        isStartNavigation: Boolean, bearing: Float?, profile: String,
    ) {


        val br = bearing ?: 0.0
        val builder = NavigationRoute.builder(carContext)
            .apikey(apikey ?: "YOUR_API_KEY_HERE")
            .origin(originPoint!!, 60.0, br.toDouble()).destination(destinationPoint!!)
            .alternatives(true)
            ///driving-traffic
            ///cycling
            ///walking
            ///motorcycle
            .profile(profile).build()
        builder.getRoute(object : Callback<DirectionsResponse> {
            override fun onResponse(
                call: Call<DirectionsResponse?>, response: Response<DirectionsResponse?>,
            ) {
                if (response.body() == null || response.body()!!.routes().size < 1) {
                    return
                }
                directionsRoutes = response.body()!!.routes()
                currentRoute = if (directionsRoutes!!.size <= primaryRouteIndex) {
                    directionsRoutes!![0]
                } else {
                    directionsRoutes!![primaryRouteIndex]
                }


                // Draw the route on the map
                if (navigationMapRoute != null) {
                    navigationMapRoute?.removeRoute()
                } else {
                    navigationMapRoute = NavigationMapRoute(
                        mSurfaceRenderer.getMapView()!!,
                        vietmapGL!!,
                        "vmadmin_province"
                    )
                }

                //show multiple route to map
                if (response.body()!!.routes().size > 1) {
                    navigationMapRoute?.addRoutes(directionsRoutes!!)
                } else {
                    navigationMapRoute?.addRoute(currentRoute)
                }


                isBuildingRoute = false
                // get route point from current route
                val routePoints: List<Point> =
                    currentRoute?.routeOptions()?.coordinates() as List<Point>
                animateVietmapGLForRouteOverview(padding, routePoints)
                vietMapCarSurfaceHelper.updateOnRouteBuiltTemplate( )
                invalidate()
                //Start Navigation again from new Point, if it was already in Progress
                if (isNavigationInProgress || isStartNavigation) {
                    startNavigation()
                }
            }

            override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                isBuildingRoute = false

            }
        })
    }

    private fun animateVietmapGLForRouteOverview(padding: IntArray, routePoints: List<Point>) {
        isOverviewing = true
        if (routePoints.size <= 1) {
            return
        }
        val resetUpdate: CameraUpdate = buildResetCameraUpdate()
        val overviewUpdate: CameraUpdate = buildOverviewCameraUpdate(padding, routePoints)
        vietmapGL?.animateCamera(
            resetUpdate, 150, CameraOverviewCancelableCallback(overviewUpdate, vietmapGL)
        )
    }

    private fun buildResetCameraUpdate(): CameraUpdate {
        val resetPosition: CameraPosition = CameraPosition.Builder().tilt(0.0).bearing(0.0).build()
        return CameraUpdateFactory.newCameraPosition(resetPosition)
    }

    private fun buildOverviewCameraUpdate(
        padding: IntArray, routePoints: List<Point>,
    ): CameraUpdate {
        val routeBounds = convertRoutePointsToLatLngBounds(routePoints)
        return newLatLngBounds(
            routeBounds, padding[0], padding[1], padding[2], padding[3]
        )
    }

    private fun convertRoutePointsToLatLngBounds(routePoints: List<Point>): LatLngBounds {
        val latLngs: MutableList<LatLng> = java.util.ArrayList()
        for (routePoint in routePoints) {
            latLngs.add(LatLng(routePoint.latitude(), routePoint.longitude()))
        }
        return LatLngBounds.Builder().includes(latLngs).build()
    }


    override fun willDisplay(instructions: BannerInstructions?): BannerInstructions {
        return instructions!!
    }

    override fun onCancelNavigation() {
    }

    override fun onNavigationFinished() {
    }

    override fun onNavigationRunning() {
    }

    override fun allowRerouteFrom(offRoutePoint: Point?): Boolean {
        return true
    }

    override fun onOffRoute(offRoutePoint: com.mapbox.geojson.Point?) {
    }

    override fun onRerouteAlong(directionsRoute: DirectionsRoute?) {
    }

    override fun onFailedReroute(errorMessage: String?) {
    }

    override fun onArrival() {
        vietMapCarSurfaceHelper.initNavigationTemplate()
        invalidate()
    }

    override fun willVoice(announcement: SpeechAnnouncement?): SpeechAnnouncement {
        return announcement!!
    }

    override fun onMilestoneEvent(
        routeProgress: RouteProgress?,
        instruction: String?,
        milestone: Milestone?,
    ) {
//        playVoiceAnnouncement(milestone)
    }

    override fun onRunning(running: Boolean) {
    }

    override fun userOffRoute(location: Location?) {
    }

    override fun fasterRouteFound(directionsRoute: DirectionsRoute?) {
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onProgressChange(location: Location?, routeProgress: RouteProgress?) {

        var currentSpeed = location?.speed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            currentSpeed = location?.speedAccuracyMetersPerSecond
        }
        if (!isNavigationCanceled) {
            try {
                val noRoutes: Boolean = directionsRoutes?.isEmpty() ?: true

                val newCurrentRoute: Boolean = !routeProgress!!.directionsRoute()
                    .equals(directionsRoutes?.get(primaryRouteIndex))
                val isANewRoute: Boolean = noRoutes || newCurrentRoute
                if (isANewRoute) {
                } else {

                    distanceRemaining = routeProgress.distanceRemaining()
                    durationRemaining = routeProgress.durationRemaining()


                    if (!isDisposed && !isBuildingRoute) {
                        val snappedLocation: Location =
                            snapEngine.getSnappedLocation(location, routeProgress)

                        currentCenterPoint =
                            CurrentCenterPoint(
                                snappedLocation.latitude,
                                snappedLocation.longitude,
                                snappedLocation.bearing
                            )
                        if (!isOverviewing) {
                            this.routeProgress = routeProgress
                            if (currentSpeed!! > 0) {
                                moveCamera(
                                    LatLng(snappedLocation.latitude, snappedLocation.longitude),
                                    snappedLocation.bearing
                                )
                            }
                        }

                        vietmapGL?.locationComponent?.forceLocationUpdate(snappedLocation)
                    }

//                    if (simulateRoute && !isDisposed && !isBuildingRoute) {
//                        vietmapGL?.locationComponent?.forceLocationUpdate(location)
//                    }

                    if (!isRefreshing) {
                        isRefreshing = true
                    }
                }

                handleProgressChange(routeProgress, location!!)
            } catch (e: java.lang.Exception) {
                Log.e("onProgressChange", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    private fun moveCamera(location: LatLng, bearing: Float?) {
        val cameraPosition = CameraPosition.Builder().target(location).zoom(zoom).tilt(tilt)

        if (bearing != null) {
            cameraPosition.bearing(bearing.toDouble())
        }

        var duration = 1000
        if (!animateBuildRoute) duration = 1
        vietmapGL?.animateCamera(
            CameraUpdateFactory.newCameraPosition(cameraPosition.build()), duration
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleProgressChange(routeProgress: RouteProgress, location: Location) {
        // println("handleProgressChange")
        if (location.speed < 1) return
        // println("start handleProgressChange")

        val distanceRemainingToNextTurn =
            routeProgress.currentLegProgress()?.currentStepProgress()?.distanceRemaining()
        val turnGuideText: String =
            routeProgress.currentLegProgress()?.currentStep()?.maneuver()?.instruction().toString()

        val distanceToNextTurn =
            routeProgress.currentLegProgress()?.currentStepProgress()?.distanceRemaining().let {
                if (it != null) {
                    round(it)
                } else {
                    0.0
                }
            }
        val distanceEstimate = round(routeProgress.distanceRemaining())
        val durationEstimate = round(routeProgress.durationRemaining())
        val durationRemaining = round(routeProgress.durationRemaining())
        updateTravelEstimate(
            distanceEstimate,
            durationEstimate.toLong(),
            "Còn ${VietMapHelper.getDisplayDuration(durationRemaining / 60)}"
        )
        updateManeuver()
        updateStep(turnGuideText)

        updateRoutingInfo(distanceToNextTurn)
        if(isOverviewing) return
        if (distanceRemainingToNextTurn != null && distanceRemainingToNextTurn < 30) {
            isNextTurnHandling = true
            val resetPosition: CameraPosition =
                CameraPosition.Builder().tilt(tilt).zoom(17.0).bearing(bearing).build()
            val cameraUpdate = CameraUpdateFactory.newCameraPosition(resetPosition)
            vietmapGL?.animateCamera(
                cameraUpdate, 1000
            )
        } else {
            if (routeProgress.currentLegProgress().currentStepProgress()
                    .distanceTraveled() > 30 && !isOverviewing
            ) {
                isNextTurnHandling = false
            }
        }
    }

    fun snapLocationToClosestLatLng(targetLocation: LatLng, latLngList: List<LatLng>): LatLng? {
        var closestLatLng: LatLng? = null
        var closestDistance = Double.MAX_VALUE

        for (latLng in latLngList) {
            val distance = calculateHaversineDistance(targetLocation, latLng)
            if (distance < closestDistance) {
                closestDistance = distance
                closestLatLng = latLng
            }
        }

        return closestLatLng
    }

    private fun calculateHaversineDistance(point1: LatLng, point2: LatLng): Double {
        val lat1 = Math.toRadians(point1.latitude)
        val lon1 = Math.toRadians(point1.longitude)
        val lat2 = Math.toRadians(point2.latitude)
        val lon2 = Math.toRadians(point2.longitude)

        val dLat = lat2 - lat1
        val dLon = lon2 - lon1

        val a = sin(dLat / 2).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        // Radius of the Earth in kilometers (mean value)
        val earthRadius = 6371.0

        return earthRadius * c
    }

}