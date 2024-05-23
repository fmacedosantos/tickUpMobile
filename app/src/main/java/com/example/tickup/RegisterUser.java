package com.example.tickup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

public class RegisterUser extends AppCompatActivity {
    private TextView inputNome, inputTelefone, inputIdade, inputCpf, inputEmail, inputSenha;
    private Button btnCadastrar;
    private String ipAddress, port;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        inputNome = findViewById(R.id.inputNome);
        inputTelefone = findViewById(R.id.inputTelefone);
        inputIdade = findViewById(R.id.inputIdade);
        inputCpf = findViewById(R.id.inputCpf);
        inputEmail = findViewById(R.id.inputEmail);
        inputSenha = findViewById(R.id.inputSenha);
        btnCadastrar = findViewById(R.id.btnCadastrar);

        // Obtendo o endereço IP usando a classe utilitária
        ipAddress = NetworkUtils.getLocalIpAddress();
        port = "5076";



        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = inputNome.getText().toString();
                String telefone = inputTelefone.getText().toString();
                String idade = inputIdade.getText().toString();
                String cpf = inputCpf.getText().toString();
                String email = inputEmail.getText().toString();
                String senha = inputSenha.getText().toString();

                consumeApi(nome, telefone, idade, cpf, email, senha);
            }
        });

    }

    private void consumeApi(String nome, String telefone, String idade, String cpf, String email, String senha) {
        OkHttpClient client = new OkHttpClient();
        String url = "http://" + ipAddress + ":" + port + "/api/Usuario/Cadastrar";

        // Criar o JSON para enviar no corpo da requisição
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("nome", nome);
            jsonObject.put("telefone", telefone);
            jsonObject.put("idade", idade);
            jsonObject.put("cpf", cpf);
            jsonObject.put("email", email);
            jsonObject.put("senha", senha);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Criar o RequestBody com o JSON
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.get("application/json; charset=utf-8"));

        // Criar a requisição POST
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(RegisterUser.this, "Erro na API: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    runOnUiThread(() -> Toast.makeText(RegisterUser.this, "Resposta da API: " + responseData, Toast.LENGTH_LONG).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterUser.this, "Erro na API: " + response.message(), Toast.LENGTH_LONG).show());
                }
            }
        });
    }
}