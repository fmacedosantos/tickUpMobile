package com.example.tickup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UserLogin extends AppCompatActivity {
    private EditText inputEmail, inputSenha;
    private Button btnEntrar;
    private ImageButton showHidePasswordButton;
    private OkHttpClient client;
    private JSONObject jsonObject;
    private String url;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        inputEmail = findViewById(R.id.inputEmailUsuario);
        inputSenha = findViewById(R.id.inputSenhaUsuario);
        btnEntrar = findViewById(R.id.btnEntrarUsuario);
        showHidePasswordButton = findViewById(R.id.showHidePasswordButton);

        client = new OkHttpClient();
        jsonObject = new JSONObject();
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Usuario/Login";

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                String senha = inputSenha.getText().toString();

                try {
                    jsonObject.put("email", email);
                    jsonObject.put("senha", senha);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UserLogin.this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            boolean loggedIn = Boolean.parseBoolean(responseData);
                            if (loggedIn) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(UserLogin.this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(UserLogin.this, MyTickets.class);
                                        intent.putExtra("emailUsuario", email);
                                        startActivity(intent);
                                    }
                                });
                            }
                        } else if (response.code() == 401) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UserLogin.this, "Dados inválidos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(UserLogin.this, "Erro: " + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });


            }
        });

        showHidePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    // Hide password
                    inputSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showHidePasswordButton.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    // Show password
                    inputSenha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showHidePasswordButton.setImageResource(R.drawable.ic_visibility);
                }
                // Move cursor to end of input
                inputSenha.setSelection(inputSenha.getText().length());
                isPasswordVisible = !isPasswordVisible;
            }
        });
    }
}
