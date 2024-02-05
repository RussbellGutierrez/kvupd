package com.upd.kvupd.domain

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationClient {
    fun getLocationUpdates(normal: Long, fast: Long, distancia: Float): Flow<Location>
    class LocationException(message: String) : Exception()
}