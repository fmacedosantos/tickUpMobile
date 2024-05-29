package com.example.tickup;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
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

public class RegisterUser extends AppCompatActivity {

    private EditText inputNome, inputTelefone, inputIdade, inputCpf, inputEmail, inputSenha;
    private Button btnCadastrar;
    private OkHttpClient client;
    private JSONObject jsonObject;
    private String url;

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

        inputCpf.addTextChangedListener(new TextWatcher() {
            private static final int MAX_LENGTH = 14;
            private boolean isUpdating;
            private String oldString = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().replaceAll("[^\\d]", "");
                if (!isUpdating) {
                    isUpdating = true;
                    StringBuilder mask = new StringBuilder();
                    int i = 0;
                    for (char m : "###.###.###-##".toCharArray()) {
                        if (m != '#' && str.length() > oldString.length()) {
                            mask.append(m);
                            continue;
                        }
                        try {
                            mask.append(str.charAt(i));
                        } catch (Exception e) {
                            break;
                        }
                        i++;
                    }
                    oldString = mask.toString();
                    inputCpf.setText(mask.toString());
                    inputCpf.setSelection(mask.length());
                    isUpdating = false;
                }
            }
        });

        inputTelefone.addTextChangedListener(new TextWatcher() {
            private boolean isUpdating;
            private String oldString = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString().replaceAll("[^\\d]", "");
                if (!isUpdating) {
                    isUpdating = true;
                    StringBuilder mask = new StringBuilder();
                    int i = 0;
                    for (char m : "(##) #####-####".toCharArray()) {
                        if (m != '#' && str.length() > oldString.length()) {
                            mask.append(m);
                            continue;
                        }
                        try {
                            mask.append(str.charAt(i));
                        } catch (Exception e) {
                            break;
                        }
                        i++;
                    }
                    oldString = mask.toString();
                    inputTelefone.setText(mask.toString());
                    inputTelefone.setSelection(mask.length());
                    isUpdating = false;
                }
            }
        });

        client = new OkHttpClient();
        jsonObject = new JSONObject();
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Usuario/Cadastrar";

        btnCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = inputNome.getText().toString();
                String telefone = inputTelefone.getText().toString();
                String idadeStr = inputIdade.getText().toString();
                String cpf = inputCpf.getText().toString();
                String email = inputEmail.getText().toString();
                String senha = inputSenha.getText().toString();

                if (nome.isEmpty() || telefone.isEmpty() || idadeStr.isEmpty() || cpf.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(RegisterUser.this, "Todos os campos devem ser preenchidos", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isEmailValid(email)) {
                    Toast.makeText(RegisterUser.this, "Email inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isCpfValid(cpf)) {
                    Toast.makeText(RegisterUser.this, "CPF inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isTelefoneValid(telefone)) {
                    Toast.makeText(RegisterUser.this, "Telefone inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                int idade;
                try {
                    idade = Integer.parseInt(idadeStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(RegisterUser.this, "Idade inválida", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    jsonObject.put("email", email);
                    jsonObject.put("cpf", cpf);
                    jsonObject.put("nome", nome);
                    jsonObject.put("telefone", telefone);
                    jsonObject.put("senha", senha);
                    jsonObject.put("idade", idade);
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
                                Toast.makeText(RegisterUser.this, "Erro ao registrar usuário", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterUser.this, "Usuário registrado com sucesso", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(RegisterUser.this, "Erro: " + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isCpfValid(String cpf) {
        String unformattedCpf = cpf.replaceAll("[^\\d]", "");
        return unformattedCpf.length() == 11;
    }

    private boolean isTelefoneValid(String telefone) {
        String unformattedTelefone = telefone.replaceAll("[^\\d]", "");
        return unformattedTelefone.length() == 11;
    }
}
