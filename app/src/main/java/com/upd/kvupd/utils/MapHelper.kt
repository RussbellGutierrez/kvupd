import android.graphics.Color
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.upd.kvupd.ui.fragment.type.MapData
import com.upd.kvupd.utils.MapInfoWindow
import com.upd.kvupd.utils.settingsMap

class MapHelper(
    private val inflater: LayoutInflater
) {
    interface OnInfoWindowClickListener<T : MapData> {
        fun onClick(data: T)
    }

    interface OnMarkerMovedListener<T : MapData> {
        fun onMoved(data: T, position: LatLng)
    }

    private var infoWindowClickListener: ((MapData) -> Unit)? = null

    private var googleMap: GoogleMap? = null
    private var myLocationEnabled = false

    private val markers = mutableListOf<Marker>()
    private val polygons = mutableListOf<Polygon>()
    private var markerMovedListener: ((MapData, LatLng) -> Unit)? = null

    // Colas para dibujar o ejecutar después
    private var pendingFocus: MapData? = null
    private val pendingPolygons = mutableListOf<List<LatLng>>()
    private val pendingCamera = mutableListOf<() -> Unit>()

    fun attachMap(map: GoogleMap) {
        googleMap = map
        map.settingsMap()
        map.setInfoWindowAdapter(MapInfoWindow(inflater))

        map.setOnMarkerClickListener { marker ->
            val data = marker.tag as? MapData
                ?: return@setOnMarkerClickListener false

            focus(data)
            true
        }

        map.setOnInfoWindowClickListener { marker ->
            val data = marker.tag as? MapData
                ?: return@setOnInfoWindowClickListener

            infoWindowClickListener?.invoke(data)
        }

        map.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {

            override fun onMarkerDragStart(marker: Marker) {
                marker.alpha = 0.7f
                marker.setAnchor(0.5f, 1.2f) // pequeño efecto de elevación
            }

            override fun onMarkerDrag(marker: Marker) {}

            override fun onMarkerDragEnd(marker: Marker) {

                marker.alpha = 1f
                marker.setAnchor(0.5f, 1f)

                val data = marker.tag as? MapData ?: return

                markerMovedListener?.invoke(data, marker.position)
            }
        })

        // procesar polígonos pendientes
        pendingPolygons.forEach { drawPolygon(it) }
        pendingPolygons.clear()

        // procesar movimientos de cámara pendientes
        pendingCamera.forEach { it.invoke() }
        pendingCamera.clear()
    }

    fun <T : MapData> setOnMarkerMovedListener(
        clazz: Class<T>,
        listener: OnMarkerMovedListener<T>
    ) {
        markerMovedListener = { data, pos ->
            if (clazz.isInstance(data)) {
                @Suppress("UNCHECKED_CAST")
                listener.onMoved(data as T, pos)
            }
        }
    }

    fun <T : MapData> setOnInfoWindowClickListener(
        clazz: Class<T>,
        listener: OnInfoWindowClickListener<T>
    ) {
        infoWindowClickListener = { data ->
            if (clazz.isInstance(data)) {
                @Suppress("UNCHECKED_CAST")
                listener.onClick(data as T)
            }
        }
    }

    fun enableMyLocation() {
        val map = googleMap ?: return
        if (myLocationEnabled) return

        try {
            map.isMyLocationEnabled = true
            myLocationEnabled = true
        } catch (e: SecurityException) {
            // permiso no otorgado
        }
    }

    fun disableMyLocation() {
        val map = googleMap ?: return
        if (!myLocationEnabled) return

        try {
            map.isMyLocationEnabled = false
            myLocationEnabled = false
        } catch (e: SecurityException) {
            // ignore
        }
    }

    fun changeMarkerIcon(
        data: MapData,
        icon: BitmapDescriptor
    ) {
        val marker = markers.firstOrNull {
            (it.tag as? MapData)?.mapId == data.mapId
        } ?: return

        marker.setIcon(icon)
    }

    // -------- MARKERS -------- //
    fun clearMarkers() {
        markers.forEach { it.remove() }
        markers.clear()
    }

    fun addMarker(
        data: MapData,
        lat: Double,
        lng: Double,
        icon: BitmapDescriptor,
        movable: Boolean = false
    ): Marker? {
        val map = googleMap
            ?: return null

        val marker = map.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
                .icon(icon)
                .draggable(movable)
        ) ?: return null

        marker.tag = data
        markers.add(marker)
        return marker
    }

    // -------- POLYGONS -------- //
    fun drawPolygon(
        latLngs: List<LatLng>,
        strokeColor: Int = Color.parseColor("#D01215"),
        fillColor: Int = Color.argb(102, 118, 131, 219)
    ) {
        if (latLngs.isEmpty()) return

        val map = googleMap
        if (map == null) {
            pendingPolygons.add(latLngs)
            return
        }

        val poly = map.addPolygon(
            PolygonOptions()
                .addAll(latLngs)
                .strokeWidth(2f)
                .strokeColor(strokeColor)
                .fillColor(fillColor)
        )

        polygons.add(poly)
    }

    fun clearPolygons() {
        polygons.forEach { it.remove() }
        polygons.clear()
    }

    // -------- CAMERA -------- //
    private fun moveCameraLatLng(lat: Double, lng: Double, zoom: Float = 15f) {
        val map = googleMap

        if (map == null) {
            pendingCamera.add { moveCameraLatLng(lat, lng, zoom) }
            return
        }

        val block = {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), zoom)
            )
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            block()
        } else {
            Handler(Looper.getMainLooper()).post {
                block()
            }
        }
    }

    fun moveCamera(location: Location, zoom: Float = 15f) {
        moveCameraLatLng(location.latitude, location.longitude, zoom)
    }

    fun centerMarkers(includeLocation: LatLng? = null) {
        val map = googleMap

        if (map == null) {
            pendingCamera.add { centerMarkers(includeLocation) }
            return
        }

        if (markers.isEmpty() && includeLocation == null) return

        val builder = LatLngBounds.Builder()
        markers.forEach { builder.include(it.position) }
        includeLocation?.let { builder.include(it) }

        val bounds = builder.build()

        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, 200)
        )
    }

    fun centerMarkersWithMaxZoom(
        includeLocation: LatLng? = null,
        padding: Int = 200,
        maxZoom: Float = 15f
    ) {
        val map = googleMap

        if (map == null) {
            pendingCamera.add { centerMarkersWithMaxZoom(includeLocation, padding, maxZoom) }
            return
        }

        if (markers.isEmpty() && includeLocation == null) return

        val builder = LatLngBounds.Builder()
        markers.forEach { builder.include(it.position) }
        includeLocation?.let { builder.include(it) }

        val bounds = builder.build()

        map.animateCamera(
            CameraUpdateFactory.newLatLngBounds(bounds, padding),
            object : GoogleMap.CancelableCallback {
                override fun onFinish() {
                    val currentZoom = map.cameraPosition.zoom
                    if (currentZoom > maxZoom) {
                        map.animateCamera(
                            CameraUpdateFactory.zoomTo(maxZoom)
                        )
                    }
                }

                override fun onCancel() {}
            }
        )
    }

    fun resolvePendingFocus() {
        val data = pendingFocus ?: return
        pendingFocus = null
        focus(data)
    }

    fun <T> getMarkerData(clazz: Class<T>): List<T> =
        markers.mapNotNull { clazz.cast(it.tag) }

    fun focus(
        data: MapData,
        zoom: Float = 18f,
        showInfo: Boolean = true
    ) {
        val map = googleMap
        if (map == null || markers.isEmpty()) {
            pendingFocus = data
            return
        }

        val marker = markers.firstOrNull {
            (it.tag as? MapData)?.mapId == data.mapId
        } ?: return

        map.animateCamera(
            CameraUpdateFactory.newLatLngZoom(marker.position, zoom)
        )

        if (showInfo) marker.showInfoWindow()
    }

    fun hideInfoWindow(data: MapData) {
        val marker = markers.firstOrNull {
            (it.tag as? MapData)?.mapId == data.mapId
        } ?: return
        marker.hideInfoWindow()
    }

    fun getMap(): GoogleMap? = googleMap
}