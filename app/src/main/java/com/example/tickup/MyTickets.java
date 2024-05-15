package com.example.tickup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyTickets extends AppCompatActivity {
    private TextView mensagem;
    private Button atualizar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        mensagem = findViewById(R.id.textView); // Nome mais descritivo para a TextView
        atualizar = findViewById(R.id.btnTestar);

        atualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // testando o OkHTTP

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("https://meowfacts.herokuapp.com/?count=3")
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            // Processar a resposta
                            final String json = response.body().string();

                            // Parsear o JSON para acessar a mensagem
                            Gson gson = new Gson();
                            MyObject object = gson.fromJson(json, MyObject.class);
                            String message = "";
                            for (int i = 0; i < object.data.length; i++){
                                message += object.data[i] + "\n\n";
                            }

                            // Atualizar o TextView na thread da UI
                            String finalMessage = message;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mensagem.setText(finalMessage);
                                }
                            });
                        } else {
                            // Tratar o erro
                            System.out.println("Falha na requisição: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        // Tratar o erro de rede
                        System.out.println("Falha na requisição: " + e.getMessage());
                    }
                });
            }
        });
    }

    // Definir a classe MyObject se necessário (caso a estrutura da resposta mude)
    public class MyObject {
        public String[] data;
    }
}
