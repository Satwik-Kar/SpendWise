package com.spendwise

class Date {
    private var day: String
    private var month: String
    private var year: String

    constructor(day: String, month: String, year: String) {

        this.day = day
        this.month = month
        this.year = year


    }

    fun returnYearAndMonth(): String {

        return "$year-$month"
    }


}