package gaur.himanshu.gpstracker.service

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import gaur.himanshu.gpstracker.R
import kotlinx.coroutines.flow.channelFlow

class LocationService : Service() {

    private val TAG=javaClass.simpleName

    private val locationRequest by lazy {
        Log.d(TAG,"LocationRequest initilzed")
        LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000).setIntervalMillis(1000).build()
    }


    private val locationCallBack by lazy {
        object : LocationCallback(){
            override fun onLocationResult(location: LocationResult) {
                val lattitute=location.lastLocation?.latitude.toString()
                val longitude=location.lastLocation?.longitude.toString()
                Log.d(TAG, "onLocationResult: $lattitute, $longitude")
                startServiceofForground(lattitute,longitude)
            }

            override fun onLocationAvailability(location: LocationAvailability) {

            }
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        Log.d(TAG,"Onstart Command")
        updateLocation()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun updateLocation(){
        Log.d(TAG,"update Location Called :: updateLocation()")
        val fusedLocationClient= LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallBack,
            null
        )
    }

    private fun startServiceofForground(lat:String,lng:String){
        //Build the Notification
        Log.d(TAG,"called startServiceofForground()")
        val notification= NotificationCompat.Builder(this, "channel_id")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Location Updates")
            .setContentText("$lat, $lng")
            .build()









        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU){

            if (ContextCompat.checkSelfPermission(
                this,
                POST_NOTIFICATIONS
            )  == PackageManager.PERMISSION_GRANTED){
                Log.d(TAG,"Permission already Granted")
                //Then only we will showw the Notification
                startForeground(1,notification)
            }else{
                Log.d(TAG,"Permission is Not Granted")
                startForeground(1,notification)
            }





        }
    }
}