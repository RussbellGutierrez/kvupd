package com.upd.kvupd.domain

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface OldLocationClient {
    fun getLocationUpdates(normal: Long, fast: Long, distancia: Float): Flow<Location>
    class LocationException(message: String) : Exception()
}