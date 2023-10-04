package com.spendwise

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class FlightApiController() {

    private lateinit var context: Context

    constructor(context: Context) : this() {
        this.context = context
    }

    fun fetchFlightData(departureDate: Date, returnDate: Date, destination: String) {
        val currency =
            context.getSharedPreferences("credentials", AppCompatActivity.MODE_PRIVATE)
                .getString("country", null)

        val countryShort = Constants.getMapShort()[currency]

        Log.e("TAG", "fetchFlightData: ${departureDate.returnYearAndMonth()}")

        val api_request_url =
            "https://api.travelpayouts.com/v1/prices/cheap?origin=MOW&destination=BCN&depart_date=${departureDate.returnYearAndMonth()}&return_date=${returnDate.returnYearAndMonth()}&page=1&currency=$countryShort&token=" +
                    Constants.flight_data_access_token
        val requestQueue = Volley.newRequestQueue(context)
        //SIMPLE DATE FORMAT

        val jsonObjectRequest = JsonObjectRequest(

            Request.Method.GET, api_request_url, null,
            { response ->

                println(response)
            },
            { error ->

                println(error)
            }
        )

        requestQueue.add(jsonObjectRequest)


    }

}