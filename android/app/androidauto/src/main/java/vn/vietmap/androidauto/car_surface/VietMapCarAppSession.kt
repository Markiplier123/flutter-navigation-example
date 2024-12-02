package vn.vietmap.androidauto.car_surface

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import vn.vietmap.androidauto.screens.VietMapNavigationScreen
import vn.vietmap.vietmapsdk.Vietmap

class VietMapCarAppSession: Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        Vietmap.getInstance(carContext)
        val mNavigationCarSurface = VietMapAndroidAutoSurface(carContext, lifecycle)
//        val screenMap = VietMapCarAppScreen(carContext,mNavigationCarSurface)
//        return screenMap
        return VietMapNavigationScreen(carContext,mNavigationCarSurface)
    }
}