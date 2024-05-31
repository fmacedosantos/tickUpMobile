package com.example.tickup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
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
    private EditText emailInput, cpfCnpjInput;
    private Button enterBtn;
    private OkHttpClient client;
    private JSONObject jsonObject;
    private String url;
    private Switch switchCpfCnpj;
    private TextWatcher cpfTextWatcher, cnpjTextWatcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailInput = findViewById(R.id.emailContactInput);
        cpfCnpjInput = findViewById(R.id.cpfCnpjInput);
        enterBtn = findViewById(R.id.btnEntrar);
        switchCpfCnpj = findViewById(R.id.cnpjSwt);

        client = new OkHttpClient();
        jsonObject = new JSONObject();
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Evento/Login";

        cpfTextWatcher = createTextWatcher("###.###.###-##");
        cnpjTextWatcher = createTextWatcher("##.###.###/####-##");

        switchCpfCnpj.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                cpfCnpjInput.setHint("CNPJ");
                cpfCnpjInput.removeTextChangedListener(cpfTextWatcher);
                cpfCnpjInput.addTextChangedListener(cnpjTextWatcher);
            } else {
                cpfCnpjInput.setHint("CPF");
                cpfCnpjInput.removeTextChangedListener(cnpjTextWatcher);
                cpfCnpjInput.addTextChangedListener(cpfTextWatcher);
            }
            cpfCnpjInput.setText("");
        });

        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                String cpfCnpj = cpfCnpjInput.getText().toString();

                try {
                    jsonObject.put("emailContato", email);
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
                            }
                        } else if (response.code() == 401) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AdminLogin.this, "Dados inválidos", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else {
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

        switchCpfCnpj.setChecked(false);
        cpfCnpjInput.addTextChangedListener(cpfTextWatcher);
    }

    private TextWatcher createTextWatcher(final String maskPattern) {
        return new TextWatcher() {
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
                    for (char m : maskPattern.toCharArray()) {
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
                    cpfCnpjInput.setText(mask.toString());
                    cpfCnpjInput.setSelection(mask.length());
                    isUpdating = false;
                }
            }
        };
    }
}
