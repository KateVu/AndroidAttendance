package com.katevu.attendance.data.model

//data class Attendance(
//        var token: String,
//        var userId: String?,
//        var nfcId: String,
//        var date: String,
//)

data class Attendance(
        var studentID: String,
        var dateTime: String,
        var nfcID: String,
        var activityID: String,
        var phoneId: String

)


//{
//        "studentID": "23213",
//        "dateTime": "ml",
//        "nfcID": "nfcID"
//}