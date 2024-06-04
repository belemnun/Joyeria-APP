package com.example.joyeria_nunbelem;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.joyeria_nunbelem.databinding.FragmentUsuariosBinding;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuariosFragment extends Fragment {

    private FragmentUsuariosBinding binding;

    private EditText id_usuarioEditText;
    private TextInputEditText nombreEditText;
    private EditText apellidoEditText;
    private EditText emailEditText;
    private EditText passwordEditText;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentUsuariosBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        id_usuarioEditText = root.findViewById(R.id.consultaUsuarioEditText);
        nombreEditText = root.findViewById(R.id.nombreEditText);
        apellidoEditText = root.findViewById(R.id.apellidoEditText);
        emailEditText = root.findViewById(R.id.emailEditText);
        passwordEditText = root.findViewById(R.id.passwordEditText);

        Button agregarButton = root.findViewById(R.id.agregarButton);
        Button eliminarButton = root.findViewById(R.id.eliminarButton);
        Button modificarButton = root.findViewById(R.id.modificarButton);
        Button consultarButton = root.findViewById(R.id.consultarButton);

        agregarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores ingresados por el usuario
                String nombre = nombreEditText.getText().toString().trim();
                String apellido = apellidoEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (nombre.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el Nombre del Usuario", Toast.LENGTH_SHORT).show();
                    nombreEditText.requestFocus();
                    return;
                } else if (apellido.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el Apellido del Usuario", Toast.LENGTH_SHORT).show();
                    apellidoEditText.requestFocus();
                    return;
                } else if (email.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el Email del Usuario", Toast.LENGTH_SHORT).show();
                    emailEditText.requestFocus();
                    return;
                } else if (password.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese la Contraseña del Usuario", Toast.LENGTH_SHORT).show();
                    passwordEditText.requestFocus();
                    return;
                }

                // Crear un objeto User con los datos ingresados
                User nuevoUsuario = new User();
                nuevoUsuario.setNombre(nombre);
                nuevoUsuario.setApellido(apellido);
                nuevoUsuario.setEmail(email);
                nuevoUsuario.setPassword(password);

                // Aquí puedes enviar el nuevoUsuario a través de Retrofit para agregarlo a la API

                // Por ejemplo:
                agregarUsuario(nuevoUsuario);
                limpiarCajasTexto();
            }
        });

        modificarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtener los valores ingresados por el usuario
                String id_usuarioStr = id_usuarioEditText.getText().toString().trim();
                String nombre = nombreEditText.getText().toString().trim();
                String apellido = apellidoEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (id_usuarioStr.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el ID del Usuario a actualizar", Toast.LENGTH_SHORT).show();
                    id_usuarioEditText.requestFocus();
                    return;
                } else if (nombre.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el Nombre del Usuario", Toast.LENGTH_SHORT).show();
                    nombreEditText.requestFocus();
                    return;
                } else if (apellido.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el Apellido del Usuario", Toast.LENGTH_SHORT).show();
                    apellidoEditText.requestFocus();
                    return;
                } else if (email.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el Email del Usuario", Toast.LENGTH_SHORT).show();
                    emailEditText.requestFocus();
                    return;
                } else if (password.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese la Contraseña del Usuario", Toast.LENGTH_SHORT).show();
                    passwordEditText.requestFocus();
                    return;
                }


                // Crear un objeto User con los datos ingresados
                User nuevoUsuario = new User();
                nuevoUsuario.setId_usuario(id_usuarioStr);
                nuevoUsuario.setNombre(nombre);
                nuevoUsuario.setApellido(apellido);
                nuevoUsuario.setEmail(email);
                nuevoUsuario.setPassword(password);

                // Aquí puedes enviar el nuevoUsuario a través de Retrofit para actualizarlo en la API

                // Por ejemplo:
                actualizarUsuario(nuevoUsuario);
                limpiarCajasTexto();

            }
        });

        eliminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el ID del usuario ingresado por el usuario
                String nombre = id_usuarioEditText.getText().toString().trim();

                if (nombre.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el ID del Usuario a eliminar", Toast.LENGTH_SHORT).show();
                    id_usuarioEditText.requestFocus();
                    return;
                }
                // Aquí puedes enviar el ID del usuario a través de Retrofit para eliminarlo de la API

                // Por ejemplo:
                eliminarUsuario(nombre);
                limpiarCajasTexto();
            }
        });

        consultarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el ID del usuario ingresado por el usuario
                String nombre = id_usuarioEditText.getText().toString().trim();

                if (nombre.isEmpty()) {
                    Toast.makeText(getActivity(), "Ingrese el Nombre del Usuario a consultar", Toast.LENGTH_SHORT).show();
                    id_usuarioEditText.requestFocus();
                    return;
                }

                // Por ejemplo:
                consultarUsuario(nombre);
            }
        });

        return root;
    }

    private void agregarUsuario(User usuario) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.createUser(usuario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Manejar la respuesta aquí
                if (response.isSuccessful()) {
                    // El usuario se agregó exitosamente
                    Log.d("Exito", "Exito");
                    Toast.makeText(getActivity(), "Usuario agregado correctamente", Toast.LENGTH_SHORT).show();
                } else {
                    // Ocurrió un error al agregar el usuario
                    Toast.makeText(getActivity(), "Error al agregar usuario", Toast.LENGTH_SHORT).show();
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

    private void actualizarUsuario(User usuario) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.updateUser(usuario);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Manejar la respuesta aquí
                if (response.isSuccessful()) {
                    // La categoría se actualizó exitosamente
                    Log.d("Exito", "Exito");
                    Toast.makeText(getActivity(), "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
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

    private void eliminarUsuario(String nombre) {

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<Void> call = apiService.deleteUser(nombre);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                // Manejar la respuesta aquí
                if (response.isSuccessful()) {
                    // La categoría se actualizó exitosamente
                    Log.d("Exito", "Exito");
                    Toast.makeText(getActivity(), "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                    //obtenerCategoriasDesdeApi();
                } else {
                    // Ocurrió un error al actualizar la categoría
                    Toast.makeText(getActivity(), "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
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

    private void consultarUsuario(String nombre) {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<User> call = apiService.getUserByName(nombre);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    // Producto encontrado
                    User user = response.body();
                    // Aquí puedes manejar el producto obtenido, por ejemplo, mostrarlo en los EditText
                    if (user != null) {
                        nombreEditText.setText(user.getNombre());
                        apellidoEditText.setText(user.getApellido());
                        emailEditText.setText(user.getEmail());
                        passwordEditText.setText(user.getPassword());

                        if (emailEditText.getText().toString() == "") {
                            Toast.makeText(getActivity(), "Usuario no encontrada", Toast.LENGTH_SHORT).show();
                            limpiarCajasTexto();

                        }

                    } else {
                        Toast.makeText(getActivity(), "Producto no encontrado", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Ocurrió un error al obtener el producto
                    Toast.makeText(getActivity(), "Error al obtener el producto", Toast.LENGTH_SHORT).show();
                    Log.d("Error", "Error: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // Manejar el fallo aquí
                Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarCajasTexto() {
        id_usuarioEditText.setText("");
        nombreEditText.setText("");
        apellidoEditText.setText("");
        emailEditText.setText("");
        passwordEditText.setText("");
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
