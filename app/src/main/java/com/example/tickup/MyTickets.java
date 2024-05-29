package com.example.tickup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyTickets extends AppCompatActivity {
    private TextView txtViewIngressos;
    private String email, url;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        txtViewIngressos = findViewById(R.id.teste);
        email = getIntent().getStringExtra("emailUsuario");
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Compra/Usuario/" + email;
        fetchTickets(email);
    }

    private void fetchTickets(String email) {
        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtViewIngressos.setText("Requisição falhou: " + e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String json = response.body().string();

                    Gson gson = new Gson();
                    Type listType = new TypeToken<List<Ingresso>>() {}.getType();
                    List<Ingresso> ingressos = gson.fromJson(json, listType);

                    StringBuilder message = new StringBuilder();
                    if (ingressos != null && !ingressos.isEmpty()) {
                        for (Ingresso ingresso : ingressos) {
                            message.append("Código: ").append(ingresso.getIdIngresso())
                                    .append(", Evento: ").append(ingresso.getNomeEvento())
                                    .append("\n\n");
                        }
                    } else {
                        message.append("O usuário cadastrado não possui ingressos.");
                    }

                    final String finalMessage = message.toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtViewIngressos.setText(finalMessage);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txtViewIngressos.setText("Falha em encontrar os ingressos: " + response.code());
                        }
                    });
                }
            }
        });
    }
}