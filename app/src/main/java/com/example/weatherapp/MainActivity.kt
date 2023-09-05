package com.example.weatherapp

import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import com.example.weatherapp.DataModal.WeatherAppDataModal
import com.example.weatherapp.Interface.ApiInterface
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //fd40867826196edaff8010af60935725
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // val tempX = findViewById<TextView>(R.id.txtTemp)
        fetchData("jaipur")
        searchCity()


    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {

                if (p0!=null){
                    fetchData(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    fun fetchData(cityName: String) {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response =
            retrofit.getWeatherData("jaipur", "fd40867826196edaff8010af60935725", "metric")
        response.enqueue(object : Callback<WeatherAppDataModal> {
            override fun onResponse(
                call: Call<WeatherAppDataModal>,
                response: Response<WeatherAppDataModal>
            ) {
                val reponseBody = response.body()
                if (response.isSuccessful && reponseBody != null) {
                    val temperature = reponseBody.main.temp.toString()
                    val maxTemp = reponseBody.main.temp_max.toString()
                    val minTemp = reponseBody.main.temp_min.toString()
                    val humidity = reponseBody.main.temp.toString()
                    val windSpeed = reponseBody.wind.speed.toString()
                    val sunRise = reponseBody.sys.sunrise.toString()
                    val sunSet = reponseBody.sys.sunset.toString()
                    val seeLevel = reponseBody.main.pressure.toString()
                    val condition = reponseBody.weather.firstOrNull()?.main ?: "unknown"


                    // tempX.text = temperature
                    binding.txtTemp.text = "$temperature°C";
                    binding.txtmaxTemp.text = "Max Temp: $maxTemp°C"
                    binding.txtMinTemp.text = "Min Temp: $minTemp°C"
                    binding.txtHumiduty.text = "$humidity%"
                    binding.txtWindSpeed.text = "$windSpeed m/s"
                    binding.txtSunrise.text = sunRise
                    binding.txtSunset.text = sunSet
                    binding.txtSealevel.text = "$seeLevel hPa"
                    binding.txtCondition.text = condition
                    binding.txtDay.text =dayName(System.currentTimeMillis())
                    binding.txtDate.text =date()
                    binding.txtCityName.text = "$cityName"
                }

            }

            override fun onFailure(call: Call<WeatherAppDataModal>, t: Throwable) {
                Toast.makeText(this@MainActivity," Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }

        })

    }

    private fun date(): String? {

        val sdf= SimpleDateFormat("dd MMMM YYY", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun dayName(timestamp:Long):String{
        val sdf= SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
}