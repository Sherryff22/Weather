package ru.sherry.test.weather

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Button
import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import okhttp3.HttpUrl

class MainActivity : AppCompatActivity(), View.OnClickListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    val tempArray: Array<String> =
        arrayOf("Температура (°C) = \nНет данных", "Ощущаемая температура (°C) = \nНет данных")


    //локация

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //создали кнопку и слушаем нажатие
        var requestButton = findViewById<Button>(R.id.button)
        requestButton.setOnClickListener(this)

        // use a linear layout manager
        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(tempArray)
        recyclerView = findViewById<RecyclerView>(R.id.outputView).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = viewManager
            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }


    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.button -> {
                val okHttpHandler = OkHttpHandler()
                okHttpHandler.execute()
            }
        }
    }


    //кастом хендлера
    inner class OkHttpHandler : AsyncTask<String, Void, String>() {

        private val client = OkHttpClient.Builder()
            .addInterceptor(LoggingInterceptor())
            .build()

        override fun doInBackground(vararg params: String): String? {



            val url = HttpUrl.Builder()
                .scheme("https")
                .host("api.weather.yandex.ru")
                .addPathSegment("v1")
                .addPathSegment("forecast")
                .addQueryParameter("lat", "56.85836")
                .addQueryParameter("lon", "35.90057")
                .addQueryParameter("extra", "false")
                .build()

            val builder = Request.Builder()
            builder.url(url)
                .get()
                .addHeader("x-yandex-api-key", "b193ae3e-0f29-4e62-a412-5a1bd80c83a4")

            val request = builder.build()

            try {
                val response = client.newCall(request).execute()
                return response.body()?.string()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPostExecute(s: String) {

            super.onPostExecute(s)
            var jobjectAll = JSONObject(s)
            var outAll = jobjectAll.get("fact")
            var jobjectFact = JSONObject(outAll.toString())
            var outFact = jobjectFact.get("temp")
            var outFeelsLike = jobjectFact.get("feels_like")

            Log.i("TAG", "значение из JSON = $outFact")
            //      Log.i("TAG", "значение из JSON = $outFeelsLike")

            tempArray[0] = "Температура (°C) = \n$outFact"
            tempArray[1] = "Ощущаемая температура (°C) = \n$outFeelsLike"

            Log.i("TAG", "1 значение в списке = " + tempArray[0])
            Log.i("TAG", "2 значение в списке = " + tempArray[1])
            // specify an viewAdapter (see also next example)


            recyclerView.adapter = MyAdapter(tempArray)


        }
    }

    internal class LoggingInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            val t1 = System.nanoTime()
            Log.i(
                "TAG",
                String.format(
                    "Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()
                )
            )

            val response = chain.proceed(request)

            val t2 = System.nanoTime()
            Log.i(
                "TAG",
                String.format(
                    "Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6, response.headers()
                )
            )

            return response
        }
    }


}
