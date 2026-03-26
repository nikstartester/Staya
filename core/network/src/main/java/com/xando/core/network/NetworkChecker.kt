package com.xando.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Проверяет наличие подключения к интернету.
 */
// TODO: Возможно спрячем, чтобы в каждый репозиторий не предавать, но пока так
@Singleton
class NetworkChecker @Inject constructor(@param:ApplicationContext private val context: Context) {

    /**
     * Возвращает `true`, если устройство подключено к сети с доступом в интернет.
     */
    fun isConnected(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}