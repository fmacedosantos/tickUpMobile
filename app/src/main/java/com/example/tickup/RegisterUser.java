package com.example.tickup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
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

public class RegisterUser extends AppCompatActivity {

    private EditText nameInput, phoneInput, ageInput, cpfInput, emailInput, passwordInput;
    private Button registerBtn;

    private ImageButton showHidePasswordButton;
    private OkHttpClient client;
    private JSONObject jsonObject;
    private String url;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        nameInput = findViewById(R.id.nameInput);
        phoneInput = findViewById(R.id.phoneInput);
        ageInput = findViewById(R.id.ageInput);
        cpfInput = findViewById(R.id.cpfInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        registerBtn = findViewById(R.id.registerBtn);
        showHidePasswordButton = findViewById(R.id.showHidePasswordButton);

        cpfInput.addTextChangedListener(new TextWatcher() {
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
                    cpfInput.setText(mask.toString());
                    cpfInput.setSelection(mask.length());
                    isUpdating = false;
                }
            }
        });

        phoneInput.addTextChangedListener(new TextWatcher() {
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
                    phoneInput.setText(mask.toString());
                    phoneInput.setSelection(mask.length());
                    isUpdating = false;
                }
            }
        });

        client = new OkHttpClient();
        jsonObject = new JSONObject();
        url = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Usuario/Cadastrar";

        showHidePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showHidePasswordButton.setImageResource(R.drawable.ic_visibility_off);
                } else {
                    passwordInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showHidePasswordButton.setImageResource(R.drawable.ic_visibility);
                }
                passwordInput.setSelection(passwordInput.getText().length());
                isPasswordVisible = !isPasswordVisible;
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameInput.getText().toString();
                String phone = phoneInput.getText().toString();
                String ageStr = ageInput.getText().toString();
                String cpf = cpfInput.getText().toString();
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (name.isEmpty() || phone.isEmpty() || ageStr.isEmpty() || cpf.isEmpty() || email.isEmpty() || password.isEmpty()) {
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

                if (!isTelefoneValid(phone)) {
                    Toast.makeText(RegisterUser.this, "Telefone inválido", Toast.LENGTH_SHORT).show();
                    return;
                }

                int age;
                try {
                    age = Integer.parseInt(ageStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(RegisterUser.this, "Idade inválida", Toast.LENGTH_SHORT).show();
                    return;
                }

                verifyEmail(email, emailExist -> {
                    if (emailExist) {
                        runOnUiThread(() -> Toast.makeText(RegisterUser.this, "Email já cadastrado", Toast.LENGTH_SHORT).show());
                    } else {
                        verifyCpf(cpf, cpfExist -> {
                            if (cpfExist) {
                                runOnUiThread(() -> Toast.makeText(RegisterUser.this, "CPF já cadastrado", Toast.LENGTH_SHORT).show());
                            } else {
                                registerUser(name, phone, age, cpf, email, password);
                            }
                        });
                    }
                });
            }
        });
    }

    private void registerUser(String name, String phone, int age, String cpf, String email, String password) {
        try {
            jsonObject.put("email", email);
            jsonObject.put("cpf", cpf);
            jsonObject.put("nome", name);
            jsonObject.put("telefone", phone);
            jsonObject.put("senha", password);
            jsonObject.put("idade", age);
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
                runOnUiThread(() -> Toast.makeText(RegisterUser.this, "Erro ao registrar usuário", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    runOnUiThread(() -> {
                        Toast.makeText(RegisterUser.this, "Usuário registrado com sucesso", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterUser.this, MyTickets.class);
                        intent.putExtra("emailUsuario", email);
                        startActivity(intent);
                        nameInput.setText("");
                        phoneInput.setText("");
                        ageInput.setText("");
                        cpfInput.setText("");
                        emailInput.setText("");
                        passwordInput.setText("");
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterUser.this, "Erro: " + response.message(), Toast.LENGTH_SHORT).show());
                }
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

    private void verifyEmail(String email, VerificationCallback callback) {
        String verifyEmailUrl = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Usuario/Verificar/Email/" + email;

        Request request = new Request.Builder()
                .url(verifyEmailUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onResult(false));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> callback.onResult(true));
                } else {
                    runOnUiThread(() -> callback.onResult(false));
                }
            }
        });
    }

    private void verifyCpf(String cpf, VerificationCallback callback) {
        String verifyCpfUrl = "https://tick-up-1fb4969b94c5.herokuapp.com/api/Usuario/Verificar/Cpf/" + cpf;

        Request request = new Request.Builder()
                .url(verifyCpfUrl)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> callback.onResult(false));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> callback.onResult(true));
                } else {
                    runOnUiThread(() -> callback.onResult(false));
                }
            }
        });
    }

    interface VerificationCallback {
        void onResult(boolean exist);
    }
}
