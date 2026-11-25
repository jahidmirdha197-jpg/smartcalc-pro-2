package com.example.data.remote

import com.example.domain.tools.CurrencyData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class CurrencyRatesSnapshot(
    val ratesPerUsd: Map<String, Double>,
    val isLive: Boolean,
    val lastUpdatedLabel: String
)

object CurrencyRateService {
    suspend fun fetchLatestRates(): Result<CurrencyRatesSnapshot> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL("https://api.frankfurter.app/latest?from=USD")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
            }
            try {
                if (conn.responseCode != 200) {
                    error("HTTP ${conn.responseCode}")
                }
                val body = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(body)
                val ratesJson = json.getJSONObject("rates")
                val dateStr = json.optString("date", "Today")
                val updated = CurrencyData.defaultRatesMap.toMutableMap()
                updated["USD"] = 1.0
                val keys = ratesJson.keys()
                while (keys.hasNext()) {
                    val code = keys.next()
                    updated[code] = ratesJson.getDouble(code)
                }
                CurrencyRatesSnapshot(
                    ratesPerUsd = updated,
                    isLive = true,
                    lastUpdatedLabel = "Live ($dateStr)"
                )
            } finally {
                conn.disconnect()
            }
        }
    }
}
