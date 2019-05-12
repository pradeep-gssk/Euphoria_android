package com.sparta.euphoria.Model

import com.google.gson.annotations.SerializedName
import com.sparta.euphoria.Generic.JSONConvertable
import java.io.Serializable

data class EUActivity(
    @SerializedName("TrtDescription")
    var trtDescription: String = "",
    @SerializedName("TrtDuration")
    var trtDuration: Int = 0,
    @SerializedName("TrtPrice")
    var trtPrice: Double = 0.0,
    @SerializedName("DiscountPrcnt")
    var discountPrcnt: Double = 0.0,
    @SerializedName("DiscountValue")
    var discountValue: Double = 0.0,
    @SerializedName("AppointmentDate")
    var appointmentDate: String = "",
    @SerializedName("AppointmentStartTime")
    var appointmentStartTime: String = "",
    @SerializedName("AppointmentEndTime")
    var appointmentEndTime: String = "",
    @SerializedName("AppointmentTherapist")
    var appointmentTherapist: String = "",
    @SerializedName("AppointmentRoom")
    var appointmentRoom: String = "",
    @SerializedName("PMSRoom")
    var pmsRoom: String = "",
    @SerializedName("ProdDescr")
    var prodDescr: String = ""
) : JSONConvertable, Serializable {
}