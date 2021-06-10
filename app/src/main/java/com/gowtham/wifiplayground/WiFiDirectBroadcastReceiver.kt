package com.gowtham.wifiplayground

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager

class WiFiDirectBroadcastReceiver(
    val manager: WifiP2pManager?,
    val channel: WifiP2pManager.Channel,
    val mainActivity: MainActivity
) : BroadcastReceiver() {

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {

        val action: String = intent.action ?: "NO ACTION"
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
                manager?.requestPeers(channel) { peers: WifiP2pDeviceList? ->
                    // Handle peers list
                    LogMessage.v("peer list ${peers?.deviceList}")
                }
                when (state) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        // Wifi P2P is enabled
                        LogMessage.v("Wifi P2P is enabled")
                    }
                    else -> {
                        LogMessage.v("Wi-Fi P2P is not enabled")
                        // Wi-Fi P2P is not enabled
                    }
                }
            }
        }

    }
}