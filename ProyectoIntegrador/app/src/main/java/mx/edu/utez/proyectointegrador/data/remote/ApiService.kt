package mx.edu.utez.proyectointegrador.data.remote

import mx.edu.utez.proyectointegrador.data.remote.dto.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/productos")
    suspend fun productos(): Response<List<ProductoDto>>

    @Multipart
    @POST("api/productos")
    suspend fun crearProducto(
        @Part("producto") producto: RequestBody,
        @Part foto: MultipartBody.Part?
    ): Response<ProductoDto>

    @Multipart
    @PUT("api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Part("producto") producto: RequestBody,
        @Part foto: MultipartBody.Part?
    ): Response<ProductoDto>

    @DELETE("api/productos/{id}")
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Unit>

    @GET("api/mermas")
    suspend fun mermas(): Response<List<MermaDto>>

    @POST("api/mermas")
    suspend fun crearMerma(@Body request: MermaRequest): Response<MermaDto>

    @PUT("api/mermas/{id}")
    suspend fun actualizarMerma(@Path("id") id: Long, @Body request: MermaRequest): Response<MermaDto>

    @DELETE("api/mermas/{id}")
    suspend fun eliminarMerma(@Path("id") id: Long): Response<Unit>

    @GET("api/usuarios")
    suspend fun usuarios(): Response<List<UsuarioDto>>

    @POST("api/usuarios")
    suspend fun crearUsuario(@Body request: UsuarioRequest): Response<UsuarioDto>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Long, @Body request: UsuarioRequest): Response<UsuarioDto>

    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long): Response<Unit>
}
