package com.example.joyeria_nunbelem.ui.home;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.joyeria_nunbelem.ApiService;
import com.example.joyeria_nunbelem.Product;
import com.example.joyeria_nunbelem.R;
import com.example.joyeria_nunbelem.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private List<Product> productList;
    private List<Product> filteredProductList;
    private GridLayout gridLayout;
    private EditText searchEditText;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        gridLayout = root.findViewById(R.id.gridProductos);
        searchEditText = root.findViewById(R.id.search_edit_text);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No es necesario implementar este método
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarProductos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No es necesario implementar este método
            }
        });

        obtenerProductos();

        return root;
    }

    private void filtrarProductos(String query) {
        filteredProductList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getNombre().toLowerCase().contains(query.toLowerCase())) {
                filteredProductList.add(product);
            }
        }
        cargarProductos(filteredProductList);
    }

    private void obtenerProductos() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<List<Product>> call = apiService.getProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    productList = response.body();
                    if (productList != null && !productList.isEmpty()) {
                        cargarProductos(productList);
                    } else {
                        // No se encontraron productos
                    }
                } else {
                    // Respuesta no exitosa
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                // Error de conexión
            }
        });
    }

    private void cargarProductos(List<Product> products) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        gridLayout.removeAllViews(); // Limpiar el GridLayout antes de cargar los productos

        for (Product product : products) {
            View itemView = inflater.inflate(R.layout.item_producto, gridLayout, false);
            ImageView imageView = itemView.findViewById(R.id.imageViewProducto);
            TextView textView = itemView.findViewById(R.id.textViewProductoNombre);

            textView.setText(product.getNombre());

            // Cargar la imagen desde la ruta guardada
            Picasso.get().load(new File(product.getImagen())).into(imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarDetalleProducto(product);
                }
            });

            gridLayout.addView(itemView);
        }
    }

    private void mostrarDetalleProducto(Product product) {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_product_detail);

        ImageView imageView = dialog.findViewById(R.id.imageViewProductoDetalle);
        TextView nombreTextView = dialog.findViewById(R.id.textViewProductoNombreDetalle);
        TextView precioTextView = dialog.findViewById(R.id.textViewProductoPrecioDetalle);
        TextView descripcionTextView = dialog.findViewById(R.id.textViewProductoDescripcionDetalle);
        TextView existenciaTextView = dialog.findViewById(R.id.textViewProductoExistenciaDetalle);

        // Configurar los valores del producto en las vistas correspondientes
        nombreTextView.setText(product.getNombre());
        descripcionTextView.setText(product.getDescripcion());
        precioTextView.setText("Precio: $" + product.getPrecio());
        existenciaTextView.setText("Existencia: " + product.getExistencia());

        // Cargar la imagen desde la ruta guardada
        Picasso.get().load(new File(product.getImagen())).into(imageView);

        // Configurar el tamaño del dialog
        dialog.getWindow().setLayout(
                (int) (getResources().getDisplayMetrics().widthPixels * 0.8), // 80% del ancho de la pantalla
                (int) (getResources().getDisplayMetrics().heightPixels * 0.6) // 60% del alto de la pantalla
        );

        dialog.show();
    }
}
