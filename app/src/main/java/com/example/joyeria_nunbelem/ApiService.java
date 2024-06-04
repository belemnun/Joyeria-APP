package com.example.joyeria_nunbelem;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    /*    USUARIOS     */

    @GET("/usuarios/usuarios.php")
    Call<User> getUserByName(@Query("nombre") String Nombre);

    @POST("/usuarios/usuarios.php")
    Call<Void> createUser(@Body User user);

    @PUT("/usuarios/usuarios.php")
    Call<Void> updateUser(@Body User user);

    @DELETE("/usuarios/usuarios.php")
    Call<Void> deleteUser(@Query("nombre") String Nombre);


    /*    CATEGORIAS     */

    @GET("/categorias/categorias.php")
    Call<Category> getCategoryByName(@Query("nombre") String Nombre);

    @POST("/categorias/categorias.php")
    Call<Void> createCategory(@Body Category category);

    @GET("/categorias/categorias.php") // "categories" es la parte de la URL que representa el endpoint para obtener las categorías
    Call<List<Category>> getCategories(); // Se espera que la API devuelva una lista de objetos Category

    @PUT("/categorias/categorias.php")
    Call<Void> updateCategory(@Body Category category);

    @DELETE("/categorias/categorias.php")
    Call<Void> deleteCategory(@Query("nombre") String Nombre);

    /*    PRODUCTOS     */

    @POST("/productos/productos.php")
    Call<Void> createProduct(@Body Product product);

    @GET("/productos/productos.php")
    Call<List<Product>> getProducts();

    @GET("/productos/productos.php")
    Call<Product> getProductByName(@Query("nombre") String Nombre);

    @DELETE("/productos/productos.php")
    Call<Void> deleteProduct(@Query("nombre") String Nombre);

    @PUT("/productos/productos.php")
    Call<Void> updateProduct(@Body Product product);



}


