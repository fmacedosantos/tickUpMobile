package com.example.tickup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private EditText textoEmail, textoSenha;
    private Button botaoEntrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textoEmail = findViewById(R.id.textoEmail);
        textoSenha = findViewById(R.id.textoSenha);
        botaoEntrar = findViewById(R.id.botaoEntrar);

        botaoEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = textoEmail.toString();
                String senha = textoSenha.toString();

                // adiconar verificação banco de dados

                Intent intent = new Intent(MainActivity.this, MyTickets.class);
                startActivity(intent);
            }
        });
    }
}