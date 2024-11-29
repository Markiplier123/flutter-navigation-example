## Add below dependencies in your app:androidauto level build.gradle file

```gradle
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation "androidx.recyclerview:recyclerview:1.3.2"
    implementation "androidx.cardview:cardview:1.0.0"
    implementation "androidx.lifecycle:lifecycle-extensions:2.2.0"
    implementation "com.google.android.gms:play-services-location:21.0.1"
    implementation "com.jakewharton:butterknife:10.2.3"
    implementation 'com.github.vietmap-company:maps-sdk-navigation-ui-android:2.3.2'
    implementation 'com.github.vietmap-company:maps-sdk-navigation-android:2.3.3'
    implementation 'com.github.vietmap-company:vietmap-services-core:1.0.0'
    implementation 'com.github.vietmap-company:vietmap-services-directions-models:1.0.1'
    implementation 'com.github.vietmap-company:vietmap-services-turf-android:1.0.2'
    implementation 'com.github.vietmap-company:vietmap-services-android:1.1.2'
    implementation 'com.squareup.picasso:picasso:2.71828'
    implementation 'com.github.vietmap-company:vietmap-services-geojson-android:1.0.0'
    implementation group: 'com.squareup.okhttp3', name: 'okhttp', version: '4.10.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
```
## Create VietMapNavigationScreen.kt file
```kotlin

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Template
import vn.vietmap.services.android.navigation.ui.v5.listeners.BannerInstructionsListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.NavigationListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.RouteListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener
import vn.vietmap.services.android.navigation.v5.milestone.MilestoneEventListener
import vn.vietmap.services.android.navigation.v5.navigation.NavigationEventListener
import vn.vietmap.services.android.navigation.v5.offroute.OffRouteListener
import vn.vietmap.services.android.navigation.v5.route.FasterRouteListener
import vn.vietmap.services.android.navigation.v5.routeprogress.ProgressChangeListener
import vn.vietmap.vietmapandroidautosdk.map.VietMapAndroidAutoSurface
import vn.vietmap.vietmapsdk.maps.VietMapGL

class VietMapNavigationScreen(
    carContext: CarContext,
    private val mSurfaceRenderer: VietMapAndroidAutoSurface,
) : Screen(carContext), ProgressChangeListener,
    OffRouteListener, MilestoneEventListener, NavigationEventListener, NavigationListener,
    FasterRouteListener, SpeechAnnouncementListener, BannerInstructionsListener, RouteListener,
    VietMapGL.OnMapLongClickListener, VietMapGL.OnMapClickListener  {
    override fun onGetTemplate(): Template {
        TODO("Not yet implemented")
    }
}
```

## Create CurrentCenterPoint.kt file to save current location
```kotlin
    data class CurrentCenterPoint(var latitude:Double,var longitude:Double, var bearing:Float)
```