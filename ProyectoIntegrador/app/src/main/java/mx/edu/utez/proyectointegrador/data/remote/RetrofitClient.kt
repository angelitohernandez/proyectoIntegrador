package mx.edu.utez.proyectointegrador.data.remote

import mx.edu.utez.proyectointegrador.data.remote.dto.ProductoDto
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServerConfig {
    const val DEFAULT_URL = "http://192.168.1.68:8080/"
    //const val DEFAULT_URL = "http://10.0.2.2:8080/"  //para emulador de android

    @Volatile
    var baseUrl: String = DEFAULT_URL

    @Volatile
    var token: String? = null

    fun normalizar(raw: String): String {
        var url = raw.trim()
        if (url.isEmpty()) return DEFAULT_URL
        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "http://$url"
        if (!url.endsWith("/")) url += "/"
        return url
    }
}

object RetrofitClient {
    private val dynamicInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
        ServerConfig.baseUrl.toHttpUrlOrNull()?.let { target ->
            val nuevaUrl = original.url.newBuilder()
                .scheme(target.scheme)
                .host(target.host)
                .port(target.port)
                .build()
            builder.url(nuevaUrl)
        }
        ServerConfig.token?.let { builder.header("Authorization", "Bearer $it") }
        chain.proceed(builder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(dynamicInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl("http://localhost/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    fun fotoUrl(producto: ProductoDto): String =
        "${ServerConfig.baseUrl}api/productos/${producto.id}/foto?v=${producto.fotoVersion}"
}
