package com.example.tickup;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;

public class MyTickets extends AppCompatActivity {
    ListView listaIngressos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        listaIngressos = findViewById(R.id.listaIngressos);

    }
}
