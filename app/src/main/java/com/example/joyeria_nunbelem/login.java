package com.example.joyeria_nunbelem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class login extends AppCompatActivity {

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Objetos del XML
        editTextEmail = findViewById(R.id.edit_text_email);
        editTextPassword = findViewById(R.id.edit_text_password);
        buttonLogin = findViewById(R.id.button_login);

        // Evento Click
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // llamar la función
                fetchData();
            }
        });
    }

    // Esta función realiza el llamado a la API
    private void fetchData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api-joyeriabel.grstechs.com/usuarios/usuarios.php")
                .build();

        client.newCall(request).enqueue(new Callback() {

            // Error
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Manejar el fallo de la solicitud
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(login.this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // Exito
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    // Procesar los datos en el hilo principal
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Sí se conectó a la API, llama la siguiente función
                            processResponse(responseData);
                        }
                    });
                }
            }
        });
    }

    // Esta función Extrae los datos de la API
    private void processResponse(String responseData) {
        try {
            JSONArray jsonArray = new JSONArray(responseData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String email = jsonObject.getString("email");
                String password = jsonObject.getString("password");
                String name = jsonObject.getString("nombre");

                // Compara el email y la contraseña ingresados con los de la API
                String inputEmail = editTextEmail.getText().toString().trim();
                String inputPassword = editTextPassword.getText().toString().trim();

                // Inicio exitoso
                if (inputEmail.equals(email) && inputPassword.equals(password)) {
                    Toast.makeText(login.this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(login.this, SidebarActivity.class);
                    intent.putExtra("USER_NAME", name); // Pasar el nombre del usuario
                    intent.putExtra("EMAIL", email); // Pasar el correo del usuario
                    startActivity(intent);
                    finish(); // Terminar la actividad actual para evitar que el usuario vuelva atrás
                    Log.d("login", "Inicio de sesión exitoso");
                    return;
                }
            }

            // Inicio no exitoso
            Toast.makeText(login.this, "Credenciales inválidas", Toast.LENGTH_SHORT).show();
            Log.d("login", "Credenciales inválidas");

        } catch (JSONException e) {
            e.printStackTrace();
            // Manejo de errores de JSON
            Toast.makeText(login.this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
            Log.e("login", "Error al procesar los datos JSON");
        }
    }

}
