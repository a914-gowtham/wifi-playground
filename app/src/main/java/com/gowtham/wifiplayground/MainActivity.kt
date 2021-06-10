package com.gowtham.wifiplayground

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.annotations.AfterPermissionGranted
import java.util.jar.Manifest

class MainActivity : AppCompatActivity(), WifiP2pManager.PeerListListener {

    val manager: WifiP2pManager? by lazy(LazyThreadSafetyMode.NONE) {
        getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager?
    }

    var channel: WifiP2pManager.Channel? = null
    var receiver: BroadcastReceiver? = null

    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        channel = manager?.initialize(this, mainLooper) {
            LogMessage.v("Disconnected....")
        }
        channel?.also { channel ->
            receiver = WiFiDirectBroadcastReceiver(manager, channel, this)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        findViewById<Button>(R.id.btn_discover).setOnClickListener {
            checkPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        receiver?.also { receiver ->
            registerReceiver(receiver, intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()
        receiver?.also { receiver ->
            unregisterReceiver(receiver)
        }
    }

    override fun onPeersAvailable(peers: WifiP2pDeviceList?) {
        LogMessage.v("onPeersAvailable ${peers?.deviceList}")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(232)
    fun checkPermission() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION,
            )
        ) {
            manager?.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    manager?.requestPeers(channel, this@MainActivity)
                    LogMessage.v("Peers discovered success")
                }

                override fun onFailure(reasonCode: Int) {
                    LogMessage.v("Peers discovered failed $reasonCode")
                }
            })
        } else {
            EasyPermissions.requestPermissions(
                host = this,
                rationale = "Location permission is required to continue",
                requestCode = 232,
                perms = arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                )
            )
        }
    }
}