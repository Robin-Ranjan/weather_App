package com.example.weatherapp

import android.annotation.SuppressLint
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import com.example.weatherapp.dataModal.WeatherAppDataModal
import com.example.weatherapp.`interface`.ApiInterface
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    //fd40867826196eda ff8010af60935725
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        // val tempX = findViewById<TextView>(R.id.txtTemp)
        fetchData("patna")
        searchCity()


    }

    private fun searchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {

                if (p0 != null) {
                    fetchData(p0)
                } else {
                    Toast.makeText(this@MainActivity, "Enter City Name", Toast.LENGTH_SHORT).show()
                }
                searchView.clearFocus()
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
            retrofit.getWeatherData(cityName, "fd40867826196edaff8010af60935725", "metric")
        response.enqueue(object : Callback<WeatherAppDataModal> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<WeatherAppDataModal>,
                response: Response<WeatherAppDataModal>
            ) {
                val reponseBody = response.body()
                if (response.isSuccessful && reponseBody != null) {
                    val temperature = reponseBody.main.temp.toString()
                    val maxTemp = reponseBody.main.temp_max.toString()
                    val minTemp = reponseBody.main.temp_min.toString()
                    val humidity = reponseBody.main.humidity.toString()
                    val windSpeed = reponseBody.wind.speed.toString()
                    val sunRise = reponseBody.sys.sunrise.toLong()
                    val sunSet = reponseBody.sys.sunset.toLong()
                    val seeLevel = reponseBody.main.pressure.toString()
                    val conditions = reponseBody.weather.firstOrNull()?.main ?: "unknown"


                    // tempX.text = temperature
                    binding.txtTemp.text = "$temperature°C"
                    binding.txtMaxTemp.text = "Max Temp: $maxTemp°C"
                    binding.txtMinTemp.text = "Min Temp: $minTemp°C"
                    binding.txtHumidity.text = "$humidity%"
                    binding.txtWindSpeed.text = "$windSpeed m/s"
                    binding.txtSunrise.text = "${time(sunRise)}"
                    binding.txtSunset.text = "${time(sunSet)}"
                    binding.txtSeelevel.text = "$seeLevel hPa"
                    binding.txtCondition.text = conditions
                    binding.txtWeather.text = conditions
                    binding.txtDay.text = dayName(System.currentTimeMillis())
                    binding.txtDate.text = date()
                    binding.txtCityName.text = cityName
                    changeTHeBackground(conditions)
                }

            }

            override fun onFailure(call: Call<WeatherAppDataModal>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    " Check Your Internet Connection",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })

    }

    private fun changeTHeBackground(conditions: String) {
        when (conditions) {
            "Haze", "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottyBackground.setAnimation(R.raw.cloud_lotty_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud_lotty)
            }

            "Clear Sky", "Sunny", "Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sunny_lotty)
                binding.lottyBackground.setAnimation(R.raw.summer_lotty_background)
            }

            "Light Rain", "Drizzle", "Moderate Rain", "Showers","rain", "Heavy Rain" -> {
                Toast.makeText(this@MainActivity,"Rain",Toast.LENGTH_SHORT).show()
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottyBackground.setAnimation(R.raw.rainy_lotty_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain_lotty)
            }

            "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
                binding.lottyBackground.setAnimation(R.raw.rainy_lotty_background)

            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sunny_lotty)
                binding.lottyBackground.setAnimation(R.raw.summer_lotty_background)
            }
        }
        binding.lottieAnimationView.playAnimation()
        binding.lottyBackground.playAnimation()
    }

    private fun date(): String? {

        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }


    fun dayName(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:MM", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
}