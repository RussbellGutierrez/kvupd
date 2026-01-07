import android.content.Context
import android.graphics.Color
import android.location.Location
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.upd.kvupd.utils.ALLInfoWindow
import com.upd.kvupd.utils.settingsMap

class MapHelper(
    private val inflater: LayoutInflater
) {

    private var googleMap: GoogleMap? = null
    private var myLocationEnabled = false

    private val markers = mutableListOf<Marker>()
    private val polygons = mutableListOf<Polygon>()

    // Colas para dibujar después
    private val pendingPolygons = mutableListOf<List<LatLng>>()
    private val pendingCamera = mutableListOf<() -> Unit>()

    fun attachMap(map: GoogleMap) {
        googleMap = map
        map.settingsMap()
        map.setInfoWindowAdapter(ALLInfoWindow(inflater))

        // procesar polígonos pendientes
        pendingPolygons.forEach { drawPolygon(it) }
        pendingPolygons.clear()

        // procesar movimientos de cámara pendientes
        pendingCamera.forEach { it.invoke() }
        pendingCamera.clear()
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

    // -------- MARKERS -------- //
    fun clearMarkers() {
        markers.forEach { it.remove() }
        markers.clear()
    }

    fun markerList(): List<Marker> = markers

    fun addMarker(
        data: Any,
        lat: Double,
        lng: Double,
        icon: BitmapDescriptor
    ): Marker? {
        val map = googleMap
            ?: return null

        val marker = map.addMarker(
            MarkerOptions()
                .position(LatLng(lat, lng))
                .icon(icon)
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
    fun moveCameraLatLng(lat: Double, lng: Double, zoom: Float = 15f) {
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
}