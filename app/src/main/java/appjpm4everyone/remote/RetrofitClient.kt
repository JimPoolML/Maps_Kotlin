package appjpm4everyone.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


private const val TIME_OUT: Long = 60

object RetrofitClient {
    private var retrofit: Retrofit?=null

    //Interceptor to show every Request and response
    private fun setHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val originalRequest: Request = chain.request()
                val newRequest: Request =
                    originalRequest.newBuilder()
                        .header("Accept", "application/json")
                        .build()
                chain.proceed(newRequest)
            })
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
    }

    //To add headers
    private fun clientHeaders(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.apply { loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY }
        val httpClientBuilder: OkHttpClient.Builder = setHttpClient(loggingInterceptor)
        return httpClientBuilder.build()
    }

    //Create Retrofit Client
    fun getClient(baseUrl: String) : Retrofit{
        if(retrofit==null){
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(clientHeaders())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}