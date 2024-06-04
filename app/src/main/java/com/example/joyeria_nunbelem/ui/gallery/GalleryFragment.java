package com.example.joyeria_nunbelem.ui.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.joyeria_nunbelem.ApiService;
import com.example.joyeria_nunbelem.Category;
import com.example.joyeria_nunbelem.Product;
import com.example.joyeria_nunbelem.R;
import com.example.joyeria_nunbelem.RetrofitClient;
import com.example.joyeria_nunbelem.databinding.FragmentGalleryBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GalleryFragment extends Fragment {

    private static final int PICK_IMAGE = 1;
    private FragmentGalleryBinding binding;
    private EditText id_productoEditText;
    private EditText nombreEditText;
    private EditText precioEditText;
    private EditText descripcionEditText;
    private EditText existenciaEditText;
    private ImageView imageView;
    private EditText url_imagen;

    private Spinner estadoSpinner;
    private int categoriaSpinner;


    public static final int RESULT_OK = -1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        id_productoEditText = root.findViewById(R.id.consultaEditText);
        nombreEditText = root.findViewById(R.id.nombreEditText);
        precioEditText = root.findViewById(R.id.precioEditText);
        descripcionEditText = root.findViewById(R.id.descripcionEditText);
        existenciaEditText = root.findViewById(R.id.existenciaEditText);
        url_imagen = root.findViewById(R.id.url_imagen);
        imageView = root.findViewById(R.id.imageView);

        estadoSpinner = root.findViewById(R.id.estadoSpinner);

        obtenerCategoriasDesdeApi();

        // Define una lista de elementos para el Spinner
        String[] opciones = {"Opción 1", "Opción 2", "Opción 3"};

        // Crea un adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, opciones);

        // Especifica el diseño a usar cuando se despliega el Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Aplica el adaptador al Spinner
        estadoSpinner.setAdapter(adapter);

        Button selectImageButton = root.findViewById(R.id.selectImageButton);
        Button agregarButton = root.findViewById(R.id.agregarButton);
        Button eliminarButton = root.findViewById(R.id.eliminarButton);
        Button modificarButton = root.findViewById(R.id.modificarButton);
        Button consultarButton = root.findViewById(R.id.consultarButton);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        agregarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarProducto();
            }
        });

        modificarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificarProducto();
            }
        });

        eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eliminarProducto();
            }
        });

        consultarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el ID del producto ingresado por el usuario
                String nombre = id_productoEditText.getText().toString().trim();

                // Validar si el campo está vacío
                if (nombre.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el ID del producto a consultar", Toast.LENGTH_SHORT).show();
                    id_productoEditText.requestFocus();
                    return;
                }

                // Por ejemplo:
                consultarProducto(nombre);
            }
        });


        return root;
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    private String copiarImagenInterna(Uri uriImagen) {
        String nombreArchivo = "imagen_" + System.currentTimeMillis() + ".jpg";
        File directorio = new File(requireContext().getFilesDir(), "imagenes");
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        File archivoDestino = new File(directorio, nombreArchivo);

        try (InputStream inputStream = requireContext().getContentResolver().openInputStream(uriImagen);
             OutputStream outputStream = new FileOutputStream(archivoDestino)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return archivoDestino.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            String rutaImagen = copiarImagenInterna(imageUri);
            if (rutaImagen != null) {
                Picasso.get().load(new File(rutaImagen)).into(imageView);
                url_imagen.setText(rutaImagen);
            } else {
                Toast.makeText(getActivity(), "Error al copiar la imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void agregarProducto() {
        String nombre = nombreEditText.getText().toString().trim();
        String precioStr = precioEditText.getText().toString().trim();
        String existenciaStr = existenciaEditText.getText().toString().trim();
        String descripcion = descripcionEditText.getText().toString().trim();
        String url = url_imagen.getText().toString().trim();

        if (!validarCampos(nombre, precioStr, existenciaStr, descripcion)) {
            return;
        }

        double precio = Double.parseDouble(precioStr);
        int existencia = Integer.parseInt(existenciaStr);

        Product producto = new Product();
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setDescripcion(descripcion);
        producto.setExistencia(existencia);
        producto.setId_categoria(categoriaSpinner); // Aquí se establece el ID de la categoría seleccionada
        producto.setImagen(url);

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.createProduct(producto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Producto agregado correctamente", Toast.LENGTH_SHORT).show();
                    limpiarCajasTexto();
                } else {
                    Toast.makeText(getActivity(), "Error al agregar producto", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void modificarProducto() {
        String idProducto = id_productoEditText.getText().toString().trim();
        String nombre = nombreEditText.getText().toString().trim();
        String precioStr = precioEditText.getText().toString().trim();
        String existenciaStr = existenciaEditText.getText().toString().trim();
        String descripcion = descripcionEditText.getText().toString().trim();
        String url = url_imagen.getText().toString().trim();

        if (!validarCampos(idProducto, nombre, precioStr, existenciaStr, descripcion)) {
            return;
        }

        double precio = Double.parseDouble(precioStr);
        int existencia = Integer.parseInt(existenciaStr);

        Product producto = new Product();
        producto.setId_producto(idProducto);
        producto.setNombre(nombre);
        producto.setPrecio(precio);
        producto.setDescripcion(descripcion);
        producto.setExistencia(existencia);
        producto.setId_categoria(categoriaSpinner); // Aquí se establece el ID de la categoría seleccionada
        producto.setImagen(url);

        Log.d("Producto", producto.getId_producto());
        Log.d("Producto", producto.getNombre());
        Log.d("Producto", producto.getDescripcion());
        Log.d("Producto", String.valueOf(producto.getId_categoria()));
        Log.d("Producto", String.valueOf(producto.getPrecio()));
        Log.d("Producto", producto.getImagen());

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.updateProduct(producto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show();
                    limpiarCajasTexto();
                } else {
                    Toast.makeText(getActivity(), "Error al actualizar el producto", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void eliminarProducto() {
        String idProducto = id_productoEditText.getText().toString().trim();

        if (idProducto.isEmpty()) {
            Toast.makeText(getActivity(), "Por favor, ingresa el ID del producto", Toast.LENGTH_SHORT).show();
            return;
        }


        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.deleteProduct(idProducto);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Producto eliminado correctamente", Toast.LENGTH_SHORT).show();
                    limpiarCajasTexto();
                } else {
                    Toast.makeText(getActivity(), "Error al eliminar el producto", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void consultarProducto(String nombre) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Product> call = apiService.getProductByName(nombre);
        call.enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    Product producto = response.body();
                    if (producto != null) {
                        // Producto encontrado, llenar los campos del formulario
                        nombreEditText.setText(producto.getNombre());
                        precioEditText.setText(String.valueOf(producto.getPrecio()));
                        descripcionEditText.setText(producto.getDescripcion());
                        existenciaEditText.setText(String.valueOf(producto.getExistencia()));
                        url_imagen.setText(producto.getImagen());

                        // Mostrar la imagen si está disponible
                        String imageUrl = producto.getImagen();
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            // Check if the imageUrl is a local file path or a remote URL
                            if (imageUrl.startsWith("http")) {
                                // It's a remote URL, load it with Picasso or Glide
                                Picasso.get().load(imageUrl).into(imageView);
                                // Or use Glide
                                // Glide.with(getContext()).load(imageUrl).into(imageView);
                            } else {
                                // It's a local file path, load it with Picasso or Glide
                                Picasso.get().load(new File(imageUrl)).into(imageView);
                                // Or use Glide
                                // Glide.with(getContext()).load(new File(imageUrl)).into(imageView);
                            }
                        } else {
                            imageView.setImageResource(R.drawable.ic_menu_gallery);
                        }

                    } else {
                        // Producto no encontrado, limpiar los campos y mostrar un mensaje de error
                        limpiarCajasTexto();
                        Toast.makeText(getActivity(), "Producto no encontrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Respuesta no exitosa del servidor
                    Toast.makeText(getActivity(), "Error al obtener el producto: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                // Error de conexión
                Toast.makeText(getActivity(), "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }





    private void limpiarCajasTexto() {
        id_productoEditText.setText("");
        nombreEditText.setText("");
        precioEditText.setText("");
        descripcionEditText.setText("");
        existenciaEditText.setText("");
        url_imagen.setText("");
        imageView.setImageResource(R.drawable.ic_menu_gallery);
    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void obtenerCategoriasDesdeApi() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Category>> call = apiService.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful()) {
                    List<Category> categorias = response.body();
                    if (categorias != null && !categorias.isEmpty()) {
                        cargarCategoriasEnSpinner(categorias);
                    } else {
                        // No se encontraron categorías
                    }
                } else {
                    // Respuesta no exitosa
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                // Error de conexión
            }
        });
    }

    private void cargarCategoriasEnSpinner(List<Category> categorias) {
        List<String> nombresCategorias = new ArrayList<>();
        for (Category categoria : categorias) {
            nombresCategorias.add(categoria.getNombre()); // Agrega el nombre de la categoría al Spinner
        }

        // Crear un adaptador para el Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item, nombresCategorias);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Asignar el adaptador al Spinner
        estadoSpinner.setAdapter(adapter);

        // Manejar la selección del Spinner
        estadoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Aquí puedes obtener el ID de la categoría seleccionada
                Category categoriaSeleccionada = categorias.get(position);
                int idCategoriaSeleccionada = Integer.parseInt(categoriaSeleccionada.getId_categoria());
                categoriaSpinner = idCategoriaSeleccionada;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Manejar la situación en la que no se ha seleccionado nada en el Spinner
            }
        });
    }



    private boolean validarCampos(String... campos) {
        for (String campo : campos) {
            if (campo.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }
}
