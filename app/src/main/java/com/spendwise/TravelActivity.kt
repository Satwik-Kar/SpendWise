package com.spendwise

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.util.Date

class TravelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel)
        var date = Date()



        FlightApiController(applicationContext).fetchFlightData(
            Date(
                "00",
                "11",
                "2023"
            ), Date("00", "12", "2023"), "India"
        )


    }


}