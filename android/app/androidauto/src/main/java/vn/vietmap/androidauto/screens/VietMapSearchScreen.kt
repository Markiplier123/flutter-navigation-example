package vn.vietmap.androidauto.screens

import android.util.Log
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.SearchTemplate
import androidx.car.app.model.Template
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import vn.vietmap.androidauto.helper.VietMapNavigationHelper

class VietMapSearchScreen(carContext: CarContext) : Screen(carContext) {
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://maps.vietmap.vn")
        .build()
    private val searchCallback = object : SearchTemplate.SearchCallback {
        override fun onSearchTextChanged(query: String) {
            // Handle search text change event here
            Log.d("VietMapSearchScreen", "onSearchTextChanged: $query")
        }

        override fun onSearchSubmitted(query: String) {
            // Handle search submit event here
            Log.d("VietMapSearchScreen", "onSearchSubmitted: $query")
        }
    }

    private var searchTemplate = SearchTemplate.Builder(searchCallback)
        .setHeaderAction(Action.BACK)
        .setSearchHint("Search")
        .setInitialSearchText("Initial search text")
        .build()
    override fun onGetTemplate(): Template {
        return searchTemplate
    }
}