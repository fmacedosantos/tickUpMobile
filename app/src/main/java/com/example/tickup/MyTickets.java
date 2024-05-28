package com.example.tickup;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyTickets extends AppCompatActivity {
    private ListView listaIngressos;
    private String email, url;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        listaIngressos = findViewById(R.id.listaIngressos);
        email = getIntent().getStringExtra("emailUsuario");
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Compra/Usuario/" + email;


    }

    private void obterIngressos(){
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();


    }

}
