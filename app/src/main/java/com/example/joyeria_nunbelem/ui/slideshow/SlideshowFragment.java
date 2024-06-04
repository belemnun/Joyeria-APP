package com.example.joyeria_nunbelem.ui.slideshow;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.joyeria_nunbelem.ApiService;
import com.example.joyeria_nunbelem.Category;
import com.example.joyeria_nunbelem.Product;
import com.example.joyeria_nunbelem.R;
import com.example.joyeria_nunbelem.RetrofitClient;
import com.example.joyeria_nunbelem.databinding.FragmentGalleryBinding;
import com.example.joyeria_nunbelem.databinding.FragmentSlideshowBinding;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SlideshowFragment extends Fragment {
    private FragmentSlideshowBinding binding;
    private Spinner estadoSpinner;

    private EditText nombreCategoriaEditText;
    private EditText id_categoriaEditText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        nombreCategoriaEditText = root.findViewById(R.id.nombreCategoriaEditText);
        id_categoriaEditText = root.findViewById(R.id.consultaCategoriaEditText);

        estadoSpinner = root.findViewById(R.id.estadoSpinner);



        Button agregarButton = root.findViewById(R.id.agregarButton);
        Button eliminarButton = root.findViewById(R.id.eliminarButton);
        Button modificarButton = root.findViewById(R.id.modificarButton);
        Button consultarButton = root.findViewById(R.id.consultarButton);

        agregarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el nombre de la categoría ingresado por el usuario
                String nombre = nombreCategoriaEditText.getText().toString().trim();

                if (nombre.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el Nombre de la Categoría", Toast.LENGTH_SHORT).show();
                    nombreCategoriaEditText.requestFocus();
                    return;
                }

                // Crear un objeto Category con los datos ingresados
                Category categoria = new Category();
                categoria.setNombre(nombre);

                // Aquí puedes enviar la nueva categoría a través de Retrofit para agregarla a la API

                // Por ejemplo:
                agregarCategoria(categoria);
                limpiarCajasTexto();
            }
        });

        eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el ID de la categoría ingresado por el usuario
                String id_categoria = id_categoriaEditText.getText().toString().trim();

                if (id_categoria.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el ID de la Categoría a eliminar", Toast.LENGTH_SHORT).show();
                    id_categoriaEditText.requestFocus();
                    return;
                }

                // Por ejemplo:
                eliminarCategoria(id_categoria);
                limpiarCajasTexto();
            }
        });

        modificarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados por el usuario
                String nombre = nombreCategoriaEditText.getText().toString().trim();
                String id_categoria = id_categoriaEditText.getText().toString().trim();

                if (id_categoria.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el ID de la Categoría a modificar", Toast.LENGTH_SHORT).show();
                    id_categoriaEditText.requestFocus();
                    return;
                } else if (nombre.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el Nombre de la Categoría", Toast.LENGTH_SHORT).show();
                    nombreCategoriaEditText.requestFocus();
                    return;
                }


                // Crear un objeto Category con los datos ingresados
                Category categoria = new Category();
                categoria.setId_categoria(id_categoria);
                categoria.setNombre(nombre);

                // Aquí puedes enviar la categoría actualizada a través de Retrofit para modificarla en la API

                // Por ejemplo:
                actualizarCategoria(categoria);
                limpiarCajasTexto();
            }
        });

        consultarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el ID de la categoría ingresado por el usuario
                String nombre = id_categoriaEditText.getText().toString().trim();

                if (nombre.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el ID de la Categoría a consultar", Toast.LENGTH_SHORT).show();
                    id_categoriaEditText.requestFocus();
                    return;
                }

                // Por ejemplo:
                consultarCategoria(nombre);
            }
        });

        return root;
    }



    private void agregarCategoria(Category categoria) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.createCategory(categoria);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Manejar la respuesta aquí
                if (response.isSuccessful()) {
                    // El usuario se agregó exitosamente
                    Log.d("Exito", "Exito");
                    Toast.makeText(getActivity(), "Categoría agregada correctamente", Toast.LENGTH_SHORT).show();
                    //obtenerCategoriasDesdeApi();
                } else {
                    // Ocurrió un error al agregar el usuario
                    Toast.makeText(getActivity(), "Error al agregar la categoría", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error" + response.message() );

                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Manejar el fallo aquí
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void eliminarCategoria(String id_categoria) {

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.deleteCategory(id_categoria);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Manejar la respuesta aquí
                if (response.isSuccessful()) {
                    // La categoría se actualizó exitosamente
                    Log.d("Exito", "Exito");
                    Toast.makeText(getActivity(), "Categoria eliminada correctamente", Toast.LENGTH_SHORT).show();
                    //obtenerCategoriasDesdeApi();
                } else {
                    // Ocurrió un error al actualizar la categoría
                    Toast.makeText(getActivity(), "Error al eliminar la categoria", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Manejar el fallo aquí
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void consultarCategoria(String nombre) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Category> call = apiService.getCategoryByName(nombre);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful()) {
                    // Producto encontrado
                    Category category = response.body();
                    // Aquí puedes manejar el producto obtenido, por ejemplo, mostrarlo en los EditText
                    if (category != null) {
                        nombreCategoriaEditText.setText(category.getNombre());

                    } else {
                        Toast.makeText(getActivity(), "Categoría no encontrada", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Ocurrió un error al obtener el producto
                    Toast.makeText(getActivity(), "Error al obtener el producto", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                // Manejar el fallo aquí
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void actualizarCategoria(Category categoria) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.updateCategory(categoria);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Manejar la respuesta aquí
                if (response.isSuccessful()) {
                    // La categoría se actualizó exitosamente
                    Log.d("Exito", "Exito");
                    Toast.makeText(getActivity(), "Categoría actualizada correctamente", Toast.LENGTH_SHORT).show();
                    //obtenerCategoriasDesdeApi();
                } else {
                    // Ocurrió un error al actualizar la categoría
                    Toast.makeText(getActivity(), "Error al actualizar la categoría", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // Manejar el fallo aquí
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarCajasTexto() {
        id_categoriaEditText.setText("");
        nombreCategoriaEditText.setText("");
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}