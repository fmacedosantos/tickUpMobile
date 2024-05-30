package com.example.tickup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class AdminLogin extends AppCompatActivity {
    private EditText inputEmailContato, inputCpfCnpj;
    private Button btnEntrar;
    private OkHttpClient client;
    private JSONObject jsonObject;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        inputEmailContato = findViewById(R.id.inputEmailContato);
        inputCpfCnpj = findViewById(R.id.inputCpfCnpj);
        btnEntrar = findViewById(R.id.btnEntrar);

        client = new OkHttpClient();
        jsonObject = new JSONObject();
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Evento/Login";

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailContato = inputEmailContato.getText().toString();
                String cpfCnpj = inputCpfCnpj.getText().toString();

                try {
                    jsonObject.put("emailContato", emailContato);
                    jsonObject.put("cpfCnpj", cpfCnpj);
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
                                Toast.makeText(AdminLogin.this, "Erro ao fazer login", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(AdminLogin.this, "Login bem-sucedido", Toast.LENGTH_SHORT).show();

                                        Intent intent = new Intent(AdminLogin.this, VerifyTicket.class);
                                        startActivity(intent);
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AdminLogin.this, "Credenciais inválidas", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AdminLogin.this, "Erro: " + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }
}