package com.katevu.attendance.data.model

import com.squareup.moshi.Json

data class StudentActivity(
    @Json(name = "_id")
    val id: String,
    val type: String,
    val startTime: String,
    val endTime: String,
    var roomId: String,
    var nfcId: String,
    var unitID: String,
)

data class ListActivities(
    val message: String,
    val data: List<StudentActivity>
)

data class GetActivitiesResult(
    val success: ListActivities? = null,
    val error: Int? = null
)

//{
//    "attendants": [],
//    "_id": "6099958f826fc111ba7c02b2",
//    "day": "Tuesday",
//    "type": "Lecture",
//    "startTime": "2021-10-05T12:48:00.000Z",
//    "endTime": "2021-10-05T14:48:00.000Z",
//    "roomId": "EN205",
//    "nfcId": "en222nfc",
//    "unitID": "ICT2010",
//    "activitySize": 50,
//    "__v": 0
//},
